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
package org.solmix.exchange.interceptor.phase;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicReference;

import org.solmix.commons.collections.ModCountCopyOnWriteArrayList;
import org.solmix.exchange.Message;
import org.solmix.exchange.interceptor.Interceptor;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月17日
 */

public final class PhaseChainCache
{
    AtomicReference<ChainHolder> lastData = new AtomicReference<ChainHolder>();
    
    
    @SuppressWarnings("unchecked")
    public PhaseInterceptorChain get(SortedSet<Phase> phaseList,
                                     List<Interceptor<? extends Message>> p1) {
        return getChain(phaseList, p1);
    }

    @SuppressWarnings("unchecked")
    public PhaseInterceptorChain get(SortedSet<Phase> phaseList,
                                     List<Interceptor<? extends Message>> p1,
                                     List<Interceptor<? extends Message>> p2) {
        return getChain(phaseList, p1, p2);
    }
    @SuppressWarnings("unchecked")
    public PhaseInterceptorChain get(SortedSet<Phase> phaseList,
                                     List<Interceptor<? extends Message>> p1,
                                     List<Interceptor<? extends Message>> p2,
                                     List<Interceptor<? extends Message>> p3) {
        return getChain(phaseList, p1, p2, p3);
    }
    @SuppressWarnings("unchecked")
    public PhaseInterceptorChain get(SortedSet<Phase> phaseList,
                                     List<Interceptor<? extends Message>> p1,
                                     List<Interceptor<? extends Message>> p2,
                                     List<Interceptor<? extends Message>> p3,
                                     List<Interceptor<? extends Message>> p4) {
        return getChain(phaseList, p1, p2, p3, p4);
    }
    @SuppressWarnings("unchecked")
    public PhaseInterceptorChain get(SortedSet<Phase> phaseList,
                                     List<Interceptor<? extends Message>> p1,
                                     List<Interceptor<? extends Message>> p2,
                                     List<Interceptor<? extends Message>> p3,
                                     List<Interceptor<? extends Message>> p4,
                                     List<Interceptor<? extends Message>> p5) {
        return getChain(phaseList, p1, p2, p3, p4, p5);
    }
    
    private PhaseInterceptorChain getChain(SortedSet<Phase> phaseList,
                                           List<Interceptor<? extends Message>> ... providers) {
        ChainHolder last = lastData.get();
        
        if (last == null 
            || !last.matches(providers)) {
            
            PhaseInterceptorChain chain = new PhaseInterceptorChain(phaseList);
            List<ModCountCopyOnWriteArrayList<Interceptor<? extends Message>>> copy 
                = new ArrayList<ModCountCopyOnWriteArrayList<
                    Interceptor<? extends Message>>>(providers.length);
            for (List<Interceptor<? extends Message>> p : providers) {
                if(p!=null){
                    copy.add(new ModCountCopyOnWriteArrayList<Interceptor<? extends Message>>(p));
                    chain.add(p);
                }
            }
            last = new ChainHolder(chain, copy);
            lastData.set(last);
        }
        
        
        return last.chain.cloneChain();
    }
    
    private static class ChainHolder {
        List<ModCountCopyOnWriteArrayList<Interceptor<? extends Message>>> lists;
        PhaseInterceptorChain chain;
        
        ChainHolder(PhaseInterceptorChain c, 
                    List<ModCountCopyOnWriteArrayList<Interceptor<? extends Message>>> l) {
            lists = l;
            chain = c;
        }
        
        boolean matches(List<Interceptor<? extends Message>> ... providers) {
            if (lists.size() == providers.length) {
                for (int x = 0; x < providers.length; x++) {
                    if (lists.get(x).size() != providers[x].size()) {
                        return false;
                    }
                    
                    if (providers[x].getClass() == ModCountCopyOnWriteArrayList.class) {
                        if (((ModCountCopyOnWriteArrayList<?>)providers[x]).getModCount()
                            != lists.get(x).getModCount()) {
                            return false;
                        }
                    } else {
                        ListIterator<Interceptor<? extends Message>> i1 = lists.get(x).listIterator();
                        ListIterator<Interceptor<? extends Message>> i2 = providers[x].listIterator();
                        
                        while (i1.hasNext()) {
                            if (i1.next() != i2.next()) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            }
            return false;
        }
    }
}
