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
import it.tidalwave.datamanager.model.DataManager.ManagedFileFinder;
import it.tidalwave.datamanager.model.ManagedFile;

/***********************************************************************************************************************
 *
 * A specialised {@link it.tidalwave.util.Finder} for {@link ManagedFileEntity}.
 *
 * @stereotype Finder
 * @author Fabrizio Giudici
 *
 **********************************************************************************************************************/
@SuppressFBWarnings("SE_BAD_FIELD")
public class JpaManagedFileFinder
        extends JpaSpecificationFinder<ManagedFile, ManagedFileEntity, ManagedFileFinder, ManagedFileEntityJpaRepository>
        implements ManagedFileFinder
  {
    @Serial private static final long serialVersionUID = 0L;

    private final Optional<String> fingerprint;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public JpaManagedFileFinder (@Nonnull final ManagedFileEntityJpaRepository repository,
                                 @Nonnull final Function<ManagedFileEntity, ManagedFile> transformer)
      {
        this(repository, transformer, Optional.empty());
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private JpaManagedFileFinder (@Nonnull final ManagedFileEntityJpaRepository repository,
                                  @Nonnull final Function<ManagedFileEntity, ManagedFile> transformer,
                                  @Nonnull final Optional<String> fingerprint)
      {
        super(repository, transformer);
        this.fingerprint = fingerprint;
      }

    /*******************************************************************************************************************
     * The copy constructor required by {@link it.tidalwave.util.spi.HierarchicFinderSupport}.
     ******************************************************************************************************************/
    public JpaManagedFileFinder (@Nonnull final JpaManagedFileFinder other, @Nonnull final Object override)
      {
        super(other, override);
        final var source = getSource(JpaManagedFileFinder.class, other, override);
        this.fingerprint = source.fingerprint;
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override @Nonnull
    public ManagedFileFinder withFingerprint (@Nonnull final Optional<String> fingerprint)
      {
        return clonedWith(new JpaManagedFileFinder(repository, entityToModel, fingerprint));
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    protected void composeSpecification (@Nonnull final Root<ManagedFileEntity> root,
                                         @Nonnull final CriteriaBuilder criteriaBuilder,
                                         @Nonnull final List<? super Predicate> predicates)
      {
        fingerprint.ifPresent(f -> predicates.add(criteriaBuilder.equal(root.join("fingerprints").get("value"), f)));
      }
  }

