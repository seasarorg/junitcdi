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
package org.seasar.junitcdi.jta.datasource;

import javax.annotation.sql.DataSourceDefinition;

/**
 * {@link AbstractDataSource}のサブクラスに{@link DataSourceDefinition}
 * が注釈されていない場合にスローされる例外です．
 * 
 * @author koichik
 */
public class DataSourceDefinitionNotSpecifiedException extends Exception {
    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** {@link DataSourceDefinition}が注釈されていない{@link AbstractDataSource}のサブクラス */
    protected Class<?> dataSourceClass;

    // /////////////////////////////////////////////////////////////////
    // constructors
    //
    /**
     * インスタンスを構築します．
     * 
     * @param dataSourceClass
     *            {@link DataSourceDefinition}が注釈されていない
     *            {@link AbstractDataSource}のサブクラス
     */
    public DataSourceDefinitionNotSpecifiedException(
            final Class<?> dataSourceClass) {
        super(dataSourceClass.getName()
            + " does not have a @DataSourceDefinition.");
        this.dataSourceClass = dataSourceClass;
    }
}
