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
package it.tidalwave.util.spring.jpa.impl;

import jakarta.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceUnitUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.transaction.Transactional;
import it.tidalwave.util.spring.jpa.FinderJpaRepository;
import lombok.extern.slf4j.Slf4j;
import static it.tidalwave.util.spring.jpa.JpaRepositoryFinder.JpaSorter;

/***********************************************************************************************************************
 *
 * A specialisation of {@link JpaRepository} to support {@code Finder}s.
 *
 * @param   <E> the static type of the JPA entity
 * @param   <K> the static type of the JPA entity key
 * @stereotype  Repository
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Slf4j
public class DefaultFinderJpaRepository<E, K> extends SimpleJpaRepository<E, K> implements FinderJpaRepository<E, K>
  {
    @Nonnull
    private final EntityManager em;

    @Nonnull
    private final PersistenceUnitUtil puUtil;

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @SuppressFBWarnings("MALICIOUS_CODE")
    public DefaultFinderJpaRepository (@Nonnull final JpaEntityInformation<E, ?> entityInformation,
                                       @Nonnull final EntityManager em)
      {
        super(entityInformation, em);
        this.em = em;
        this.puUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
      }

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    @SuppressFBWarnings("MALICIOUS_CODE")
    public DefaultFinderJpaRepository (@Nonnull final Class<E> domainClass, @Nonnull final EntityManager em)
      {
        super(domainClass, em);
        this.em = em;
        this.puUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Transactional @Nonnull
    public List<E> findAll (final int first, final int max, @Nonnull final List<JpaSorter> sorters)
      {
        log.info("findAll({}, {}, {})", first, max, sorters);
        final var sort = Sort.by(sorters.stream().map(JpaSorter::toOrder).toList());
        final var result = findAll(PageRequest.of(first, max, sort)).toList();
        log.info(">>>> returning {} items", result.size());
        log.trace(">>>> returning {}", result);
        return result;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override @Transactional @Nonnull @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    public <R extends Collection<Q>, Q> R fetch (@Nonnull final E entity,
                                                 @Nonnull final Function<? super E, R> function)
      {
        log.info("fetch({}, id={})", entity.getClass().getSimpleName(), puUtil.getIdentifier(entity));
        final var result = function.apply(em.merge(entity));
        result.size(); // trigger fetch
        log.info(">>>> returning {} items", result.size());
        log.trace(">>>> returning {}", result);
        return result;
      }
  }
