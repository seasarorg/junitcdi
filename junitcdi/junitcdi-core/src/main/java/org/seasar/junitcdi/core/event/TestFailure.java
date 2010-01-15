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
package org.seasar.junitcdi.core.event;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * テストメソッドが失敗したイベントを受け取るための注釈です．
 * <p>
 * {@link org.junit.runner.notification.Failure}型のパラメータに注釈します．
 * </p>
 * 
 * <pre>
 * public void onTestFailure(&#x40;Observes &#x40;TestFailure Failure failure) {...}
 * </pre>
 * 
 * @see org.junit.runner.notification.RunListener#testFailure(org.junit.runner.notification.Failure)
 * @author koichik
 */
@Qualifier
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface TestFailure {
}
