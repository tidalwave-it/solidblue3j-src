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
import org.springframework.data.jpa.repository.JpaRepository;
import static it.tidalwave.util.spring.jpa.JpaRepositoryFinder.JpaSorter;

/***********************************************************************************************************************
 *
 * A specialisation of {@link JpaRepository} to support {@code Finder}s. Must be configured with annotation:
 * <pre>
 *   @EnableJpaRepositories(repositoryBaseClass = DefaultFinderJpaRepository.class)
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
     * Returns all the entities in the given range.
     *
     * @param   first         the first entity to retrieve
     * @param   max           the max number of entities to retrieve
     * @param   sorters       the sorters
     * @return                the entities
     *
     ******************************************************************************************************************/
    @Nonnull
    public List<E> findAll (int first, int max, @Nonnull List<JpaSorter> sorters);

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
