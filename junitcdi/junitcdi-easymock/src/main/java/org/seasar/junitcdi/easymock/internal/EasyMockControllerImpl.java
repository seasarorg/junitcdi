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

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;

import org.seasar.junitcdi.core.event.TestInfo;
import org.seasar.junitcdi.core.event.TestMethodFinished;
import org.seasar.junitcdi.core.event.TestMethodStarted;
import org.seasar.junitcdi.easymock.EasyMockController;

/**
 * EasyMockによって作成されたモックの状態を制御するbeanです．
 * <p>
 * テストのライフサイクルごとに次のように振る舞います．
 * </p>
 * <dl>
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
    }
}
