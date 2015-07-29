/*
 * Copyright 2014 The Solmix Project
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
package org.solmx.service.velocity.support;

import org.junit.Assert;
import org.junit.Test;
import org.solmix.runtime.Container;
import org.solmix.runtime.support.spring.ContainerApplicationContext;
import org.solmix.runtime.support.spring.SpringContainerFactory;

import static org.junit.Assert.*;
/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年7月29日
 */

public class SpringLoadedTest
{
    @Test
    public void testSpringApplicationContext(){
        String file = "/spring/spring-velocity.xml";
        ContainerApplicationContext overload = new ContainerApplicationContext(
            file, true);
        Assert.assertNotNull(overload);
        Container c = (Container) overload.getBean("solmix");
        assertNotNull(c);
        UserService us= c.getExtension(UserService.class);
       assertNotNull(us.getTemplateService());
       overload.close();
    }

    @Test
    public void testSpringContainer() {
        Container sc = new SpringContainerFactory().createContainer("/spring/spring-velocity.xml");
        assertNotNull(sc);
        UserService us = sc.getExtension(UserService.class);
        assertNotNull(us.getTemplateService());
        sc.close();
    }
}
