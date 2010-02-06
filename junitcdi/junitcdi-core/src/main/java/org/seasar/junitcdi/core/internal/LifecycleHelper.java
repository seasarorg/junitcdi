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
package org.seasar.junitcdi.core.internal;

import org.jboss.weld.bootstrap.api.Lifecycle;
import org.jboss.weld.context.AbstractApplicationContext;
import org.jboss.weld.context.ContextLifecycle;
import org.jboss.weld.context.api.helpers.ConcurrentHashMapBeanStore;
import org.jboss.weld.context.beanstore.HashMapBeanStore;

import static org.seasar.junitcdi.core.internal.BeanManagerHelper.*;

/**
 * {@link Lifecycle}を操作するためのヘルパークラスです．
 * 
 * @author koichik
 */
public class LifecycleHelper {
    // /////////////////////////////////////////////////////////////////
    // methods
    //
    /**
     * デフォルトの全コンテキストを準備します．
     */
    public static void setupDefaultContexts() {
        final ContextLifecycle lifecycle = getServices(ContextLifecycle.class);
        lifecycle.beginApplication(getApplicationContextStore());
        lifecycle.restoreSession("UnitTest", new ConcurrentHashMapBeanStore());
        lifecycle.beginRequest("UnitTest", new HashMapBeanStore());
    }

    /**
     * デフォルトの全コンテキストを破棄します．
     */
    public static void destroyDefaultContexts() {
        final ContextLifecycle lifecycle = getServices(ContextLifecycle.class);
        lifecycle.endRequest("UnitTest", new HashMapBeanStore());
        lifecycle.endSession("UnitTest", new ConcurrentHashMapBeanStore());

        final AbstractApplicationContext singletonContext =
            lifecycle.getSingletonContext();
        singletonContext.destroy();

        final AbstractApplicationContext applicationContext =
            lifecycle.getApplicationContext();
        applicationContext.destroy();
    }

    /**
     * テストクラス・スコープのコンテキストを準備します．
     */
    public static void setupTestClassContext() {
        final TestClassContext testClassContext =
            getBeanInstance(TestClassContextRegistrant.class).getContext();
        testClassContext.setBeanStore(new HashMapBeanStore());
        testClassContext.setActive(true);
    }

    /**
     * テストクラス・スコープのコンテキストを破棄します．
     */
    public static void destroyTestClassContext() {
        final TestClassContext testClassContext =
            getBeanInstance(TestClassContextRegistrant.class).getContext();
        testClassContext.destroy();
        testClassContext.setActive(false);
        testClassContext.setBeanStore(null);
    }

    /**
     * リクエスト・スコープのコンテキストを準備します．
     */
    public static void setupRequestContexts() {
        final ContextLifecycle lifecycle = getServices(ContextLifecycle.class);
        lifecycle.beginRequest("UnitTest", new HashMapBeanStore());
    }

    /**
     * リクエスト・スコープのコンテキストを破棄します．
     */
    public static void destroyRequestContexts() {
        final ContextLifecycle lifecycle = getServices(ContextLifecycle.class);
        lifecycle.endRequest("UnitTest", new HashMapBeanStore());
    }
}
