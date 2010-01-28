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
package org.seasar.junitcdi.easymock;

import java.util.concurrent.Callable;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.junitcdi.core.BeforeMethod;
import org.seasar.junitcdi.core.runner.CDI;

import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * 
 * 
 * @author koichik
 */
@RunWith(CDI.class)
@Singleton
public class EasyMockTest {
    /** */
    @EasyMock
    @Produces
    Callable<String> mock;

    @Inject
    ConsumerBean bean;

    /**
     * @throws Exception
     */
    @BeforeMethod
    public void beforeTest() throws Exception {
        expect(mock.call()).andReturn("hoge");
        expect(mock.call()).andReturn("moge");
    }

    /**
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        assertThat(mock, is(notNullValue()));
        assertThat(mock.call(), is("hoge"));

        assertThat(bean.mock, is(notNullValue()));
        assertThat(bean.mock.call(), is("moge"));
    }

    /**
     * @throws Exception
     */
    @BeforeMethod
    public void beforeTestWithException() throws Exception {
        expect(mock.call()).andReturn("hoge");
    }

    /**
     * @throws Exception
     */
    @Test(expected = RuntimeException.class)
    public void testWithException() throws Exception {
        assertThat(mock, is(notNullValue()));
        assertThat(mock.call(), is("hoge"));
        throw new RuntimeException();
    }
}
