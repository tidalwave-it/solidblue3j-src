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
import java.util.List;
import org.springframework.boot.DefaultApplicationArguments;
import it.tidalwave.datamanager.application.nogui.DataManagerPresentationControl;
import it.tidalwave.datamanager.application.nogui.DataManagerPresentationControl.ManagedFileOptions;
import it.tidalwave.datamanager.application.nogui.MockDataManagerPresentation;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static it.tidalwave.datamanager.application.nogui.DataManagerPresentationControl.ManagedFileOptions.*;
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

    private DataManagerPresentationControl presentationController;

    private MockDataManagerPresentation presentation;

    private UsageArgsInterpreter usageArgsInterpreter;

    /******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        presentationController = mock(DataManagerPresentationControl.class, CALLS_REAL_METHODS);
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
    @Test(dataProvider = "argsAndOptions")
    public void must_render_data (@Nonnull final List<String> args, @Nonnull final ManagedFileOptions.Builder expectedOptions)
      {
        // when
        underTest.run(new DefaultApplicationArguments(args.toArray(new String[0])));
        // then
        verify(presentationController).renderManagedFiles(any(ManagedFileOptions.Builder.class));
        verify(presentationController).renderManagedFiles(expectedOptions.build());
        verifyNoMoreInteractions(presentationController);
        verify(usageArgsInterpreter).disableUsage();
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
    @DataProvider
    private static Object[][] argsAndOptions()
      {
        return new Object[][]
          {
            { List.of("list-files"),                      withDefaultOptions()        },
            { List.of("list-files", "--max=1"),           with().max(1)               },
            { List.of("list-files", "--regex=.*2"),       with().regex(".*2")         },
            { List.of("list-files", "--fingerprints"),    with().renderFingerprints() },
            { List.of("list-files", "--fingerprint=fp"),  with().fingerprint("fp")    },
            { List.of("list-files", "--missing"),         with().missingFiles()       }
          };
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
