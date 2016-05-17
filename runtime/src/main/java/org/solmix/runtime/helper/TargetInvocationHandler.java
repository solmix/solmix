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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.Reflection;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年8月7日
 */

public class TargetInvocationHandler implements InvocationHandler
{
    
    public TargetInvocationHandler(Object target){
        this.target=target;
    }

    private Object target;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Assert.assertNotNull(target,"target");
        if (Reflection.isEqualsMethod(method)) {
            return equals(args[0]);
        }
        if (Reflection.isHashCodeMethod(method)) {
            return hashCode();
        }

        Object retVal = null;
        try {
            retVal = Reflection.invokeMethod(target, method, args);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        } catch (IllegalArgumentException ex) {
            throw new InvocationTargetException(ex, "Invocation configuration seems to be invalid: tried calling method [" + method + "] on target ["
                + target + "]");
        }
        Class<?> returnType = method.getReturnType();
        if (retVal != null && retVal == target  && returnType.isInstance(proxy)) {
            retVal = proxy;
        } else if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
            throw new IllegalArgumentException("Null return value from advice does not match primitive return type for: " + method);
        }
        return retVal;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

}
