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
public class BackupFileEntityTest extends EntityTestSupport
  {
    private TestEntityFactory tef;
    private ManagedFileEntity managedFile;

    /******************************************************************************************************************/
    @BeforeMethod
    public void prepare()
      {
        tef = new TestEntityFactory();
        managedFile = tef.createManagedFileEntity("/foo/bar", 0);
      }

    /******************************************************************************************************************/
    @Test @Rollback
    public void test_equals_and_hashCode()
      {
        // given
        final var underTest = tef.createBackupFileEntity(managedFile);
        runInTx(em -> em.persist(managedFile));
        runInTx(em -> em.persist(underTest.getBackup()));
        // then
        assertEqualityConsistency(BackupFileEntity.class, underTest);
      }

    /******************************************************************************************************************/
    @Test @Rollback
    public void test_toString_without_proxy()
      {
        // given
        final var underTest = tef.createBackupFileEntity(managedFile);
        // when
        final var actualResult = underTest.toString();
        // then
        final var expectedResult = ("BackupFileEntity@%x(id=00000003-0000-0000-0000-000000000000, " +
                                    "backupId=00000001-0000-0000-0000-000000000000, " +
                                    "path=path, " +
                                    "file=ManagedFileEntity@%x(id=00000000-0000-0000-0000-000000000000, " +
                                    "path=/foo/bar, " +
                                    "fingerprints=[]))")
                .formatted(System.identityHashCode(underTest), System.identityHashCode(managedFile));
        assertThat(actualResult, is(expectedResult));
      }
  }
