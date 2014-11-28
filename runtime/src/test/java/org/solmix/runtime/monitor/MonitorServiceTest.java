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

package org.solmix.runtime.monitor;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.Containers;
import org.solmix.runtime.adapter.AdapterManager;
import org.solmix.runtime.monitor.support.MonitorServiceImpl;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年8月15日
 */

public class MonitorServiceTest {

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        Containers.set(null);
    }

    @Test
    public void test() throws InterruptedException {
        MonitorServiceImpl i = new MonitorServiceImpl();
        MonitorInfo old = i.getMonitorInfo();
        Assert.assertNotNull(old.getUsedMemory() > 100);
    }

    @Test
    public void testContainerLoad() throws InterruptedException {
        MonitorServiceImpl ms = new MonitorServiceImpl();
        MonitorInfo old = ms.getMonitorInfo();
        List<Container> lists = new ArrayList<Container>();
        long b = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            Container container = ContainerFactory.newInstance().createContainer();
            lists.add(container);
        }
        MonitorInfo last = ms.getMonitorInfo();
        System.out.println("memery used:"
            + (last.getUsedMemory() - old.getUsedMemory()));
        System.out.println(System.currentTimeMillis() - b);
    }

    @Test
    public void testDiffer() throws InterruptedException {
        Container one = ContainerFactory.newInstance().createContainer();
        AdapterManager a1 = one.getExtension(AdapterManager.class);
        Container other = ContainerFactory.newInstance().createContainer();
        AdapterManager a2 = other.getExtension(AdapterManager.class);
        Assert.assertTrue(a1.hashCode() != a2.hashCode());
    }
}
