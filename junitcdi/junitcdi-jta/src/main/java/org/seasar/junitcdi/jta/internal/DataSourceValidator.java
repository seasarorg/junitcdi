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
package org.seasar.junitcdi.jta.internal;

import javax.annotation.sql.DataSourceDefinition;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.util.AnnotationLiteral;

import org.seasar.junitcdi.jta.datasource.AbstractDataSource;
import org.seasar.junitcdi.jta.datasource.DataSourceDefinitionNotSpecifiedException;

/**
 * {@link AbstractDataSource}のサブクラスに{@link DataSourceDefinition}
 * が注釈されていることを検証します．
 * 
 * @author koichik
 */
public class DataSourceValidator implements Extension {
    // /////////////////////////////////////////////////////////////////
    // methods
    //
    /**
     * {@link AbstractDataSource}のサブクラスに{@link DataSourceDefinition}
     * が注釈されていることを検証するための {@link Extension}です．
     * 
     * @param event
     *            イベント
     * @param beanManager
     *            {@link BeanManager}
     * @throws DataSourceDefinitionNotSpecifiedException
     *             {@link AbstractDataSource}に{@link DataSourceDefinition}
     *             が注釈されていない場合
     */
    public void afterDeploymentValidation(
            @Observes final AfterDeploymentValidation event,
            final BeanManager beanManager)
            throws DataSourceDefinitionNotSpecifiedException {
        for (final Bean<?> bean : beanManager.getBeans(
            AbstractDataSource.class,
            new AnnotationLiteral<Any>() {})) {
            final Class<?> beanClass = bean.getBeanClass();
            if (!beanClass.isAnnotationPresent(DataSourceDefinition.class)) {
                event
                    .addDeploymentProblem(new DataSourceDefinitionNotSpecifiedException(
                        beanClass));
                // addDeploymentProblem() しても無視されるのでやむを得ず．
                throw new DataSourceDefinitionNotSpecifiedException(beanClass);
            }
        }
    }
}
