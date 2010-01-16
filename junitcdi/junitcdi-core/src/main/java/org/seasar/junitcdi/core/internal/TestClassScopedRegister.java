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
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

import org.seasar.junitcdi.core.TestClassScoped;

/**
 * テストクラス・スコープを追加するための{@link Extension}です．
 * 
 * @author koichik
 */
public class TestClassScopedRegister implements Extension {
    // /////////////////////////////////////////////////////////////////
    // methods
    //
    /**
     * CDIコンテナがbeanを探す前に呼び出されます．
     * <p>
     * CDIコンテナにテストクラス・スコープを注釈する{@link TestClassScoped}を登録します．
     * </p>
     * 
     * @param event
     *            イベント
     */
    public void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery event) {
        event.addScope(TestClassScoped.class, true, false);
    }

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
        event.addContext(BeanManagerHelper.getTestClassContext());
    }
}
