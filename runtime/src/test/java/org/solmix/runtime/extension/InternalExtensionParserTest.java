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

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月28日
 */

public class InternalExtensionParserTest
{

    @Test
    public void test(){
      URL url=  this.getClass().getResource("java.util.List");
      List<ExtensionInfo> extensions= new InternalExtensionParser(null).getExtensions(url);
      Assert.assertEquals("Unexpected number of Extension elements.", 2, extensions.size());
      
      ExtensionInfo e1=extensions.get(0);
      Assert.assertTrue(e1.isDeferred());
      Assert.assertTrue(e1.isOptional());
      ExtensionInfo e2=extensions.get(1);
      Assert.assertEquals("java.util.LinkedList", e2.getClassname());
      Assert.assertEquals("java.util.List", e2.getInterfaceName());
      Assert.assertTrue(LinkedList.class==e2.getClassObject(Thread.currentThread().getContextClassLoader()));
    }
}
