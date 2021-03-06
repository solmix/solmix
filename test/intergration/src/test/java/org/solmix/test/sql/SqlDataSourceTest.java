/*
 *  Copyright 2012 The Solmix Project
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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.solmix.runtime.SystemContext;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.fmk.SlxContext;
import org.solmix.jpa.JPADataSource;
import org.solmix.sql.ConnectionManager;
import org.solmix.sql.SQLDataSource;
import org.solmix.test.SolmixTestCase;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-10
 */

public class SqlDataSourceTest extends SolmixTestCase
{

    @Test
    public void getSqlDataSource() {
        SystemContext sc=SlxContext.getSystemContext();
        DataSourceManager dsm= sc.getExtension(DataSourceManager.class);
       List<DataSource> dss=dsm.getProviders();
       for(DataSource ds:dss){
           if(ds.getServerType().equals("sql")){
               Assert.assertTrue(ds instanceof SQLDataSource);
           }else if(ds.getServerType().equals("jpa")){
               Assert.assertTrue(ds instanceof JPADataSource);
           }
       }
       Assert.assertTrue(dss.size()>1);
    }
    @Test
    public void assertConnectionManagerIsSame(){
        SystemContext sc=SlxContext.getSystemContext();
        DataSourceManager dsm= sc.getExtension(DataSourceManager.class);
       List<DataSource> dss=dsm.getProviders();
       for(DataSource ds:dss){
           if(ds.getServerType().equals("sql")){
              SQLDataSource sds = (SQLDataSource)ds;
              ConnectionManager original= sc.getExtension(ConnectionManager.class);
              Assert.assertSame(sds.getConnectionManager(), original);
           }
       }
    }

}
