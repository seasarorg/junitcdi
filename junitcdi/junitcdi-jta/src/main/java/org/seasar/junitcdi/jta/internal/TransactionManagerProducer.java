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

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

import org.jboss.weld.transaction.spi.TransactionServices;
import org.seasar.junitcdi.core.internal.BeanManagerHelper;

/**
 * {@link TransactionManager}および{@link TransactionSynchronizationRegistry}
 * をbeanとして提供します．
 * 
 * @author koichik
 */
@Singleton
public class TransactionManagerProducer {
    // /////////////////////////////////////////////////////////////////
    // methods
    //
    /**
     * {@link TransactionManager}を返します．
     * 
     * @return TransactionManager
     */
    @Produces
    public TransactionManager getTransactionManager() {
        return ((TransactionServicesImpl) BeanManagerHelper
            .getServices(TransactionServices.class)).getTransactionManager();
    }

    /**
     * {@link TransactionSynchronizationRegistry}を返します．
     * 
     * @return TransactionSynchronizationRegistry
     */
    @Produces
    public TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() {
        return ((TransactionServicesImpl) BeanManagerHelper
            .getServices(TransactionServices.class))
            .getTransactionSynchronizationRegistry();
    }
}
