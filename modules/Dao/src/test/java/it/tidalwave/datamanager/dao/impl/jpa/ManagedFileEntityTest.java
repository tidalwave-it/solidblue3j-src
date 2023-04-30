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
import org.springframework.test.context.ActiveProfiles;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;
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
public class ManagedFileEntityTest extends EntityTestSupport
  {
    /******************************************************************************************************************/
    @Test
    public void test_equals_and_hashCode()
      {
        // given
        final var underTest = new ManagedFileEntity("id", "path", List.of());
        // then
        assertEqualityConsistency(ManagedFileEntity.class, underTest);
      }

    /******************************************************************************************************************/
    @Test @Transactional(SUPPORTS)
    public void test_toString_without_proxy()
      {
        // given
        final var underTest = new ManagedFileEntity("id", "path", List.of());
        // when
        final var actualResult = underTest.toString();
        // then
        final var expectedResult =
            "ManagedFileEntity@%x(id=id, path=path, fingerprints=[])".formatted(System.identityHashCode(underTest));
        assertThat(actualResult, is(expectedResult));
      }

    /******************************************************************************************************************/
    @Test
    public void test_toString_with_proxy()
      {
        // given
        runInTx(em -> em.persist(new ManagedFileEntity("id2", "path", List.of())));
        final var underTest =  runInTxWithResult(em -> em.find(ManagedFileEntity.class, "id2"));
        // when
        final var actualResult = underTest.toString();
        // then
        final var expectedResult = "ManagedFileEntity@%x(id=id2, path=path, fingerprints=not initialized)"
            .formatted(System.identityHashCode(underTest));
        assertThat(actualResult, is(expectedResult));
      }
  }
