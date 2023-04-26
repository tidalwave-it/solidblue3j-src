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

import java.time.LocalDateTime;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import it.tidalwave.util.spring.jpa.impl.DefaultFinderJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@EnableJpaRepositories(repositoryBaseClass = DefaultFinderJpaRepository.class)
@ActiveProfiles("test")
@Slf4j
public class FingerprintEntityTest extends EntityTestSupport
  {
    @Test @Rollback
    public void test_equals_and_hashcode()
      {
        final var underTest = new FingerprintEntity("id", "", "", "", LocalDateTime.now(), "");
        assertEqualityConsistency(FingerprintEntity.class, underTest);
      }
  }
