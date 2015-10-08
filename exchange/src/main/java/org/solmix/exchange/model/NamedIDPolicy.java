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

package org.solmix.exchange.model;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.solmix.commons.util.ClassDescUtils;
import org.solmix.exchange.Exchange;
import org.solmix.exchange.ServiceCreateException;
import org.solmix.exchange.support.ReflectServiceFactory;

/**
 * NamedId 生成策略,主要针对命名空间的初始化和默认处理
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年12月1日
 */

public class NamedIDPolicy {

    protected ReflectServiceFactory serviceFactory;

    protected String serviceNamespace;
    
    protected final String protocol;

    public NamedIDPolicy(ReflectServiceFactory factory, String protocol) {
        serviceFactory = factory;
        this.protocol = protocol;
    }
    
    public ReflectServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    public void setServiceFactory(ReflectServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    /**   */
    public String getServiceNamespace() {
        if (serviceNamespace == null && serviceFactory != null && serviceFactory.getServiceClass() != null) {
            serviceNamespace = makeNamespaceFromClassName(serviceFactory.getServiceClass(),protocol);
        }
        return serviceNamespace;
    }

   

    /**   */
    public void setServiceNamespace(String serviceNamespace) {
        this.serviceNamespace = serviceNamespace;
    }

    /**
     * @param method
     */
    public Boolean isValidOperation(Method method) {
        if (getServiceFactory().getIgnoredClasses().contains(
            method.getDeclaringClass().getName())) {
            return Boolean.FALSE;
        }

        for (Method m : getServiceFactory().getIgnoredMethods()) {
            if (m.getName().equals(method.getName())
                && Arrays.equals(method.getParameterTypes(),
                    m.getParameterTypes())
                && m.getReturnType() == method.getReturnType()) {
                return Boolean.FALSE;
            }
        }

        final int modifiers = method.getModifiers();
        if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)
            && !method.isSynthetic()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;

    }
    
    public String getServiceName() {
        return getServiceFactory().getServiceClass().getSimpleName();
    }

    public NamedID getEndpointName() {
        return new NamedID(
            serviceFactory.getServiceNamespace(),
            new StringBuilder().append(getServiceName()).append("Endpoint").toString());
    }
    
    public NamedID getInterfaceName() {
        return new NamedID(
            serviceFactory.getServiceNamespace(),
            new StringBuilder().append(getServiceName()).append("Type").toString());
    }
    
    public NamedID getOperationName(InterfaceInfo intf, Method method) {
        String sns = intf.getName().getServiceNamespace();
        String name = method.getName();
        String desc=ClassDescUtils.getTypeDesc(method.getParameterTypes());
        name+="("+desc+")";
        NamedID nid = new NamedID(sns, name);
        if (intf.getOperation(nid) == null) {
            return nid;
        }
        int i = 1;
        while (true) {
            nid = new NamedID(sns, name + i);
            if (intf.getOperation(nid) == null) {
                return nid;
            } else {
                i++;
            }
        }
    }

   
    public NamedID getInMessageName(OperationInfo op, Method method) {
        return new NamedID(op.getName());
    }

    public NamedID getOutMessageName(OperationInfo op, Method method) {
        return new NamedID(op.getName().getServiceNamespace(), op.getName().getName() + "Response");
    }
    
    public boolean isInParam(Method method, int j) {
        if (j >= 0) {
            Class<?> c = method.getParameterTypes()[j];
            if (Exchange.class.equals(c)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public NamedID getInArgumentName(OperationInfo op, Method method, int j) {
        return new NamedID(op.getName().getServiceNamespace(), getDefaultLocalName(op,method,j,"arg"));
    }

    public NamedID getOutArgumentName(OperationInfo op, Method method, int j) {
        return new NamedID(op.getName().getServiceNamespace(), getDefaultLocalName(op,method,j,"retrun"));
    }
   
    /**是否有返回信息*/
    public boolean hasOutMessage(Method method) {
        return Boolean.TRUE;
    }

  
    public String getFaultMessageName(OperationInfo op, Class<?> exClass,
        Class<?> beanClass) {
        return null;
    }

   
    public NamedID getFaultName(InterfaceInfo service, OperationInfo op,
        Class<?> exClass, Class<?> beanClass) {
        String name = makeServiceNameFromClassName(beanClass);
        return new NamedID(service.getName().getServiceNamespace(), name);
    }

    public static String makeServiceNameFromClassName(Class<?> beanClass) {
        String name = beanClass.getName();
        int last = name.lastIndexOf(".");
        if (last != -1) {
            name = name.substring(last + 1);
        }

        int inner = name.lastIndexOf("$");
        if (inner != -1) {
            name = name.substring(inner + 1);
        }

        return name;
    }
    
    
    /**
     * 根据类名生成服务空间名
     */
    public static String makeNamespaceFromClassName(Class<?> serviceClass,
        String protocol) {

        String className = serviceClass.getName();
        int index = className.lastIndexOf(".");

        if (index == -1 && protocol != null) {
            return protocol + "://" + "default";
        }
        String packageName = className.substring(0, index);

        StringTokenizer st = new StringTokenizer(packageName, ".");
        String[] words = new String[st.countTokens()];

        for (int i = 0; i < words.length; ++i) {
            words[i] = st.nextToken();
        }

        StringBuilder sb = new StringBuilder(80);
        sb.append(protocol).append("://");

        for (int i = words.length - 1; i >= 0; --i) {
            String word = words[i];

            // seperate with dot
            if (i != words.length - 1) {
                sb.append('.');
            }
            sb.append(word);
        }
        return sb.toString();
    }

    private String getDefaultLocalName(OperationInfo op, Method method, int paramNumber, String prefix) {
        Class<?> impl = getServiceFactory().getServiceClass();
        // try to grab the implementation class so we can read the debug symbols from it
        if (impl != null) {
            try {
                method = impl.getMethod(method.getName(), method.getParameterTypes());
            } catch (Exception e) {
                throw new ServiceCreateException(e);
            }
        }
        
        return createName(method, paramNumber, op.getInput().size(), false, prefix);
    }

    public static String createName(final Method method, final int paramNumber, final int currentSize, boolean addMethodName, final String flow) {
        String paramName = "";

        if (paramNumber != -1) {
            String[] names = ClassDescUtils.getParameterNamesFromDebugInfo(method);

            // get the specific parameter name from the parameter Number
            if (names != null && names[paramNumber] != null) {
                paramName = names[paramNumber];
                addMethodName = false;
            } else {
                paramName = flow + currentSize;
            }
        } else {
            paramName = flow;
        }

        paramName = addMethodName ? method.getName() + paramName : paramName;

        return paramName;
    }
}
