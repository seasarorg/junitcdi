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

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jboss.weld.bootstrap.api.ServiceRegistry;
import org.jboss.weld.bootstrap.api.helpers.SimpleServiceRegistry;
import org.jboss.weld.bootstrap.spi.BeanDeploymentArchive;
import org.jboss.weld.ejb.spi.EjbDescriptor;
import org.seasar.junitcdi.core.exception.BeanDeploymentFailedException;

import static java.util.Collections.*;

/**
 * JUnit環境における{@link BeanDeploymentArchive}の実装クラスです．
 * 
 * @author koichik
 */
public class BeanDeploymentArchiveImpl implements BeanDeploymentArchive {
    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** サービスのレジストリ */
    protected final ServiceRegistry serviceRegistry =
        new SimpleServiceRegistry();

    /** bean クラスの{@link Set} */
    protected final Set<Class<?>> beanClasses = new HashSet<Class<?>>();

    /** {@code beans.xml}の{@link Set} */
    protected final Set<URL> beansXmls = new HashSet<URL>();

    // /////////////////////////////////////////////////////////////////
    // constructors
    //
    /**
     * インスタンスを構築します．
     */
    public BeanDeploymentArchiveImpl() {
        scan();
    }

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    @Override
    public Collection<Class<?>> getBeanClasses() {
        return beanClasses;
    }

    @Override
    public Collection<BeanDeploymentArchive> getBeanDeploymentArchives() {
        return emptySet();
    }

    @Override
    public Collection<URL> getBeansXml() {
        return beansXmls;
    }

    @Override
    public Collection<EjbDescriptor<?>> getEjbs() {
        return emptySet();
    }

    @Override
    public String getId() {
        return "junit-cdi";
    }

    @Override
    public ServiceRegistry getServices() {
        return serviceRegistry;
    }

    /**
     * {@code beans.xml}ファイルが存在するディレクトリまたはJarファイルからbeanクラスをスキャンします．
     */
    protected void scan() {
        try {
            final ClassLoader loader =
                Thread.currentThread().getContextClassLoader();
            final Enumeration<URL> urls =
                loader.getResources("META-INF/beans.xml");
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                final String protocol = url.getProtocol();
                if (protocol.equals("file")) {
                    final String path =
                        URLDecoder.decode(url.getPath(), "UTF-8");
                    final File file = new File(path);
                    final File baseDir = file.getParentFile().getParentFile();
                    beansXmls.add(url);
                    scanDirectory(loader, baseDir, "");
                } else if (protocol.equals("jar")) {
                    final URL fileUrl = new URL(url.getPath());
                    final String path = fileUrl.getPath();
                    final String jarFilePath =
                        path.substring(0, path.lastIndexOf("!"));
                    final File jarFile =
                        new File(URLDecoder.decode(jarFilePath, "UTF-8"));
                    beansXmls.add(url);
                    scanJar(loader, jarFile.getAbsoluteFile());
                } else {
                }
            }
        } catch (final Exception e) {
            throw new BeanDeploymentFailedException(e);
        }
    }

    /**
     * ディレクトリに含まれるbeanクラスをスキャンします．
     * 
     * @param loader
     *            クラスローダ
     * @param directory
     *            ディレクトリ
     * @param path
     *            基点となるディレクトリからのパス
     */
    protected void scanDirectory(final ClassLoader loader,
            final File directory, final String path) {
        for (final File child : directory.listFiles()) {
            final String name = child.getName();
            if (child.isDirectory()) {
                scanDirectory(loader, child, path + name + "/");
            } else if (name.endsWith(".class")) {
                try {
                    final String className =
                        (path + name.substring(0, name.lastIndexOf(".class")))
                            .replace('/', '.');
                    beanClasses.add(loader.loadClass(className));
                } catch (final Exception e) {
                    throw new BeanDeploymentFailedException(e);
                }
            }
        }
    }

    /**
     * Jarファイルに含まれるbeanクラスをスキャンします．
     * 
     * @param loader
     *            クラスローダ
     * @param file
     *            Jarファイル
     */
    protected void scanJar(final ClassLoader loader, final File file) {
        try {
            final JarFile jarFile = new JarFile(file);
            try {
                final Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    final JarEntry entry = entries.nextElement();
                    final String name = entry.getName();
                    if (name.endsWith(".class")) {
                        final String className =
                            (name.substring(0, name.lastIndexOf(".class")))
                                .replace('/', '.');
                        beanClasses.add(loader.loadClass(className));
                    }
                }
            } finally {
                jarFile.close();
            }
        } catch (final Exception e) {
            throw new BeanDeploymentFailedException(e);
        }
    }
}
