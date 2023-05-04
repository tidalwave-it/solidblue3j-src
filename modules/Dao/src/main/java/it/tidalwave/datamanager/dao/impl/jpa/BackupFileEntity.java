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
package it.tidalwave.datamanager.dao.impl.jpa;

import jakarta.annotation.Nonnull;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/***********************************************************************************************************************
 *
 * @stereotype  JPA Entity
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Entity
@Table(name="backup_files", indexes = { @Index(name = "backup_files__id", columnList = "id"),
                                        @Index(name = "backup_files__file_id", columnList = "file_id")})
@NoArgsConstructor @AllArgsConstructor @Getter
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class BackupFileEntity
  {
    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "backup_id", nullable = false)
    private BackupEntity backup;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id", nullable = false)
    private ManagedFileEntity managedFile;

    @Column(name = "path", nullable = false, columnDefinition = "text")
    private String path;

    @Override @Nonnull
    public String toString()
      {
        return "BackupFileEntity@%x(id=%s, backupId=%s, path=%s, file=%s)".formatted(
                System.identityHashCode(this),
                id, backup == null ? null : backup.getId(), path, managedFile);
      }

    // See https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    @Override
    public boolean equals (final Object other)
      {
        if (this == other)
          {
            return true;
          }

        if (!(other instanceof BackupFileEntity))
          {
            return false;
          }

        return id != null && Objects.equals(id, ((BackupFileEntity)other).getId());
      }

    @Override
    public int hashCode()
      {
        return getClass().hashCode();
      }
  }
