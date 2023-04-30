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
import java.util.function.Function;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * A convenience class to fetch relation collections of an Entity. Being a separate object, it is transactional even
 * if called from outside a transactional context.
 *
 * @stereotype  Repository
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Component @AllArgsConstructor @Slf4j
public class Fetcher
  {
    @Nonnull
    private final EntityManager em;

    /*******************************************************************************************************************
     *
     * Fetches elements in a lazy related collection in a transactional context.
     *
     * @param   entity        the entity
     * @param   function      the function to extract the related collection
     * @return                the related collection
     * @param   <E>           the static type of the entity
     * @param   <Q>           the static type of the related JPA entity
     * @param   <R>           the static type of the collection of Q
     *
     ******************************************************************************************************************/
    @Transactional
    @Nonnull @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    public <E, R extends Collection<Q>, Q> R fetch (@Nonnull final E entity,
                                                    @Nonnull final Function<? super E, R> function)
      {
        final var puUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
        log.info("fetch({}, id={})", entity.getClass().getSimpleName(), puUtil.getIdentifier(entity));
        final var result = function.apply(em.merge(entity));
        result.size(); // trigger fetch
        log.info(">>>> returning {} items", result.size());
        log.trace(">>>> returning {}", result);
        return result;
      }
  }
