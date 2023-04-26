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
package it.tidalwave.datamanager.application.nogui.args;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import org.springframework.boot.DefaultApplicationArguments;
import it.tidalwave.datamanager.application.nogui.DataManagerPresentationController;
import it.tidalwave.datamanager.application.nogui.MockDataManagerPresentation;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class ListManagedFilesArgsInterpreterTest
  {
    private ListManagedFilesArgsInterpreter underTest;

    private DataManagerPresentationController presentationController;

    private MockDataManagerPresentation presentation;

    private UsageArgsInterpreter usageArgsInterpreter;

    /******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        presentationController = mock(DataManagerPresentationController.class);
        presentation = new MockDataManagerPresentation();
        usageArgsInterpreter = mock(UsageArgsInterpreter.class);
        underTest = new ListManagedFilesArgsInterpreter(presentationController, presentation, usageArgsInterpreter);
      }

    /******************************************************************************************************************/
    @Test
    public void must_do_nothing_when_no_args()
      {
        // when
        underTest.run(new DefaultApplicationArguments());
        // then
        assertThat(presentation.outputToString(), is(""));
        verifyNoInteractions(presentationController);
        verifyNoInteractions(usageArgsInterpreter);
      }

    /******************************************************************************************************************/
    @Test(dataProvider = "errorArgs")
    public void must_emit_error_when_both_max_and_filter (@Nonnull final String[] args)
      {
        // when
        underTest.run(new DefaultApplicationArguments(args));
        // then
        assertThat(presentation.errorToString(), is("--max cannot be used with --regex or --missing"));
        verifyNoInteractions(presentationController);
        verifyNoInteractions(usageArgsInterpreter);
      }

    /******************************************************************************************************************/
    @Test
    public void must_emit_error_when_invalid_args()
      {
        // when
        underTest.run(new DefaultApplicationArguments("list-files", "--foobar"));
        // then
        assertThat(presentation.errorToString(), is("Invalid options: --foobar"));
        verifyNoInteractions(presentationController);
        verifyNoInteractions(usageArgsInterpreter);
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_data()
      {
        // when
        underTest.run(new DefaultApplicationArguments("list-files"));
        // then
        verify(presentationController).renderManagedFiles(false, Optional.empty(), Optional.empty(), false);
        verifyNoMoreInteractions(presentationController);
        verify(usageArgsInterpreter).disableUsage();
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_data_with_max()
      {
        // when
        underTest.run(new DefaultApplicationArguments("list-files", "--max=1"));
        // then
        verify(presentationController).renderManagedFiles(false, Optional.of(1), Optional.empty(), false);
        verifyNoMoreInteractions(presentationController);
        verify(usageArgsInterpreter).disableUsage();
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_data_with_regex()
      {
        // when
        underTest.run(new DefaultApplicationArguments("list-files", "--regex=.*2"));
        // then
        verify(presentationController).renderManagedFiles(false, Optional.empty(), Optional.of(".*2"), false);
        verifyNoMoreInteractions(presentationController);
        verify(usageArgsInterpreter).disableUsage();
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_data_with_fingerprints()
      {
        // when
        underTest.run(new DefaultApplicationArguments("list-files", "--fingerprints"));
        // then
        verify(presentationController).renderManagedFiles(true, Optional.empty(), Optional.empty(), false);
        verifyNoMoreInteractions(presentationController);
        verify(usageArgsInterpreter).disableUsage();
      }

    /******************************************************************************************************************/
    @Test
    public void must_render_data_with_missing()
      {
        // when
        underTest.run(new DefaultApplicationArguments("list-files", "--missing"));
        // then
        verify(presentationController).renderManagedFiles(false, Optional.empty(), Optional.empty(), true);
        verifyNoMoreInteractions(presentationController);
        verify(usageArgsInterpreter).disableUsage();
      }

    /******************************************************************************************************************/
    @DataProvider
    private static Object[][] errorArgs()
      {
        return new Object[][]
          {
            {"list-files", "--max=10", "--missing"},
            {"list-files", "--max=10", "--regex=foobar"}
          };
      }
  }
