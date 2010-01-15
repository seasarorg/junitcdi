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
package org.seasar.junitcdi.core;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.NormalScope;
import javax.inject.Singleton;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * テストクラス・スコープのbeanに注釈します．
 * <p>
 * テストクラス・スコープは，一つのテストクラスのメソッドが実行されている間，そのスレッドでだけ有効です．
 * テストクラスに複数のテストメソッドがある場合，それぞれのテストメソッドを実行している間は一つのテストクラススコープが有効になります．
 * </p>
 * <p>
 * CDIコンテナはテストメソッドごとに作られます． このため，テストクラス・スコープは{@link Singleton}や
 * {@link ApplicationScoped}よりも長いスコープになります．
 * </p>
 * 
 * @author koichik
 */
@NormalScope
@Inherited
@Documented
@Target( { TYPE, METHOD, FIELD })
@Retention(RUNTIME)
public @interface TestClassScoped {
}
