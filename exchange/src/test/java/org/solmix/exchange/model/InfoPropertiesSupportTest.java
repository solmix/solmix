/**
 * Copyright (c) 2015 The Solmix Project
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

package org.solmix.exchange.model;

import org.junit.Assert;
import org.junit.Test;
import org.solmix.exchange.model.EndpointInfo;
import org.solmix.exchange.model.InfoPropertiesSupport;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年1月20日
 */

public class InfoPropertiesSupportTest extends Assert {

    @Test
    public void testExtension() {
        InfoPropertiesSupport is = new InfoPropertiesSupport() {
        };
        EndpointInfo mi = new EndpointInfo();
        is.addExtension(mi);
        assertEquals(mi, is.getExtension(EndpointInfo.class));
    }

}
