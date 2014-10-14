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


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月14日
 */

public abstract class PhaseInterceptorSupport<T extends Message> implements PhaseInterceptor<T>
{
    private final String id;
    private final String phase;
    private final Set<String> before = new SortedArraySet<String>();
    private final Set<String> after = new SortedArraySet<String>();
    public PhaseInterceptorSupport(String i, String p, boolean uniqueId) {
        if (i == null) {
            i = getClass().getName();
        }
        if (uniqueId) {
            i += System.identityHashCode(this);
        }
        id = i;
        phase = p;
    }
    public PhaseInterceptorSupport(String phase) {
        this(null, phase, false);
    }
    
    public PhaseInterceptorSupport(String i, String p) {
        this(i, p, false);
    }
    
    public PhaseInterceptorSupport(String phase, boolean uniqueId) {
        this(null, phase, uniqueId);
    }
    public void setBefore(Collection<String> i) {
        before.clear();
        before.addAll(i);
    }
    
    public void setAfter(Collection<String> i) {
        after.clear();
        after.addAll(i);
    }
    
    public void addBefore(Collection<String> i) {
        before.addAll(i);
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
    @Override
    public final Set<String> getAfter() {
        return after;
    }

    @Override
    public final Set<String> getBefore() {
        return before;
    }
    @Override
    public final String getId() {
        return id;
    }

    @Override
    public final String getPhase() {
        return phase;
    }
    public void handleFault(T message) {
    }
    
   @Override
   public Collection<PhaseInterceptor<? extends Message>> extInterceptors(){
       return null;
   }
}
