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
import java.util.Set;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import it.tidalwave.datamanager.application.nogui.DataManagerPresentation;
import lombok.RequiredArgsConstructor;
import static it.tidalwave.datamanager.application.nogui.args.ArgumentsUtils.findBadOptions;
import static java.util.stream.Collectors.*;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@RequiredArgsConstructor
public abstract class ArgsInterpreterSupport implements ApplicationRunner
  {
    @Nonnull
    private final String command;

    @Nonnull
    private final Set<String> validOptions;

    @Nonnull
    private final DataManagerPresentation presentation;

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override
    public final void run (@Nonnull final ApplicationArguments args)
      {
        if (args.getNonOptionArgs().contains(command))
          {
            final var bad = findBadOptions(args, validOptions);

            if (!bad.isEmpty())
              {
                presentation.notifyError("Invalid options: " + bad.stream().map(s -> "--" + s).collect(joining()));
              }
            else
              {
                doRun(args);
              }
          }
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    protected abstract void doRun (@Nonnull ApplicationArguments args);
  }
