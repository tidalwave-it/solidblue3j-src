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
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import lombok.AllArgsConstructor;
import lombok.ToString;
import static it.tidalwave.util.spring.jpa.JpaRepositoryFinder.JpaSorter;

/***********************************************************************************************************************
 *
 * A specialisation of {@link JpaRepository} to support {@code Finder}s. Must be configured with annotation:
 * <pre>
 *   {@code @EnableJpaRepositories(repositoryBaseClass} = DefaultFinderJpaRepository.class)
 * </pre>
 *
 * @param   <E> the static type of the JPA entity
 * @param   <K> the static type of the JPA entity key
 * @stereotype  Repository
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public interface FinderJpaRepository<E, K> extends JpaRepository<E, K>
  {
    /*******************************************************************************************************************
     *
     * Holds parameters for querying an entity and performs a query by invoking a repository. This class can be
     * specialised to hold further parameters.
     *
     * @param   <E>           the static type of the entity
     * @param   <R>           the static type of the repository
     *
     ******************************************************************************************************************/
    @AllArgsConstructor @ToString
    public static class QueryParameters<E, R extends FinderJpaRepository<E, ?>>
      {
        private final int first;

        private final int max;

        @Nonnull
        private final List<JpaSorter> sorters;

        @Nonnull
        public List<E> execute (@Nonnull final R repository)
          {
            return repository.findAll(getPageRequest()).toList();
          }

        @Nonnull
        protected PageRequest getPageRequest()
          {
            return PageRequest.of(first, max, Sort.by(sorters.stream().map(JpaSorter::toOrder).toList()));
          }
      }

    /*******************************************************************************************************************
     *
     * Fetches elements in a lazy related collection in a transactional context.
     *
     * @param   entity        the entity
     * @param   function      the function to extract the related collection
     * @return                the related collection
     * @param   <Q>           the static type of the related JPA entity
     * @param   <R>           the static type of the collection of Q
     *
     ******************************************************************************************************************/
    @Nonnull
    public <R extends Collection<Q>, Q> R fetch (@Nonnull E entity, @Nonnull Function<? super E, R> function);
  }
