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
package it.tidalwave.datamanager.application.nogui.impl;

import java.time.LocalDateTime;
import it.tidalwave.util.Id;
import it.tidalwave.datamanager.model.Fingerprint;
import org.testng.annotations.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class FingerprintDisplayableTest
  {
    @Test
    public void testDisplayable()
      {
        // given
        final var fingerprint = Fingerprint.builder().id(Id.of("id"))
                .name("name")
                .algorithm("md5")
                .timestamp(LocalDateTime.of(2023, 4, 27, 14,35, 4))
                .fingerprint("6f38a63e8e9159057a5262cdff0116fe")
                .build();
        final var underTest = new FingerprintDisplayable(fingerprint);
        // when
        final var actualResult = underTest.getDisplayName();
        // then
        assertThat(actualResult, is("2023-04-27T14:35:04 md5:6f38a63e8e9159057a5262cdff0116fe"));
      }
  }
