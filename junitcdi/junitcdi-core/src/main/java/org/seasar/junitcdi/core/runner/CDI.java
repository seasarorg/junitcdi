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
package org.seasar.junitcdi.core.runner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.seasar.junitcdi.core.AfterMethod;
import org.seasar.junitcdi.core.BeforeMethod;
import org.seasar.junitcdi.core.event.TestInfo;
import org.seasar.junitcdi.core.internal.BeanManagerHelper;
import org.seasar.junitcdi.core.internal.LifecycleHelper;
import org.seasar.junitcdi.core.internal.TestEventNotifier;

/**
 * JUnit4のテストクラスでJSR-299 CDI(Contexts and Dependency Injection)を利用するための
 * {@link Runner}です．
 * <p>
 * テストクラスの{@link RunWith}アノテーションでこのクラスを指定することで， CDIがブートストラップされます．
 * </p>
 * 
 * <pre>
 * &#x40;RunWith(CDI.class)
 * public class XxxTest {
 *     ...
 * }
 * </pre>
 * 
 * @author koichik
 */
public class CDI extends BlockJUnit4ClassRunner {
    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** CDIコンテナ */
    protected BeanManager beanManager;

    /** テストイベントを通知するbean */
    protected TestEventNotifier testEventNotifier;

    /** テストクラスのbean定義 */
    protected Bean<?> testBean;

    /** テストクラスのインスタンスを作成したコンテキスト */
    protected CreationalContext<?> creationalContext;

    // /////////////////////////////////////////////////////////////////
    // constructors
    //
    /**
     * @param clazz
     * @throws Exception
     */
    public CDI(final Class<?> clazz) throws Exception {
        super(clazz);
    }

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    @Override
    protected Statement classBlock(final RunNotifier notifier) {
        final Statement statement = super.classBlock(notifier);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                LifecycleHelper.setupTestClassContext();
                try {
                    statement.evaluate();
                } finally {
                    LifecycleHelper.destroyTestClassContext();
                }
            }
        };
    }

    @Override
    protected void runChild(final FrameworkMethod method,
            final RunNotifier runNotifier) {
        try {
            beanManager = BeanManagerHelper.getBeanManager();
            LifecycleHelper.setupDefaultContexts();
            testEventNotifier =
                BeanManagerHelper.getBeanInstance(
                    beanManager,
                    TestEventNotifier.class);
            runNotifier.addListener(testEventNotifier);
            try {
                super.runChild(method, runNotifier);
            } finally {
                runNotifier.removeListener(testEventNotifier);
                LifecycleHelper.destroyDefaultContexts();
                testEventNotifier = null;
                beanManager = null;
            }
        } catch (final Throwable e) {
            runNotifier.fireTestFailure(new Failure(getDescription(), e));
        }
    }

    @Override
    protected Statement methodBlock(final FrameworkMethod method) {
        final Statement statement = super.methodBlock(method);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    statement.evaluate();
                } finally {
                    if (creationalContext != null) {
                        creationalContext.release();
                    }
                    testBean = null;
                }
            }
        };
    }

    @Override
    protected Object createTest() throws Exception {
        final Class<?> testClass = getTestClass().getJavaClass();
        for (final Bean<?> bean : beanManager.getBeans(testClass)) {
            if (bean.getBeanClass() == testClass) {
                testBean = bean;
                creationalContext = beanManager.createCreationalContext(bean);
                final Object test =
                    beanManager
                        .getReference(bean, testClass, creationalContext);
                testEventNotifier.testObjectObtained(new TestInfo(
                    getDescription(),
                    bean,
                    test));
                return test;
            }
        }
        throw new TestClassNotBeanException(testClass);
    }

    @Override
    protected Statement methodInvoker(final FrameworkMethod method,
            final Object test) {
        final Statement statement = super.methodInvoker(method, test);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                testEventNotifier.testMethodStarted(createTestInfo(null));
                try {
                    statement.evaluate();
                    testEventNotifier.testMethodFinished(createTestInfo(null));
                } catch (Throwable e) {
                    testEventNotifier.testMethodFinished(createTestInfo(e));
                    throw e;
                }
            }

            private TestInfo createTestInfo(Throwable e) {
                return new TestInfo(getDescription(), testBean, test, method
                    .getMethod(), e);
            }
        };
    }

    @Override
    protected Statement withBefores(final FrameworkMethod method,
            final Object target, Statement statement) {
        final List<FrameworkMethod> beforeMethods =
            getCallbackMethods(method.getName(), CallbackMethodType.BEFORE);
        if (!beforeMethods.isEmpty()) {
            statement = new RunBefores(statement, beforeMethods, target);
        }
        return super.withBefores(method, target, statement);
    }

    @Override
    protected Statement withAfters(final FrameworkMethod method,
            final Object target, Statement statement) {
        final List<FrameworkMethod> afterMethods =
            getCallbackMethods(method.getName(), CallbackMethodType.AFTER);
        if (!afterMethods.isEmpty()) {
            statement = new RunAfters(statement, afterMethods, target);
        }
        return super.withAfters(method, target, statement);
    }

    /**
     * 指定したテストメソッドに対応するBeforeまたはAfterメソッドを返します．
     * 
     * @param testMethod
     *            テストメソッド
     * @param type
     *            BeforeまたはAfter
     * @return テストメソッドに対応するBeforeまたはAfterメソッド
     */
    protected List<FrameworkMethod> getCallbackMethods(final String testMethod,
            final CallbackMethodType type) {
        final List<FrameworkMethod> callbackMethods =
            new ArrayList<FrameworkMethod>();
        for (final FrameworkMethod callbackMethod : getTestClass()
            .getAnnotatedMethods(type.annotation)) {
            if (isCallbackMethod(callbackMethod, testMethod, type)) {
                callbackMethods.add(callbackMethod);
            }
        }
        return callbackMethods;
    }

    /**
     * メソッドが指定のメソッドに対応するBeforeまたはAfterメソッドなら{@literal true}を返します．
     * 
     * @param method
     *            メソッド
     * @param testMethod
     *            テストメソッド
     * @param type
     *            BeforeまたはAfter
     * @return メソッドが指定のメソッドに対応するBeforeまたはAfterメソッドなら{@literal true}
     */
    protected static boolean isCallbackMethod(final FrameworkMethod method,
            final String testMethod, final CallbackMethodType type) {
        final String[] targetMethods = type.getTarget(method);
        if (targetMethods.length == 0) {
            if (isCallbackMethod(method.getName(), testMethod, type.prefix)) {
                return true;
            }
        } else {
            if (isCallbackMethod(targetMethods, testMethod)) {
                return true;
            }
        }
        return false;
    }

    /**
     * メソッドが指定のメソッドに対応するBeforeまたはAfterメソッドなら{@literal true}を返します．
     * 
     * @param method
     *            メソッド
     * @param testMethod
     *            テストメソッド
     * @param prefix
     *            接頭辞
     * @return メソッドが指定のメソッドに対応するBeforeまたはAfterメソッドなら{@literal true}
     */
    protected static boolean isCallbackMethod(final String method,
            final String testMethod, final String prefix) {
        if (method.startsWith(prefix)
            && method.substring(prefix.length()).equals(capitalize(testMethod))) {
            return true;
        }
        return false;
    }

    /**
     * メソッドが指定のメソッドに対応するBeforeまたはAfterメソッドなら{@literal true}を返します．
     * 
     * @param targetMethods
     *            メソッドが対称とするテストメソッド
     * @param testMethod
     *            テストメソッド
     * @return メソッドが指定のメソッドに対応するBeforeまたはAfterメソッドなら{@literal true}
     */
    protected static boolean isCallbackMethod(final String[] targetMethods,
            final String testMethod) {
        for (final String targetMethod : targetMethods) {
            if (targetMethod.equals(testMethod)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 文字列をキャピタライズして返します．
     * 
     * @param s
     *            文字列
     * @return キャピタライズした文字列
     */
    protected static String capitalize(final String s) {
        final int first = s.codePointAt(0);
        if (Character.isLowerCase(first)) {
            return new String(new StringBuilder(s.length()).appendCodePoint(
                Character.toUpperCase(first)).append(
                s.substring(Character.toChars(first).length)));
        }
        return s;
    }

    // /////////////////////////////////////////////////////////////////
    // helper types
    //
    /**
     * BeforeまたはAfterを表す列挙です．
     * 
     * @author koichik
     */
    enum CallbackMethodType {
        /**
         * Beforeを表す定数です．
         */
        BEFORE("before", BeforeMethod.class) {
            @Override
            public String[] getTarget(final FrameworkMethod method) {
                return method.getAnnotation(BeforeMethod.class).value();
            }
        },

        /**
         * Afterを表す定数です．
         */
        AFTER("after", AfterMethod.class) {
            @Override
            public String[] getTarget(final FrameworkMethod method) {
                return method.getAnnotation(AfterMethod.class).value();
            }
        };

        // instance fields
        /** コールバックメソッドの接頭辞 */
        public final String prefix;

        /** コールバックメソッドに付けられるアノテーション */
        public final Class<? extends Annotation> annotation;

        // constructors
        /**
         * インスタンスを構築します．
         * 
         * @param prefix
         *            コールバックメソッドの接頭辞
         * @param annotation
         *            コールバックメソッドに付けられるアノテーション
         */
        private CallbackMethodType(final String prefix,
                final Class<? extends Annotation> annotation) {
            this.prefix = prefix;
            this.annotation = annotation;
        }

        // methods
        /**
         * メソッドに{@link BeforeMethod}または{@link AfterMethod}の{@literal target}
         * 要素で指定された文字列の配列を返します．
         * 
         * @param method
         *            メソッド
         * @return メソッドに{@link BeforeMethod}または{@link AfterMethod}の{@literal
         *         target} 要素で指定された文字列の配列
         */
        public abstract String[] getTarget(FrameworkMethod method);
    }
}
