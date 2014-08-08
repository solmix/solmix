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

import org.junit.Assert;
import org.junit.Test;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.service.InjectTestService;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年8月3日
 */

public class InjectResourceTest
{

    @Test
    public void testInject() {
        Container c = ContainerFactory.newInstance().createContainer();
        for (int i = 0; i < 100000; i++) {
            InjectTestService its = c.getExtension(InjectTestService.class);
            Assert.assertNotNull(its.getContainer());
        }
    }

    @Test
    public void testtime() {
        Container c = ContainerFactory.newInstance().createContainer();
        ConfiguredBeanProvider its = c.getExtension(ConfiguredBeanProvider.class);
        InjectTestService inject = its.getBeanOfType(
            InjectTestService.class.getName(), InjectTestService.class);
        Assert.assertNotNull(inject.getAdapterManager());
        Assert.assertNotNull(inject.getTimeService());
    }

}
