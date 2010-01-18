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
package org.seasar.junitcdi.jta.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.sql.DataSourceDefinition;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;

import org.seasar.extension.dbcp.impl.ConnectionPoolImpl;
import org.seasar.extension.dbcp.impl.DataSourceImpl;
import org.seasar.extension.dbcp.impl.XADataSourceImpl;

/**
 * beanとして利用可能な{@link DataSource}の抽象クラスです．
 * <p>
 * このDataSourceはS2DBCPをラップしており，S2JTAと連携します．
 * </p>
 * <p>
 * このクラスを利用するにはサブクラスを作成して{@link DataSourceDefinition}で接続情報を注釈します．
 * </p>
 * 
 * <pre>
 * &#x40;DataSourceDefinition(
 *     name = "...", 
 *     className = "...", 
 *     url = "...", 
 *     user = "...",
 *     password = "...")
 * public class DefaultDataSource extends AbstractDataSource {
 * }
 * </pre>
 * 
 * <p>
 * このデータソースはbeanとして他のbeanにDIすることが可能です．
 * </p>
 * 
 * <pre>
 *     &#x40;Inject
 *     DataSource ds;
 * </pre>
 * 
 * <p>
 * 接続情報は以下のように指定します．
 * </p>
 * 
 * <table border="1">
 * <thead>
 * <tr>
 * <th>要素</th>
 * <th>説明</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td>
 * <code>name</code></td>
 * <td>データソースをJNDIに登録する名前です．</td>
 * </tr>
 * <tr>
 * <td>
 * <code>className</code></td>
 * <td>
 * JDBC ドライバのクラス名です．({@link DataSourceDefinition}本来の仕様では {@link DataSource}
 * のクラス名を指定することになっていますが， このクラスではJDBCドライバのクラス名として解釈します)</td>
 * </tr>
 * <tr>
 * <td>
 * <code>url</code></td>
 * <td>接続 URL です．</td>
 * </tr>
 * <tr>
 * <td>
 * <code>name</code></td>
 * <td>ユーザ名です．</td>
 * </tr>
 * <tr>
 * <td>
 * <code>password</code></td>
 * <td>パスワードです．</td>
 * </tr>
 * </tbody>
 * </table>
 * 
 * <p>
 * 複数の{@link DataSource}を使用する場合は{@link AbstractDataSource}のサブクラスを複数作成します．
 * デフォルトで使用されるデータソース (高々一つ) を除いたクラスには，{@link Qualifier qualifier}を指定します．
 * </p>
 * 
 * <pre>
 * &#x40;Qualifier
 * &#x40;Target( { TYPE, FIELD, PARAMETER })
 * &#x40;Retention(RUNTIME)
 * public @interface Hoge {}
 * 
 * &#x40;Hoge
 * &#x40;DataSourceDefinition(...)
 * public class HogeDataSource extends AbstractDataSource {
 * }
 * </pre>
 * 
 * <p>
 * このデータソースを使用する場所も同じ{@link Qualifier}で注釈します．
 * </p>
 * 
 * <pre>
 * &#x40;Inject
 * &#x40;Hoge
 * DataSource dataSource;
 * </pre>
 * 
 * <p>
 * {@link DataSource}のスコープは{@link ApplicationScoped}であり，
 * スコープが破棄される際に自動的にクローズされます．
 * </p>
 * 
 * @author koichik
 */
@ApplicationScoped
public abstract class AbstractDataSource implements DataSource {
    // /////////////////////////////////////////////////////////////////
    // instance fields
    //
    /** {@link BeanManager} */
    @Inject
    protected BeanManager beanManager;

    /** 連携する{@link TransactionManager} */
    @Inject
    TransactionManager transactionManager;

    /** コネクションプール */
    protected DataSourceImpl dataSource;

    // /////////////////////////////////////////////////////////////////
    // methods
    //
    /**
     * コネクションプールをオープンします．
     * 
     * @throws Exception
     *             例外が発生した場合
     */
    @PostConstruct
    public void open() throws Exception {
        final DataSourceDefinition definition =
            getClass().getAnnotation(DataSourceDefinition.class);
        final ConnectionPoolImpl pool = new ConnectionPoolImpl();
        pool.setTransactionManager(transactionManager);
        pool.setXADataSource(createXADataSource(definition));
        dataSource = new DataSourceImpl(pool);
    }

    /**
     * コネクションプールをクローズします．
     */
    @PreDestroy
    public void close() {
        (dataSource).getConnectionPool().close();
    }

    /**
     * {@link DataSourceDefinition}の情報から{@link XADataSource}を作成して返します．
     * 
     * @param definition
     *            接続情報
     * @return {@link XADataSource}
     * @throws SQLException
     *             SQL例外が発生した場合
     */
    protected XADataSource createXADataSource(
            final DataSourceDefinition definition) throws SQLException {
        final XADataSourceImpl xaDataSource = new XADataSourceImpl();
        xaDataSource.setDriverClassName(definition.className());
        xaDataSource.setURL(definition.url());
        xaDataSource.setUser(definition.user());
        xaDataSource.setPassword(definition.password());
        xaDataSource.setLoginTimeout(definition.loginTimeout());
        return xaDataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public Connection getConnection(final String username, final String password)
            throws SQLException {
        return dataSource.getConnection(username, password);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getLogWriter();
    }

    @Override
    public void setLoginTimeout(final int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }

    @Override
    public void setLogWriter(final PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        throw new SQLException();
    }
}
