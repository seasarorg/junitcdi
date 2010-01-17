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

/**
 * EasyMockによってモックを作成し，状態を制御するbeanのインタフェースです．
 * <p>
 * テストメソッドでモックを作成したり，記録・再生・検証モードの切り替えを制御したい場合に利用します．
 * テストメソッドが呼び出された時点では再生モードになっているので，記録モードから始めたい場合は最初に{@link #reset()}
 * メソッドを呼び出してください．
 * </p>
 * 
 * @author koichik
 */
public interface EasyMockController {
    // /////////////////////////////////////////////////////////////////
    // methods
    //
    /**
     * デフォルトのモックを作成します。
     * <p>
     * 作成されたモックはこのbeanで管理されます．
     * </p>
     * 
     * @param <T>
     *            モックの型
     * @param clazz
     *            モックの対象とするクラス
     * @return 作成されたモック
     */
    <T> T createMock(final Class<T> clazz);

    /**
     * Niceモードのモックを作成します。
     * <p>
     * 作成されたモックはこのbeanで管理されます．
     * </p>
     * 
     * @param <T>
     *            モックの型
     * @param clazz
     *            モックの対象となるクラス
     * @return 作成されたモック
     */
    <T> T createNiceMock(final Class<T> clazz);

    /**
     * Strictモードのモックを作成します。
     * <p>
     * 作成されたモックはこのbeanで管理されます．
     * </p>
     * 
     * @param <T>
     *            モックの型
     * @param clazz
     *            モックの対象となるクラス
     * @return 作成された
     */
    <T> T createStrictMock(final Class<T> clazz);

    /**
     * このbeanで管理するすべてのモックを再生モードに設定します。
     */
    void replay();

    /**
     * このbeanで管理するすべてのモックとのインタラクションを検証します。
     * 
     */
    void verify();

    /**
     * このbeanで管理するすべてのモックをリセットし，記録モードに戻します。
     */
    void reset();
}
