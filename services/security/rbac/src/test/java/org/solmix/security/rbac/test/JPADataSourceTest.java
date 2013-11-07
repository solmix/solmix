/*
 * SOLMIX PROJECT
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

package org.solmix.security.rbac.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.solmix.api.data.DataSourceData;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.fmk.datasource.DSRequestImpl;
import org.solmix.jpa.JPADataSource;
import org.springframework.context.ApplicationContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-10-21
 */

public class JPADataSourceTest
{

    ApplicationContext ctx;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        // Mocks.initContext();
        //System.setProperty("solmix.base", "web-root");
        //ctx = new ClassPathXmlApplicationContext("spring-jpa.xml");
    }

//    @Test
    public void test() throws SlxException {
       DataSource ds =mockds();
        DSRequestImpl dsreq = new DSRequestImpl(ds, Eoperation.FETCH);
        DSResponse res = ds.execute(dsreq);
        System.out.println(res.getContext().getData());
        
        ds=mockds();
        DSRequestImpl add = new DSRequestImpl(ds, Eoperation.ADD);
        Map values = new HashMap();
        values.put("actionName", "ssssssss");
        add.getContext().setValues(values);
        DSResponse addres = ds.execute(add);
    }
    
    @Test
    public void mock(){
        
    }
    private DataSource mockds() throws SlxException{
        JPADataSource jpa = ctx.getBean("jpaDatasource", JPADataSource.class);
        TdataSource tds = new TdataSource();
        tds.setID("JPA_TEST");
        tds.setSchemaBean("org.solmix.security.entity.AuthAction");
        DataSourceData dsd = new DataSourceData(tds);
        DataSource ds = jpa.instance(dsd);
        return ds;
    }

}
