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
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.datamanager.application.nogui.DataManagerPresentationControl.BackupOptions.with;
import static it.tidalwave.datamanager.application.nogui.args.ArgumentsUtils.getStringOption;

/***********************************************************************************************************************
 *
 * The command line args interpreter for the {@code list-files} command.
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Component @Order(0) @Slf4j
public class ListBackupsArgsInterpreter extends ArgsInterpreterSupport implements UsageCapable
  {
    private static final String COMMAND = "list-backups";
    private static final String O_LABEL = "label";
    private static final String O_VOLUME_ID = "volume-id";
    private static final String O_FILE_ID = "file-id";
    private static final String O_FILES = "files";

    @Nonnull
    private final DataManagerPresentationControl presentationControl;

    @Nonnull
    private final DataManagerPresentation presentation;

    @Nonnull
    private final UsageArgsInterpreter usageArgsInterpreter;

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    public ListBackupsArgsInterpreter (@Nonnull final DataManagerPresentationControl presentationControl,
                                       @Nonnull final DataManagerPresentation presentation,
                                       @Nonnull final UsageArgsInterpreter usageArgsInterpreter)
      {
        super(COMMAND, Set.of(O_LABEL, O_VOLUME_ID, O_FILE_ID, O_FILES), presentation);
        this.presentationControl = presentationControl;
        this.presentation = presentation;
        this.usageArgsInterpreter = usageArgsInterpreter;
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override
    public void doRun (@Nonnull final ApplicationArguments args)
      {
        final var label = getStringOption(args, O_LABEL);
        final var volumeId = getStringOption(args, O_VOLUME_ID);
        final var renderFiles = args.containsOption(O_FILES);
        final var fileId = getStringOption(args, O_FILE_ID);
        usageArgsInterpreter.disableUsage();
        presentationControl.renderBackups(with().label(label)
                                                .volumeId(volumeId)
                                                .fileId(fileId)
                                                .renderFiles(renderFiles));
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override
    public void printUsage()
      {
        presentation.output("""
            solidblue3 %1$s [--%2$s=<label>] [--%3$s=<volume-id>] [--%4$s=<file-id>] [--%5$s]
                       list backups on the console
                       --%2$s=<label>         the label of the backup
                       --%3$s=<volume-id> the volume id of the backup
                       --%4$s=<file-id>     the id of a file in the backup
                       --%5$s                 also render files
            """.formatted(COMMAND, O_LABEL, O_VOLUME_ID, O_FILE_ID, O_FILES));
      }
  }
