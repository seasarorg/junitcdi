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

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

/**
 * テストクラス・スコープを提供するコンテキストを追加するための{@link Extension}です．
 * 
 * @author koichik
 */
public class TestClassContextRegistrant implements Extension {
    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** テストクラス・スコープを提供するコンテキスト */
    protected final TestClassContext context = new TestClassContext();

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    /**
     * テストクラス・スコープを提供するコンテキストを返します．
     * 
     * @return テストクラス・スコープを提供するコンテキスト
     */
    public TestClassContext getContext() {
        return context;
    }

    // /////////////////////////////////////////////////////////////////
    // observer methods
    //
    /**
     * CDIコンテナがbeanを探した後に呼び出されます．
     * <p>
     * CDIコンテナにテストクラス・スコープの{@link TestClassContext コンテキスト}を登録します．
     * </p>
     * 
     * @param event
     *            イベント
     */
    public void afterBeanDiscovery(@Observes final AfterBeanDiscovery event) {
        event.addContext(context);
    }
}
