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
package it.tidalwave.datamanager.application.nogui.impl;

import jakarta.annotation.Nonnull;
import it.tidalwave.role.ui.Displayable;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.datamanager.model.Fingerprint;
import lombok.AllArgsConstructor;

/***********************************************************************************************************************
 *
 * An implementation of {@link Displayable} for {@link Fingerprint}.
 *
 * @stereotype  Role
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@DciRole(datumType = Fingerprint.class) @AllArgsConstructor
public class FingerprintDisplayable implements Displayable
  {
    @Nonnull
    private final Fingerprint owner;

    @Override @Nonnull
    public String getDisplayName()
      {
        return "%s %s:%s".formatted(owner.getTimestamp(), owner.getAlgorithm(), owner.getFingerprint());
      }
  }
