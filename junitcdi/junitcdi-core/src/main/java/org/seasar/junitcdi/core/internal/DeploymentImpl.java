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

import java.util.Collection;

import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.bootstrap.spi.Deployment;

import static java.util.Arrays.*;

/**
 * JUnit環境における{@link Deployment}の実装クラスです．
 * 
 * @author koichik
 */
public class DeploymentImpl implements Deployment {
    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** 唯一の{@link BeanDeploymentArchive} */
    protected final BeanDeploymentArchive beanDeploymentArchive =
        new BeanDeploymentArchiveImpl();

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    @Override
    public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
        return asList(beanDeploymentArchive);
    }

    @Override
    public ServiceRegistry getServices() {
        return beanDeploymentArchive.getServices();
    }

    @Override
    public BeanDeploymentArchive loadBeanDeploymentArchive(Class<?> beanClass) {
        return beanDeploymentArchive;
    }
}
