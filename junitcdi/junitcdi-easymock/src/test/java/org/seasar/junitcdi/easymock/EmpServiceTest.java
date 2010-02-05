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

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

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
public class EmpServiceTest {
    @Produces
    @EasyMock
    @AlternativeMock
    EmpDao dao;

    @Inject
    EmpService service;

    /**
     * @throws Exception
     */
    @BeforeMethod
    public void beforeTest() throws Exception {
        expect(dao.findByName("hoge")).andReturn(new Emp("hoge"));
    }

    /**
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        Emp emp = service.findByName("hoge");
        assertThat(emp, is(not(nullValue())));
        assertThat(emp.name, is(equalTo("hoge")));
    }
}
