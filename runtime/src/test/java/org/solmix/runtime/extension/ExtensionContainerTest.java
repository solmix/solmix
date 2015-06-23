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
package org.solmix.runtime.extension;

import java.util.Collection;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.adapter.AdapterManager;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.extension.ExtensionContainer.ContainerStatus;
import org.solmix.runtime.resource.ResourceManager;
import org.solmix.runtime.resource.support.ResourceManagerImpl;
import org.solmix.runtime.service.DateTimeService;
import org.solmix.runtime.service.TimeService;
import org.solmix.runtime.support.ContainerFactoryImpl;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年8月3日
 */

public class ExtensionContainerTest extends Assert
{
    @Test
    public void testGetBean() {
        ContainerFactory container=  ContainerFactory.newInstance(ContainerFactoryImpl.class.getName());
         Container c=  container.createContainer();
         ConfiguredBeanProvider provider=  c.getExtension(ConfiguredBeanProvider.class);
         DateTimeService pdm= provider.getBeanOfType(DateTimeService.class.getName(), DateTimeService.class);
         DateTimeService  tm=c.getExtension(DateTimeService.class);
         assertSame("他们两个是同一个",pdm, tm);
         Collection<? extends TimeService>  cl=provider.getBeansOfType(TimeService.class);
         assertEquals("总共找到了两个服务", 2,cl.size());
         assertEquals("总共找到了1个服务", 1,provider.getBeansOfType(DateTimeService.class).size());
         List<String> names=provider.getBeanNamesOfType(DateTimeService.class);
         Assert.assertEquals("只能找到DateTimeService类","org.solmix.runtime.service.DateTimeService" ,names.get(0));
         List<String> tnames=provider.getBeanNamesOfType(TimeService.class);
         assertEquals("应该找到TimeService的连个实现类", 2,tnames.size());
         Assert.assertNotNull(tm);
         Assert.assertNotNull(c.getExtension(TimeService.class));
         Assert.assertNotNull(c.getExtension(ResourceManager.class).getClass().getName(),ResourceManagerImpl.class.getName());
         AdapterManager adm= c.getExtension(AdapterManager.class);
         Assert.assertNotNull(adm);
        tm.getCurrentTime();
    }

    @Test
    public void testThreadSame() {
        ContainerFactory.setDefaultContainer(null);
        ContainerFactory.setThreadDefaultContainer(null);
        Container context = ContainerFactory.newInstance().createContainer();
        Container context2=ContainerFactory.getThreadDefaultContainer(false);
        assertSame(context, context2);
        context.close(true);
    }
 
    @Test
    public void testClassExtension() {
        ExtensionContainer context=new ExtensionContainer();
        String solmix="Solmix Container";
        context.setExtension(solmix, String.class);
        assertSame(solmix, context.getExtension(String.class));
        context.close(true);
    }
    @Test
    public void testContextID() {
        ExtensionContainer context=new ExtensionContainer();
        String id= context.getId();
        Assert.assertEquals(Container.DEFAULT_CONTAINER_ID+"-"+Math.abs(context.hashCode()), id);
        context.setId("test-context");
        Assert.assertEquals("test-context", context.getId());
        context.close(true);
    }
    
    @Test
    public void testOpen() {
        final ExtensionContainer context = new ExtensionContainer();
        Thread t = new Thread() {
            @Override
            public void run() {
                context.open();
            }
        };
        t.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            // ignore;
        }
        try {
            t.join(400);
        } catch (InterruptedException ex) {
            // ignore
        }
        assertEquals(ContainerStatus.CREATED, context.getStatus());
    }
    
    @Test
    public void testClose() {
        final ExtensionContainer context = new ExtensionContainer();
        Thread t = new Thread() {
            @Override
            public void run() {
                context.open();
            }
        };
        t.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            // ignore;
        }
        context.close(true);
        try {
            t.join(400);
        } catch (InterruptedException ex) {
            // ignore
        }
        assertEquals(ContainerStatus.CLOSED, context.getStatus());
    }
    @Test
    public void testProperty() {
        final ExtensionContainer context = new ExtensionContainer();
        Integer level=new Integer(100);
        context.setProperty("test-prop", level);
        Assert.assertEquals(new Integer(100), context.getProperty("test-prop"));
        Assert.assertNull(context.getProperty("test-prop-no"));
    }
    @AfterClass
    public static void clear(){
        ContainerFactory.setDefaultContainer(null);
    }

}
