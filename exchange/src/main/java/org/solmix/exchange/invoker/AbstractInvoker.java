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

package org.solmix.exchange.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.exchange.Exchange;
import org.solmix.exchange.MessageList;
import org.solmix.exchange.Service;
import org.solmix.exchange.interceptor.Fault;
import org.solmix.exchange.model.OperationInfo;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月25日
 */

public abstract class AbstractInvoker implements Invoker {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractInvoker.class);

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Exchange exchange, Object o) {
        final Object serviceObject = getServiceObject(exchange);
        // ReflectServiceFactory put
        OperationDispatcher od = (OperationDispatcher) exchange.get(
            Service.class).get(OperationDispatcher.class.getName());
        OperationInfo operationInfo = exchange.get(OperationInfo.class);
        Method method = operationInfo == null ? null
            : od.getMethod(operationInfo);
        if (method == null && od == null) {
            LOG.error("Missing service operation");
            throw new Fault("Invoke a service with no operation or method");
        }
        try {
            List<Object> params = null;
            if (o instanceof List<?>) {
                params = (List<Object>) o;
            } else if (o != null) {
                params = new MessageList(o);
            }
            method = matchMethod(method, serviceObject);
            return invoke(exchange, serviceObject, method, params);
        } finally {
            releaseServiceObject(exchange, serviceObject);
        }
    }

    protected Object invoke(Exchange exchange, Object serviceObject,
        Method method, List<Object> params) {
        Object res;
        try {
            Object[] paramArray = new Object[] {};
            if (params != null) {
                paramArray = params.toArray();
            }

            res = performInvocation(exchange, serviceObject, method, paramArray);

            if (exchange.isOneWay()) {
                return null;
            }

            return res;
        } catch (InvocationTargetException e) {

            Throwable t = e.getCause();

            if (t == null) {
                t = e;
            }
            if (t instanceof Fault) {
                throw (Fault) t;
            } else {
                throw new Fault("Exception invoke method " + method.toString()
                    + " params " + params + " " + e.getMessage(), t);
            }
        } catch (Fault f) {
            throw f;
        } catch (Exception e) {
            throw new Fault("Exception invoke method " + method.toString()
                + " params " + params + " " + e.getMessage(), e);
        }
    }

    protected Object performInvocation(Exchange exchange,
        final Object serviceObject, Method m, Object[] paramArray)
        throws Exception {
        paramArray = insertExchange(m, paramArray, exchange);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Invoke service {} ,method {},params {}", serviceObject,
                m, paramArray);
        }
        return m.invoke(serviceObject, paramArray);
    }

    public Object[] insertExchange(Method method, Object[] params,
        Exchange context) {
        Object[] newParams = params;
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            if (method.getParameterTypes()[i].equals(Exchange.class)) {
                newParams = new Object[params.length + 1];

                for (int j = 0; j < newParams.length; j++) {
                    if (j == i) {
                        newParams[j] = context;
                    } else if (j > i) {
                        newParams[j] = params[j - 1];
                    } else {
                        newParams[j] = params[j];
                    }
                }
            }
        }
        return newParams;
    }

    private static Method matchMethod(Method methodToMatch, Object targetObject) {
        if (isJdkDynamicProxy(targetObject)) {
            Class<?>[] interfaces = targetObject.getClass().getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                Method m = getMostSpecificMethod(methodToMatch, interfaces[i]);
                if (!methodToMatch.equals(m)) {
                    return m;
                }
            }
        }
        return methodToMatch;
    }

    /**
     * 是否为J2SE动态代理
     * 
     * @param object
     * @return
     */
    public static boolean isJdkDynamicProxy(Object object) {
        return object != null && Proxy.isProxyClass(object.getClass());
    }

    /** 该方法可能来自一个接口,或者AOP代理类,根本没有实现该方法 */
    public static Method getMostSpecificMethod(Method method,
        Class<?> targetClass) {
        if (method != null && targetClass != null) {
            try {
                method = targetClass.getMethod(method.getName(),
                    method.getParameterTypes());
            } catch (NoSuchMethodException ex) {
                // Perhaps the target class doesn't implement this method:
                // that's fine, just use the original method
            }
        }
        return method;
    }

    /**
     * Called when the invoker is done with the object. Default implementation
     * does nothing.
     * 
     * @param context
     * @param obj
     */
    public void releaseServiceObject(final Exchange context, Object obj) {
    }

    /**
     * Creates and returns a service object depending on the scope.
     */
    public abstract Object getServiceObject(final Exchange context);
}
