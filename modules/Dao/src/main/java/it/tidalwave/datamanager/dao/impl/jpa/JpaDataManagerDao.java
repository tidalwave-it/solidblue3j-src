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
import java.util.List;
import java.nio.file.Path;
import org.springframework.stereotype.Component;
import it.tidalwave.util.Id;
import it.tidalwave.util.spring.jpa.impl.Fetcher;
import it.tidalwave.datamanager.model.DataManager.ManagedFileFinder;
import it.tidalwave.datamanager.model.Fingerprint;
import it.tidalwave.datamanager.model.ManagedFile;
import it.tidalwave.datamanager.dao.DataManagerDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * The DAO for the application.
 *
 * @stereotype  DAO
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Component @AllArgsConstructor @Slf4j
public class JpaDataManagerDao implements DataManagerDao
  {
    @Nonnull
    private final ManagedFileEntityJpaRepository managedFileRepo;

    @Nonnull
    private final Fetcher fetcher;

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override @Nonnull
    public ManagedFileFinder findManagedFiles()
      {
        return new JpaManagedFileFinder(managedFileRepo, this::managedFileEntityToModel);
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Nonnull
    public ManagedFile managedFileEntityToModel (@Nonnull final ManagedFileEntity entity)
      {
        return new ManagedFile(Id.of(entity.getId()),
             Path.of(entity.getPath()),
             entity.isInitialized()
             ? () -> fingerprintEntitiesToModel(entity.getFingerprints())
             : () -> fingerprintEntitiesToModel(fetcher.fetch(entity, ManagedFileEntity::getFingerprints)));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static List<Fingerprint> fingerprintEntitiesToModel (@Nonnull final List<? extends FingerprintEntity> entities)
      {
        return entities.stream().map(JpaDataManagerDao::fingerprintEntityToModel).toList();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Fingerprint fingerprintEntityToModel (@Nonnull final FingerprintEntity entity)
      {
        return Fingerprint.builder()
                          .id(Id.of(entity.getId()))
                          .name(entity.getName())
                          .algorithm(entity.getAlgorithm())
                          .fingerprint(entity.getValue())
                          .timestamp(entity.getTimestamp())
                          .build();
      }
  }
