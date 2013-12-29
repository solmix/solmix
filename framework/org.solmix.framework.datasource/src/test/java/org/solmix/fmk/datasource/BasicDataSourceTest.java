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

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.solmix.api.datasource.DataSourceData;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.ObjectFactory;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.Tfields;
import org.solmix.fmk.AbstractSolmixTestCase;

/**
 * 
 * @author Administrator
 * @version 110035 2012-3-31
 */

public class BasicDataSourceTest extends AbstractSolmixTestCase
{

    BasicDataSource ds;

    @Test
    public void toClientValueMap() {
        long _s = System.currentTimeMillis();
        Map<String, ?> client = ds.toClientValueMap();
        long s_ = System.currentTimeMillis();
        System.out.println(s_ - _s);
        Assert.assertEquals(client.get("cacheData"), "dd");
        Assert.assertNull(client.get("dbName"));
        Assert.assertEquals(((Map) ((List) client.get("fields")).get(0)).get("name"), "123");
        System.out.println(client.get("fields"));
    }

    @Before
    public void setUp() throws SlxException {

        ObjectFactory of = new ObjectFactory();
        TdataSource tds = of.createTdataSource();
        Tfields fields = of.createTfields();
        Tfield field1 = of.createTfield();
        fields.getField().add(field1);
        field1.setName("123");
        tds.setCacheData("dd");
        tds.setDbName("solmix");
        tds.setFields(fields);
        DataSourceData data = new DataSourceData(tds);
        ds = new BasicDataSource();
        ds.init(data);
    }
}
