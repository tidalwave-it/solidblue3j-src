/*
 * *********************************************************************************************************************
 *
 * SolidBlue 3: Data safety
 * http://tidalwave.it/projects/solidblue3
 *
 * Copyright (C) 2023 - 2023 by Tidalwave s.a.s. (http://tidalwave.it)
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 *
 * git clone https://bitbucket.org/tidalwave/solidblue3j-src
 * git clone https://github.com/tidalwave-it/solidblue3j-src
 *
 * *********************************************************************************************************************
 */
package it.tidalwave.datamanager.dao.impl.jpa;

import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Path;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import jakarta.transaction.Transactional;
import it.tidalwave.util.LazySupplier;
import it.tidalwave.util.spring.jpa.impl.LoggingJpaTransactionManager;
import it.tidalwave.datamanager.model.ManagedFile;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static it.tidalwave.datamanager.model.DataManager.BackupFinder.SortingKeys.LABEL;
import static it.tidalwave.datamanager.model.DataManager.ManagedFileFinder.SortingKeys.PATH;
import static jakarta.transaction.Transactional.TxType.NEVER;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static it.tidalwave.util.Finder.SortDirection.ASCENDING;
import static it.tidalwave.util.spring.jpa.JpaSpecificationFinder.by;
import static it.tidalwave.util.test.FileComparisonUtils.assertSameContents;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@EnableJpaRepositories
@Slf4j
public class JpaDataManagerDaoTest extends AbstractTestNGSpringContextTests
  {
    private static final int MAX_MANAGED_FILES = 10;
    private static final int MAX_FINGERPRINTS = 10;
    private static final int BACKUP_ENTITY_COUNT = 10;
    private static final int MAX_BACKUP_FILES = 10;

    @Inject
    private JpaDataManagerDao underTest;

    @Inject
    private EntityManager em;

    @Inject
    private EntityManagerFactory emf;

    @Inject
    private LoggingJpaTransactionManager txManager;

    private TestEntityFactory tef;

    private List<ManagedFileEntity> managedFileEntities;

    private List<BackupEntity> backupEntities;

    /******************************************************************************************************************/
    @BeforeClass
    public void prepare()
      {
        tef = new TestEntityFactory();
        managedFileEntities = tef.createManagedFileEntities(MAX_MANAGED_FILES, MAX_FINGERPRINTS);
        backupEntities = tef.createBackupEntities(managedFileEntities, BACKUP_ENTITY_COUNT, MAX_BACKUP_FILES);
        log.info("Populating database...");
        runInOtherTx(em -> managedFileEntities.forEach(em::persist));
        runInOtherTx(em -> backupEntities.forEach(em::persist));
      }

    /******************************************************************************************************************/
    @AfterClass
    public void cleanUp()
      {
        log.info("Scratching database...");
        runInOtherTx(em -> Stream.of("ManagedFile", "Fingerprint", "Backup", "BackupFile")
                                 .map(e -> String.format("DELETE FROM %sEntity", e))
                                 .forEach(q -> em.createQuery(q).executeUpdate()));
      }

    /******************************************************************************************************************/
    @Test
    public void test_database_schema()
            throws IOException, InterruptedException
      {
        final var dbFile = Path.of("target/test1.db");
        final var actualDump = Path.of("target/actual-schema.sql");
        final var expectedDump = Path.of("src/test/resources/expected-schema.sql");
        dumpSchema(dbFile, actualDump);
        assertSameContents(expectedDump, actualDump);
      }

    // In below tests transactional mode is NEVER because a pre-existing tx context would prevent from counting commits
    /******************************************************************************************************************/
    @Test(dataProvider = "fingerprintParameters") @Transactional(NEVER)
    public void test_findManagedFiles (@Nonnull final Optional<String> fingerprint)
            throws IOException
      {
        // given
        txManager.resetCounters();
        // when
        final var actualResult = underTest.findManagedFiles()
                                          .sort(by(PATH), ASCENDING)
                                          .withFingerprint(fingerprint)
                                          .results();
        // then
        assertThat(txManager.getCommitCount(), is(1));

        final var expectedResult = managedFileEntities.stream()
                                                      .filter(mfe -> fingerprint.map(fp -> contains(mfe, fp)).orElse(true))
                                                      .map(underTest::managedFileEntityToModel)
                                                      .toList();

        log.info("Asserting that lazy collection of fingerprints not fetched yet...");
        // TODO: this does not test ManagedFileEntity lazy field, only ManagedFile
        actualResult.stream().map(mf -> (LazySupplier<?>)inspect(mf, "fingerprints"))
                    .forEach(lz -> assertThat(lz.isInitialized(), is(false)));

        log.info("Triggering lazy fetch...");
        txManager.resetCounters();
        actualResult.forEach(ManagedFile::getFingerprints);
        assertThat(txManager.getCommitCount(), is(expectedResult.size()));

        // Dumps for easier manual inspection in case of mismatch
        final var s = String.format("-fingerprint_%s", fingerprint.orElse(""));
        log.info("Actual result:");
        tef.dumpToYaml(actualResult, Path.of("target/findManagedFiles-actual" + s + ".yaml"));
        log.info("Expected result:");
        tef.dumpToYaml(expectedResult, Path.of("target/findManagedFiles-expected" + s + ".yaml"));

        txManager.resetCounters();
        assertThat(actualResult, containsInAnyOrder(expectedResult.toArray()));
        assertThat(txManager.getCommitCount(), is(0));
      }

    /******************************************************************************************************************/
    @Test(dataProvider = "backupParameters") @Transactional(NEVER)
    public void test_findBackups (@Nonnull final Optional<String> label,
                                  @Nonnull final Optional<String> volumeId,
                                  @Nonnull final Optional<String> fileId)
            throws IOException
      {
        // given
        txManager.resetCounters();
        // when
        final var actualResult = underTest.findBackups()
                                          .sort(by(LABEL), ASCENDING)
                                          .withLabel(label)
                                          .withVolumeId(volumeId)
                                          .withFileId(fileId)
                                          .results();
        // then
        assertThat(txManager.getCommitCount(), is(1));

        final var expectedResult = backupEntities.stream()
            .filter(be -> label.map(n -> be.getLabel().equals(n)).orElse(true))
            .filter(be -> volumeId.map(v -> be.getVolumeId().equals(v)).orElse(true))
            .filter(be -> fileId.map(i -> be.getBackupFiles().stream()
                                            .anyMatch(bf -> bf.getManagedFile().getId().equals(i)))
                                .orElse(true))
            .map(underTest::backupEntityToModel)
            .toList();

        log.info("Asserting that lazy collection of backup files not fetched yet...");
        // TODO: this does not test BackupEntity lazy field, only Backup
        actualResult.stream().map(b -> (LazySupplier<?>)inspect(b, "backupFiles"))
                             .forEach(lz -> assertThat(lz.isInitialized(), is(false)));

        log.info("Triggering lazy fetch...");
        txManager.resetCounters();
        actualResult.forEach(b -> b.getBackupFiles().forEach(bf -> assertThat(bf.getBackup(), is(sameInstance(b)))));
        assertThat(txManager.getCommitCount(), is(expectedResult.size()));

        // they will be triggered by next asserts, invalidating assertion count
        log.info("Triggering lazy fetch for ManagedFiles...");
        expectedResult.forEach(b -> b.getBackupFiles().forEach(bg -> bg.getManagedFile().getFingerprints()));
        actualResult.forEach(b -> b.getBackupFiles().forEach(bg -> bg.getManagedFile().getFingerprints()));

        // Dumps for easier manual inspection in case of mismatch
        final var s = String.format("-label_%s-volumeId-%s-fileId_%s",
                                    label.orElse(""), volumeId.orElse(""), fileId.orElse(""));
        log.info("Actual result:");
        tef.dumpToYaml(actualResult, Path.of("target/findBackups-actual" + s + ".yaml"));
        log.info("Expected result:");
        tef.dumpToYaml(expectedResult, Path.of("target/findManagedFiles-expected" + s + ".yaml"));

        txManager.resetCounters();
        assertThat(actualResult, containsInAnyOrder(expectedResult.toArray()));
        assertThat(txManager.getCommitCount(), is(0));
      }

    /******************************************************************************************************************/
    private static boolean contains (@Nonnull final ManagedFileEntity entity, @Nonnull final String fingerprint)
      {
        return entity.getFingerprints().stream().anyMatch(fp -> fp.getValue().equals(fingerprint));
      }

    /******************************************************************************************************************/
    private static Object inspect (@Nonnull final Object object, @Nonnull final String fieldName)
      {
        try
          {
            final var field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
          }
        catch (NoSuchFieldException | IllegalAccessException e)
          {
            throw new RuntimeException(e);
          }
      }

    /******************************************************************************************************************/
    private void dumpSchema (@Nonnull final Path dbFile, @Nonnull final Path dumpFile)
            throws IOException, InterruptedException
      {
        log.info("Dumping schema to {} ...", dumpFile);
        final var cmd = new String[]{"/bin/sh", "-c", "sqlite3 %s .schema > %s".formatted(dbFile, dumpFile)};
        final var status = Runtime.getRuntime().exec(cmd).waitFor();
        assertThat(status, is(0));
      }

    /******************************************************************************************************************/
    private void runInOtherTx (@Nonnull final Consumer<? super EntityManager> runnable)
      {
        try (final var em = emf.createEntityManager())
          {
            em.getTransaction().begin();
            runnable.accept(em);
            em.getTransaction().commit();
          }
      }

    /******************************************************************************************************************/
    @DataProvider
    private static Object[][] fingerprintParameters()
      {
        return new Object[][]
          {
            { Optional.empty() },
            { Optional.of("missing") },
            { Optional.of("80fe035fe88f37471862c5ba5013b472") }
          };
      }

    /******************************************************************************************************************/
    @DataProvider
    private static Object[][] backupParameters()
      {
        return new Object[][]
          {
            { Optional.empty(),       Optional.empty(),           Optional.empty() },
            { Optional.of("missing"), Optional.empty(),           Optional.empty() },
            { Optional.of("label 4"), Optional.empty(),           Optional.empty() },
            { Optional.empty(),       Optional.of("missing"),     Optional.empty() },
            { Optional.empty(),       Optional.of("volumeId 7"),  Optional.empty() },
            { Optional.empty(),       Optional.empty(),           Optional.of("missing") },
            { Optional.empty(),       Optional.empty(),           Optional.of("id") },
          };
      }
  }