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

import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/***********************************************************************************************************************
 *
 * A JPA Entity for {@link it.tidalwave.datamanager.model.Fingerprint}.
 *
 * @stereotype  JPA Entity
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Entity
@Table(name="fingerprints", indexes = {@Index(name = "fingerprints__name", columnList = "name"),
                                       @Index(name = "fingerprints__timestamp", columnList = "timestamp"),
                                       @Index(name = "fingerprints__file_id", columnList = "file_id")})
@NoArgsConstructor @AllArgsConstructor @Getter @ToString
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class FingerprintEntity
  {
    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @Column(nullable = false, length = 16)
    private String algorithm;

    @Column(name = "fingerprint", nullable = false, length = 32)
    private String value;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "file_id", nullable = false, length = 36)
    private String fileId;

    // See https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    @Override
    public boolean equals (final Object other)
      {
        if (this == other)
          {
            return true;
          }

        if (!(other instanceof FingerprintEntity))
          {
            return false;
          }

        return id != null && Objects.equals(id, ((FingerprintEntity)other).getId());
      }

    @Override
    public int hashCode()
      {
        return getClass().hashCode();
      }
  }
