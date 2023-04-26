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
import java.util.ArrayList;
import java.util.List;
import it.tidalwave.util.As;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.ui.PresentationModel;
import it.tidalwave.datamanager.model.Fingerprint;
import it.tidalwave.datamanager.model.ManagedFile;
import lombok.Getter;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class MockDataManagerPresentation implements DataManagerPresentation
  {
    private static final As.Type<SimpleComposite<As>> _CompositeOfAs_ = As.type(SimpleComposite.class);

    @Getter
    public final List<Object> objects = new ArrayList<>();

    public final List<String> output = new ArrayList<>();

    public final List<String> error = new ArrayList<>();

    @Override
    public void renderManagedFiles (@Nonnull final PresentationModel pm)
      {
        pm.as(_CompositeOfAs_).findChildren().stream().forEach(o ->
          {
            objects.add(o.as(ManagedFile.class).getPath().toString());
            o.as(_CompositeOfAs_).findChildren().stream().map(c -> c.as(Fingerprint.class).getFingerprint()).forEach(objects::add);
          });
      }

    @Override
    public void output (@Nonnull final String string)
      {
        output.add(string);
      }

    @Override
    public void notifyError (@Nonnull final String string)
      {
        error.add(string);
      }

    @Nonnull
    public String outputToString()
      {
        return String.join("", output);
      }

    @Nonnull
    public String errorToString()
      {
        return String.join("", error);
      }
  }