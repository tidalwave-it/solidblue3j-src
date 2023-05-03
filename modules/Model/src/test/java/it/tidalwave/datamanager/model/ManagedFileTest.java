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
public class ManagedFileTest
  {
    private ManagedFile underTest;

    /******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        underTest = new TestModelFactory().createManagedFile(1);
      }

    /******************************************************************************************************************/
    @Test
    public void test_equals_and_hashCode()
      {
        EqualsVerifier.forClass(ManagedFile.class).withIgnoredFields("asDelegate").verify();
      }

    /******************************************************************************************************************/
    @Test
    public void test_toString_without_supplier_invoked()
      {
        // when
        final var actualResult = underTest.toString();
        // then
        assertThat(actualResult, is("ManagedFile(id=00000000-0000-0000-0000-000000000000, " +
                                    "path=/foo/bar/e047, " +
                                    "fingerprints=LazySupplier(<not initialised>))"));
      }

    /******************************************************************************************************************/
    @Test
    public void test_toString_with_supplier_invoked()
      {
        // when
        underTest.getFingerprints();
        final var actualResult = underTest.toString();
        // then
        assertThat(actualResult, is("ManagedFile(id=00000000-0000-0000-0000-000000000000, " +
                                    "path=/foo/bar/e047, " +
                                    "fingerprints=LazySupplier(" +
                                        "[Fingerprint(id=00000001-0000-0000-0000-000000000000, " +
                                        "name=2e10, " +
                                        "algorithm=md5, " +
                                        "fingerprint=bdca7cec8394ba8119ab66409f13dd16, " +
                                        "timestamp=2023-03-15T07:41:36)]))"));
      }
  }
