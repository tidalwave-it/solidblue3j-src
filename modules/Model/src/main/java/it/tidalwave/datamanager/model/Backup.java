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
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.nio.file.Path;
import it.tidalwave.util.As;
import it.tidalwave.util.Id;
import it.tidalwave.util.LazySupplier;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Delegate;
import lombok.experimental.Tolerate;

/***********************************************************************************************************************
 *
 * A backup.
 *
 * @stereotype  Model
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Builder @EqualsAndHashCode @ToString(doNotUseGetters = true)
public final class Backup implements As
  {
    public static class BackupBuilder
      {
        @Nonnull @Tolerate
        public BackupBuilder latestCheckDate (@Nonnull final LocalDateTime timestamp)
          {
            return latestCheckDate(Optional.of(timestamp));
          }

        @Nonnull @Tolerate
        public BackupBuilder backupFiles (@Nonnull final Supplier<List<BackupFile>> backupFiles)
          {
            return backupFiles(LazySupplier.of(backupFiles));
          }
      }

    @Delegate @ToString.Exclude @EqualsAndHashCode.Exclude
    private final As asDelegate = As.forObject(this);

    @Getter @Nonnull
    private final Id id;

    /** The label of the backup. */
    @Getter @Nonnull
    private final String label;

    /** The volume id of the backup. */
    @Getter @Nonnull
    private final Id volumeId;

    /** Whether the backup is encrypted. */
    @Getter
    private final boolean encrypted;

    /** The base path of the files in the backup. */
    @Getter @Nonnull
    private final Path basePath;

    /** The timestamp of backup creation. */
    @Getter @Nonnull
    private final LocalDateTime creationDate;

    /** The timestamp of backup registration. */
    @Getter @Nonnull
    private final LocalDateTime registrationDate;

    /** The latest time the backup was checked. */
    @Getter @Nonnull
    private final Optional<LocalDateTime> latestCheckDate;

    @Nonnull
    private final LazySupplier<List<BackupFile>> backupFiles;

    @Nonnull
    public List<BackupFile> getBackupFiles()
      {
        return backupFiles.get();
      }
  }
