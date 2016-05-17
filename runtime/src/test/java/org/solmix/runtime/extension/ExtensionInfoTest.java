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
package org.solmix.runtime.extension;

import junit.framework.Assert;

import org.junit.Test;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月28日
 */

public class ExtensionInfoTest
{

    @Test
    public void testSet() {
        ExtensionInfo info= new ExtensionInfo();
        String className= "org.solmix.runtime.extension.ExtensionManagerImpl";
        info.setClassname(className);
        Assert.assertEquals("Unexpected class name.", className, info.getClassname());
        Assert.assertNull(info.getInterfaceName());
        String inf="org.solmix.runtime.extension.ExtensionManager";
        info.setInterfaceName(inf);
        Assert.assertEquals("Unexpected Interface name.", inf, info.getInterfaceName());
        
        Assert.assertTrue(!info.isDeferred());
        info.setDeferred(true);
        Assert.assertTrue(info.isDeferred());
        Assert.assertTrue(!info.isOptional());
        info.setOptional(true);
        Assert.assertTrue(info.isOptional());
        Assert.assertEquals("Unexpected size of namespace list.", 0, info.getNamespaces().size());

    }
    @Test
    public void testLoadClass() {
        ExtensionInfo info= new ExtensionInfo();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        info.setClassname("no.extension");
        try {
            info.load(cl, null);                  
        } catch (ExtensionException ex) {
            Assert.assertTrue("ExtensionException does not wrap ClassNotFoundException",
                       ex.getCause() instanceof ClassNotFoundException);
        }
        info.setClassname("java.lang.System");
        try {
            info.load(cl, null);                  
        } catch (ExtensionException ex) {
            Assert. assertTrue("ExtensionException does not wrap NoSuchMethodException " + ex.getCause(),
                       ex.getCause() instanceof NoSuchMethodException);
        } 
        info.setClassname(MyServiceConstructorThrowsException.class.getName());
        try {
            info.load(cl, null);                  
        } catch (ExtensionException ex) {
            Assert.assertTrue("ExtensionException does not wrap IllegalArgumentException",
                       ex.getCause() instanceof IllegalArgumentException);
        } 
        info.setClassname("java.lang.String");
        Object obj =info.load(cl, null);
        Assert.assertTrue("Object is not type String", obj instanceof String); 
    }
    @Test
    public void testLoadInterface() {
        ExtensionInfo info= new ExtensionInfo();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        info.setInterfaceName("no.extension");
        try {
            info.loadInterface(cl);                  
        } catch (ExtensionException ex) {
            Assert.assertTrue("ExtensionException does not wrap ClassNotFoundException",
                       ex.getCause() instanceof ClassNotFoundException);
        }
        info.setInterfaceName(ExtensionManagerImpl.class.getName());
        Class<?> loadInf=info.loadInterface(cl);
       Assert.assertNotNull(loadInf);
    }
    static class MyServiceConstructorThrowsException {
        public MyServiceConstructorThrowsException() {
            throw new IllegalArgumentException();
        }
    }
}
