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
package org.seasar.junitcdi.easymock.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;

import org.seasar.junitcdi.core.event.TestInfo;
import org.seasar.junitcdi.core.event.TestMethodFinished;
import org.seasar.junitcdi.core.event.TestMethodStarted;
import org.seasar.junitcdi.core.event.TestObjectObtained;
import org.seasar.junitcdi.easymock.EasyMock;
import org.seasar.junitcdi.easymock.EasyMockController;
import org.seasar.junitcdi.easymock.EasyMockType;

/**
 * EasyMockによってモックを作成し，状態を制御するbeanです．
 * <p>
 * テストのライフサイクルごとに次のように振る舞います．
 * </p>
 * <dl>
 * <dt>{@link TestObjectObtained}</dt>
 * <dd>テストクラスの{@link EasyMock}で注釈されたフィールドに対してモックを作成して設定します．
 * 作成されたモックはこのbeanによって管理されます．</dd>
 * <dt>{@link TestMethodStarted}</dt>
 * <dd>このbeanが管理しているすべてのモックオブジェクトを再生モードに設定します．</dd>
 * <dt>{@link TestMethodFinished}</dt>
 * <dd>テストメソッドが成功した場合は，このbeanが管理しているすべてのモックオブジェクトを検証します．</dd>
 * </dl>
 * 
 * @author koichik
 */
@RequestScoped
public class EasyMockControllerImpl implements EasyMockController {
    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** モックのリスト */
    protected final List<Object> mocks = new ArrayList<Object>();

    /** モックがバインディングされたフィールド */
    protected final Set<Field> boundFields = new LinkedHashSet<Field>();

    // /////////////////////////////////////////////////////////////////
    // methods from MockController
    //
    public <T> T createMock(final Class<T> clazz) {
        final T mock = EasyMockSupport.createMock(clazz);
        mocks.add(mock);
        return mock;
    }

    public <T> T createNiceMock(final Class<T> clazz) {
        final T mock = EasyMockSupport.createNiceMock(clazz);
        mocks.add(mock);
        return mock;
    }

    public <T> T createStrictMock(final Class<T> clazz) {
        final T mock = EasyMockSupport.createStrictMock(clazz);
        mocks.add(mock);
        return mock;
    }

    public void replay() {
        for (final Object mock : mocks) {
            EasyMockSupport.replay(mock);
        }
    }

    public void verify() {
        for (final Object mock : mocks) {
            EasyMockSupport.verify(mock);
        }
    }

    public void reset() {
        for (final Object mock : mocks) {
            EasyMockSupport.reset(mock);
        }
    }

    // /////////////////////////////////////////////////////////////////
    // observer methods
    //
    /**
     * テストクラスの{@link EasyMock}で注釈されたフィールドに対してモックを作成して設定します．
     * 
     * @param testContext
     *            テストコンテキスト
     * @throws Exception
     *             例外が発生した場合
     */
    public void onTestObjectObtained(
            @Observes @TestObjectObtained final TestInfo testContext)
            throws Exception {
        final Class<?> testClass = testContext.getBean().getBeanClass();
        final Object test = testContext.getInstance();
        for (Class<?> clazz = testClass; clazz != Object.class; clazz =
            clazz.getSuperclass()) {
            for (final Field field : clazz.getDeclaredFields()) {
                bindMockField(field, test);
            }
        }
    }

    /**
     * このbeanが管理しているすべてのモックオブジェクトを再生モードに設定します．
     * 
     * @param testContext
     *            テストコンテキスト
     */
    public void onTestMethodStarted(
            @Observes @TestMethodStarted final TestInfo testContext) {
        replay();
    }

    /**
     * テストメソッドが成功した場合は，このbeanが管理しているすべてのモックオブジェクトを検証します．
     * 
     * @param testInfo
     *            テストの情報
     */
    public void onTestMethodFinished(
            @Observes @TestMethodFinished final TestInfo testInfo) {
        try {
            final Class<? extends Throwable> expected =
                testInfo.getExpectedException();
            final Throwable exception = testInfo.getThrowable();
            if (expected == null && exception != null) {
                return;
            }
            if (expected != null && exception == null) {
                return;
            }
            if (expected != null && exception != null
                && !expected.isAssignableFrom(exception.getClass())) {
                return;
            }
            verify();
        } finally {
            unbindMockFields(testInfo.getInstance());
            clear();
        }
    }

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    /**
     * このオブジェクトの状態を消去します。
     */
    protected void clear() {
        mocks.clear();
        boundFields.clear();
    }

    /**
     * {@link EasyMock}アノテーションで注釈されてフィールドにモックを設定します．
     * 
     * @param field
     *            フィールド
     * @param test
     *            テストクラスのインスタンス
     * @throws Exception
     *             例外が発生した場合
     */
    protected void bindMockField(final Field field, final Object test)
            throws Exception {
        final int modifier = field.getModifiers();
        if (Modifier.isFinal(modifier)) {
            return;
        }
        final EasyMock annotation = field.getAnnotation(EasyMock.class);
        if (annotation == null) {
            return;
        }
        field.setAccessible(true);
        Object mock = field.get(test);
        if (mock != null) {
            mocks.add(mock);
            return;
        }
        final Class<?> clazz = field.getType();
        final EasyMockType mockType = annotation.value();
        if (mockType == EasyMockType.STRICT) {
            mock = createStrictMock(clazz);
        } else if (mockType == EasyMockType.NICE) {
            mock = createNiceMock(clazz);
        } else {
            mock = createMock(clazz);
        }
        field.set(test, mock);
        boundFields.add(field);
    }

    /**
     * テストクラスに設定したモックを解除します。
     * 
     * @param test
     *            テストクラスのインスタンス
     */
    protected void unbindMockFields(final Object test) {
        for (final Field field : boundFields) {
            try {
                field.set(test, null);
            } catch (final IllegalArgumentException e) {
                System.err.println(e);
            } catch (final IllegalAccessException e) {
                System.err.println(e);
            }
        }
        boundFields.clear();
    }
}
