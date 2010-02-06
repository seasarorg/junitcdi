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

import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.naming.NamingException;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.seasar.junitcdi.core.internal.JndiContextFactory;

/**
 * {@link UserTransaction}および{@link TransactionSynchronizationRegistry}
 * をJNDIに登録ための{@link Extension}です．
 * 
 * @author koichik
 */
public class UserTransactionRegistrant implements Extension {
    // /////////////////////////////////////////////////////////////////
    // constants
    //
    /** {@link UserTransaction}のJNDI名 */
    protected static final String USER_TRANSACTION_JNDI_NAME =
        "java:comp/UserTransaction";

    /** {@link TransactionSynchronizationRegistry}のJNDI名 */
    protected static final String TRANSACTION_SYNCHRONIZATION_REGISTRY_JNDI_NAME =
        "java:comp/TransactionSynchronizationRegistry";

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    /**
     * {@link UserTransaction}および{@link TransactionSynchronizationRegistry}
     * をJNDIに登録します．
     * 
     * @param event
     *            イベント
     * @param beanManager
     *            {@link BeanManager}
     * @throws NamingException
     *             JNDIの登録に失敗した場合
     */
    public void afterDeploymentValidation(
            @Observes final AfterDeploymentValidation event,
            final BeanManager beanManager) throws NamingException {
        bindBeanToJndi(
            beanManager,
            UserTransaction.class,
            USER_TRANSACTION_JNDI_NAME);
        bindBeanToJndi(
            beanManager,
            TransactionSynchronizationRegistry.class,
            TRANSACTION_SYNCHRONIZATION_REGISTRY_JNDI_NAME);
    }

    /**
     * beanをJNDIに登録します．
     * 
     * @param beanManager
     *            {@link BeanManager}
     * @param beanClass
     *            beanの型
     * @param jndiName
     *            JNDIに登録する名前
     * @throws NamingException
     *             JNDIの登録に失敗した場合
     */
    protected void bindBeanToJndi(final BeanManager beanManager,
            final Class<?> beanClass, final String jndiName)
            throws NamingException {
        final Set<Bean<?>> beans = beanManager.getBeans(beanClass);
        if (beans.isEmpty()) {
            return;
        }
        final Bean<?> bean = beans.iterator().next();
        JndiContextFactory.getContext().bind(jndiName, bean);
    }
}
