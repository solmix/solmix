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
package org.solmix.test.sql;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.Test;
import org.solmix.api.exception.SlxException;
import org.solmix.commons.util.Assert;
import org.solmix.fmk.SlxContext;
import org.solmix.runtime.SystemContext;
import org.solmix.sql.ConnectionManager;
import org.solmix.test.SolmixTestCase;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年6月15日
 */

public class ConnectionManagetTest extends SolmixTestCase
{
    @Test
    public void testConnectionPool() {
        SystemContext sc=  SlxContext.getSystemContext();
        ConnectionManager cm= sc.getBean(ConnectionManager.class);
        try {
            String dbName=this.getClass().getSimpleName();
            Connection conn= cm.getConnection(dbName);
            Assert.isNotNull(conn);
            DataSource ds =cm.getDataSource(dbName);
            Assert.isNotNull(ds);
        } catch (SlxException e) {
            e.printStackTrace();
        }
        
    }
}
