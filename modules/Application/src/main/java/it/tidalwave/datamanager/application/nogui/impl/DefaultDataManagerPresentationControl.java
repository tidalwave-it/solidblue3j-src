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
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.nio.file.Files;
import org.springframework.stereotype.Component;
import it.tidalwave.util.RoleFactory;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.datamanager.model.DataManager;
import it.tidalwave.datamanager.model.ManagedFile;
import it.tidalwave.datamanager.application.nogui.DataManagerPresentation;
import it.tidalwave.datamanager.application.nogui.DataManagerPresentationController;
import lombok.RequiredArgsConstructor;
import static it.tidalwave.util.Finder.SortDirection.ASCENDING;
import static it.tidalwave.util.spring.jpa.JpaRepositoryFinder.by;
import static it.tidalwave.role.ui.spi.PresentationModelCollectors.toCompositePresentationModel;

/***********************************************************************************************************************
 *
 * The default implementation of {@link DataManagerPresentationController}.
 *
 * @stereotype  Presentation Control
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Component @RequiredArgsConstructor
public class DefaultDataManagerPresentationControl implements DataManagerPresentationController
  {
    @Nonnull
    private final DataManager dataManager;

    @Nonnull
    private final DataManagerPresentation presentation;

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override
    public void renderManagedFiles (final boolean renderFingerprints,
                                    @Nonnull final Optional<Integer> max,
                                    @Nonnull final Optional<String> regex,
                                    final boolean missing)
      {
        final Predicate<ManagedFile> filter1 = __ -> true;
        final var filter2 = regex.map(r -> filter1.and(mf -> mf.getPath().toString().matches(r))).orElse(filter1);
        final var filter3 = !missing ? filter2 : filter2.and(mf -> !Files.exists(mf.getPath()));

        final RoleFactory<ManagedFile> rf =
                o -> SimpleComposite.ofCloned(renderFingerprints ? o.getFingerprints() : List.of());

        final var pm = dataManager.findManagedFiles()
                                  .sort(by("path"), ASCENDING)
                                  .max(max)
                                  .stream()
                                  .filter(filter3)
                                  .map(m -> PresentationModel.of(m, rf))
                                  // .map(m -> m.as(_Presentable_).createPresentationModel(rf)) TODO breaks test
                                  .collect(toCompositePresentationModel());
        presentation.renderManagedFiles(pm);
      }
  }