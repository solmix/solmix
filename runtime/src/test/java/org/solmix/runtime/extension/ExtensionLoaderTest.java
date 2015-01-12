/**
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

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;
import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.service.TimeService;
import org.solmix.runtime.service.provider1.IProvider1;
import org.solmix.runtime.service.provider1.Provider11;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年8月6日
 */

public class ExtensionLoaderTest {

    public static Container c;

    @BeforeClass
    public static void before() {
        c = ContainerFactory.newInstance().createContainer();
    }

    @Test
    public void testLoader() {
        ExtensionLoader<IProvider1> ext = c.getExtensionLoader(IProvider1.class);
        IProvider1 provider = ext.getDefault();
        Assert.assertEquals("provider1", provider.sayHello());
        IProvider1 p2 = ext.getExtension("provider2");
        Assert.assertEquals("provider2", p2.sayHello());
    }

    @Test
    public void testexception() {
        try {
            c.getExtensionLoader(null);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(),
                containsString("Extension Type is null!"));
        }
        try {
            c.getExtensionLoader(Provider11.class);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), containsString("is not a interface!"));
        }
        try {
            c.getExtensionLoader(TimeService.class);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), containsString("without @Extension"));
        }
    }

    @Test
    public void testextensionName() {
        ExtensionLoader<IProvider1> p = c.getExtensionLoader(IProvider1.class);
        Assert.assertEquals(p.getExtensionName(Provider11.class), "provider1");
        Assert.assertTrue(p.hasExtension("provider1"));
    }

    @Test
    public void testexts() {
        for (int i = 0; i < 1000000; i++) {
            ExtensionLoader<IProvider1> p = c.getExtensionLoader(IProvider1.class);
            IProvider1 p1 = p.getDefault();
        }
    }

    @BeforeClass
    public static void after() {
        ContainerFactory.setDefaultContainer(null);
    }
}
