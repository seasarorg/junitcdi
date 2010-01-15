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

import java.lang.annotation.Annotation;
import java.util.ArrayList;

import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Qualifier;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.seasar.junitcdi.core.event.TestAssumptionFailure;
import org.seasar.junitcdi.core.event.TestFailure;
import org.seasar.junitcdi.core.event.TestFinished;
import org.seasar.junitcdi.core.event.TestIgnored;
import org.seasar.junitcdi.core.event.TestStarted;

/**
 * JUnitのテストイベントを通知するbeanです．
 * 
 * @author koichik
 */
public class TestEventNotifier extends RunListener {
    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** {@link BeanManager} */
    @Inject
    protected BeanManager beanManager;

    /** {@link Description}型のイベントを通知するBean */
    @Inject
    Event<Description> descriptionEvent;

    /** {@link Failure}型のイベントを通知するBean */
    @Inject
    Event<Failure> failureEvent;

    /**
     * 
     */
    public TestEventNotifier() {
    }

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    @Override
    public void testStarted(final Description description) throws Exception {
        descriptionEvent
            .select(
                getQualifiers(
                    description,
                    new AnnotationLiteral<TestStarted>() {}))
            .fire(description);
    }

    @Override
    public void testFinished(final Description description) throws Exception {
        descriptionEvent
            .select(
                getQualifiers(
                    description,
                    new AnnotationLiteral<TestFinished>() {}))
            .fire(description);
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        failureEvent.select(
            getQualifiers(
                failure.getDescription(),
                new AnnotationLiteral<TestFailure>() {})).fire(failure);
    }

    @Override
    public void testAssumptionFailure(final Failure failure) {
        failureEvent.select(
            getQualifiers(
                failure.getDescription(),
                new AnnotationLiteral<TestAssumptionFailure>() {})).fire(
            failure);
    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        descriptionEvent
            .select(
                getQualifiers(
                    description,
                    new AnnotationLiteral<TestIgnored>() {}))
            .fire(description);
    }

    /**
     * テストクラスおよびテストメソッドに注釈された{@link Qualifier}の配列を返します．
     * <p>
     * 配列の先頭要素は{@literal eventAnnotation}です．
     * </p>
     * 
     * @param description
     *            {@link Description}
     * @param eventAnnotation
     *            イベントのアノテーション
     * @return テストクラスおよびテストメソッドに注釈された{@link Qualifier}の配列
     */
    protected Annotation[] getQualifiers(final Description description,
            final Annotation eventAnnotation) {
        final ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        annotations.add(eventAnnotation);
        for (final Annotation annotation : description.getAnnotations()) {
            if (beanManager.isQualifier(annotation.annotationType())) {
                annotations.add(annotation);
            }
        }
        return annotations.toArray(new Annotation[annotations.size()]);
    }
}
