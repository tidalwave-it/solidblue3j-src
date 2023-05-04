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

import java.util.List;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@EnableJpaRepositories
@ActiveProfiles("test")
@Slf4j
public class BackupEntityTest extends EntityTestSupport
  {
    private TestEntityFactory tef;

    /******************************************************************************************************************/
    @BeforeMethod
    public void prepare()
      {
        tef = new TestEntityFactory();
      }

    /******************************************************************************************************************/
    @Test @Rollback
    public void test_equals_and_hashCode()
      {
        // given
        final var underTest = tef.createBackupEntity(1, List.of(), 0);
        // then
        assertEqualityConsistency(BackupEntity.class, underTest);
      }

    /******************************************************************************************************************/
    @Test
    public void test_toString_without_proxy()
      {
        // given
        final var underTest = tef.createBackupEntity(1, List.of(), 0);
        // when
        final var actualResult = underTest.toString();
        // then
        final var expectedResult = ("BackupEntity@%x(id=00000000-0000-0000-0000-000000000000, " +
                                    "label=Label #1, " +
                                    "volumeId=00000001-0000-0000-0000-000000000000, " +
                                    "encrypted=false, " +
                                    "basePath=basePath 1, " +
                                    "creationDate=2023-03-15T07:41:36, " +
                                    "registrationDate=2020-12-11T03:09:52, " +
                                    "latestCheckDate=2022-10-01T14:34:25, " +
                                    "backupFiles=[])").formatted(System.identityHashCode(underTest));
        assertThat(actualResult, is(expectedResult));
      }

    /******************************************************************************************************************/
    @Test @Rollback
    public void test_toString_with_proxy()
      {
        // given
        final var entity = tef.createBackupEntity(1, List.of(), 0);
        runInTx(em -> em.persist(entity));
        final var underTest =  runInTxWithResult(em -> em.find(BackupEntity.class, entity.getId()));
        // when
        final var actualResult = underTest.toString();
        // then
        final var expectedResult = ("BackupEntity@%x(id=00000000-0000-0000-0000-000000000000, " +
                                    "label=Label #1, " +
                                    "volumeId=00000001-0000-0000-0000-000000000000, " +
                                    "encrypted=false, " +
                                    "basePath=basePath 1, " +
                                    "creationDate=2023-03-15T07:41:36, " +
                                    "registrationDate=2020-12-11T03:09:52, " +
                                    "latestCheckDate=2022-10-01T14:34:25, " +
                                    "backupFiles=not initialized)").formatted(System.identityHashCode(underTest));
        runInTx(em -> em.remove(em.merge(entity))); // clean up
        assertThat(actualResult, is(expectedResult));
      }
  }
