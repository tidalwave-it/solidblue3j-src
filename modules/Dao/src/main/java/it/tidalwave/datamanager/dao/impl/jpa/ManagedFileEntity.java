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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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
 * A JPA Entity for {@link ManagedFileEntity}.
 *
 * @stereotype  JPA Entity
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Entity @Table(name="files")
@NoArgsConstructor @AllArgsConstructor @Getter
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ManagedFileEntity
  {
    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, columnDefinition = "text")
    private String path;

    @Setter
    @OneToMany(mappedBy = "fileId", fetch = FetchType.LAZY)
    @OrderBy("timestamp asc")
    @Cascade(CascadeType.PERSIST)
    private List<FingerprintEntity> fingerprints = new ArrayList<>();

    public boolean isInitialized()
      {
        return Hibernate.isInitialized(fingerprints);
      }

    @Override @Nonnull
    public String toString()
      {
        return "ManagedFileEntity@%x(id=%s, path=%s, fingerprints=%s)".formatted(
                System.identityHashCode(this),
                id, path, Hibernate.isInitialized(fingerprints) ? fingerprints : "not initialized");
      }

    // See https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    @Override
    public boolean equals (final Object other)
      {
        if (this == other)
          {
            return true;
          }

        if (!(other instanceof ManagedFileEntity))
          {
            return false;
          }

        return id != null && Objects.equals(id, ((ManagedFileEntity)other).getId());
      }

    @Override
    public int hashCode()
      {
        return getClass().hashCode();
      }
  }
