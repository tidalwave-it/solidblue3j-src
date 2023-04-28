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
import it.tidalwave.util.spring.jpa.impl.DefaultFinderJpaRepository;
import it.tidalwave.util.spring.jpa.impl.LoggingJpaTransactionManager;
import it.tidalwave.datamanager.model.ManagedFile;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static jakarta.transaction.Transactional.TxType.NEVER;
import static java.util.Comparator.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static it.tidalwave.util.Finder.SortDirection.ASCENDING;
import static it.tidalwave.util.FunctionalCheckedExceptionWrappers.*;
import static it.tidalwave.util.StreamUtils.randomLocalDateTimeStream;
import static it.tidalwave.util.spring.jpa.JpaRepositoryFinder.by;
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
@EnableJpaRepositories(repositoryBaseClass = DefaultFinderJpaRepository.class)
@Slf4j
public class JpaDataManagerDaoTest extends AbstractTestNGSpringContextTests
  {
    @Inject
    private JpaDataManagerDao underTest;

    @Inject
    private TestEntityManager em;

    @Inject
    private LoggingJpaTransactionManager txManager;

    private final List<ManagedFileEntity> entities = new ArrayList<>();

    private final IdFactory idFactory = IdFactory.MOCK;

    private final Iterator<LocalDateTime> timestampSequence = randomLocalDateTimeStream(
            17L,
            LocalDateTime.of(2020, 1, 1, 0, 0),
            LocalDateTime.of(2023, 12, 31, 23, 59))
            .iterator();

    /******************************************************************************************************************/
    @Test
    public void test_populateDatabase()
      {
        entities.addAll(createTestEntities());
        runInTx(em -> entities.forEach(em::persist));
        // TODO: dump database, assert schema
      }

    /******************************************************************************************************************/
    @Test(dataProvider = "queryParameters", dependsOnMethods = "test_populateDatabase") @Transactional(NEVER)
    public void test_findManagedFiles (@Nonnull final Optional<String> fingerprint)
      {
        // given
        txManager.resetCounters();
        // when
        final var actualResult = underTest.findManagedFiles()
                                          .sort(by("path"), ASCENDING)
                                          .withFingerprint(fingerprint)
                                          .results();
        // then
        assertThat(txManager.getCommitCount(), is(1));

        final var expectedResult = entities.stream()
                                           .filter(mfe -> fingerprint.map(fp -> contains(mfe, fp)).orElse(true))
                                           .map(underTest::entityToModel)
                                           .toList();

        log.info("Asserting that lazy collection of fingerprints not fetched yet...");
        // TODO: this does not test ManagedFileEntity lazy field, only ManagedFile
        actualResult.stream()
                    .map(mf -> inspect(mf, "fingerprints"))
                    .map(lz -> inspect(lz, "ref"))
                    .forEach(ref -> assertThat(ref, is(nullValue())));

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
    @Nonnull
    private List<ManagedFileEntity> createTestEntities()
      {
        final var paths = new Random(4)
                .ints(0x2540, 0xffff)
                .limit(10)
                .mapToObj(i -> "/foo/bar/%x".formatted(i))
                .toList();
        final var counts = new Random(5).ints(0, 10).iterator();
        return paths.stream().map(_f(p -> createTestEntity(p, counts.next()))).toList();
      }

    /******************************************************************************************************************/
    @Nonnull
    private ManagedFileEntity createTestEntity (@Nonnull final String path, final int fingerprintCount)
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
    @DataProvider(name="queryParameters")
    private static Object[][] queryParameters()
      {
        return new Object[][]
          {
            { Optional.empty() },
            { Optional.of("missing") },
            { Optional.of("80fe035fe88f37471862c5ba5013b472") }
          };
      }
  }