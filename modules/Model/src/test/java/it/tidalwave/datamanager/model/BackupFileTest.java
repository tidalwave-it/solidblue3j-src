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

import nl.jqno.equalsverifier.EqualsVerifier;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class BackupFileTest
  {
    private BackupFile underTest;

    private ManagedFile managedFile;

    private TestModelFactory tmf;

    /******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        tmf = new TestModelFactory();
        managedFile = tmf.createManagedFile(1);
        underTest = tmf.createBackup(managedFile).getBackupFiles().get(0);
      }

    /******************************************************************************************************************/
    @Test
    public void test_equals_and_hashCode()
      {
        // This looks weird, but it's required by EqualsVerifier
        // See https://jqno.nl/equalsverifier/errormessages/recursive-datastructure/
        final var red = tmf.createBackup(tmf.createManagedFile(0));
        final var blue = tmf.createBackup(tmf.createManagedFile(0));
        EqualsVerifier.forClass(BackupFile.class)
                      .withIgnoredFields("asDelegate")
                      .withIgnoredFields("backup")
                      .withPrefabValues(Backup.class, red, blue)
                      .verify();
      }

    /******************************************************************************************************************/
    @Test
    public void test_toString()
      {
        // when
        final var actualResult = underTest.toString();
        // then
        assertThat(actualResult, is("BackupFile(id=00000003-0000-0000-0000-000000000000, " +
                                    "path=/backup/e10/foo/bar/e047, " +
                                    "managedFile=" +
                                    "ManagedFile(id=00000000-0000-0000-0000-000000000000, " +
                                        "path=/foo/bar/e047, " +
                                        "fingerprints=LazySupplier(<not initialised>)))"));
      }
  }
