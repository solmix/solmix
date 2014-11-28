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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 0.0.1 2014年5月9日
 */

public class StringIDTest extends AbstractIDTestCase {

    @Override
    protected ID createID() throws IDCreateException {
        return createID(this.getClass().getName());
    }

    protected ID createID(String val) throws IDCreateException {
        return IDFactory.getDefault().createStringID(val);
    }

    @Test
    public void testCreate() {
        ID iD = createID();
        Assert.assertNotNull(iD);
    }

    @Test
    public void testCreatenull() {
        try {
            createID(null);
            fail();
        } catch (final IDCreateException e) {
            // success
        }
    }

    @Test
    public void testGetName() {
        ID iD = createID("aaaaa");
        Assert.assertEquals(iD.getName(), "aaaaa");
    }

    @Test
    public void testToQueryString() {
        ID iD = createID();
        Assert.assertNotNull(iD.toQueryString());
    }

    @Test
    public void testToString() {
        ID iD = createID();
        Assert.assertNotNull(iD.toString());
    }

    @Test
    public void testHashCode() throws Exception {
        final ID id1 = createID();
        final ID id2 = createID();
        assertTrue(id1.hashCode() == id2.hashCode());
    }

    @Test
    public void testCompareToNotEqual() throws Exception {
        final ID id1 = createID();
        final ID id2 = createID();
        assertTrue(id1.compareTo(id2) == 0);
        assertTrue(id2.compareTo(id1) == 0);
    }

    @Test
    public void testGetNamespace() throws Exception {
        final ID iD = createID();
        final Namespace ns = iD.getNamespace();
        assertNotNull(ns);
    }

    @Test
    public void testEqualNamespaces() throws Exception {
        final ID id1 = createID();
        final ID id2 = createID();
        final Namespace ns1 = id1.getNamespace();
        final Namespace ns2 = id2.getNamespace();
        assertTrue(ns1.equals(ns2));
        assertTrue(ns2.equals(ns2));
    }

    @Test
    public void testSerializable() throws Exception {
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(buf);
        try {
            out.writeObject(createID());
        } catch (final NotSerializableException ex) {
            fail(ex.getLocalizedMessage());
        } finally {
            out.close();
        }
    }

    @Test
    public void testCreateFromQueryString() throws Exception {
        final ID id1 = createID();
        final String externalForm = id1.toQueryString();
        final ID id2 = IDFactory.getDefault().createID(id1.getNamespace(),
            externalForm);
        assertTrue(id1.equals(id2));
    }
}
