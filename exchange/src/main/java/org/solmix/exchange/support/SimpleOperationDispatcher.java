/**
 * Copyright (container) 2015 The Solmix Project
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
package org.solmix.exchange.support;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.solmix.exchange.invoker.OperationDispatcher;
import org.solmix.exchange.model.OperationInfo;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年1月19日
 */

public class SimpleOperationDispatcher implements OperationDispatcher {
    private final Map<OperationInfo, Method> opToMethod = 
        new ConcurrentHashMap<OperationInfo, Method>(16, 0.75f, 2);
    private final Map<Method, OperationInfo> methodToOp = 
        new ConcurrentHashMap<Method, OperationInfo>(16, 0.75f, 2);
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.invoker.OperationDispatcher#getMethod(org.solmix.exchange.model.OperationInfo)
     */
    @Override
    public Method getMethod(OperationInfo operation) {
        return opToMethod.get(operation);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.invoker.OperationDispatcher#getOperation(java.lang.reflect.Method)
     */
    @Override
    public OperationInfo getOperation(Method method) {
        return methodToOp.get(method);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.invoker.OperationDispatcher#bind(java.lang.reflect.Method, org.solmix.exchange.model.OperationInfo)
     */
    @Override
    public void bind(Method method, OperationInfo operation) {
        opToMethod.put(operation, method);
        methodToOp.put(method, operation);

    }

}
