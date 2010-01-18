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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.spi.InitialContextFactory;

import org.jboss.weld.BeanManagerImpl;
import org.jboss.weld.injection.spi.ResourceInjectionServices;

/**
 * {@link JndiContext}のファクトリです．
 * 
 * @author koichik
 */
public class JndiContextFactory implements InitialContextFactory {
    /**
     * 現在のスレッドに関連づけられているCDI環境からJNDI {@link Context}を取得して返します．
     * 
     * @return 現在のスレッドに関連づけられているCDI環境のJNDI {@link Context}
     */
    public static Context getContext() {
        final ResourceInjectionServices resourceInjectionServices =
            ((BeanManagerImpl) BeanManagerHelper.getBeanManager())
                .getServices()
                .get(ResourceInjectionServices.class);
        return ((ResourceInjectionServicesImpl) resourceInjectionServices)
            .getContext();
    }

    @Override
    public Context getInitialContext(final Hashtable<?, ?> env) {
        return getContext();
    }
}
