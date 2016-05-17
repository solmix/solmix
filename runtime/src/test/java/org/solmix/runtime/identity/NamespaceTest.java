/**
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

package org.solmix.runtime.identity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solmix.runtime.identity.support.GUID;
import org.solmix.runtime.identity.support.StringID;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.1 2014年5月8日
 */

public class NamespaceTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testNs() {
        Namespace ns = IDFactory.getDefault().getNamespaceByName(
            StringID.class.getName());
        Assert.assertNotNull(ns);
        Namespace ns2 = IDFactory.getDefault().getNamespaceByName(
            GUID.class.getName());
        Assert.assertNotNull(ns2);
    }

    @Test
    public void testSerializable() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(buf);
        try {
            Namespace ns = IDFactory.getDefault().getNamespaceByName(
                StringID.class.getName());
            out.writeObject(ns);
        } catch (NotSerializableException ex) {
            Assert.fail(ex.getLocalizedMessage());
        } finally {
            out.close();
        }
    }

}
