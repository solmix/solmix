/**
 * Copyright 2014 The Solmix Project
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

package org.solmix.exchange.interceptor.support;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.solmix.commons.collections.ModCountCopyOnWriteArrayList;
import org.solmix.exchange.Message;
import org.solmix.exchange.interceptor.Interceptor;
import org.solmix.exchange.interceptor.InterceptorProvider;

/**
 * 提供的Interceptor是线程安全的,和{@link InterceptorProviderAttrSupport} 不同之处在于
 * {@link InterceptorProviderAttrSupport}提供的Interceptor可以通过设置被替换
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月17日
 */

public abstract class InterceptorProviderSupport extends
    ConcurrentHashMap<String, Object> implements InterceptorProvider {

    private static final long serialVersionUID = -1915876045710441978L;

    private final List<Interceptor<? extends Message>> in = new ModCountCopyOnWriteArrayList<Interceptor<? extends Message>>();

    private final List<Interceptor<? extends Message>> out = new ModCountCopyOnWriteArrayList<Interceptor<? extends Message>>();

    private final List<Interceptor<? extends Message>> outFault = new ModCountCopyOnWriteArrayList<Interceptor<? extends Message>>();

    private final List<Interceptor<? extends Message>> inFault = new ModCountCopyOnWriteArrayList<Interceptor<? extends Message>>();

    @Override
    public Object put(String s, Object o) {
        if (o == null) {
            return super.remove(s);
        }
        return super.put(s, o);
    }

    @Override
    public List<Interceptor<? extends Message>> getOutFaultInterceptors() {
        return outFault;
    }

    @Override
    public List<Interceptor<? extends Message>> getInFaultInterceptors() {
        return inFault;
    }

    @Override
    public List<Interceptor<? extends Message>> getInInterceptors() {
        return in;
    }

    @Override
    public List<Interceptor<? extends Message>> getOutInterceptors() {
        return out;
    }

    public void setInInterceptors(
        List<Interceptor<? extends Message>> interceptors) {
        in.clear();
        in.addAll(interceptors);
    }

    public void setInFaultInterceptors(
        List<Interceptor<? extends Message>> interceptors) {
        inFault.clear();
        inFault.addAll(interceptors);
    }

    public void setOutInterceptors(
        List<Interceptor<? extends Message>> interceptors) {
        inFault.clear();
        inFault.addAll(interceptors);
    }

    public void setOutFaultInterceptors(
        List<Interceptor<? extends Message>> interceptors) {
        inFault.clear();
        inFault.addAll(interceptors);
    }

    @Override
    public boolean equals(Object o) {
        return o == this;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
