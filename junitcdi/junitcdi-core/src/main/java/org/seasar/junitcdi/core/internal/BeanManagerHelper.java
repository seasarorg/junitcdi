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

import java.lang.annotation.Annotation;
import java.util.ServiceLoader;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.Environments;
import org.jboss.weld.bootstrap.api.Service;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.context.api.BeanStore;
import org.jboss.weld.environment.se.discovery.SEWeldDeployment;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.jboss.weld.environment.se.util.WeldManagerUtils;
import org.jboss.weld.injection.spi.ResourceInjectionServices;

/**
 * {@link BeanManager}を操作するためのヘルパークラスです．
 * 
 * @author koichik
 */
public class BeanManagerHelper {
    // /////////////////////////////////////////////////////////////////
    // static fields
    //
    /** スレッド固有のCDIコンテナ */
    protected static final ThreadLocal<BeanManager> beanManagers =
        new InheritableThreadLocal<BeanManager>();

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    /**
     * スレッド固有のCDIコンテナを返します．
     * 
     * @return スレッド固有のCDIコンテナ
     */
    public static BeanManager getBeanManager() {
        final BeanManager beanManager = beanManagers.get();
        if (beanManager != null) {
            return beanManager;
        }
        return createBeanManager();
    }

    /**
     * CDIコンテナを作成します．
     * 
     * @return CDIコンテナ
     */
    protected static BeanManager createBeanManager() {
        // setup deployment
        final Deployment deployment = new SEWeldDeployment() {};
        deployment.getServices().add(
            ResourceInjectionServices.class,
            new ResourceInjectionServicesImpl());
        for (final ServicesProvider provider : ServiceLoader
            .load(ServicesProvider.class)) {
            provider.registerServices(deployment);
        }

        // create container
        final Bootstrap bootstrap = new WeldBootstrap();
        final BeanStore applicationContextStore = new KeepExtensionBeanStore();
        bootstrap.startContainer(
            Environments.SE,
            deployment,
            applicationContextStore);
        final BeanDeploymentArchive mainBeanDepArch =
            deployment.getBeanDeploymentArchives().iterator().next();
        final BeanManager beanManager = bootstrap.getManager(mainBeanDepArch);
        beanManagers.set(beanManager);

        // initialize container
        bootstrap.startInitialization();
        setupLocalStorage(deployment, applicationContextStore);
        bootstrap.deployBeans();
        bootstrap.validateBeans();
        bootstrap.endInitialization();
        beanManager.fireEvent(new ContainerInitialized());

        return beanManager;
    }

    /**
     * {@link ContainerLocalStorage}を準備します．
     * 
     * @param deployment
     *            CDIコンテナの初期化に使用した{@link Deployment}
     * @param applicationContextStore
     *            {@link ApplicationScoped}の{@link BeanStore}
     */
    protected static void setupLocalStorage(final Deployment deployment,
            final BeanStore applicationContextStore) {
        final ContainerLocalStorage localStorage =
            getBeanInstance(ContainerLocalStorage.class);
        localStorage.setDeployment(deployment);
        localStorage.setApplicationContextStore(applicationContextStore);
    }

    /**
     * 指定された型のbeanを返します．
     * 
     * @param <T>
     *            beanの型
     * @param beanClass
     *            beanの型
     * @param bindings
     *            バインディング
     * @return bean
     */
    public static <T> T getBeanInstance(final Class<T> beanClass,
            final Annotation... bindings) {
        return WeldManagerUtils.getInstanceByType(
            getBeanManager(),
            beanClass,
            bindings);
    }

    /**
     * 指定された型のbeanを返します．
     * 
     * @param <T>
     *            beanの型
     * @param beanManager
     *            {@link BeanManager}
     * @param beanClass
     *            beanの型
     * @param bindings
     *            バインディング
     * @return bean
     */
    public static <T> T getBeanInstance(final BeanManager beanManager,
            final Class<T> beanClass, final Annotation... bindings) {
        return WeldManagerUtils.getInstanceByType(
            beanManager,
            beanClass,
            bindings);
    }

    /**
     * 指定された名前のbeanを返します．
     * 
     * @param <T>
     *            beanの型
     * @param name
     *            beanの名前
     * @return bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBeanInstance(final String name) {
        return (T) getBeanInstance(getBeanManager(), name);
    }

    /**
     * 指定された名前のbeanを返します．
     * 
     * @param <T>
     *            beanの型
     * @param beanManager
     *            {@link BeanManager}
     * @param name
     *            beanの名前
     * @return bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBeanInstance(final BeanManager beanManager,
            final String name) {
        final Bean<T> bean =
            (Bean<T>) beanManager.getBeans(name).iterator().next();
        final CreationalContext<T> cc =
            beanManager.createCreationalContext(bean);
        final T instance =
            (T) beanManager.getReference(bean, bean.getBeanClass(), cc);
        return instance;
    }

    /**
     * 指定されたサービスを返します．
     * 
     * @param <S>
     *            サービスの型
     * @param serviceType
     *            サービスの型
     * @return サービス
     */
    public static <S extends Service> S getServices(final Class<S> serviceType) {
        return getDeployment().getServices().get(serviceType);
    }

    /**
     * {@link Deployment}を返します．
     * 
     * @return {@link Deployment}
     */
    public static Deployment getDeployment() {
        final ContainerLocalStorage localStorage =
            getBeanInstance(ContainerLocalStorage.class);
        return localStorage.getDeployment();
    }

    /**
     * {@link ApplicationScoped}の{@link BeanStore}を返します．
     * 
     * @return {@link ApplicationScoped}の{@link BeanStore}
     */
    public static BeanStore getApplicationContextStore() {
        final ContainerLocalStorage localStorage =
            getBeanInstance(ContainerLocalStorage.class);
        return localStorage.getApplicationContextStore();
    }
}
