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


import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.extension.ExtensionManager;
import org.solmix.runtime.support.spring.SpringContainerFactory;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年5月8日
 */

public class SpringContainerFactoryTest
{

    @Test
    public void test() {
        SpringContainerFactory ssc=new SpringContainerFactory();
        Container sc=ssc.createContainer();
        ConfiguredBeanProvider provider=  sc.getExtension(ConfiguredBeanProvider.class);
      List<?> l=  provider.getBeanOfType("java.util.LinkedList", List.class);
      Assert.assertNotNull(l);
        ExtensionManager em=  sc.getExtension(ExtensionManager.class);
        Assert.assertNotNull(em);
        Assert.assertNotNull(sc);
        Assert.assertNotNull("adaptermanager must be not ull", sc.getExtension(org.solmix.runtime.adapter.AdapterManager.class));
    }

    @After
    public void tearDown() {
        ContainerFactory.setDefaultContainer(null);
    }
}
