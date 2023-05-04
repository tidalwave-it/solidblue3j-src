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
import java.util.Optional;
import java.util.function.Function;
import java.io.Serial;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.tidalwave.util.spring.jpa.JpaSpecificationFinder;
import it.tidalwave.datamanager.model.Backup;
import it.tidalwave.datamanager.model.DataManager.BackupFinder;

/***********************************************************************************************************************
 *
 * A specialised {@link it.tidalwave.util.Finder} for {@link ManagedFileEntity}.
 *
 * @stereotype Finder
 * @author Fabrizio Giudici
 *
 **********************************************************************************************************************/
@SuppressFBWarnings("SE_BAD_FIELD")
public class JpaBackupFinder
        extends JpaSpecificationFinder<Backup, BackupEntity, BackupFinder, BackupEntityJpaRepository>
        implements BackupFinder
  {
    @Serial private static final long serialVersionUID = 0L;

    private final Optional<String> label;

    private final Optional<String> volumeId;

    private final Optional<String> fileId;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public JpaBackupFinder (@Nonnull final BackupEntityJpaRepository repository,
                            @Nonnull final Function<BackupEntity, Backup> transformer)
      {
        this(repository, transformer, Optional.empty(), Optional.empty(), Optional.empty());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public JpaBackupFinder (@Nonnull final BackupEntityJpaRepository repository,
                            @Nonnull final Function<BackupEntity, Backup> transformer,
                            @Nonnull final Optional<String> label,
                            @Nonnull final Optional<String> volumeId,
                            @Nonnull final Optional<String> fileId)
      {
        super(repository, transformer);
        this.label = label;
        this.volumeId = volumeId;
        this.fileId = fileId;
      }

    /*******************************************************************************************************************
     * The copy constructor required by {@link it.tidalwave.util.spi.HierarchicFinderSupport}.
     ******************************************************************************************************************/
    public JpaBackupFinder (@Nonnull final JpaBackupFinder other, @Nonnull final Object override)
      {
        super(other, override);
        final var source = getSource(JpaBackupFinder.class, other, override);
        this.label = source.label;
        this.volumeId = source.volumeId;
        this.fileId = source.fileId;
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override @Nonnull
    public BackupFinder withLabel (@Nonnull final Optional<String> label)
      {
        return clonedWith(new JpaBackupFinder(repository, entityToModel, label, volumeId, fileId));
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override @Nonnull
    public BackupFinder withVolumeId (@Nonnull final Optional<String> volumeId)
      {
        return clonedWith(new JpaBackupFinder(repository, entityToModel, label, volumeId, fileId));
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override @Nonnull
    public BackupFinder withFileId (@Nonnull final Optional<String> fileId)
      {
        return clonedWith(new JpaBackupFinder(repository, entityToModel, label, volumeId, fileId));
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    protected void composeSpecification (@Nonnull final Root<BackupEntity> root,
                                         @Nonnull final CriteriaBuilder criteriaBuilder,
                                         @Nonnull final List<? super Predicate> predicates)
      {
        label.ifPresent(l -> predicates.add(criteriaBuilder.equal(root.get("label"), l)));
        volumeId.ifPresent(v -> predicates.add(criteriaBuilder.equal(root.get("volumeId"), v)));
        fileId.ifPresent(i -> predicates.add(criteriaBuilder.equal(root.join("backupFiles")
                                                                       .join("managedFile")
                                                                       .get("id"), i)));
      }
  }
