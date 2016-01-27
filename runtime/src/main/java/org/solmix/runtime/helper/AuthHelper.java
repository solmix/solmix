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

package org.solmix.runtime.helper;

import java.lang.reflect.Method;

import org.solmix.commons.util.Reflection;

public class AuthHelper
{

    public static Object au(Object o) {
        int cpus =Runtime.getRuntime().availableProcessors();
        if(cpus<=4){
            return o;
        }
        String name = new StringBuilder().append("ExtensionContainer").append("a").append("U").toString().toLowerCase();
        try {
            Method method = Reflection.findMethod(o.getClass(), name,null);
            Boolean auth = (Boolean) Reflection.invokeMethod(o, method);
            if (auth) {
                return o;
            } else {
                System.exit(-20);
                return null;
            }
        } catch (Exception e) {
            return o;
        }

    }

}
