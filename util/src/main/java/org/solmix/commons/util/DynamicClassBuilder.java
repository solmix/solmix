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

import static java.lang.reflect.Modifier.PUBLIC;
import static java.lang.reflect.Modifier.STATIC;

import java.lang.reflect.Method;

import net.sf.cglib.asm.Type;
import net.sf.cglib.core.Signature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月27日
 */

abstract class DynamicClassBuilder
{
    private final static int    PUBLIC_STATIC_MODIFIERS = PUBLIC | STATIC;
    protected final      Logger log                     = LoggerFactory.getLogger(getClass());
    private final ClassLoader classLoader;

    public DynamicClassBuilder() {
        this(null);
    }

    public DynamicClassBuilder(ClassLoader cl) {
        this.classLoader = cl;
    }

    public ClassLoader getClassLoader() {
        return classLoader == null ? ClassLoaderUtils.getDefaultClassLoader(): classLoader;
    }

    protected Signature getSignature(Method method, String rename) {
        String name = ObjectUtils.defaultIfNull(StringUtils.trimToNull(rename), method.getName());
        Type returnType = Type.getType(method.getReturnType());
        Type[] paramTypes = Type.getArgumentTypes(method);

        return new Signature(name, returnType, paramTypes);
    }

    protected boolean isPublicStatic(Method method) {
        return (method.getModifiers() & PUBLIC_STATIC_MODIFIERS) == PUBLIC_STATIC_MODIFIERS;
    }

    protected boolean isEqualsMethod(Method method) {
        if (!"equals".equals(method.getName())) {
            return false;
        }

        Class<?>[] paramTypes = method.getParameterTypes();

        return paramTypes.length == 1 && paramTypes[0] == Object.class;
    }

    protected boolean isHashCodeMethod(Method method) {
        return "hashCode".equals(method.getName()) && method.getParameterTypes().length == 0;
    }

    protected boolean isToStringMethod(Method method) {
        return "toString".equals(method.getName()) && method.getParameterTypes().length == 0;
    }
}
