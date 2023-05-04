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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/***********************************************************************************************************************
 *
 * A JPA Entity for {@link it.tidalwave.datamanager.model.Backup}.
 *
 * @stereotype  JPA Entity
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Entity
@Table(name="backups", indexes = { @Index(name = "backups__id", columnList = "id"),
                                   @Index(name = "backups__label", columnList = "label"),
                                   @Index(name = "backups__volume_id", columnList = "volume_id")})
@NoArgsConstructor @AllArgsConstructor @Getter
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class BackupEntity
  {
    @Id
    @Column(length = 36)
    private String id;

    @Column(unique = true, nullable = false, columnDefinition = "text")
    private String label;

    @Column(name = "volume_id", length = 36, unique = true, nullable = false)
    private String volumeId;

    private boolean encrypted;

    @Column(name = "base_path", nullable = false, columnDefinition = "text")
    private String basePath;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "latest_check_date")
    private LocalDateTime latestCheckDate;

    @Setter
    @OneToMany(mappedBy = "backup", fetch = FetchType.LAZY)
    @OrderBy("path asc")
    @Cascade(CascadeType.PERSIST)
    private List<BackupFileEntity> backupFiles = new ArrayList<>();

    public boolean isInitialized()
      {
        return Hibernate.isInitialized(backupFiles);
      }

    @Override @Nonnull
    public String toString()
      {
        return ("BackupEntity@%x(id=%s, label=%s, volumeId=%s, encrypted=%s, basePath=%s, creationDate=%s, " +
                "registrationDate=%s, latestCheckDate=%s, backupFiles=%s)").formatted(
                System.identityHashCode(this),
                id, label, volumeId, encrypted, basePath, creationDate, registrationDate, latestCheckDate,
                Hibernate.isInitialized(backupFiles) ? backupFiles : "not initialized");
      }

    // See https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    @Override
    public boolean equals (final Object other)
      {
        if (this == other)
          {
            return true;
          }

        if (!(other instanceof BackupEntity))
          {
            return false;
          }

        return id != null && Objects.equals(id, ((BackupEntity)other).getId());
      }

    @Override
    public int hashCode()
      {
        return getClass().hashCode();
      }
  }
