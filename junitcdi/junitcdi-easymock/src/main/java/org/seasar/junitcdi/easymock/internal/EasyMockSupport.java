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
package org.seasar.junitcdi.easymock.internal;

/**
 * EasyMockとの対話をサポートします。
 * <p>
 * EasyMockClassExtensionが使えない環境では{@link org.easymock.EasyMock}， 使える環境では
 * {@link org.easymock.classextension.EasyMock}を 切り替えて利用するためのファサードです．
 * </p>
 * 
 * @author koichik
 */
public class EasyMockSupport {
    // /////////////////////////////////////////////////////////////////
    // static fields
    //
    /** EasyMockの実装に委譲するオブジェクト */
    protected static final EasyMockDelegator easyMock = getEasyMockDelegator();

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
        return easyMock.createMock(clazz);
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
        return easyMock.createNiceMock(clazz);
    }

    /**
     * Strictモードのモックを作成します。
     * 
     * @param <T>
     *            モックの型
     * @param clazz
     *            モックの対象となるクラス
     * @return 作成された
     */
    public static <T> T createStrictMock(final Class<T> clazz) {
        return easyMock.createStrictMock(clazz);
    }

    /**
     * このオブジェクトで管理するすべてのモックをreplayモードに設定します。
     * 
     * @param mock
     *            モックオブジェクト
     */
    public static void replay(final Object mock) {
        easyMock.replay(mock);
    }

    /**
     * このオブジェクトで管理するすべてのモックとのインタラクションを検証します。
     * 
     * @param mock
     *            モックオブジェクト
     */
    public static void verify(final Object mock) {
        easyMock.verify(mock);
    }

    /**
     * このオブジェクトで管理するすべてのモックをリセットします。
     * 
     * @param mock
     *            モックオブジェクト
     */
    public static void reset(final Object mock) {
        easyMock.reset(mock);
    }

    /**
     * {@link EasyMockDelegator}の実装クラスを返します。
     * 
     * @return {@link EasyMockDelegator}の実装クラス
     */
    protected static EasyMockDelegator getEasyMockDelegator() {
        try {
            Class.forName("org.easymock.classextension.EasyMock");
            Class.forName("net.sf.cglib.proxy.Enhancer");
            return new ClassExtensionEacyMockDelegator();
        } catch (final Throwable ignore) {
        }
        return new DefaultEacyMockDelegator();
    }
}
