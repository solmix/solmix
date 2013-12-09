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
package org.solmix.fmk.datasource;

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
 * @version $Id$  2013-12-8
 */

public class BasicGeneratorTest
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
        tds.setServerType(EserverType.FILESYSTEM);
        tds.setSchemaClass("org.solmix.fmk.datasource.TestDataSourceSchema");
        tds.setAutoDeriveSchema(true);
        DataSourceData dsd = new DataSourceData(tds);
        try {
            DataSource ds=dsm.generateDataSource(dsd);
            Map<String, Tfield> mfs= ds.getContext().getMapFields();
            Tfield username=mfs.get("username");
            Assert.assertEquals(username.getType(), Efield.TEXT);
            Assert.assertNotNull(ds.getContext().getSuperDS());
            Tfield type=mfs.get("type");
            Assert.assertEquals(mfs.size(), 5);
            Assert.assertEquals(type.getType(), Efield.ENUM);
            Assert.assertEquals(type.getValueMap().getValue().size(), 4);
            Assert.assertEquals(ds.getServerType(), EserverType.FILESYSTEM.value());
        } catch (SlxException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(dsm);
    }
   @Test
    public void gernerateFromBean() {
        
        TdataSource tds = new TdataSource();
        tds.setID("test");
        tds.setServerType(EserverType.FILESYSTEM);
        tds.setBean("org.solmix.fmk.datasource.TestDataSourceBean");
        tds.setAutoDeriveSchema(true);
        DataSourceData dsd = new DataSourceData(tds);
        try {
            DataSource ds=dsm.generateDataSource(dsd);
            Map<String, Tfield> mfs= ds.getContext().getMapFields();
            Assert.assertEquals(mfs.size(), 6);
            Tfield bean=mfs.get("bean");
            Assert.assertEquals(bean.getType(), Efield.TEXT);
            Tfield type=mfs.get("type");
            Assert.assertEquals(type.getType(), Efield.ENUM);
            Assert.assertEquals(type.getValueMap().getValue().size(), 4);
            Assert.assertEquals(ds.getServerType(), EserverType.FILESYSTEM.value());
        } catch (SlxException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(dsm);
    }
}
