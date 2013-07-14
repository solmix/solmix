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

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.solmix.api.event.EventManager;
import org.solmix.fmk.event.EventUtils;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-2
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
        ctx = new ClassPathXmlApplicationContext("spring-ds.xml");
    }

    @Test
    public void test() {
        EventManager em = ctx.getBean("eventManager", EventManager.class);
        em.postEvent(EventUtils.createTimeMonitorEvent(1232, "test test"));

    }

}
