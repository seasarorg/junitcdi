/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.junitcdi.jta.internal;

import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.jboss.weld.transaction.spi.TransactionServices;
import org.seasar.extension.jta.TransactionManagerImpl;
import org.seasar.extension.jta.TransactionSynchronizationRegistryImpl;
import org.seasar.extension.jta.UserTransactionImpl;

/**
 * S2JTAを使用する{@link TransactionServices}の実装です．
 * 
 * @author koichik
 */
public class TransactionServicesImpl implements TransactionServices {
    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** {@link TransactionManager} */
    protected final TransactionManager transactionManager =
        new TransactionManagerImpl();

    /** {@link UserTransaction} */
    protected final UserTransaction userTransaction =
        new UserTransactionImpl(transactionManager);

    /** {@link TransactionSynchronizationRegistry} */
    protected final TransactionSynchronizationRegistry transactionSynchronizationRegistry =
        new TransactionSynchronizationRegistryImpl(transactionManager);

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    @Override
    public void registerSynchronization(
            final Synchronization synchronizedObserver) {
    }

    @Override
    public boolean isTransactionActive() {
        try {
            return userTransaction.getStatus() == Status.STATUS_ACTIVE;
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public UserTransaction getUserTransaction() {
        return userTransaction;
    }

    /**
     * {@link TransactionManager}を返します．
     * 
     * @return TransactionManager
     */
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    /**
     * {@link TransactionSynchronizationRegistry}を返します．
     * 
     * @return TransactionSynchronizationRegistry
     */
    public TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() {
        return transactionSynchronizationRegistry;
    }

    @Override
    public void cleanup() {
    }
}
