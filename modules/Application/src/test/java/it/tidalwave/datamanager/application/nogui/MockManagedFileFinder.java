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
import java.util.List;
import java.io.Serial;
import it.tidalwave.util.Pair;
import it.tidalwave.util.spi.HierarchicFinderSupport;
import it.tidalwave.datamanager.model.DataManager;
import it.tidalwave.datamanager.model.ManagedFile;
import lombok.AllArgsConstructor;

/***********************************************************************************************************************
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@AllArgsConstructor
public class MockManagedFileFinder
        extends HierarchicFinderSupport<ManagedFile, MockManagedFileFinder>
        implements DataManager.ManagedFileFinder
  {
    @Serial private static final long serialVersionUID = 0L;

    @Nonnull
    private final List<ManagedFile> result;

    @Nonnull
    private final List<Pair<SortCriterion, SortDirection>> sorters;

    public MockManagedFileFinder (@Nonnull final MockManagedFileFinder other, @Nonnull final Object override)
      {
        super(other, override);
        final var source = getSource(MockManagedFileFinder.class, other, override);
        this.result = source.result;
        this.sorters = source.sorters;
      }

    @Override @Nonnull
    public MockManagedFileFinder sort (@Nonnull final SortCriterion criterion, @Nonnull final SortDirection direction)
      {
        // just accumulate sorters for assertions
        sorters.add(Pair.of(criterion, direction));
        return clonedWith(new MockManagedFileFinder(result, sorters));
      }

    @Override @Nonnull
    protected List<ManagedFile> computeResults()
      {
        return result;
      }
  }
