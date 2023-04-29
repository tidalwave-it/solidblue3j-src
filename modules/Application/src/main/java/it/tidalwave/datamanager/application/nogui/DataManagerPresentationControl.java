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
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.With;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * The controller for {@link DataManagerPresentation}.
 *
 * @stereotype  Presentation Control
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface DataManagerPresentationControl
  {
    @AllArgsConstructor @With(PRIVATE) @ToString @EqualsAndHashCode
    public static class Options
      {
        /** Render fingerprints too. */
        public final boolean renderFingerprints;

        /** The maximum number of items to render. */
        @Nonnull
        public final Optional<Integer> max;

        /** A regex filter. */
        @Nonnull
        public final Optional<String> regex;

        /** Filter files having this fingerprint. */
        @Nonnull
        public final Optional<String> fingerprint;

        /** Filter output only to files no more present in the filesystem. */
        public final boolean missingFiles;

        @Nonnull
        public Options renderFingerprints()
          {
            return withRenderFingerprints(true);
          }

        @Nonnull
        public Options renderFingerprints (final boolean renderFingerprints)
          {
            return withRenderFingerprints(renderFingerprints);
          }

        @Nonnull
        public Options max (@Nonnull final Optional<Integer> max)
          {
            return withMax(max);
          }

        @Nonnull
        public Options max (final int max)
          {
            return withMax(Optional.of(max));
          }

        @Nonnull
        public Options regex (@Nonnull final Optional<String> regex)
          {
            return withRegex(regex);
          }

        @Nonnull
        public Options regex (@Nonnull final String regex)
          {
            return withRegex(Optional.of(regex));
          }

        @Nonnull
        public Options fingerprint (@Nonnull final Optional<String> fingerprint)
          {
            return withFingerprint(fingerprint);
          }

        @Nonnull
        public Options fingerprint (@Nonnull final String fingerprint)
          {
            return withFingerprint(Optional.of(fingerprint));
          }

        @Nonnull
        public Options missingFiles()
          {
            return withMissingFiles(true);
          }

        @Nonnull
        public Options missingFiles (final boolean missingFiles)
          {
            return withMissingFiles(missingFiles);
          }
      }

    @Nonnull
    public static Options with()
      {
        return new Options(false, Optional.empty(), Optional.empty(), Optional.empty(), false);
      }

    // Syntactic sugar
    @Nonnull
    public static Options withDefaultOptions()
      {
        return with();
      }

    /*******************************************************************************************************************
     *
     * Render managed files.
     *
     ******************************************************************************************************************/
    public default void renderManagedFiles()
      {
        renderManagedFiles(withDefaultOptions());
      }

    /*******************************************************************************************************************
     *
     * Render managed files with the specified options.
     *
     * @param   options   the options
     *
     ******************************************************************************************************************/
    public void renderManagedFiles (@Nonnull Options options);
  }
