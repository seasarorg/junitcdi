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

import org.seasar.junitcdi.easymock.internal.EasyMockSupport;

/**
 * モックオブジェクトを作成します．
 * <p>
 * このクラスで作成されたモックオブジェクトは，{@link EasyMock} アノテーションで注釈されたフィールドに設定されるモックと異なり，
 * {@link EasyMockController}で管理されません．
 * </p>
 * 
 * @author koichik
 */
public class EasyMockCreator {
    // /////////////////////////////////////////////////////////////////
    // methods
    //
    /**
     * デフォルトのモックを作成します。
     * 
     * @param <T>
     *            モックの型
     * @param clazz
     *            モックの対象とするクラス
     * @return 作成されたモック
     */
    public static <T> T createMock(final Class<T> clazz) {
        return EasyMockSupport.createMock(clazz);
    }

    /**
     * Niceモードのモックを作成します。
     * 
     * @param <T>
     *            モックの型
     * @param clazz
     *            モックの対象となるクラス
     * @return 作成されたモック
     */
    public static <T> T createNiceMock(final Class<T> clazz) {
        return EasyMockSupport.createNiceMock(clazz);
    }

    /**
     * Strictモードのモックを作成します。
     * 
     * @param <T>
     *            モックの型
     * @param clazz
     *            モックの対象となるクラス
     * @return 作成されたモック
     */
    public static <T> T createStrictMock(final Class<T> clazz) {
        return EasyMockSupport.createStrictMock(clazz);
    }
}
