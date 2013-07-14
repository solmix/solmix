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

package org.solmix.fmk.context;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import org.solmix.fmk.mock.ContextMocks;

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
        ContextMocks.initContext();
    }

    @Test
    public void test() {
        Assert.assertNotNull(SlxContext.getEventManager());
    }

}
