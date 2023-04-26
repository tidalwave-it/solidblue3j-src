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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import it.tidalwave.datamanager.application.nogui.DataManagerPresentation;
import it.tidalwave.datamanager.application.nogui.DataManagerPresentationController;
import lombok.RequiredArgsConstructor;
import static it.tidalwave.datamanager.application.nogui.args.ArgumentsUtils.*;
import static java.util.stream.Collectors.*;

/***********************************************************************************************************************
 *
 * The command line args interpreter for the {@code list-files} command.
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Component @Order(0) @RequiredArgsConstructor
public class ListManagedFilesArgsInterpreter implements ApplicationRunner, UsageCapable
  {
    private static final Set<String> VALID_OPTIONS = Set.of("fingerprints", "max", "regex", "missing");

    @Nonnull
    private final DataManagerPresentationController presentationController;

    @Nonnull
    private final DataManagerPresentation presentation;

    @Nonnull
    private final UsageArgsInterpreter usageArgsInterpreter;

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override
    public void run (@Nonnull final ApplicationArguments args)
      {
        if (args.getNonOptionArgs().contains("list-files"))
          {
            final var bad = findBadOptions(args, VALID_OPTIONS);

            if (!bad.isEmpty())
              {
                presentation.notifyError("Invalid options: " + bad.stream().map(s -> "--" + s).collect(joining()));
              }
            else
              {
                final var fingerprints = args.containsOption("fingerprints");
                final var max = getIntOption(args, "max");
                final var regex = getStringOption(args, "regex");
                final var missing = args.containsOption("missing");

                if ((regex.isPresent() || missing) && max.isPresent())
                  {
                    presentation.notifyError("--max cannot be used with --regex or --missing\n");
                  }
                else
                  {
                    usageArgsInterpreter.disableUsage();
                    presentationController.renderManagedFiles(fingerprints, max, regex, missing);
                  }
              }
          }
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override
    public void printUsage()
      {
        presentation.output("""
            Usage: solidblue3 list-files [--max=<n>] [--regex=<regex>] [--missing] [--fingerprints]
                              list files on the console
                              --max=<n>         the max number of files to list
                              --regex=<regex>   a filter for the files to list
                              --missing         only list files no more in the filesystem
                              --fingerprints    also render fingerprints
                              
                              --max cannot be used with any filter
            """);
      }
  }
