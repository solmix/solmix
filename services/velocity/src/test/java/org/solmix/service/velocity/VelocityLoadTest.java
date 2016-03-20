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
package org.solmix.service.velocity;

import org.junit.Before;
import org.junit.Test;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.extension.ExtensionLoader;
import org.solmix.runtime.monitor.support.MonitorServiceImpl;
import org.solmix.service.template.TemplateEngine;
import org.solmix.service.template.TemplateService;
import org.solmix.service.velocity.VelocityEngine;

import static org.junit.Assert.*;
/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年7月29日
 */

public class VelocityLoadTest
{

    Container c;

    @Before
    public void setup() {
        System.out.println(new MonitorServiceImpl().getMonitorInfo().getUsedMemory());
        c = ContainerFactory.getDefaultContainer(true);
    }
    
    @Test
    public void testLoading(){
        ExtensionLoader<TemplateEngine> extensions= c.getExtensionLoader(TemplateEngine.class);
        assertNotNull(extensions);
        extensions.getLoadedExtensions();
        TemplateService ts=   c.getExtension(TemplateService.class);
        assertNotNull(ts);
        VelocityEngine ve = c.getExtension(VelocityEngine.class);
        assertNotNull(ve);
        assertSame(ts.getTemplateEngine(VelocityEngine.class), ve);
        assertSame(ts.getTemplateEngine("vm"), ve);
    }

}
