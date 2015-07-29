/*
 * Copyright 2014 The Solmix Project
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
package org.solmx.service.velocity.support;

import java.io.IOException;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.solmix.commons.util.ObjectUtils;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.monitor.support.MonitorServiceImpl;
import org.solmix.runtime.support.spring.ContainerApplicationContext;
import org.solmix.runtime.support.spring.SpringContainerFactory;
import org.solmx.service.template.TemplateException;
import org.solmx.service.template.TemplateService;
import org.solmx.service.velocity.VelocityEngine;

import static org.junit.Assert.*;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年7月29日
 */

public class DefaultVelocityEngineTest
{
    Container c;

    
    TemplateService ts;
    @Before
    public void setup() {
       
        c = new SpringContainerFactory().createContainer("/spring/services.xml");
        ts= c.getExtension(TemplateService.class);
        
    }
    @After
    public void tearDown(){
        c.close();
    }
    
    @Test
    public void testDefault(){
        DefaultVelocityEngine ve=getEngine("velocity");
        assertArrayEquals(new String[]{"vm"}, ve.getDefaultExtensions());
        
        ExtendedProperties ep=  ve.getVelocityEngineInfo().getProperties();//input.encoding
        assertEquals(ep.getString(RuntimeConstants.INPUT_ENCODING), "UTF-8");
        assertEquals(ep.getString(RuntimeConstants.OUTPUT_ENCODING), "UTF-8");
        assertEquals(ep.getString(RuntimeConstants.PARSER_POOL_SIZE), "50");
        assertEquals(ep.getBoolean(RuntimeConstants.RESOURCE_MANAGER_LOGWHENFOUND), false);
        
        assertEquals(ep.getString(RuntimeConstants.VM_LIBRARY), ObjectUtils.EMPTY_STRING);
    }
    @Test
    public void testDefault2() throws TemplateException, IOException {
        DefaultVelocityEngine ve = getEngine("velocity2");
        String ct = ve.mergeTemplate("/templates/test.vm", new VelocityContext(), null);
        assertEquals(ct, "haha");
    }
    
    private DefaultVelocityEngine getEngine(String id){
        ConfiguredBeanProvider provider=  c.getExtension(ConfiguredBeanProvider.class);
        return provider.getBeanOfType(id, DefaultVelocityEngine.class);
    }

}
