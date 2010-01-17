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
package org.seasar.junitcdi.core.util;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.BeanManager;

import org.seasar.junitcdi.core.internal.BeanManagerHelper;

/**
 * beanのインスタンスをルックアップするユーティリティです．
 * 
 * @author koichik
 */
public class BeanLookupUtil {

    /**
     * 指定された型のbeanを返します．
     * 
     * @param <T>
     *            beanの型
     * @param beanClass
     *            beanの型
     * @param bindings
     *            バインディング
     * @return beanのインスタンス
     */
    public static <T> T getBeanInstance(final Class<T> beanClass,
            final Annotation... bindings) {
        return BeanManagerHelper.getBeanInstance(beanClass, bindings);
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
     * @return beanのインスタンス
     */
    public static <T> T getBeanInstance(final BeanManager beanManager,
            final Class<T> beanClass, final Annotation... bindings) {
        return BeanManagerHelper.getBeanInstance(
            beanManager,
            beanClass,
            bindings);
    }

}
