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
package it.tidalwave.datamanager.application.nogui;

import jakarta.annotation.Nonnull;
import it.tidalwave.role.ui.PresentationModel;

/***********************************************************************************************************************
 *
 * The presentation for the application.
 *
 * @stereotype  Presentation
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface DataManagerPresentation
  {
    /*******************************************************************************************************************
     *
     * Render managed files.
     *
     * @param   managedFiles        the files
     *
     ******************************************************************************************************************/
    public void renderManagedFiles (@Nonnull PresentationModel managedFiles);

    /*******************************************************************************************************************
     *
     * Render backups.
     *
     * @param   backupsPm           the backups
     *
     ******************************************************************************************************************/
    public void renderBackups (@Nonnull PresentationModel backupsPm);

    /*******************************************************************************************************************
     *
     * Output a line to the console.
     *
     * @param   string    the pattern for the output
     *
     ******************************************************************************************************************/
    public void output (@Nonnull String string);

    /*******************************************************************************************************************
     *
     * Output an error line to the console.
     *
     * @param   string    the pattern for the output
     *
     ******************************************************************************************************************/
    public void notifyError (@Nonnull String string);
  }
