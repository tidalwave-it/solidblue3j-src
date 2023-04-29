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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import it.tidalwave.datamanager.application.nogui.DataManagerPresentation;
import it.tidalwave.datamanager.application.nogui.DataManagerPresentationControl;
import static it.tidalwave.datamanager.application.nogui.DataManagerPresentationControl.Options.with;
import static it.tidalwave.datamanager.application.nogui.args.ArgumentsUtils.*;

/***********************************************************************************************************************
 *
 * The command line args interpreter for the {@code list-files} command.
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Component @Order(0)
public class ListManagedFilesArgsInterpreter extends ArgsInterpreterSupport implements UsageCapable
  {
    private static final String COMMAND = "list-files";
    private static final String O_FINGERPRINTS = "fingerprints";
    private static final String O_MAX = "max";
    private static final String O_REGEX = "regex";
    private static final String O_FINGERPRINT = "fingerprint";
    private static final String O_MISSING = "missing";

    @Nonnull
    private final DataManagerPresentationControl presentationControl;

    @Nonnull
    private final DataManagerPresentation presentation;

    @Nonnull
    private final UsageArgsInterpreter usageArgsInterpreter;

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    public ListManagedFilesArgsInterpreter (@Nonnull final DataManagerPresentationControl presentationControl,
                                            @Nonnull final DataManagerPresentation presentation,
                                            @Nonnull final UsageArgsInterpreter usageArgsInterpreter)
      {
        super(COMMAND, Set.of(O_FINGERPRINTS, O_MAX, O_REGEX, O_MISSING, O_FINGERPRINT), presentation);
        this.presentationControl = presentationControl;
        this.presentation = presentation;
        this.usageArgsInterpreter = usageArgsInterpreter;
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override
    protected void doRun (@Nonnull final ApplicationArguments args)
      {
        final var fingerprints = args.containsOption(O_FINGERPRINTS);
        final var max = getIntOption(args, O_MAX);
        final var regex = getStringOption(args, O_REGEX);
        final var fingerprint = getStringOption(args, O_FINGERPRINT);
        final var missingFiles = args.containsOption(O_MISSING);

        if ((regex.isPresent() || missingFiles) && max.isPresent())
          {
            presentation.notifyError("--%s cannot be used with --%s or --%s".formatted(O_MAX, O_REGEX, O_MISSING));
          }
        else
          {
            usageArgsInterpreter.disableUsage();
            presentationControl.renderManagedFiles(with().renderFingerprints(fingerprints)
                                                         .max(max)
                                                         .regex(regex)
                                                         .fingerprint(fingerprint)
                                                         .missingFiles(missingFiles));
          }
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override
    public void printUsage()
      {
        presentation.output("""
            solidblue3 %1$s [--%2$s=<n>] [--%3$s=<regex>] [--%4$s=<value> [--%5$s] [--%6$s]
                       list files on the console
                       --%2$s=<n>               the max number of files to list
                       --%3$s=<regex>         a filter for the files to list
                       --%4$s=<value>   filter file(s) with that fingerprint
                       --%5$s               only list files no more in the filesystem
                       --%6$s          also render fingerprints
                      
                       --max cannot be used with --regex and --missing
            """.formatted("list-files", O_MAX, O_REGEX, O_FINGERPRINT, O_MISSING, O_FINGERPRINTS));
      }
  }
