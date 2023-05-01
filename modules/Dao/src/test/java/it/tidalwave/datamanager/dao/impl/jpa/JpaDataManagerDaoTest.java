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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.nio.file.Path;
import java.math.BigInteger;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import jakarta.transaction.Transactional;
import it.tidalwave.util.IdFactory;
import it.tidalwave.util.LazySupplier;
import it.tidalwave.util.spring.jpa.impl.LoggingJpaTransactionManager;
import it.tidalwave.datamanager.model.ManagedFile;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static it.tidalwave.datamanager.model.DataManager.ManagedFileFinder.SortingKeys.PATH;
import static jakarta.transaction.Transactional.TxType.NEVER;
import static java.util.Comparator.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static it.tidalwave.util.Finder.SortDirection.ASCENDING;
import static it.tidalwave.util.FunctionalCheckedExceptionWrappers.*;
import static it.tidalwave.util.StreamUtils.randomLocalDateTimeStream;
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

    @Inject
    private JpaDataManagerDao underTest;

    @Inject
    private TestEntityManager em;

    @Inject
    private LoggingJpaTransactionManager txManager;

    private final List<ManagedFileEntity> managedFileEntities = new ArrayList<>();

    private final IdFactory idFactory = IdFactory.MOCK;

    private final Iterator<LocalDateTime> timestampSequence = randomLocalDateTimeStream(
            17L,
            LocalDateTime.of(2020, 1, 1, 0, 0),
            LocalDateTime.of(2023, 12, 31, 23, 59))
            .iterator();

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

    /******************************************************************************************************************/
    @Test(dependsOnMethods = "test_database_schema")
    public void test_populate_database()
      {
        managedFileEntities.addAll(createManagedFileEntities());
        runInTx(em -> managedFileEntities.forEach(em::persist));
      }

    /******************************************************************************************************************/
    @Test(dataProvider = "fingerprintParameters", dependsOnMethods = "test_populate_database") @Transactional(NEVER)
    public void test_findManagedFiles (@Nonnull final Optional<String> fingerprint)
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

        log.info("Actual result:");
        txManager.resetCounters();
        actualResult.forEach(m -> log.info("    {}", m));
        assertThat(actualResult, containsInAnyOrder(expectedResult.toArray()));
        assertThat(txManager.getCommitCount(), is(0));
      }

    /******************************************************************************************************************/
    private void runInTx (@Nonnull final Consumer<? super EntityManager> task)
      {
        try (final var em = txManager.getEntityManagerFactory().createEntityManager())
          {
            em.getTransaction().begin();
            task.accept(em);
            em.getTransaction().commit();
            em.clear();
          }
      }

    /******************************************************************************************************************/
    /******************************************************************************************************************/
    @Nonnull
    private List<ManagedFileEntity> createManagedFileEntities()
      {
        final var paths = new Random(4)
                .ints(0x2540, 0xffff)
                .limit(MAX_MANAGED_FILES)
                .mapToObj("/foo/bar/%x"::formatted)
                .toList();
        final var counts = new Random(5).ints(0, MAX_FINGERPRINTS).iterator();
        return paths.stream().map(_f(p -> createManagedFileEntity(p, counts.next()))).toList();
      }

    /******************************************************************************************************************/
    @Nonnull
    private ManagedFileEntity createManagedFileEntity (@Nonnull final String path, final int fingerprintCount)
            throws NoSuchAlgorithmException
      {
        final var entity = new ManagedFileEntity(idFactory.createId().stringValue(), path, List.of());
        final var algorithm = "md5";
        final var bytes = MessageDigest.getInstance(algorithm).digest(path.getBytes(UTF_8));
        final var fingerprint = ("%0" + (bytes.length * 2) + "x").formatted(new BigInteger(1, bytes));
        final var name = Path.of(path).getFileName().toString();
        final IntFunction<FingerprintEntity> fp = __ -> new FingerprintEntity(idFactory.createId().stringValue(),
                                                                              name,
                                                                              algorithm,
                                                                              fingerprint,
                                                                              timestampSequence.next(),
                                                                              entity.getId());
        entity.setFingerprints(IntStream.range(0, fingerprintCount)
                                        .mapToObj(fp)
                                        .sorted(comparing(FingerprintEntity::getTimestamp))
                                        .toList());
        return entity;
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
        final var cmd = new String[]{"/bin/sh", "-c", "sqlite3 %s .dump > %s".formatted(dbFile, dumpFile)};
        final var status = Runtime.getRuntime().exec(cmd).waitFor();
        assertThat(status, is(0));
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
  }