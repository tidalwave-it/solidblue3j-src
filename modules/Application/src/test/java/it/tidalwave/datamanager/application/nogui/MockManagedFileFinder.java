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
import java.util.Optional;
import java.io.Serial;
import it.tidalwave.util.Pair;
import it.tidalwave.util.spi.HierarchicFinderSupport;
import it.tidalwave.datamanager.model.DataManager;
import it.tidalwave.datamanager.model.ManagedFile;
import lombok.AllArgsConstructor;
import static it.tidalwave.util.CollectionUtils.concat;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * A mock for {@link it.tidalwave.datamanager.model.DataManager.ManagedFileFinder}.
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@AllArgsConstructor(access = PRIVATE)
public class MockManagedFileFinder
        extends HierarchicFinderSupport<ManagedFile, MockManagedFileFinder>
        implements DataManager.ManagedFileFinder
  {
    @Serial private static final long serialVersionUID = 0L;

    private final Holder<MockManagedFileFinder> holder;

    @Nonnull
    private final List<ManagedFile> result;

    @Nonnull
    public final List<Pair<SortCriterion, SortDirection>> sorters;

    @Nonnull
    public final Optional<String> fingerprint;

    public MockManagedFileFinder (@Nonnull final Holder<MockManagedFileFinder> holder,
                                  @Nonnull final List<ManagedFile> result)
      {
        this.holder = holder;
        this.result = result;
        this.sorters = new ArrayList<>();
        this.fingerprint = Optional.empty();
      }

    public MockManagedFileFinder (@Nonnull final MockManagedFileFinder other, @Nonnull final Object override)
      {
        super(other, override);
        final var source = getSource(MockManagedFileFinder.class, other, override);
        this.holder = source.holder;
        this.result = source.result;
        this.sorters = source.sorters;
        this.fingerprint = source.fingerprint;
        holder.set(this);
      }

    @Override @Nonnull
    public MockManagedFileFinder sort (@Nonnull final SortCriterion criterion, @Nonnull final SortDirection direction)
      {
        return clonedWith(new MockManagedFileFinder(holder,
                                                    result,
                                                    concat(sorters, Pair.of(criterion, direction)),
                                                    fingerprint));
      }

    @Nonnull
    public DataManager.ManagedFileFinder withFingerprint (@Nonnull final Optional<String> fingerprint)
      {
        return clonedWith(new MockManagedFileFinder(holder, result, sorters, fingerprint));
      }

    @Override @Nonnull
    protected List<ManagedFile> computeResults()
      {
        return result;
      }
  }
