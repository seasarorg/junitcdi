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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.context.api.BeanStore;

/**
 * CDIコンテナ固有の情報を保持しておくための{@link Extension}です．
 * <p>
 * {@link Extension}はCDIコンテナ内でインスタンスが一つだけ生成される (CDI仕様「11.5. Container lifecycle
 * events」より) こと，Beanとして{@link BeanManager}からルックアップできることを利用して，
 * コンテナより先に作成されるものなど，非Beanのインスタンスをコンテナ単位で管理します．
 * </p>
 * 
 * @author koichik
 */
public class ContainerLocalStorage implements Extension {
    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** CDIコンテナの初期化に使用した{@link Deployment} */
    protected Deployment deployment;

    /** {@link ApplicationScoped}の{@link BeanStore} */
    protected BeanStore applicationContextStore;

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    /**
     * CDIコンテナの初期化に使用した{@link Deployment}を返します．
     * 
     * @return CDIコンテナの初期化に使用した{@link Deployment}
     */
    public Deployment getDeployment() {
        return deployment;
    }

    /**
     * CDIコンテナの初期化に使用した{@link Deployment}を設定します．
     * 
     * @param deployment
     *            CDIコンテナの初期化に使用した{@link Deployment}
     */
    public void setDeployment(final Deployment deployment) {
        this.deployment = deployment;
    }

    /**
     * {@link ApplicationScoped}の{@link BeanStore}を返します．
     * 
     * @return {@link ApplicationScoped}の{@link BeanStore}
     */
    public BeanStore getApplicationContextStore() {
        return applicationContextStore;
    }

    /**
     * {@link ApplicationScoped}の{@link BeanStore}を設定します．
     * 
     * @param applicationContextStore
     *            {@link ApplicationScoped}の{@link BeanStore}
     */
    public void setApplicationContextStore(
            final BeanStore applicationContextStore) {
        this.applicationContextStore = applicationContextStore;
    }
}
