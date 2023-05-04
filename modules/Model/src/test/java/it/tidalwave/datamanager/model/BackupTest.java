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
package it.tidalwave.datamanager.model;

import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import it.tidalwave.util.LazySupplier;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class BackupTest
  {
    private Backup underTest;

    /******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        final var tmf = new TestModelFactory();
        final var managedFile = tmf.createManagedFile(0);
        underTest = tmf.createBackup(managedFile);
      }

    /******************************************************************************************************************/
    @Test
    public void test_equals_and_hashCode()
      {
        // This looks weird, but it's required by EqualsVerifier
        // See https://jqno.nl/equalsverifier/errormessages/recursive-datastructure/
        final var red = LazySupplier.of(() -> List.of("red"));
        final var blue = LazySupplier.of(() -> List.of("blue"));
        red.get();
        blue.get();
        EqualsVerifier.forClass(Backup.class)
                      .withIgnoredFields("asDelegate")
                      .withPrefabValues(LazySupplier.class, red, blue)
                      .verify();
      }

    /******************************************************************************************************************/
    @Test
    public void test_toString_without_supplier_invoked()
      {
        // when
        final var actualResult = underTest.toString();
        // then
        assertThat(actualResult, is("Backup(id=00000001-0000-0000-0000-000000000000, " +
                                    "label=Label #0, " +
                                    "volumeId=00000002-0000-0000-0000-000000000000, " +
                                    "encrypted=true, " +
                                    "basePath=/foo/bar, " +
                                    "creationDate=2023-03-15T07:41:36, " +
                                    "registrationDate=2020-12-11T03:09:52, " +
                                    "latestCheckDate=Optional[2022-10-01T14:34:25], " +
                                    "backupFiles=LazySupplier(<not initialised>))"));
      }

    /******************************************************************************************************************/
    @Test
    public void test_toString_with_supplier_invoked()
      {
        // when
        underTest.getBackupFiles();
        final var actualResult = underTest.toString();
        // then
        assertThat(actualResult, is("Backup(id=00000001-0000-0000-0000-000000000000, " +
                                    "label=Label #0, " +
                                    "volumeId=00000002-0000-0000-0000-000000000000, " +
                                    "encrypted=true, " +
                                    "basePath=/foo/bar, " +
                                    "creationDate=2023-03-15T07:41:36, " +
                                    "registrationDate=2020-12-11T03:09:52, " +
                                    "latestCheckDate=Optional[2022-10-01T14:34:25], " +
                                    "backupFiles=LazySupplier(" +
                                        "[BackupFile(id=00000003-0000-0000-0000-000000000000, " +
                                        "path=/backup/e10/foo/bar/e047, " +
                                        "managedFile=ManagedFile(id=00000000-0000-0000-0000-000000000000, " +
                                            "path=/foo/bar/e047, " +
                                            "fingerprints=LazySupplier(<not initialised>)))]))"));
      }
  }
