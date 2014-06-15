/*
 * Copyright 2013 The Solmix Project
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package org.solmix.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年6月15日
 */

public class DriverManagerDataSource implements DataSource
{

    protected PrintWriter _logWriter = null;

    private final String jdbcUrl;

    private final boolean credentialsInURL;

    private final String userName;

    private final String password;

    DriverManagerDataSource(String jdbcUrl, String userName, String password,
        boolean credentialsInURL)
    {
        this.jdbcUrl = jdbcUrl;
        this.userName = userName;
        this.password = password;
        this.credentialsInURL = credentialsInURL;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.sql.CommonDataSource#getLogWriter()
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return _logWriter;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.sql.CommonDataSource#setLogWriter(java.io.PrintWriter)
     */
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        _logWriter = out;

    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.sql.CommonDataSource#setLoginTimeout(int)
     */
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException(
            "Login timeout is not supported.");
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.sql.CommonDataSource#getLoginTimeout()
     */
    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException(
            "Login timeout is not supported.");
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.sql.Wrapper#unwrap(java.lang.Class)
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("JdbcDataSource is not a wrapper.");
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.sql.DataSource#getConnection()
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (credentialsInURL)
            return DriverManager.getConnection(jdbcUrl);
        else
            return DriverManager.getConnection(jdbcUrl, userName, password);
    }

    /**
     * {@inheritDoc}
     * 
     * @see javax.sql.DataSource#getConnection(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public Connection getConnection(String username, String password)
        throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

}
