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
import javax.enterprise.event.ObserverException;
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
import org.seasar.junitcdi.core.event.TestInfo;
import org.seasar.junitcdi.core.event.TestMethodFinished;
import org.seasar.junitcdi.core.event.TestMethodStarted;
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

    /** {@link TestObject}型のイベントを通知するBean */
    @Inject
    Event<TestInfo> testContextEvent;

    // /////////////////////////////////////////////////////////////////
    // methods from RunListener
    //
    /**
     * テストが開始したことを通知します．
     * 
     * @param description
     *            テストの記述
     * @throws Exception
     *             オブザーバーが例外をスローした場合
     */
    @Override
    public void testStarted(final Description description) throws Exception {
        try {
            descriptionEvent.select(
                getQualifiers(
                    description,
                    new AnnotationLiteral<TestStarted>() {})).fire(description);
        } catch (final ObserverException e) {
            rethrow(e);
        }
    }

    /**
     * テストが終了したことを通知します．
     * 
     * @param description
     *            テストの記述
     * @throws Exception
     *             オブザーバーが例外をスローした場合
     */
    @Override
    public void testFinished(final Description description) throws Exception {
        try {
            descriptionEvent
                .select(
                    getQualifiers(
                        description,
                        new AnnotationLiteral<TestFinished>() {}))
                .fire(description);
        } catch (final ObserverException e) {
            rethrow(e);
        }
    }

    /**
     * テストが失敗したことを通知します．
     * 
     * @param failure
     *            失敗の記述
     * @throws Exception
     *             オブザーバーが例外をスローした場合
     */
    @Override
    public void testFailure(final Failure failure) throws Exception {
        try {
            failureEvent.select(
                getQualifiers(
                    failure.getDescription(),
                    new AnnotationLiteral<TestFailure>() {})).fire(failure);
        } catch (final ObserverException e) {
            rethrow(e);
        }
    }

    /**
     * テストの前提が満たされていなかったことを通知します．
     * 
     * @param failure
     *            失敗の記述
     */
    @Override
    public void testAssumptionFailure(final Failure failure) {
        failureEvent.select(
            getQualifiers(
                failure.getDescription(),
                new AnnotationLiteral<TestAssumptionFailure>() {})).fire(
            failure);
    }

    /**
     * テストが無視したことを通知します．
     * 
     * @param description
     *            テストの記述
     * @throws Exception
     *             オブザーバーが例外をスローした場合
     */
    @Override
    public void testIgnored(final Description description) throws Exception {
        try {
            descriptionEvent.select(
                getQualifiers(
                    description,
                    new AnnotationLiteral<TestIgnored>() {})).fire(description);
        } catch (final ObserverException e) {
            rethrow(e);
        }
    }

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    /**
     * テストメソッドの実行が開始されることを通知します．
     * 
     * @param testInfo
     *            テストの情報
     * @throws Exception
     *             オブザーバーが例外をスローした場合
     */
    public void testMethodStarted(final TestInfo testInfo) throws Exception {
        try {
            testContextEvent.select(
                getQualifiers(
                    testInfo.getDescription(),
                    new AnnotationLiteral<TestMethodStarted>() {})).fire(
                testInfo);
        } catch (final ObserverException e) {
            rethrow(e);
        }
    }

    /**
     * テストメソッドの実行が終了したことを通知します．
     * 
     * @param testInfo
     *            テストの情報
     * @throws Exception
     *             オブザーバーが例外をスローした場合
     */
    public void testMethodFinished(final TestInfo testInfo) throws Exception {
        try {
            testContextEvent.select(
                getQualifiers(
                    testInfo.getDescription(),
                    new AnnotationLiteral<TestMethodFinished>() {})).fire(
                testInfo);
        } catch (final ObserverException e) {
            rethrow(e);
        }
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

    /**
     * オブザーバーがスローした例外をスローします．
     * 
     * @param e
     *            オブザーバーがスローした例外をラップした例外
     * @throws Exception
     *             オブザーバーがスローした例外
     */
    protected void rethrow(final ObserverException e) throws Exception {
        Throwable t = e.getCause();
        if (t instanceof Exception) {
            throw (Exception) e.getCause();
        } else if (t instanceof Error) {
            throw (Error) t;
        }
        throw e;
    }
}
