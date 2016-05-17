/*
 * Copyright 2015 The Solmix Project
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
package org.solmix.commons.util;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;
import org.solmix.commons.util.bean.Bean1;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年9月21日
 */

public class ClassDescUtilsTest extends Assert
{

    @Test
    public void test() throws NoSuchMethodException, SecurityException{
        Method method=  Bean1.class.getMethod("setA11", boolean.class);
        String[] names = ClassDescUtils.getParameterNamesFromDebugInfo(method);
        assertTrue(names.length==1);
        assertEquals("a11", names[0]);
    }
}
