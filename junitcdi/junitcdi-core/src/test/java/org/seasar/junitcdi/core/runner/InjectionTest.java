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
package org.seasar.junitcdi.core.runner;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.junitcdi.core.AfterMethod;
import org.seasar.junitcdi.core.BeforeMethod;

import static junit.framework.Assert.*;

/**
 * JUnit CDI extensionsを利用したテストクラスの例です．
 * 
 * @author koichik
 */
@RunWith(CDI.class)
public class InjectionTest {

    @Inject
    SingletonBean singletonBean;

    @Inject
    TestClassScopedBean testClassScopedBean;

    @Inject
    BeanManager beanManager;

    /** */
    public InjectionTest() {
        System.out.println(toString() + " instanciating.");
    }

    /** */
    @PostConstruct
    public void postConstruct() {
        System.out.println(toString() + " constructed.");
        System.out.println(singletonBean);
        System.out.println(testClassScopedBean);
    }

    /** */
    @Before
    public void before() {
        System.out.println(toString() + " before test.");
    }

    /** */
    @BeforeMethod
    public void beforeテスト() {
        System.out.println(toString() + " before test method(テスト).");
    }

    /** */
    @Test
    public void テスト() {
        System.out.println(toString() + " run test method(テスト).");
        assertNotNull(singletonBean);
        assertNotNull(testClassScopedBean);
        assertNotNull(beanManager);
    }

    /** */
    @AfterMethod
    public void afterテスト() {
        System.out.println(toString() + " after test method(テスト).");
    }

    /** */
    @BeforeMethod("foo")
    public void bar() {
        System.out.println(toString() + " before test method(foo).");
    }

    /** */
    @Test
    @Foo
    public void foo() {
        System.out.println(toString() + " run test method(foo).");
    }

    /** */
    @AfterMethod("foo")
    public void baz() {
        System.out.println(toString() + " after test method(foo).");
    }

    /** */
    @After
    public void after() {
        System.out.println(toString() + " after test.");
    }

    /** */
    @PreDestroy
    public void preDestroy() {
        System.out.println(toString() + " destructing.");
    }
}
