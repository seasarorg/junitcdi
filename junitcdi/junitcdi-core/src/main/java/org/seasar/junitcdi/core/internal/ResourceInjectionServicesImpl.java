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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.naming.Context;

import org.jboss.weld.injection.spi.ResourceInjectionServices;
import org.jboss.weld.injection.spi.helpers.AbstractResourceServices;

/**
 * {@link Resource}で注釈されたフィールドまたはプロパティDIされるオブジェクトを解決するクラスです．
 * 
 * @author koichik
 */
public class ResourceInjectionServicesImpl extends AbstractResourceServices
        implements ResourceInjectionServices {
    // /////////////////////////////////////////////////////////////////
    // constants
    //
    /** Javaコンポーネントのプレフィックス */
    protected static final String JAVA_NAMESPACE_PREFIX = "java:";

    /** JNDI環境名のプレフィックス */
    protected static final String ENV_NAMESPACE_PREFIX = "java:comp/env/";

    // /////////////////////////////////////////////////////////////////
    // static fields
    //
    /** 組み込みコンポーネントの型とJNDI名のマップ */
    protected static final Map<Class<?>, String> builtins =
        new HashMap<Class<?>, String>();
    static {
        try {
            builtins.put(BeanManager.class, "java:comp/BeanManager");
            builtins.put(
                Class.forName("javax.transaction.UserTransaction"),
                "java:comp/UserTransaction");
            builtins
                .put(
                    Class
                        .forName("javax.transaction.TransactionSynchronizationRegistry"),
                    "java:comp/TransactionSynchronizationRegistry");
        } catch (final Throwable ignore) {
        }
    }

    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** JNDIコンテキスト */
    protected final Context context = new JndiContext();

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    @Override
    protected Context getContext() {
        return context;
    }

    @Override
    protected String getResourceName(final InjectionPoint injectionPoint) {
        final Resource resource =
            injectionPoint.getAnnotated().getAnnotation(Resource.class);
        final String mappedName = resource.mappedName();
        if (!mappedName.equals("")) {
            return mappedName;
        }
        final String name = resource.name();
        if (!name.equals("")) {
            if (name.startsWith(JAVA_NAMESPACE_PREFIX)) {
                return name;
            }
            return ENV_NAMESPACE_PREFIX + name;
        }
        final Type type = injectionPoint.getType();
        if (type instanceof Class<?>) {
            @SuppressWarnings("cast")
            final String builtin = builtins.get((Class<?>) type);
            if (builtin != null) {
                return builtin;
            }
        }
        return super.getResourceName(injectionPoint);
    }
}
