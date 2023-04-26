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
import jakarta.inject.Provider;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import it.tidalwave.datamanager.application.nogui.DataManagerPresentation;
import lombok.RequiredArgsConstructor;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

/***********************************************************************************************************************
 *
 * The command line args interpreter that prints usage for all commands.
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Component @Order(LOWEST_PRECEDENCE) @RequiredArgsConstructor
public class UsageArgsInterpreter implements ApplicationRunner
  {
    @Nonnull
    private final DataManagerPresentation presentation;

    @Nonnull
    private final Provider<List<UsageCapable>> usageCapables;

    private boolean printUsage = true;

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override
    public void run (@Nonnull final ApplicationArguments args)
      {
        if (printUsage || args.containsOption("help"))
          {
            if (args.getSourceArgs().length > 0)
              {
                presentation.notifyError("Syntax error: " + String.join(" ", args.getSourceArgs()));
              }

            usageCapables.get().forEach(UsageCapable::printUsage);
          }
      }

    /*******************************************************************************************************************
     *
     * Disables usage printing.
     *
     ******************************************************************************************************************/
    public void disableUsage()
      {
        printUsage = false;
      }
  }
