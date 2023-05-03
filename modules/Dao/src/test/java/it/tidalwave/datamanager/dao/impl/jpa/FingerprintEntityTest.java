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
public class FingerprintEntityTest extends EntityTestSupport
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
    public void test_equals_and_hashcode()
      {
        // given
        final var underTest = tef.createFingerprintEntity("fileId");
        // then
        assertEqualityConsistency(FingerprintEntity.class, underTest);
      }

    /******************************************************************************************************************/
    @Test
    public void test_toString()
      {
        // given
        final var underTest = tef.createFingerprintEntity("fileId");
        // when
        final var actualResult = underTest.toString();
        // then
        final var expectedResult = "FingerprintEntity(id=00000000-0000-0000-0000-000000000000, " +
                                   "name=name, " +
                                   "algorithm=md5, " +
                                   "value=fingerprint, " +
                                   "timestamp=2023-03-15T07:41:36, " +
                                   "fileId=fileId)";
        assertThat(actualResult, is(expectedResult));
      }
  }
