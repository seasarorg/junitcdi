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
package org.seasar.junitcdi.core.event;

import java.lang.reflect.Method;

import javax.enterprise.inject.spi.Bean;

import org.junit.Test;
import org.junit.Test.None;
import org.junit.runner.Description;

/**
 * 実行中のテストの情報です．
 * 
 * @author koichik
 */
public class TestInfo {
    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** テストの記述 */
    protected final Description description;

    /** テストクラスのbean定義 */
    protected final Bean<?> bean;

    /** テストのインスタンス */
    protected final Object instance;

    /** テストメソッド */
    protected final Method method;

    /** テストメソッドがスローした例外 */
    protected final Throwable throwable;

    // /////////////////////////////////////////////////////////////////
    // constructors
    //
    /**
     * インスタンスを構築します．
     * 
     * @param description
     *            テストの記述
     * @param bean
     *            テストクラスのbean定義
     * @param instance
     *            テストのインスタンス
     */
    public TestInfo(final Description description, final Bean<?> bean,
            final Object instance) {
        this(description, bean, instance, null);
    }

    /**
     * インスタンスを構築します．
     * 
     * @param description
     *            テストの記述
     * @param bean
     *            テストクラスのbean定義
     * @param instance
     *            テストのインスタンス
     * @param method
     *            テストメソッド
     */
    public TestInfo(final Description description, final Bean<?> bean,
            final Object instance, final Method method) {
        this(description, bean, instance, method, null);
    }

    /**
     * インスタンスを構築します．
     * 
     * @param description
     *            テストの記述
     * @param bean
     *            テストクラスのbean定義
     * @param instance
     *            テストのインスタンス
     * @param method
     *            テストメソッド
     * @param throwable
     *            テストメソッドがスローした例外
     */
    public TestInfo(final Description description, final Bean<?> bean,
            final Object instance, final Method method,
            final Throwable throwable) {
        this.description = description;
        this.bean = bean;
        this.instance = instance;
        this.method = method;
        this.throwable = throwable;
    }

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    /**
     * テストの記述を返します．
     * 
     * @return テストの記述
     */
    public Description getDescription() {
        return description;
    }

    /**
     * テストクラスのbean定義を返します．
     * 
     * @return テストクラスのbean定義
     */
    public Bean<?> getBean() {
        return bean;
    }

    /**
     * テストのインスタンスを返します．
     * 
     * @return テストのインスタンス
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * テストメソッドを返します．
     * 
     * @return テストメソッド
     */
    public Method getMethod() {
        return method;
    }

    /**
     * テストメソッドが期待する例外を返します．
     * 
     * @return テストメソッドが期待する例外
     */
    public Class<? extends Throwable> getExpectedException() {
        final Test annotation = method.getAnnotation(Test.class);
        if (annotation == null || annotation.expected() == None.class) {
            return null;
        }
        return annotation.expected();
    }

    /**
     * テストメソッドがスローした例外を返します．
     * 
     * @return テストメソッドがスローした例外
     */
    public Throwable getThrowable() {
        return throwable;
    }
}
