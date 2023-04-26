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
import java.time.LocalDateTime;
import it.tidalwave.util.As;
import it.tidalwave.util.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Delegate;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * A fingerprint computed for a {@link ManagedFile}.
 *
 * @stereotype  Model
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Builder @Getter @EqualsAndHashCode @ToString @AllArgsConstructor(access = PRIVATE)
public final class Fingerprint implements As
  {
    @Delegate @ToString.Exclude @EqualsAndHashCode.Exclude
    private final As asDelegate = As.forObject(this);

    @Nonnull
    private final Id id;

    @Nonnull
    private final String name;

    @Nonnull
    private final String algorithm;

    @Nonnull
    private final String fingerprint;

    @Nonnull
    private final LocalDateTime timestamp;
  }
