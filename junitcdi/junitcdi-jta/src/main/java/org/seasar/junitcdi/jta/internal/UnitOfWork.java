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

import java.util.concurrent.Callable;

import javax.transaction.UserTransaction;

import static javax.transaction.Status.*;

/**
 * workをアトミックなunit of workとしてトランザクショナルに実行するクラスです．
 * 
 * @author koichik
 * @param <T>
 *            workの戻り値の型
 */
public class UnitOfWork<T> implements Callable<T> {
    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** {@link UserTransaction} */
    protected final UserTransaction userTransaction;

    /** トランザクショナルに実行するwork */
    protected final Callable<T> work;

    /** トランザクションを常にロールバックする場合は{@literal true} */
    protected final boolean rollbackOnly;

    // /////////////////////////////////////////////////////////////////
    // constructors
    //
    /**
     * インスタンスを構築します．
     * 
     * @param userTransaction
     *            {@link UserTransaction}
     * @param work
     *            トランザクショナルに実行するwork
     * @param rollbackOnly
     *            トランザクションを常にロールバックする場合は{@literal true}
     */
    public UnitOfWork(final UserTransaction userTransaction,
            final Callable<T> work, final boolean rollbackOnly) {
        this.userTransaction = userTransaction;
        this.work = work;
        this.rollbackOnly = rollbackOnly;
    }

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    @Override
    public T call() throws Exception {
        if (userTransaction.getStatus() != STATUS_NO_TRANSACTION) {
            return work.call();
        }

        userTransaction.begin();
        try {
            final T result = work.call();
            if (!rollbackOnly && userTransaction.getStatus() == STATUS_ACTIVE) {
                userTransaction.commit();
            }
            return result;
        } finally {
            switch (userTransaction.getStatus()) {
            case STATUS_ACTIVE:
            case STATUS_MARKED_ROLLBACK:
                userTransaction.rollback();
                break;
            }
        }
    }
}
