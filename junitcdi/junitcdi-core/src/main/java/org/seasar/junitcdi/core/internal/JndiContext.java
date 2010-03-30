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
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * JNDI {@link Context}の単純な実装です．
 * 
 * @author koichik
 */
public class JndiContext implements Context {
    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** 名前にバインドされたオブジェクトのマップ */
    protected final ConcurrentHashMap<String, Object> map =
        new ConcurrentHashMap<String, Object>();

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    @Override
    public Object lookup(final Name name) throws NamingException {
        return lookup(name.toString());
    }

    @Override
    public Object lookup(final String name) throws NamingException {
        final BeanManager beanManager = BeanManagerHelper.getBeanManager();
        if (name.equals("java:comp/BeanManager")) {
            return beanManager;
        }
        final Object obj = map.get(name);
        if (obj != null) {
            if (obj instanceof Bean<?>) {
                final Bean<?> bean = (Bean<?>) obj;
                return beanManager.getReference(
                    bean,
                    bean.getBeanClass(),
                    beanManager.createCreationalContext(bean));
            }
            return obj;
        }
        final String baseName;
        if (name.startsWith("java:comp/env/")) {
            baseName = name.substring("java:comp/env/".length());
        } else if (name.startsWith("java:comp/")) {
            baseName = name.substring("java:comp/".length());
        } else {
            baseName = name;
        }
        try {
            return BeanManagerHelper.getBeanInstance(baseName);
        } catch (NoSuchElementException e) {
            throw (NamingException) new NameNotFoundException(baseName)
                .initCause(e);
        }
    }

    @Override
    public void bind(final Name name, final Object obj) throws NamingException {
        bind(name.toString(), obj);
    }

    @Override
    public void bind(final String name, final Object obj)
            throws NamingException {
        if (map.putIfAbsent(name, obj) != null) {
            throw new NameAlreadyBoundException();
        }
    }

    @Override
    public void rebind(final Name name, final Object obj)
            throws NamingException {
        rebind(name.toString(), obj);
    }

    @Override
    public void rebind(final String name, final Object obj)
            throws NamingException {
        map.remove(name, obj);
    }

    @Override
    public void unbind(final Name name) throws NamingException {
        unbind(name.toString());
    }

    @Override
    public void unbind(final String name) throws NamingException {
        if (map.remove(name) == null) {
            throw new NameNotFoundException();
        }
    }

    @Override
    public void rename(final Name oldName, final Name newName)
            throws NamingException {
        rename(oldName.toString(), newName.toString());
    }

    @Override
    public void rename(final String oldName, final String newName)
            throws NamingException {
        if (map.putIfAbsent(newName, lookup(oldName)) != null) {
            throw new NameAlreadyBoundException();
        }
    }

    @Override
    public void close() throws NamingException {
        map.clear();
    }

    // /////////////////////////////////////////////////////////////////
    // methods (unsupported operations)
    //
    @Override
    public NamingEnumeration<NameClassPair> list(final Name name)
            throws NamingException {
        throw new UnsupportedOperationException("list(Name)");
    }

    @Override
    public NamingEnumeration<NameClassPair> list(final String name)
            throws NamingException {
        throw new UnsupportedOperationException("list(String)");
    }

    @Override
    public NamingEnumeration<Binding> listBindings(final Name name)
            throws NamingException {
        throw new UnsupportedOperationException("listBindings(Name)");
    }

    @Override
    public NamingEnumeration<Binding> listBindings(final String name)
            throws NamingException {
        throw new UnsupportedOperationException("listBindings(String)");
    }

    @Override
    public void destroySubcontext(final Name name) throws NamingException {
        throw new UnsupportedOperationException("destroySubcontext(Name)");
    }

    @Override
    public void destroySubcontext(final String name) throws NamingException {
        throw new UnsupportedOperationException("destroySubcontext(String)");
    }

    @Override
    public Context createSubcontext(final Name name) throws NamingException {
        throw new UnsupportedOperationException("createSubcontext(Name)");
    }

    @Override
    public Context createSubcontext(final String name) throws NamingException {
        throw new UnsupportedOperationException("createSubcontext(String)");
    }

    @Override
    public Object lookupLink(final Name name) throws NamingException {
        throw new UnsupportedOperationException("lookupLink(Name)");
    }

    @Override
    public Object lookupLink(final String name) throws NamingException {
        throw new UnsupportedOperationException("lookupLink(String)");
    }

    @Override
    public NameParser getNameParser(final Name name) throws NamingException {
        throw new UnsupportedOperationException("getNameParser(Name)");
    }

    @Override
    public NameParser getNameParser(final String name) throws NamingException {
        throw new UnsupportedOperationException("getNameParser(String)");
    }

    @Override
    public Name composeName(final Name name, final Name prefix)
            throws NamingException {
        throw new UnsupportedOperationException("composeName(Name, Name)");
    }

    @Override
    public String composeName(final String name, final String prefix)
            throws NamingException {
        throw new UnsupportedOperationException("composeName(String, String)");
    }

    @Override
    public Object addToEnvironment(final String propName, final Object propVal)
            throws NamingException {
        throw new UnsupportedOperationException(
            "addToEnvironment(String, Object)");
    }

    @Override
    public Object removeFromEnvironment(final String propName)
            throws NamingException {
        throw new UnsupportedOperationException("removeFromEnvironment(String)");
    }

    @Override
    public Hashtable<?, ?> getEnvironment() throws NamingException {
        throw new UnsupportedOperationException("getEnvironment()");
    }

    @Override
    public String getNameInNamespace() throws NamingException {
        throw new UnsupportedOperationException("getNameInNamespace()");
    }
}
