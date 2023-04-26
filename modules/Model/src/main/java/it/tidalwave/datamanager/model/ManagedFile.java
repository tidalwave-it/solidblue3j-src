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
import java.util.List;
import java.util.function.Supplier;
import java.nio.file.Path;
import it.tidalwave.util.As;
import it.tidalwave.util.Id;
import it.tidalwave.util.LazySupplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Delegate;

/***********************************************************************************************************************
 *
 * A file managed by the system, for which fingerprints have been computed.
 *
 * @stereotype  Model
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@EqualsAndHashCode @ToString(doNotUseGetters = true)
public final class ManagedFile implements As
  {
    @Delegate @ToString.Exclude @EqualsAndHashCode.Exclude
    private final As asDelegate = As.forObject(this);

    public ManagedFile (@Nonnull final Id id,
                        @Nonnull final Path path,
                        @Nonnull final Supplier<List<Fingerprint>> fingerprints)
      {
        this.id = id;
        this.path = path;
        this.fingerprints = LazySupplier.of(fingerprints);
      }

    @Getter @Nonnull
    private final Id id;

    @Getter @Nonnull
    private final Path path;

    @Nonnull
    private final LazySupplier<List<Fingerprint>> fingerprints;

    @Nonnull
    public List<Fingerprint> getFingerprints()
      {
        return fingerprints.get();
      }
  }
