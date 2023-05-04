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
import it.tidalwave.datamanager.application.nogui.MockDataManagerPresentation;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static it.tidalwave.datamanager.application.nogui.DataManagerPresentationControl.BackupOptions.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class ListBackupsArgsInterpreterTest
  {
    private ListBackupsArgsInterpreter underTest;

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
        underTest = new ListBackupsArgsInterpreter(presentationController, presentation, usageArgsInterpreter);
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
    public void must_render_data (@Nonnull final List<String> args, @Nonnull final Builder expectedOptions)
      {
        // when
        underTest.run(new DefaultApplicationArguments(args.toArray(new String[0])));
        // then
        verify(presentationController).renderBackups(any(Builder.class));
        verify(presentationController).renderBackups(expectedOptions.build());
        verifyNoMoreInteractions(presentationController);
        verify(usageArgsInterpreter).disableUsage();
      }

    /******************************************************************************************************************/
    @Test
    public void must_emit_error_when_invalid_args()
      {
        // when
        underTest.run(new DefaultApplicationArguments("list-backups", "--foobar"));
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
            { List.of("list-backups"),                     withDefaultOptions()   },
            { List.of("list-backups", "--label=foo"),      with().label("foo")    },
            { List.of("list-backups", "--file-id=id"),     with().fileId("id")    },
            { List.of("list-backups", "--volume-id=bar"),  with().volumeId("bar") }
          };
      }
  }
