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
package org.seasar.junitcdi.core.internal;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.bean.builtin.ExtensionBean;
import org.jboss.weld.context.api.BeanStore;
import org.jboss.weld.context.api.helpers.ConcurrentHashMapBeanStore;
import org.jboss.weld.serialization.spi.helpers.SerializableContextual;

/**
 * {@link #cliear()}が呼ばれても{@link Extension}を削除せずに維持する{@link BeanStore}です．
 * 
 * @author koichik
 */
final class KeepExtensionBeanStore extends ConcurrentHashMapBeanStore {
    // /////////////////////////////////////////////////////////////////
    // methods
    //
    @Override
    public void clear() {
        for (final String id : getContextualIds()) {
            Contextual<?> contextual = get(id).getContextual();
            if (contextual instanceof SerializableContextual<?, ?>) {
                contextual = ((SerializableContextual<?, ?>) contextual).get();
            }
            if (contextual instanceof ExtensionBean) {
                continue;
            }
            delegate().remove(id);
        }
    }
}
