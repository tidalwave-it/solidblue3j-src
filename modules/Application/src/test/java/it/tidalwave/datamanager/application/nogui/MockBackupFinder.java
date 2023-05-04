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
import it.tidalwave.datamanager.model.Backup;
import it.tidalwave.datamanager.model.DataManager;
import lombok.AllArgsConstructor;
import static it.tidalwave.util.CollectionUtils.concat;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * A mock for {@link DataManager.BackupFinder}.
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@AllArgsConstructor(access = PRIVATE)
public class MockBackupFinder
        extends HierarchicFinderSupport<Backup, MockBackupFinder>
        implements DataManager.BackupFinder
  {
    @Serial private static final long serialVersionUID = 0L;

    private final Holder<MockBackupFinder> holder;

    @Nonnull
    public final List<Pair<SortCriterion, SortDirection>> sorters;

    @Nonnull
    public final Optional<String> label;

    @Nonnull
    public final Optional<String> volumeId;

    @Nonnull
    public final Optional<String> fileId;

    public MockBackupFinder (@Nonnull final Holder<MockBackupFinder> holder)
      {
        this.holder = holder;
        this.sorters = new ArrayList<>();
        this.label = Optional.empty();
        this.volumeId = Optional.empty();
        this.fileId = Optional.empty();
      }

    public MockBackupFinder (@Nonnull final MockBackupFinder other, @Nonnull final Object override)
      {
        super(other, override);
        final var source = getSource(MockBackupFinder.class, other, override);
        this.holder = source.holder;
        this.sorters = source.sorters;
        this.label = source.label;
        this.volumeId = source.volumeId;
        this.fileId = source.fileId;
        holder.set(this);
      }

    @Override @Nonnull
    public MockBackupFinder sort (@Nonnull final SortCriterion criterion, @Nonnull final SortDirection direction)
      {
        return clonedWith(new MockBackupFinder(holder,
                                               concat(sorters, Pair.of(criterion, direction)),
                                               label,
                                               volumeId,
                                               fileId));
      }

    @Nonnull
    public DataManager.BackupFinder withLabel (@Nonnull final Optional<String> label)
      {
        return clonedWith(new MockBackupFinder(holder, sorters, label, volumeId, fileId));
      }

    @Nonnull
    public DataManager.BackupFinder withVolumeId (@Nonnull final Optional<String> volumeId)
      {
        return clonedWith(new MockBackupFinder(holder, sorters, label, volumeId, fileId));
      }

    @Nonnull
    public DataManager.BackupFinder withFileId (@Nonnull final Optional<String> fileId)
      {
        return clonedWith(new MockBackupFinder(holder, sorters, label, volumeId, fileId));
      }

    @Override @Nonnull
    protected List<Backup> computeResults()
      {
        return List.of();
      }
  }
