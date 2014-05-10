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
package org.solmix.fmk.cm;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.internal.DatasourceCM;
import org.solmix.runtime.cm.ConfigureUnit;
import org.solmix.runtime.cm.support.SpringConfigureUnitManager;
import org.springframework.core.io.Resource;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-6
 */

public class SpringConfigureUnitTest
{
    @Before
    public void setUp() throws Exception {
      System.setProperty("solmix.base", "/home/solmix/o");
    }
    @Test
    public void test() {
        SpringConfigureUnitManager sp= new SpringConfigureUnitManager();
        List<Resource> rs= sp.getConfigureResource(SpringConfigureUnitManager.DEFAULT_CFG_FILES);
        org.junit.Assert.assertNotNull(rs);
        for(Resource  r : rs){
            org.junit.Assert.assertTrue(r.exists());
        }
    }
    @Test
    public void test2() throws IOException {
        SpringConfigureUnitManager sp= new SpringConfigureUnitManager();
        ConfigureUnit cu= sp.getConfigureUnit("org.solmix.framework.datasource");
        Assert.assertEquals("/home/solmix/o/WEB-INF/template",  cu.getProperties().get("velocityTemplateDir")); 
    }
    @Test
    public void test3() throws IOException {
      Assert.assertEquals("/home/solmix/o",  DataUtil.getTemplateValue("${solmix.base}")); 
    }
    @Test
    public void test4() throws IOException {
        Boolean str=  DatasourceCM.getProperties().getBoolean("authentication.defaultRequired");
      Assert.assertEquals(Boolean.TRUE,  str); 
    }
    @Test
    public void test5() throws IOException {
        
      String str=  DatasourceCM.getProperties().getString(DatasourceCM.P_SLX_VERSION_NUMBER);
      Assert.assertEquals("0.4-SNAPHOST",  str); 
    }
}
