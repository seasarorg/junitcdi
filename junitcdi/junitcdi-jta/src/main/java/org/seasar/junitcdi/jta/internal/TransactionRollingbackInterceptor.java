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

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.UserTransaction;

import org.seasar.junitcdi.jta.Transactional;

/**
 * インターセプタが適用されたメソッドをトランザクション制御下で呼び出してからロールバックするインターセプタです．
 * <p>
 * トランザクションが開始されていない状態で対象のメソッドが呼び出された場合は トランザクションを開始します．
 * 対象のメソッドが終了するとトランザクションをロールバックします．
 * </p>
 * <p>
 * トランザクションが開始済みの状態で対象のメソッドが呼び出された場合は，何もせずに対象のメソッドを呼び出します．
 * </p>
 * 
 * @author koichik
 */
@Interceptor
@Transactional
public class TransactionRollingbackInterceptor {
    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** {@link UserTransaction} */
    @Inject
    protected UserTransaction userTransaction;

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    /**
     * インターセプタが適用されたメソッドをトランザクション制御下で呼び出しロールバックします．
     * 
     * @param invocation
     *            呼び出しコンテキスト
     * @return インターセプタが適用されたメソッドの戻り値
     * @throws Exception
     *             例外が発生した場合
     */
    @AroundInvoke
    public Object invoke(final InvocationContext invocation) throws Exception {
        return new UnitOfWork<Object>(userTransaction, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return invocation.proceed();
            }
        }, true).call();
    }
}
