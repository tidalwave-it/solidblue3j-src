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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.io.Serial;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import it.tidalwave.util.Finder;
import it.tidalwave.util.spi.HierarchicFinderSupport;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.util.CollectionUtils.concat;
import static lombok.AccessLevel.PRIVATE;

/***********************************************************************************************************************
 *
 * A {@link Finder} that works with a repository extending {@link JpaSpecificationExecutor}.
 *
 * @param   <M> the static type of the model object
 * @param   <E> the static type of the JPA entity
 * @param   <F> the static type of the {@code Finder}
 * @param   <R> the static type of the repository
 * @stereotype  Finder
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@AllArgsConstructor(access = PRIVATE) @Slf4j
public class JpaSpecificationFinder<M, E, F extends Finder<M>, R extends JpaSpecificationExecutor<E>>
        extends HierarchicFinderSupport<M, F>
  {
    @Serial private static final long serialVersionUID = 0L;

    @Nonnull
    protected final R repository;

    @Nonnull
    protected final Function<E, M> entityToModel;

    @Nonnull
    protected final List<JpaSorter> sorters;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    private record JpaSortCriterion (@Nonnull Enum<?> sortingKey) implements SortCriterion
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
                                  getName(criterion.sortingKey));
          }

        @Override @Nonnull
        public String toString()
          {
            return "JpaSorter(%s, %s)".formatted(getName(criterion.sortingKey), direction.name());
          }

        @Nonnull @SneakyThrows
        private static String getName (@Nonnull final Enum<?> sortingKey)
          {
            return (String)sortingKey.getClass().getMethod("getName").invoke(sortingKey);
          }
      }

    /*******************************************************************************************************************
     *
     * Creates a new instance given a repository and a model-to-entity transformer.
     *
     * @param     repository    the repository
     * @param     entityToModel the transformer
     *
     ******************************************************************************************************************/
    public JpaSpecificationFinder (@Nonnull final R repository, @Nonnull final Function<E, M> entityToModel)
      {
        this(repository, entityToModel, List.of());
      }

    /*******************************************************************************************************************
     *
     * The required constructor for subclasses of {@link HierarchicFinderSupport}.
     *
     ******************************************************************************************************************/
    @SuppressWarnings("unchecked")
    public JpaSpecificationFinder (@Nonnull final JpaSpecificationFinder<M, E, F, R> other, @Nonnull final Object override)
      {
        super(other, override);
        final var source = getSource(JpaSpecificationFinder.class, other, override);
        this.repository = (R)source.repository; // See https://stackoverflow.com/questions/76129388
        this.entityToModel = source.entityToModel;
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
            return clonedWith(new JpaSpecificationFinder<>(repository, entityToModel, sorters));
          }

        return super.sort(criterion, direction);
      }

    /*******************************************************************************************************************
     *
     * Creates a {@link SortCriterion} by key.
     *
     * @param     sortingKey  the key
     * @return                the {@code SortCriterion}
     *
     ******************************************************************************************************************/
    @Nonnull
    public static SortCriterion by (@Nonnull final Enum<?> sortingKey)
      {
        return new JpaSortCriterion(sortingKey);
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override @Nonnull
    protected final List<M> computeNeededResults()
      {
        final var baseTime = System.currentTimeMillis();
        final var specification = getSpecification();
        final var pageRequest = PageRequest.of(firstResult, maxResults,
                                               Sort.by(sorters.stream().map(JpaSorter::toOrder).toList()));
        log.info("computeNeededResults() - {}", pageRequest);
        final var result = repository.findAll(specification, pageRequest).stream().map(entityToModel).toList();
        log.info(">>>> returning {} items in {} msec", result.size(), System.currentTimeMillis() - baseTime);
        log.trace(">>>> returning {}", result);
        return result;
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @Nonnull
    protected Specification<E> getSpecification()
      {
        return (root, query, criteriaBuilder) ->
          {
            final var predicates = new ArrayList<Predicate>();
            composeSpecification(root, criteriaBuilder, predicates);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
          };
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    protected void composeSpecification (@Nonnull final Root<E> root,
                                         @Nonnull final CriteriaBuilder criteriaBuilder,
                                         @Nonnull final List<? super Predicate> predicates)
      {
      }
  }