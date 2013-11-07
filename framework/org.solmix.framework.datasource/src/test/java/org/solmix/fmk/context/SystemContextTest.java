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
package org.solmix.fmk.context;

import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.api.event.EventManager;
import org.solmix.api.i18n.ResourceBundleManager;
import org.solmix.fmk.context.SystemContextImpl.ContextStatus;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-7
 */

public class SystemContextTest extends Assert
{
    @Test
    public void testConstructionWithBenas()  {
        
        IMocksControl control;
        ResourceBundleManager resourceBundleManager;
        DataSourceManager dataSourceManager;
        EventManager eventManager;
        
        control = EasyMock.createNiceControl();
        
        Map<Class<?>, Object> beans = new HashMap<Class<?>, Object>();
        resourceBundleManager = control.createMock(ResourceBundleManager.class);
        dataSourceManager = control.createMock(DataSourceManager.class);
        eventManager = control.createMock(EventManager.class);
        
        beans.put(ResourceBundleManager.class, resourceBundleManager);
        beans.put(DataSourceManager.class, dataSourceManager);
        beans.put(EventManager.class, eventManager);
        
        SystemContextImpl sc = new SystemContextImpl(beans);
        
        assertSame(resourceBundleManager, sc.getBean(ResourceBundleManager.class));
        assertSame(dataSourceManager, sc.getBean(DataSourceManager.class));
        assertSame(eventManager, sc.getBean(EventManager.class));
  
    }
    @Test
    public void testSystemContextIsOpen()  {
        final SystemContextImpl sc = new SystemContextImpl();
        Thread t = new Thread() {
            @Override
            public void run() {
                sc.open();
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
        assertEquals(ContextStatus.OPENING, sc.getStatus());
    }
    @Test
    public void testSystemContextClose()  {
        final SystemContextImpl sc = new SystemContextImpl();
        Thread t = new Thread() {
            @Override
            public void run() {
                sc.open();
            }
        };
        t.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            // ignore;
        }
        sc.close(true);
        try {
            t.join();
        } catch (InterruptedException ex) {
            // ignore
        }
        assertEquals(ContextStatus.CLOSED, sc.getStatus());
    }
}
