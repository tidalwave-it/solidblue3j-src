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
import java.util.Objects;
import it.tidalwave.role.ui.Displayable;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.datamanager.model.Backup;
import lombok.AllArgsConstructor;

/***********************************************************************************************************************
 *
 * An implementation of {@link Displayable} for {@link Fingerprint}.
 *
 * @stereotype Role
 * @author Fabrizio Giudici
 *
 **********************************************************************************************************************/
@DciRole(datumType = Backup.class) @AllArgsConstructor
public class BackupDisplayable implements Displayable
  {
    @Nonnull
    private final Backup owner;

    @Override @Nonnull
    public String getDisplayName()
      {
        return ("""
                label:       %s
                volume id:   %s
                encrypted:   %s
                created:     %s
                registered:  %s
                checked:     %s
                base path:   %s""").formatted(owner.getLabel(),
                                              owner.getVolumeId(),
                                              owner.isEncrypted(),
                                              owner.getCreationDate(),
                                              owner.getRegistrationDate(),
                                              owner.getLatestCheckDate().map(Objects::toString).orElse("never"),
                                              owner.getBasePath());
      }
  }
