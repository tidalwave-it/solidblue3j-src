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
package it.tidalwave.util.spring.jpa;

import jakarta.annotation.Nonnull;
import java.util.List;
import java.io.Serial;
import org.springframework.data.domain.Sort;
import it.tidalwave.util.Finder;
import it.tidalwave.util.spi.HierarchicFinderSupport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.util.CollectionUtils.concat;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * A {@link Finder} that works with a {@link FinderJpaRepository} and is capable of converting JPA entities to model.
 *
 * @param   <M> the static type of the model object
 * @param   <E> the static type of the JPA entity
 * @param   <F> the static type of the {@code Finder}
 * @stereotype  Finder
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@AllArgsConstructor(access = PRIVATE) @Slf4j
public class JpaRepositoryFinder<M, E, F extends Finder<M>> extends HierarchicFinderSupport<M, F>
  {
    @Serial private static final long serialVersionUID = 0L;

    @Nonnull
    private final FinderJpaRepository<E, ?> repository;

    @Nonnull
    private final EntityModelMapper<M, E> mapper;

    @Nonnull
    private final List<JpaSorter> sorters;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private record JpaSortCriterion (@Nonnull String jpaFieldName) implements SortCriterion
      {
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public record JpaSorter (@Nonnull JpaSortCriterion criterion, @Nonnull SortDirection direction)
      {
        @Nonnull
        public Sort.Order toOrder()
          {
            return new Sort.Order(direction == SortDirection.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC,
                                  criterion.jpaFieldName);
          }

        @Override @Nonnull
        public String toString()
          {
            return "JpaSorter(%s, %s)".formatted(criterion.jpaFieldName, direction.name());
          }
      }

    /*******************************************************************************************************************
     *
     * Creates a new instance given a repository and a model-to-entity mapper.
     *
     * @param     repository    the repository
     * @param     mapper        the mapper
     *
     ******************************************************************************************************************/
    public JpaRepositoryFinder (@Nonnull final FinderJpaRepository<E, ?> repository,
                                @Nonnull final EntityModelMapper<M, E> mapper)
      {
        this(repository, mapper, List.of());
      }

    /*******************************************************************************************************************
     *
     * The required constructor for subclasses of {@link HierarchicFinderSupport}.
     *
     ******************************************************************************************************************/
    @SuppressWarnings("unchecked")
    public JpaRepositoryFinder (@Nonnull final JpaRepositoryFinder<M, E, F> other, @Nonnull final Object override)
      {
        super(other, override);
        final var source = getSource(JpaRepositoryFinder.class, other, override);
        this.repository = source.repository;
        this.mapper = source.mapper;
        this.sorters = source.sorters;
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override @Nonnull
    public F sort (@Nonnull final SortCriterion criterion, @Nonnull final SortDirection direction)
      {
        if (criterion instanceof final JpaSortCriterion jpaSortCriterion)
          {
            final var sorters = concat(this.sorters, new JpaSorter(jpaSortCriterion, direction));
            return clonedWith(new JpaRepositoryFinder<M, E, F>(repository, mapper, sorters));
          }

        return super.sort(criterion, direction);
      }

    /*******************************************************************************************************************
     *
     * Creates a {@link SortCriterion} by JPA field name.
     *
     * @param     jpaFieldName    the JPA field name
     * @return                    the {@code SortCriterion}
     *
     ******************************************************************************************************************/
    @Nonnull
    public static SortCriterion by (@Nonnull final String jpaFieldName)
      {
        return new JpaSortCriterion(jpaFieldName);
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override @Nonnull
    protected List<M> computeNeededResults()
      {
        return repository.findAll(firstResult, maxResults, sorters).stream().map(mapper::entityToModel).toList();
      }
  }