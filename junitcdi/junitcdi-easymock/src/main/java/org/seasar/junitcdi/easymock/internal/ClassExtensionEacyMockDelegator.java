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

import org.easymock.classextension.EasyMock;

/**
 * EasyMock Class ExtensionのEasyMockに委譲する{@link EasyMockDelegator}の実装クラスです．
 * 
 * @author koichik
 */
public class ClassExtensionEacyMockDelegator implements EasyMockDelegator {
    // /////////////////////////////////////////////////////////////////
    // methods
    //
    public <T> T createMock(final Class<T> clazz) {
        return EasyMock.createMock(clazz);
    }

    public <T> T createNiceMock(final Class<T> clazz) {
        return EasyMock.createNiceMock(clazz);
    }

    public <T> T createStrictMock(final Class<T> clazz) {
        return EasyMock.createStrictMock(clazz);
    }

    public void replay(final Object... mocks) {
        EasyMock.replay(mocks);
    }

    public void reset(final Object... mocks) {
        EasyMock.reset(mocks);
    }

    public void verify(final Object... mocks) {
        EasyMock.verify(mocks);
    }
}
