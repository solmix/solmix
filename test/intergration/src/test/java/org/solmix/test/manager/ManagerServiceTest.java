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
package org.solmix.test.manager;

import org.junit.Assert;
import org.junit.Test;
import org.solmix.api.context.SystemContext;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.fmk.SlxContext;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-8
 */

public class ManagerServiceTest extends Assert
{
    @Test
    public void getDsRepositoryManager(){
        final  SystemContext sc= SlxContext.getSystemContext();
        DataSourceManager dsm=sc.getBean(DataSourceManager.class);
        Assert.assertNotNull(dsm);
        sc.close(true);
        
    }

}
