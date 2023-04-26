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
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Function;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.hibernate.Session;
import lombok.Setter;
import static org.testng.Assert.*;

/***********************************************************************************************************************
 *
 * Adapted from https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
public class EntityTestSupport extends AbstractTestNGSpringContextTests
  {
    protected enum Options
      {
        IGNORE_PROXY_EQUALITY
      }

    @Inject
    private JpaTransactionManager txManager;

    @Setter
    private EntityManagerFactory emf;

    /******************************************************************************************************************/
    protected <T> void assertEqualityConsistency (@Nonnull final Class<T> entityClass,
                                                  @Nonnull final T entity,
                                                  @Nonnull final Options ... options)
      {
        emf = txManager.getEntityManagerFactory();

        final var tuples = new HashSet<>();

        tuples.add(entity);
        assertTrue(tuples.contains(entity));

        if (getId(entity) == null)
          {
            runInTx(em ->
              {
                em.persist(entity);
                em.flush();
                assertTrue(tuples.contains(entity), "Not found in the Set after its persisted.");
              });
          }
        else
          {
            runInTx(em ->
              {
                em.persist(entity);
                em.flush();
              });
          }

        assertTrue(tuples.contains(entity));

        runInTx(em ->
          {
            final var entityProxy = em.getReference(entityClass, getId(entity));
          assertEquals(entity, entityProxy, "Proxy is not equal to the entity.");
          });

        if (!Arrays.asList(options).contains(Options.IGNORE_PROXY_EQUALITY))
          {
            runInTx(em ->
              {
                final var entityProxy = em.getReference(entityClass, getId(entity));
                assertEquals(entityProxy, entity, "Not equal to the entity proxy.");
              });
          }

        runInTx(em ->
          {
            final var _entity = em.merge(entity);
            assertTrue(tuples.contains(_entity), "Not found in the Set after its merged.");
          });

        runInTx(em ->
          {
            em.unwrap(Session.class).merge(entity);
            assertTrue(tuples.contains(entity), "TNot found in the Set after its reattached.");
          });

        runInTx(em ->
          {
            final var _entity = em.find(entityClass, getId(entity));
            assertTrue(tuples.contains(_entity), "Not found in the Set after loaded in a different PersistenceContext.");
          });

        runInTx(em ->
          {
            final var _entity = em.getReference(entityClass, getId(entity));
            assertTrue(tuples.contains(_entity), "Not found in the Set after loaded as a proxy in a different Persistence Context.");
          });

        final var deletedEntity = runInTxWithResult(em ->
          {
            final var _entity = em.getReference(entityClass, getId(entity));
            em.remove(_entity);
            return _entity;
          });

        assertTrue(tuples.contains(deletedEntity), "Not found in the Set even after its deleted.");
      }

    /******************************************************************************************************************/
    protected void runInTx (@Nonnull final Consumer<? super EntityManager> task)
      {
        runInTxWithResult(em ->  { task.accept(em); return null; });
      }

    /******************************************************************************************************************/
    @Nonnull
    protected <S> S runInTxWithResult (@Nonnull final Function<? super EntityManager, S> task)
      {
        try (final var em = emf.createEntityManager())
          {
            em.getTransaction().begin();
            final var result = task.apply(em);
            em.getTransaction().commit();
            return result;
          }
      }

    /******************************************************************************************************************/
    @Nullable
    private Object getId (@Nonnull final Object entity)
      {
        return emf.getPersistenceUnitUtil().getIdentifier(entity);
      }
  }
