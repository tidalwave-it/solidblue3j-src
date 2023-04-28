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
package it.tidalwave.datamanager.model;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import it.tidalwave.util.spi.ExtendedFinderSupport;

/***********************************************************************************************************************
 *
 * The business controller for the application.
 *
 * @stereotype  Business Controller
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface DataManager
  {
    /*******************************************************************************************************************
     * A {@link it.tidalwave.util.Finder} for querying {@link ManagedFile}s.
     ******************************************************************************************************************/
    public static interface ManagedFileFinder extends ExtendedFinderSupport<ManagedFile, ManagedFileFinder>
      {
        /***************************************************************************************************************
         * Specifies that returned {@link ManagedFile}s must contain the given fingerprint.
         * @param     fingerprint   the fingerprint
         * @return                  the same finder in fluent style
         **************************************************************************************************************/
        @Nonnull
        public ManagedFileFinder withFingerprint (@Nonnull final Optional<String> fingerprint);

        /***************************************************************************************************************
         * Specifies that returned {@link ManagedFile}s must contain the given fingerprint.
         * @param     fingerprint   the fingerprint
         * @return                  the same finder in fluent style
         **************************************************************************************************************/
        @Nonnull
        public default ManagedFileFinder withFingerprint (@Nonnull final String fingerprint)
        {
          return withFingerprint(Optional.of(fingerprint));
        }
      }

    /*******************************************************************************************************************
     *
     * Queries the managed files.
     *
     * @return    a {@link it.tidalwave.util.Finder} for {@link it.tidalwave.datamanager.model.ManagedFile}s
     *
     ******************************************************************************************************************/
    @Nonnull
    public ManagedFileFinder findManagedFiles();
  }
