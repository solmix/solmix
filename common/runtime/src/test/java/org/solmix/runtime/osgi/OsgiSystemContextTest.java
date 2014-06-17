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
package org.solmix.runtime.osgi;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.solmix.runtime.SystemContext;
import org.solmix.runtime.adapter.AdapterManager;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年5月8日
 */

public class OsgiSystemContextTest
{
    private IMocksControl control;
    private SystemContext sc;
    private BundleContext bundleContext;
    private Bundle bundle;
    @Before
    public void setup(){
        control=EasyMock.createNiceControl();
        sc=control.createMock(SystemContext.class);
        AdapterManager adm=control.createMock(AdapterManager.class);
        EasyMock.expect(sc.getBean(AdapterManager.class)).andReturn(adm).anyTimes();
        
        bundleContext = control.createMock(BundleContext.class);
        bundle = control.createMock(Bundle.class);
        BundleContext app = control.createMock(BundleContext.class);
        EasyMock.expect(sc.getBean(BundleContext.class)).andReturn(app).anyTimes();
        EasyMock.expect(app.getBundle()).andReturn(bundle).anyTimes();
    }
    @Test
    public void test(){
        
    }
}