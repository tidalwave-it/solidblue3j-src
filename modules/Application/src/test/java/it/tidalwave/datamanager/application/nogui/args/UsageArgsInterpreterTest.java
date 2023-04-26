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

import java.util.List;
import org.springframework.boot.DefaultApplicationArguments;
import it.tidalwave.datamanager.application.nogui.DataManagerPresentation;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;

/***********************************************************************************************************************
 *
 * The command line args interpreter that prints usage for all commands.
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class UsageArgsInterpreterTest
  {
    private UsageArgsInterpreter underTest;

    private DataManagerPresentation presentation;

    private UsageCapable usageCapable;

    /******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        usageCapable = mock(UsageCapable.class);
        presentation = spy(DataManagerPresentation.class);
        underTest = new UsageArgsInterpreter(presentation, () -> List.of(usageCapable));
      }

    /******************************************************************************************************************/
    @Test
    public void must_do_nothing_when_a_command_ran()
      {
        // given
        underTest.disableUsage();
        // when
        underTest.run(new DefaultApplicationArguments());
        // then
        verifyNoInteractions(usageCapable);
      }

    /******************************************************************************************************************/
    @Test
    public void must_print_usage_when_no_command_ran()
      {
        // when
        underTest.run(new DefaultApplicationArguments());
        // then
        verify(usageCapable).printUsage();
      }

    /******************************************************************************************************************/
    @Test
    public void must_print_usage_and_emit_error_when_no_command_ran_and_args()
      {
        // when
        underTest.run(new DefaultApplicationArguments("foo", "--bar"));
        // then
        verify(presentation).notifyError("Syntax error: foo --bar");
        verify(usageCapable).printUsage();
      }

    /******************************************************************************************************************/
    @Test
    public void must_print_usage_when_help_arg()
      {
        // when
        underTest.run(new DefaultApplicationArguments("--help"));
        // then
        verify(usageCapable).printUsage();
      }
  }
