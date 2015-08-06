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
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;
import org.solmix.runtime.extension.ExtensionContainer;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月6日
 */

public class ProxyHelperTest
{
    
    @Test
    public void test(){
        ProxyInterface proxy=(ProxyInterface)   ProxyHelper.getProxy(this.getClass().getClassLoader(), 
            new Class[]{ProxyInterface.class}, new InvocationHandler(){

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String methodName = method.getName();
                    if (Object.class.equals(method.getDeclaringClass())) {
                        if (methodName.equals("equals")) {
                              return (proxy == args[0]);
                        }
                        else if (methodName.equals("hashCode")) {
                              return System.identityHashCode(proxy);
                        }
                  }
                    if (methodName.equals("sayHello")){
                        return "hello";
                    }
                    return null;
                }
            
        });
       String hello= proxy.sayHello();
       
       Assert.assertEquals(hello, "hello");
    }
    
    interface ProxyInterface{
        String sayHello();
    }
    
    @Test
    public void testcglib(){
        ExtensionContainer proxy=(ExtensionContainer)   ProxyHelper.getProxy(this.getClass().getClassLoader(), 
            new Class[]{ExtensionContainer.class}, new InvocationHandler(){

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String methodName = method.getName();
                    if (Object.class.equals(method.getDeclaringClass())) {
                        if (methodName.equals("equals")) {
                              return (proxy == args[0]);
                        }
                        else if (methodName.equals("hashCode")) {
                              return System.identityHashCode(proxy);
                        }
                  }
                    if (methodName.equals("getId")){
                        return "hello";
                    }
                    return null;
                }
            
        });
        String id =proxy.getId();
        Assert.assertEquals(id, "hello");
    }

}
