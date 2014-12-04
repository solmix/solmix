/**
 * Copyright (c) 2014 The Solmix Project
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

package org.solmix.runtime.identity;

import org.junit.Assert;
import org.junit.Test;
import org.solmix.runtime.exchange.model.NamedID;
import org.solmix.runtime.exchange.model.NamedIDNamespace;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月27日
 */

public class IDFactoryTest {

    @Test
    public void test() {
        Namespace ns = IDFactory.getDefault().getNamespaceByName(
            NamedIDNamespace.class.getName());
        Assert.assertNotNull(ns);
        NamedID id = (NamedID) IDFactory.getDefault().createID(ns,
            new String[] {"org.solmix.test", "service" });
        Assert.assertNotNull(id);
        Assert.assertEquals("org.solmix.test", id.getServiceNamespace());
        Assert.assertEquals("service", id.getName());
    }

}
