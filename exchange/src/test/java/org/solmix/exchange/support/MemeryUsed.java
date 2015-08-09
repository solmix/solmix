/**
 * Copyright (container) 2014 The Solmix Project
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

package org.solmix.exchange.support;

import java.util.ArrayList;
import java.util.List;

import org.solmix.exchange.model.ServiceInfo;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.monitor.MonitorInfo;
import org.solmix.runtime.monitor.support.MonitorServiceImpl;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月26日
 */

public class MemeryUsed {

    private MemeryUsed() {

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        createContainerUsedMemery();
    }
    public static void createServiceInfoUsedMemery() {
        MonitorServiceImpl ms = new MonitorServiceImpl();
        MonitorInfo old = ms.getMonitorInfo();
        List<ServiceInfo> lists = new ArrayList<ServiceInfo>();
        long b = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            ServiceInfo info = new ServiceInfo();
            lists.add(info);
        }
        MonitorInfo last = ms.getMonitorInfo();
        System.out.println("memery used:"
            + (last.getUsedMemory() - old.getUsedMemory()));
        System.out.println(System.currentTimeMillis() - b);
    }
    public static void createContainerUsedMemery() {
        MonitorServiceImpl ms = new MonitorServiceImpl();
        MonitorInfo old = ms.getMonitorInfo();
        List<Container> lists = new ArrayList<Container>();
        long b = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            Container container = ContainerFactory.newInstance().createContainer();
            lists.add(container);
        }
        MonitorInfo last = ms.getMonitorInfo();
        System.out.println("memery used:"
            + (last.getUsedMemory() - old.getUsedMemory()));
        System.out.println(System.currentTimeMillis() - b);
    }
    
    

}
