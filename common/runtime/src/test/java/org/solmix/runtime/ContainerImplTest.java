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
package org.solmix.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import junit.framework.Assert;

import org.junit.Test;
import org.solmix.runtime.extension.ExtensionContainer;
import org.solmix.runtime.extension.ExtensionContainer.ContainerStatus;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年5月8日
 */

public class ContainerImplTest
{

    @Test
    public void testThreadSame() {
        ContainerFactory.setDefaultContainer(null);
        ContainerFactory.setThreadDefaultContainer(null);
        Container context = ContainerFactory.newInstance().createContainer();
        Container context2=ContainerFactory.getThreadDefaultContainer(false);
        assertSame(context, context2);
        Assert.assertTrue(context.hasBeanByName("solmix"));
        context.close(true);
    }
    @Test
    public void testClassExtension() {
        ExtensionContainer context=new ExtensionContainer();
        String solmix="Solmix Container";
        context.setBean(solmix, String.class);
        assertSame(solmix, context.getBean(String.class));
        context.close(true);
    }
    @Test
    public void testContextID() {
        ExtensionContainer context=new ExtensionContainer();
        String id= context.getId();
        Assert.assertEquals(Container.DEFAULT_CONTAINER_ID+Math.abs(context.hashCode()), id);
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
        Assert.assertNull(context.getProperty("test-prop"));
    }
}
