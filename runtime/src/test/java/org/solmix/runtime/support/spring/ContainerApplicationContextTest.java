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
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.service.ContainerAwareService;
import org.solmix.runtime.service.ContainerRefTestService;
import org.solmix.runtime.service.DateTimeService;
import org.solmix.runtime.service.InjectTestService;
import org.solmix.runtime.service.SystemTimeService;
import org.springframework.context.ApplicationContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年8月7日
 */

public class ContainerApplicationContextTest {

    @Test
    public void testLoadFailed() {
        try {
            ContainerApplicationContext ctx = new ContainerApplicationContext(
                "xxxoo.xml", null, false);
            Assert.fail();
        } catch (Exception e) {
        }
    }
    @Test
    public void testLoad2() {
        SpringContainerFactory factory = new SpringContainerFactory();
        String file = "/org/solmix/runtime/support/spring/container-overload.xml";
        Container c = factory.createContainer(file, true);
        Assert.assertTrue(c.isProduction());
       Object o= c.getExtension(ApplicationContext.class).getBean("solmix");
       Assert.assertSame(o, c);
        Assert.assertEquals(c.getProperty("runtime.production"),"true");
        Container c2= c.getExtension(ApplicationContext.class).getBean("solmix2",Container.class);
        Assert.assertEquals(c2.getProperty("runtime.production"),"false");
        
        
        ContainerAwareService cas = c.getExtension(ContainerAwareService.class);
        c.getExtension(ContainerRefTestService.class);
        Assert.assertNotNull(cas.getContainer());
        Assert.assertSame(cas.getContainer(), c);
        Assert.assertTrue(cas.isProduction());
        
        
        ContainerAwareService cas2 = c2.getExtension(ContainerAwareService.class);
        Assert.assertSame(cas2.getContainer(), c);
        Assert.assertTrue(cas2.isProduction());
        Assert.assertTrue(!c2.isProduction());
        
    }
    
    
    @Test
    public void testLoad3() {
        //配置中没有，但包含了默认的
        SpringContainerFactory factory = new SpringContainerFactory();
        String file = "/org/solmix/runtime/support/spring/container-no-default.xml";
        //返回的不是配置的，时默认的
        Container c = factory.createContainer(file, true);
       Object o= c.getExtension(ApplicationContext.class).getBean("solmix");
       Assert.assertSame(o, c);
       Container[] containers= ContainerFactory.getContainers();
       System.out.println(containers.length);
        Container c2= c.getExtension(ApplicationContext.class).getBean("solmix2",Container.class);
        Assert.assertEquals(c2.getProperty("runtime.production"),"false");
      
    }
    @Test(expected=RuntimeException.class)
    public void testLoad4() {
        SpringContainerFactory factory = new SpringContainerFactory();
        String file = "/org/solmix/runtime/support/spring/container-no-default.xml";
        //配置中没有，也不加载默认，不能初始化container。
        Container c = factory.createContainer(file, false);
       Object o= c.getExtension(ApplicationContext.class).getBean("solmix");
       Assert.assertSame(o, c);
        Assert.assertEquals(c.getProperty("runtime.production"),"true");
        Container c2= c.getExtension(ApplicationContext.class).getBean("solmix2",Container.class);
        Assert.assertEquals(c2.getProperty("runtime.production"),"false");
    }
    
    @Test
    public void testLoad5() {
        String file = "/org/solmix/runtime/support/spring/container-no-default.xml";
        ContainerApplicationContext overload = new ContainerApplicationContext(
            file, false);
        //由spring决定去一个container。
        Container c = (Container) overload.getBean("solmix2");
        Assert.assertTrue(!c.isProduction());
       Object o= overload.getBean(Container.class);
       Assert.assertSame(o, c);
        Assert.assertEquals(c.getProperty("runtime.production"),"false");
        overload.close();
    }
    @Test
    public void testLoad() {
        try {
            String file = "/org/solmix/runtime/support/spring/solmix-overload.xml";
            ContainerApplicationContext overload = new ContainerApplicationContext(
                file, false);
            Assert.assertNotNull(overload);
            Container c = (Container) overload.getBean("solmix");
            Assert.assertEquals(c.getContainerListeners().size(), 2);
            Assert.assertEquals(1, overload.getConfigResources().length);

            ContainerApplicationContext ctx = new ContainerApplicationContext(
                file, true);
            Assert.assertEquals(2, ctx.getConfigResources().length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJSR250() {
        try {
            String file = "/org/solmix/runtime/support/spring/resource.xml";
            @SuppressWarnings("resource")
            ContainerApplicationContext overload = new ContainerApplicationContext(file, true);
            InjectTestService c = (InjectTestService) overload.getBean("timeservice");
            Assert.assertNotNull(c.getAdapterManager());
            Assert.assertNotNull(c.getContainer());
            Assert.assertTrue(c.getTimeService().getClass()==SystemTimeService.class);
            Assert.assertTrue(c.getTimeService2().getClass()==DateTimeService.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
