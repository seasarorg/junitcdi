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

import java.sql.Connection;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.junitcdi.core.runner.CDI;
import org.seasar.junitcdi.jta.Transactional;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * {@link DataSource}のテストクラスの例です．
 * 
 * @author koichik
 */
@RunWith(CDI.class)
public class DataSourceTest {
    @Inject
    DataSource defaultDataSource;

    @Inject
    @Hoge
    DataSource hogeDataSource;

    /**
     * @throws Exception
     */
    @Test
    @Transactional
    public void test() throws Exception {
        assertThat(defaultDataSource, is(notNullValue()));
        assertThat(hogeDataSource, is(notNullValue()));
        assertThat(defaultDataSource, is(not(sameInstance(hogeDataSource))));
        final Connection con = defaultDataSource.getConnection();
        assertThat(con, is(notNullValue()));
    }
}
