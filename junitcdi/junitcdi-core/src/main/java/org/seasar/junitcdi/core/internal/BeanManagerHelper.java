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

import java.util.ServiceLoader;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.Environments;
import org.jboss.weld.bootstrap.api.Service;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.Deployment;
import org.jboss.weld.context.AbstractApplicationContext;
import org.jboss.weld.context.ContextLifecycle;
import org.jboss.weld.context.api.helpers.ConcurrentHashMapBeanStore;
import org.jboss.weld.context.beanstore.HashMapBeanStore;
import org.jboss.weld.environment.se.ShutdownManager;
import org.jboss.weld.environment.se.discovery.SEWeldDeployment;
import org.jboss.weld.environment.se.events.ContainerInitialized;
import org.jboss.weld.environment.se.util.WeldManagerUtils;
import org.seasar.junitcdi.core.spi.ServicesProvider;

/**
 * CDIコンテナ({@link BeanManager})の作成などを行うヘルパークラスです．
 * <p>
 * 現在の実装はCDIのRIである<a href="http://seamframework.org/Weld">JBoss
 * Weld</a>に依存しています．
 * </p>
 * 
 * @author koichik
 */
public class BeanManagerHelper {
    // /////////////////////////////////////////////////////////////////
    // static fields
    //
    /** スレッド固有の{@link BeanManager} */
    protected static final ThreadLocal<BeanManager> beanManagers =
        new InheritableThreadLocal<BeanManager>() {
            @Override
            protected BeanManager initialValue() {
                return createBeanManager(deployments.get());
            }
        };

    /** スレッド固有のデプロイメント */
    protected static final ThreadLocal<Deployment> deployments =
        new InheritableThreadLocal<Deployment>() {
            @Override
            protected Deployment initialValue() {
                return new SEWeldDeployment() {};
            }
        };

    /** テストクラスコンテキスト */
    protected static final TestClassContext testClassContext =
        new TestClassContext();

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    /**
     * CDIコンテナを作成します．
     * 
     * @return CDIコンテナ
     */
    public static BeanManager createBeanManager() {
        return createBeanManager(new SEWeldDeployment() {});
    }

    /**
     * スレッド固有のCDIコンテナを作成します．
     * 
     * @param deployment
     *            デプロイメント
     * @return CDIコンテナ
     */
    protected static BeanManager createBeanManager(final Deployment deployment) {
        for (ServicesProvider provider : ServiceLoader
            .load(ServicesProvider.class)) {
            provider.registerServices(deployment);
        }
        final Bootstrap bootstrap = new WeldBootstrap();
        bootstrap.startContainer(
            Environments.SE,
            deployment,
            new ConcurrentHashMapBeanStore());
        final BeanDeploymentArchive mainBeanDepArch =
            deployment.getBeanDeploymentArchives().iterator().next();
        final BeanManager beanManager = bootstrap.getManager(mainBeanDepArch);
        bootstrap.startInitialization();
        bootstrap.deployBeans();
        WeldManagerUtils
            .getInstanceByType(beanManager, ShutdownManager.class)
            .setBootstrap(bootstrap);
        bootstrap.validateBeans();
        bootstrap.endInitialization();
        beanManager.fireEvent(new ContainerInitialized());
        return beanManager;
    }

    /**
     * スレッド固有のCDIコンテナを返します．
     * 
     * @return スレッド固有のCDIコンテナ
     */
    public static BeanManager getBeanManager() {
        return beanManagers.get();
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
     * @return bean
     */
    public static <T> T getBean(final BeanManager beanManager,
            final Class<T> beanClass) {
        return WeldManagerUtils.getInstanceByType(beanManager, beanClass);
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
        return deployments.get().getServices().get(serviceType);
    }

    /**
     * テストクラスコンテキストを返します．
     * 
     * @return テストクラスコンテキスト
     */
    public static TestClassContext getTestClassContext() {
        return testClassContext;
    }

    /**
     * デフォルトの全コンテキストを準備します．
     */
    public static void setupDefaultContexts() {
        final ContextLifecycle lifecycle =
            deployments.get().getServices().get(ContextLifecycle.class);
        lifecycle.beginApplication(new ConcurrentHashMapBeanStore());
        lifecycle.restoreSession("UnitTest", new ConcurrentHashMapBeanStore());
        lifecycle.beginRequest("UnitTest", new HashMapBeanStore());
    }

    /**
     * デフォルトの全コンテキストを破棄します．
     */
    public static void destroyDefaultContexts() {
        final ContextLifecycle lifecycle =
            deployments.get().getServices().get(ContextLifecycle.class);
        lifecycle.endRequest("UnitTest", new HashMapBeanStore());
        lifecycle.endSession("UnitTest", new ConcurrentHashMapBeanStore());
        final AbstractApplicationContext singletonContext =
            lifecycle.getSingletonContext();
        singletonContext.destroy();
        singletonContext.setActive(false);
        singletonContext.setBeanStore(null);
        final AbstractApplicationContext applicationContext =
            lifecycle.getApplicationContext();
        applicationContext.destroy();
        applicationContext.setActive(false);
        applicationContext.setBeanStore(null);
    }

    /**
     * テストクラス・スコープのコンテキストを準備します．
     */
    public static void setupTestClassContext() {
        testClassContext.setBeanStore(new HashMapBeanStore());
        testClassContext.setActive(true);
    }

    /**
     * テストクラス・スコープのコンテキストを破棄します．
     */
    public static void destroyTestClassContext() {
        testClassContext.destroy();
        testClassContext.setActive(false);
        testClassContext.setBeanStore(null);
    }

    /**
     * リクエスト・スコープのコンテキストを準備します．
     */
    public static void setupRequestContexts() {
        final ContextLifecycle lifecycle =
            deployments.get().getServices().get(ContextLifecycle.class);
        lifecycle.beginRequest("UnitTest", new HashMapBeanStore());
    }

    /**
     * リクエスト・スコープのコンテキストを破棄します．
     */
    public static void destroyRequestContexts() {
        final ContextLifecycle lifecycle =
            deployments.get().getServices().get(ContextLifecycle.class);
        lifecycle.endRequest("UnitTest", new HashMapBeanStore());
    }
}
