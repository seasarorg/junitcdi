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
package org.seasar.junitcdi.validation;

import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seasar.junitcdi.core.runner.CDI;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * BeanValidation (オプション) を使ったテストクラスの例です．
 * 
 * @author koichik
 */
@RunWith(CDI.class)
public class ValidatorTest {
    @Inject
    Validator validator;

    /**
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        assertThat(validator, is(notNullValue()));

        Set<ConstraintViolation<Form>> result = validator.validate(new Form());
        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(1));

        result = validator.validate(new Form("moge"));
        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(0));
    }

    /**
     * @author koichik
     */
    public static class Form {
        /** */
        @NotNull
        String name;

        /**
         * 
         */
        public Form() {
        }

        /**
         * @param name
         */
        public Form(final String name) {
            this.name = name;
        }
    }
}
