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
package org.seasar.junitcdi.jta;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * トランザクション境界を注釈します．
 * <p>
 * このアノテーションはテストメソッドに対して指定することを意図しています． そのためにデフォルトではトランザクションはロールバックされます．
 * </p>
 * 
 * @author koichik
 */
@InterceptorBinding
@Documented
@Target( { TYPE, METHOD })
@Retention(RUNTIME)
public @interface Transactional {
    /**
     * トランザクションをコミットする場合は{@code true}を指定します．
     * 
     * @return トランザクションをコミットする場合は{@code true}
     */
    boolean commit() default false;
}
