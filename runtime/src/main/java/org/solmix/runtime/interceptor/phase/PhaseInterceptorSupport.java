/*
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

package org.solmix.runtime.interceptor.phase;

import java.util.Collection;
import java.util.Set;

import org.solmix.commons.collections.SortedArraySet;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.MessageUtils;

/**
 * 其他拦截器必须继承该类.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月19日
 */

public abstract class PhaseInterceptorSupport<T extends Message> implements
    PhaseInterceptor<T>
{

    private final String id;

    private final String phase;

    private final Set<String> before = new SortedArraySet<String>();

    private final Set<String> after = new SortedArraySet<String>();

    public PhaseInterceptorSupport(String phase)
    {
        this(null, phase, false);
    }

    public PhaseInterceptorSupport(String id, String phase)
    {
        this(id, phase, false);
    }

    public PhaseInterceptorSupport(String phase, boolean uniqueId)
    {
        this(null, phase, uniqueId);
    }

    public PhaseInterceptorSupport(String id, String phase, boolean uniqueId)
    {
        if (id == null) {
            id = getClass().getName();
        }
        if (uniqueId) {
            id += System.identityHashCode(this);
        }
        this.id = id;
        this.phase = phase;
    }

    @Override
    public void handleFault(T message) {

    }

    public void setAfter(Collection<String> i) {
        after.clear();
        after.addAll(i);
    }

    public void addAfter(Collection<String> i) {
        after.addAll(i);
    }

    public void addBefore(String i) {
        before.add(i);
    }

    public void addAfter(String i) {
        after.add(i);
    }

    public void setBefore(Collection<String> i) {
        before.clear();
        before.addAll(i);
    }

    public void addBefore(Collection<String> i) {
        before.addAll(i);
    }

    @Override
    public final Set<String> getAfter() {
        return after;
    }

    @Override
    public final Set<String> getBefore() {
        return before;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getPhase() {
        return phase;
    }

    @Override
    public Collection<PhaseInterceptor<? extends Message>> extInterceptors() {
        return null;
    }
    protected boolean isRequest(T message) {
        return MessageUtils.isRequest(message);
    }  
}
