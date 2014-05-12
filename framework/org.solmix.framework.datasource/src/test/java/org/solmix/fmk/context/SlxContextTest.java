/*
 *  Copyright 2012 The Solmix Project
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

package org.solmix.fmk.context;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.solmix.api.call.DSCallManagerFactory;
import org.solmix.runtime.SystemContext;
import org.solmix.runtime.SystemContextFactory;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.api.i18n.ResourceBundleManager;
import org.solmix.fmk.SlxContext;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-2
 */

public class SlxContextTest
{

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void systemContextGetAndSet() {
        SystemContextFactory.setDefaultSystemContext(null);
        SystemContextFactory.setThreadDefaultSystemContext(null);
        SystemContext newsc = SystemContextFactory.newInstance().createContext();
        SystemContext origisc=SystemContextFactory.getThreadDefaultSystemContext(false);
        Assert.assertSame(newsc, origisc);
        newsc.close(true);
    }
    @Test
    public void getRPCManagerFactory() {
        SystemContext sc= SlxContext.getSystemContext();
        DSCallManagerFactory dsm=sc.getBean(DSCallManagerFactory.class);
        Assert.assertNotNull(dsm);
        sc.close(true);
    }
    @Test
    public void getDastaSourceManager() {
        SystemContext sc= SlxContext.getSystemContext();
        DataSourceManager dsm=sc.getBean(DataSourceManager.class);
        Assert.assertNotNull(dsm);
        sc.close(true);
    }
    @Test
    public void getResourceBundleManager() {
        SystemContext sc= SlxContext.getSystemContext();
        ResourceBundleManager dsm=sc.getBean(ResourceBundleManager.class);
        Assert.assertNotNull(dsm);
        sc.close(true);
    }
    

}
