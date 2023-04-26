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
import java.util.Collection;
import it.tidalwave.role.ui.Presentable;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.dci.annotation.DciRole;
import it.tidalwave.datamanager.model.ManagedFile;
import lombok.AllArgsConstructor;

/***********************************************************************************************************************
 *
 * An implementation of {@link Presentable} for {@link ManagedFile}.
 *
 * @stereotype  Role
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@DciRole(datumType = ManagedFile.class) @AllArgsConstructor
public class ManagedFilePresentable implements Presentable
  {
    @Nonnull
    private final ManagedFile owner;

    @Override @Nonnull
    public PresentationModel createPresentationModel (@Nonnull final Collection<Object> instanceRoles)
      {
        return PresentationModel.of(owner, instanceRoles);
      }
  }
