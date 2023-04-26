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
import java.io.Serial;
import java.nio.file.Path;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;
import it.tidalwave.util.Id;
import it.tidalwave.util.spring.jpa.EntityModelMapper;
import it.tidalwave.util.spring.jpa.FinderJpaRepository;
import it.tidalwave.util.spring.jpa.JpaRepositoryFinder;
import it.tidalwave.datamanager.model.DataManager.ManagedFileFinder;
import it.tidalwave.datamanager.model.Fingerprint;
import it.tidalwave.datamanager.model.ManagedFile;
import it.tidalwave.datamanager.dao.DataManagerDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

/***********************************************************************************************************************
 *
 * The DAO for the application.
 *
 * @stereotype  DAO
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Component @Transactional @AllArgsConstructor @Slf4j
public class JpaDataManagerDao implements DataManagerDao, EntityModelMapper<ManagedFile, ManagedFileEntity>
  {
    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    static class JpaManagedFileFinder
            extends JpaRepositoryFinder<ManagedFile, ManagedFileEntity, ManagedFileFinder> implements ManagedFileFinder
      {
        @Serial private static final long serialVersionUID = 0L;

        public JpaManagedFileFinder (@Nonnull final FinderJpaRepository<ManagedFileEntity, ?> repository,
                                     @Nonnull final EntityModelMapper<ManagedFile, ManagedFileEntity> mapper)
          {
            super(repository, mapper);
          }

        public JpaManagedFileFinder (@Nonnull final JpaManagedFileFinder other, @Nonnull final Object override)
          {
            super(other, override);
          }
      }

    @Nonnull
    private final ManagedFileEntityJpaRepository managedFileRepo;

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override @Transactional(SUPPORTS) @Nonnull
    public ManagedFileFinder findManagedFiles()
      {
        return new JpaManagedFileFinder(managedFileRepo, this);
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Transactional(SUPPORTS) @Override @Nonnull
    public ManagedFile entityToModel (@Nonnull final ManagedFileEntity entity)
      {
        return new ManagedFile(Id.of(entity.getId()),
             Path.of(entity.getPath()),
             entity.isInitialized()
             ? () -> entityToModel(entity.getFingerprints())
             : () -> entityToModel(managedFileRepo.fetch(entity, ManagedFileEntity::getFingerprints)));
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static List<Fingerprint> entityToModel (@Nonnull final List<? extends FingerprintEntity> entities)
      {
        return entities.stream().map(JpaDataManagerDao::entityToModel).toList();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    private static Fingerprint entityToModel (@Nonnull final FingerprintEntity entity)
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