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
import java.util.LinkedHashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

import org.jboss.interceptor.util.InterceptionUtils;
import org.seasar.junitcdi.core.internal.BeanManagerHelper;
import org.seasar.junitcdi.easymock.EasyMock;
import org.seasar.junitcdi.easymock.EasyMockController;

/**
 * {@link EasyMock}で注釈されたフィールドにEasyMockで作成されたモックをDIする{@link Extension}です．
 * <p>
 * 現在の実装は Weld (JBoss Interceptor) に依存しています．
 * </p>
 * 
 * @author koichik
 */
public class EasyMockInjectionTargetProcessor implements Extension {
    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** モックを作成するbean */
    protected EasyMockController mockController;

    // /////////////////////////////////////////////////////////////////
    // observer methods
    //
    /**
     * DI対象となるbeanを処理します．
     * 
     * @param <X>
     *            beanの型
     * @param event
     *            イベント
     */
    public <X> void processInjectionTarget(
            @Observes final ProcessInjectionTarget<X> event) {
        final AnnotatedType<X> bean = event.getAnnotatedType();
        for (final AnnotatedField<?> field : bean.getFields()) {
            if (field.isAnnotationPresent(EasyMock.class)) {
                event.setInjectionTarget(new EasyMockInjectionTarget<X>(
                    bean,
                    event.getInjectionTarget()));
                return;
            }
        }
    }

    /**
     * beanのバリデーションが終わった後に通知されます．
     * 
     * @param event
     *            イベント
     * @param beanManager
     *            {@link BeanManager}
     */
    public void afterBeanDiscovery(@Observes final AfterBeanDiscovery event,
            final BeanManager beanManager) {
        mockController =
            BeanManagerHelper.getBeanInstance(
                beanManager,
                EasyMockController.class);
    }

    // /////////////////////////////////////////////////////////////////
    // inner classes
    //
    /**
     * モックオブジェクトがDIされる対象を処理します．
     * 
     * @author koichik
     * @param <X>
     *            beanの型
     */
    public class EasyMockInjectionTarget<X> implements InjectionTarget<X> {
        // /////////////////////////////////////////////////////////////////
        // instance fields
        //
        /** beanの型 */
        protected final AnnotatedType<X> bean;

        /** 移譲先となる{@link InjectionTarget} */
        protected final InjectionTarget<X> delegate;

        /** {@link EasyMock}で注釈されたフィールド */
        protected final Set<Field> mockFields = new LinkedHashSet<Field>();

        /** モックがバインディングされたフィールド */
        protected final Set<Field> boundFields = new LinkedHashSet<Field>();

        // /////////////////////////////////////////////////////////////////
        // constructors
        //
        /**
         * インスタンスを構築します．
         * 
         * @param bean
         *            bean
         * @param delegate
         *            移譲先となる{@link InjectionTarget}
         */
        public EasyMockInjectionTarget(final AnnotatedType<X> bean,
                final InjectionTarget<X> delegate) {
            this.bean = bean;
            this.delegate = delegate;
            for (final AnnotatedField<?> field : bean.getFields()) {
                final Field javaField = field.getJavaMember();
                if (javaField.isAnnotationPresent(EasyMock.class)
                    && !Modifier.isFinal(javaField.getModifiers())) {
                    javaField.setAccessible(true);
                    mockFields.add(javaField);
                }
            }
        }

        // /////////////////////////////////////////////////////////////////
        // methods from InjectionTarget
        //
        @Override
        public void inject(final X instance, final CreationalContext<X> ctx) {
            final X rawInstance = InterceptionUtils.getRawInstance(instance);
            for (final Field field : mockFields) {
                try {
                    bindMockField(field, rawInstance);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
            delegate.inject(instance, ctx);
        }

        @Override
        public void postConstruct(final X instance) {
            delegate.postConstruct(instance);
        }

        @Override
        public void preDestroy(final X instance) {
            delegate.preDestroy(instance);
            try {
                unbindMockFields(InterceptionUtils.getRawInstance(instance));
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }

        // /////////////////////////////////////////////////////////////////
        // methods from Producer
        //
        @Override
        public void dispose(final X instance) {
            delegate.dispose(instance);
        }

        @Override
        public Set<InjectionPoint> getInjectionPoints() {
            return delegate.getInjectionPoints();
        }

        @Override
        public X produce(final CreationalContext<X> ctx) {
            return delegate.produce(ctx);
        }

        // /////////////////////////////////////////////////////////////////
        // methods
        //
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
            Object mock = field.get(test);
            if (mock != null) {
                return;
            }
            final Class<?> clazz = field.getType();
            switch (field.getAnnotation(EasyMock.class).value()) {
            case DEFAULT:
                mock = mockController.createMock(clazz);
                break;
            case STRICT:
                mock = mockController.createStrictMock(clazz);
                break;
            case NICE:
                mock = mockController.createNiceMock(clazz);
                break;
            }
            field.set(test, mock);
            boundFields.add(field);
        }

        /**
         * テストクラスに設定したモックを解除します。
         * 
         * @param test
         *            テストクラスのインスタンス
         * @throws Exception
         *             例外が発生した場合
         */
        protected void unbindMockFields(final Object test) throws Exception {
            try {
                for (final Field field : boundFields) {
                    field.set(test, null);
                }
            } finally {
                boundFields.clear();
            }
        }
    }
}
