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
import java.util.function.Consumer;
import org.springframework.stereotype.Component;
import it.tidalwave.util.As;
import it.tidalwave.util.Pair;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.datamanager.application.nogui.DataManagerPresentation;
import lombok.AllArgsConstructor;
import static it.tidalwave.role.ui.Displayable._Displayable_;

/***********************************************************************************************************************
 *
 * An implementation of {@link DataManagerPresentation} that goes on the terminal.
 *
 * @stereotype  Presentation
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Component @AllArgsConstructor
public class TerminalDataManagerPresentation implements DataManagerPresentation
  {
    private static final As.Type<SimpleComposite<As>> _CompositeOfAs_ = As.type(SimpleComposite.class);

    private static final As.Type<SimpleComposite<PresentationModel>> _CompositeOfPresentationModel_
            = As.type(SimpleComposite.class);

    private final Consumer<String> printer;

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    public TerminalDataManagerPresentation()
      {
        printer = System.out::println;
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override
    public void renderManagedFiles (@Nonnull final PresentationModel pm)
      {
        final var s = pm.as(_CompositeOfPresentationModel_).findChildren().stream();
        Pair.indexedPairStream(s, Pair.BASE_1).forEach(this::renderManagedFile);
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override
    public void output (@Nonnull final String string)
      {
        printer.accept(string);
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override
    public void notifyError (@Nonnull final String string)
      {
        System.err.println(string);
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private void renderManagedFile (@Nonnull final Pair<Integer, ? extends PresentationModel> pair)
      {
        final var managedFilePm = pair.b;
        printer.accept("%05d) %s".formatted(pair.a, managedFilePm.as(_Displayable_).getDisplayName()));
        managedFilePm.as(_CompositeOfAs_)
                     .findChildren()
                     .stream()
                     .forEach(f -> printer.accept("    " + f.as(_Displayable_).getDisplayName()));
      }
  }
