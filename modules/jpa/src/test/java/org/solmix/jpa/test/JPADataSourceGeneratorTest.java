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
package org.solmix.jpa.test;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solmix.api.context.SystemContext;
import org.solmix.api.data.DataSourceData;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Efield;
import org.solmix.api.jaxb.EserverType;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.jaxb.Tfield;
import org.solmix.fmk.context.SlxContext;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-12-9
 */

public class JPADataSourceGeneratorTest
{

    DataSourceManager dsm;
    @Before
    public void init(){
        SystemContext sc=  SlxContext.getThreadSystemContext();
         dsm=sc.getBean(DataSourceManager.class);
    }

    @Test
    public void gernerateFromSchema() {
        
        TdataSource tds = new TdataSource();
        tds.setID("test");
        tds.setServerType(EserverType.JPA);
        tds.setSchemaClass("org.solmix.jpa.test.entity.AuthUser");
        tds.setAutoDeriveSchema(true);
        DataSourceData dsd = new DataSourceData(tds);
        try {
            DataSource ds=dsm.generateDataSource(dsd);
            Map<String, Tfield> mfs= ds.getContext().getMapFields();
            Tfield username=mfs.get("userId");
            Assert.assertEquals(username.getType(), Efield.INTEGER);
            Assert.assertNotNull(ds.getContext().getSuperDS());
            Tfield createId=mfs.get("createId");
            Assert.assertEquals(createId.getType(), Efield.FLOAT);
            Tfield createDate=mfs.get("createDate");
            Assert.assertEquals(createDate.getType(), Efield.DATE);
            Assert.assertEquals(ds.getServerType(), EserverType.JPA.value());
            Tfield authRoles=mfs.get("authRoles");
            Assert.assertNull(authRoles);
            
        } catch (SlxException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(dsm);
    }

}
