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
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Tolerate;

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
    @Builder(builderClassName = "Builder") @ToString @EqualsAndHashCode
    public static class ManagedFileOptions
      {
        public static class Builder
          {
            @Nonnull @Tolerate
            public Builder renderFingerprints()
              {
                return renderFingerprints(true);
              }

            @Nonnull @Tolerate
            public Builder max (final int max)
              {
                return max(Optional.of(max));
              }

            @Nonnull @Tolerate
            public Builder regex (@Nonnull final String regex)
              {
                return regex(Optional.of(regex));
              }

            @Nonnull @Tolerate
            public Builder fingerprint (@Nonnull final String fingerprint)
              {
                return fingerprint(Optional.of(fingerprint));
              }

            @Nonnull @Tolerate
            public Builder missingFiles()
              {
                return missingFiles(true);
              }
          }

        /** Render fingerprints too. */
        public final boolean renderFingerprints;

        /** The maximum number of items to render. */
        @Nonnull @Default
        public final Optional<Integer> max = Optional.empty();

        /** A regex filter. */
        @Nonnull @Default
        public final Optional<String> regex = Optional.empty();

        /** Filter files having this fingerprint. */
        @Nonnull @Default
        public final Optional<String> fingerprint = Optional.empty();

        /** Filter output only to files no more present in the filesystem. */
        public final boolean missingFiles;

        // Syntactic sugar
        @Nonnull
        public static ManagedFileOptions.Builder with()
          {
            return builder();
          }

        @Nonnull
        public static ManagedFileOptions.Builder withDefaultOptions()
          {
            return builder();
          }
      }

    /*******************************************************************************************************************
     *
     * Render managed files.
     *
     ******************************************************************************************************************/
    public default void renderManagedFiles()
      {
        renderManagedFiles(ManagedFileOptions.withDefaultOptions());
      }

    /*******************************************************************************************************************
     *
     * Render managed files with the specified options.
     *
     * @param   options   the options
     *
     ******************************************************************************************************************/
    public default void renderManagedFiles (@Nonnull final ManagedFileOptions.Builder options)
      {
        renderManagedFiles(options.build());
      }

    /*******************************************************************************************************************
     *
     * Render managed files with the specified options.
     *
     * @param   options   the options
     *
     ******************************************************************************************************************/
    public void renderManagedFiles (@Nonnull ManagedFileOptions options);
  }
