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

import javax.annotation.Nonnegative;
import jakarta.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.Serial;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***********************************************************************************************************************
 *
 * A specialisation of Spring {@link JpaTransactionManager} that logs transaction demarcation and exposes some basic
 * metrics about transactions.
 *
 * @author      Fabrizio Giudici
 *
 **********************************************************************************************************************/
@Component @Qualifier("transactionManager")
@NoArgsConstructor @Slf4j
public class LoggingJpaTransactionManager extends JpaTransactionManager
  {
    @Serial private static final long serialVersionUID = 0L;

    private final AtomicInteger commitCount = new AtomicInteger();

    private final AtomicInteger rollbackCount = new AtomicInteger();

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    public LoggingJpaTransactionManager (@Nonnull final EntityManagerFactory emf)
      {
        super(emf);
      }

    /*******************************************************************************************************************
     *
     * Returns the count of performed commits.
     *
     * @return the count of commits
     *
     ******************************************************************************************************************/
    @Nonnegative
    public int getCommitCount()
      {
        return commitCount.intValue();
      }

    /*******************************************************************************************************************
     *
     * Returns the count of performed rollbacks.
     *
     * @return the count of rollbacks
     *
     ******************************************************************************************************************/
    @Nonnegative
    public int getRollbackCount()
      {
        return rollbackCount.intValue();
      }

    /*******************************************************************************************************************
     *
     * Resets the commit/rollback counters. For tests only.
     *
     ******************************************************************************************************************/
    public void resetCounters()
      {
        commitCount.set(0);
        rollbackCount.set(0);
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override
    protected void doBegin (@Nonnull final Object transaction, @Nonnull final TransactionDefinition definition)
      {
        log.info("SQL: BEGIN - tx definition: {}", definition);
        super.doBegin(transaction, definition);
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override
    protected void doCommit (@Nonnull final DefaultTransactionStatus status)
      {
        log.info("SQL: COMMIT");
        super.doCommit(status);
        commitCount.incrementAndGet();
      }

    /*******************************************************************************************************************
     * {@inheritDoc}
     ******************************************************************************************************************/
    @Override
    protected void doRollback (@Nonnull final DefaultTransactionStatus status)
      {
        log.warn("SQL: ROLLBACK");
        super.doRollback(status);
        rollbackCount.incrementAndGet();
      }
  }
