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
package org.seasar.junitcdi.validation.internal;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.jboss.weld.validation.spi.ValidationServices;

/**
 * デフォルトの{@ValidatorFactory}を提供する{@link ValidationServices}
 * の実装です．
 * 
 * @author koichik
 */
public class ValidationServicesImpl implements ValidationServices {
    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** デフォルトの{@link ValidatorFactory} */
    protected final ValidatorFactory factory =
        Validation.buildDefaultValidatorFactory();

    // /////////////////////////////////////////////////////////////////
    // methods from ValidationServices
    //
    @Override
    public void cleanup() {
    }

    @Override
    public ValidatorFactory getDefaultValidatorFactory() {
        return factory;
    }
}
