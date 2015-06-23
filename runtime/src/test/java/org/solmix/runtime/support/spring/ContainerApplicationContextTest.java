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
import org.solmix.runtime.service.InjectTestService;

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
            ContainerApplicationContext overload = new ContainerApplicationContext(
                file, true);
            InjectTestService c = (InjectTestService) overload.getBean("timeservice");
            Assert.assertNotNull(c.getAdapterManager());
            Assert.assertNotNull(c.getContainer());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
