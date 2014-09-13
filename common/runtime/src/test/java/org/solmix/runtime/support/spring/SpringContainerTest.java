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

package org.solmix.runtime.support.spring;

import org.junit.Assert;
import org.junit.Test;
import org.solmix.runtime.Container;
import org.solmix.runtime.adapter.AdapterManager;
import org.solmix.runtime.resource.ResourceManager;
import org.solmix.runtime.service.InjectTestService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年8月7日
 */

public class SpringContainerTest
{

    @Test
    public void test() {
        SpringContainerFactory factory = new SpringContainerFactory();
        String file = "/org/solmix/runtime/support/spring/resource.xml";
        Container c = factory.createContainer(file, true);
        ResourceManager rm = c.getExtension(ResourceManager.class);
        InjectTestService ad = rm.resolveResource("timeservice",
            InjectTestService.class);
        Assert.assertNotNull(ad);
        Assert.assertNotNull(ad.getAdapterManager());
        AdapterManager apm = rm.resolveResource(
            "org.solmix.runtime.adapter.support.AdapterManagerImpl",
            AdapterManager.class);
        Assert.assertNotNull(apm);
    }
    @Test
    public void testSchema(){
        ClassPathXmlApplicationContext context =null;
        try {
            context = new ClassPathXmlApplicationContext("/org/solmix/runtime/support/spring/container.xml");
            Container c=  context.getBean("solmix1",Container.class);
            Assert.assertNotNull(c);
            Container c1=  context.getBean("solmix",Container.class);
            Assert.assertNotSame(c, c1);
            Assert.assertNotNull(c1);
        } finally{
            if(context!=null)
            context.close();
        }
        
    }
}
