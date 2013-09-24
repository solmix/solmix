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
package org.solmix.modules.spring.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.solmix.api.event.TimeMonitorEvent;
import org.solmix.fmk.spring.event.DefaultEventManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-9-23
 */

public class EventManagerTest
{
    ApplicationContext ctx;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
//        Mocks.initContext();
        System.setProperty("solmix.base", "web-root");
        ctx = new ClassPathXmlApplicationContext("spring-test.xml");
    }

    @Test
    public void test() {
        try {
            DefaultEventManager eventManager= ctx.getBean("eventManager", DefaultEventManager.class);
            Map values = new HashMap();
            long id=Thread.currentThread().getId();
            for(int i=0;i<100;i++){
            values.put("TheadID",id );
            values.put("time",System.currentTimeMillis() );
            System.out.println("product-id-"+id);
            TimeMonitorEvent te = new TimeMonitorEvent(values);
            eventManager.postEvent(te);
            }
            //protected Thread shutdown and asy handler is not be called.
            try {
                Thread.currentThread().sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (BeansException e) {
            e.printStackTrace();
        }
    }
}
