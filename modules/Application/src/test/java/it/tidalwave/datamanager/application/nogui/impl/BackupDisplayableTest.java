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

import it.tidalwave.datamanager.model.TestModelFactory;
import org.testng.annotations.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class BackupDisplayableTest
  {
    @Test
    public void testDisplayable()
      {
        // given
        final var tmf = new TestModelFactory();
        final var managedFile = tmf.createManagedFile(0);
        final var backup = tmf.createBackup(managedFile);
        final var underTest = new BackupDisplayable(backup);
        // when
        final var actualResult = underTest.getDisplayName();
        // then
        assertThat(actualResult, is("""
                  label:       Label #0
                  volume id:   00000002-0000-0000-0000-000000000000
                  encrypted:   true
                  created:     2023-03-15T07:41:36
                  registered:  2020-12-11T03:09:52
                  checked:     2022-10-01T14:34:25
                  base path:   /foo/bar"""));
      }
  }
