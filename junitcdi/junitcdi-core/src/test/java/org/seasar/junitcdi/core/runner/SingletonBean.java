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
package org.seasar.junitcdi.core.runner;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.junit.runner.Description;
import org.seasar.junitcdi.core.event.TestFinished;
import org.seasar.junitcdi.core.event.TestStarted;

/**
 * {@link Singleton}のbean．
 * 
 * @author koichik
 */
@Singleton
public class SingletonBean {
    /** */
    public SingletonBean() {
        System.out.println(toString() + " instanciating.");
    }

    /** */
    @PostConstruct
    public void postConstruct() {
        System.out.println(toString() + " constructed.");
    }

    /** */
    @PreDestroy
    public void preDestroy() {
        System.out.println(toString() + " destructing.");
    }

    /**
     * @param description
     */
    public void onStarted(@Observes @TestStarted final Description description) {
        System.out.println(toString() + " test started. "
            + description.getDisplayName());
    }

    /**
     * @param description
     */
    public void onFinished(@Observes @TestFinished final Description description) {
        System.out.println(toString() + " test finished. "
            + description.getDisplayName());
    }

    /**
     * @param description
     */
    public void onFooStarted(
            @Observes @TestStarted @Foo final Description description) {
        System.out.println(toString() + " test @foo started. "
            + description.getDisplayName());
    }

    /**
     * @param description
     */
    public void onFooFinished(
            @Observes @TestFinished @Foo final Description description) {
        System.out.println(toString() + " test @foo finished. "
            + description.getDisplayName());
    }
}
