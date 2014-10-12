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
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.annotation.ThreadSafe;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.interceptor.Interceptor;
import org.solmix.runtime.interceptor.InterceptorChain;


/**
 * 分阶拦截链,分截拦截器可在链中按不同阶段遍历执行.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月12日
 */
@ThreadSafe
public class PhaseInterceptorChain implements InterceptorChain
{

    private static final Logger LOG= LoggerFactory.getLogger(PhaseInterceptorChain.class);
    
    /** 缓存当前消息  */
    private static final ThreadLocal<Message> CURRENT_MESSAGE = new ThreadLocal<Message>();
    /**分阶队列*/
    private final Phase phases[];
    /**阶段名称对应序号*/
    private final Map<String, Integer> nameMap;
    /** 拦截链状态   */
    private  State state;
    /** 拦截器迭代器   */
    private PhaseIterator iterator;
    
    /**  heads[phase]为该阶段拦截器的第一个节点 heads[phase] 和tails[phase]直接的节点为该阶段(phase)所有的拦截器*/
    private Holder heads[];
    /** tails[phase]为该阶段拦截器的最后一个节点    */
    private Holder tails[];
    private boolean hasAfters[];
    /** 分阶段构建拦截器 */
    public PhaseInterceptorChain(SortedSet<Phase> ps) {
        state = State.EXECUTING;
        int numPhases = ps.size();
        phases = new Phase[numPhases];
        nameMap = new HashMap<String, Integer>();
        
        heads = new Holder[numPhases];
        tails = new Holder[numPhases];
        hasAfters = new boolean[numPhases];
        int idx = 0;
        for (Phase phase : ps) {
            phases[idx] = phase; 
            nameMap.put(phase.getName(), idx);
            ++idx;
        }
    }
    private PhaseInterceptorChain(PhaseInterceptorChain other){
      //only used for clone
        state = State.EXECUTING;
        
        //immutable, just repoint
        nameMap = other.nameMap;
        phases = other.phases;
    }
    public static Message getCurrentMessage() {
        return CURRENT_MESSAGE.get();
    }
    
    public static boolean setCurrentMessage(PhaseInterceptorChain chain, Message m) {
        if (getCurrentMessage() == m) { 
            return false;
        }
        if (chain.iterator.hasPrevious()) {
            chain.iterator.previous();
            if (chain.iterator.next() instanceof ServiceInvokerInterceptor) {
                CURRENT_MESSAGE.set(m);
                return true;
            } else {
                String error = "Only ServiceInvokerInterceptor can update the current chain message";
                LOG.warn(error);
                throw new IllegalStateException(error);   
            }
        }
        return false;
        
    }
    @Override
    public Iterator<Interceptor<? extends Message>> iterator() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void add(Interceptor<? extends Message> i) {
        add(i, false);
    }
   
    public void add(Interceptor<? extends Message> i,boolean force) {
        PhaseInterceptor<? extends Message> pi = (PhaseInterceptor<? extends Message>)i;
        String phaseName = pi.getPhase();        
        Integer phase = nameMap.get(phaseName);
        
        if (phase == null) {
            LOG.warn("Skipping interceptor " + i.getClass().getName() 
                + ((phaseName == null) ? ": Phase declaration is missing." 
                : ": Phase " + phaseName + " specified does not exist."));
        } else {            
            if (LOG.isTraceEnabled()) {
                LOG.trace("Adding interceptor " + i + " to phase " + phaseName);
            }

            insert(phase, pi, force);
        }
        Collection<PhaseInterceptor<? extends Message>> extras  = pi.extInterceptors();
        if (extras != null) {
            for (PhaseInterceptor<? extends Message> p : extras) {
                add(p, force);
            }
        }
    }
    
    /**根据分阶不同插入拦截器*/
    private void insert(Integer phase,PhaseInterceptor<? extends Message> pi, boolean force) {
        Holder ih = new Holder(pi, phase);
        if (heads[phase] == null) {//该阶段还未设置任何拦截器
            heads[phase] = ih;
            tails[phase] = ih;
            hasAfters[phase] = !pi.getAfter().isEmpty();
            
            int idx = phase - 1;
            while (idx >= 0) {
                if (tails[idx] != null) {
                    break;
                }
                --idx;
            }
            if (idx >= 0) {
                //found something before us, in an earlier phase
                ih.prev = tails[idx];
                ih.next = tails[idx].next;
                if (ih.next != null) {
                    ih.next.prev = ih;
                }
                tails[idx].next = ih;
            } else {
                //did not find something before us, try after
                idx = phase + 1;
                while (idx < heads.length) {
                    if (heads[idx] != null) {
                        break;
                    }
                    ++idx;
                }
                
                if (idx != heads.length) {
                    //found something after us
                    ih.next = heads[idx];
                    heads[idx].prev = ih;
                }
            }
        }else{//该阶段已经设置了拦截器
            Set<String> before = pi.getBefore();
            Set<String> after = pi.getAfter();
            //此拦截器需要插入这个节点的前面,如果为null,说明此拦截器不在任何节点的前面,则为最后一个节点.
            Holder firstBefore = null;
            //此拦截器需要插入这个节点的后面
            Holder lastAfter = null;
            String id= pi.getId();
            //设置了前后
            if (hasAfters[phase] || !before.isEmpty()) {
                Holder ih2 = heads[phase];
                while (ih2 != tails[phase].next) {
                    PhaseInterceptor<? extends Message> cmp = ih2.interceptor;
                    String cmpId = cmp.getId();
                    if (cmpId != null && firstBefore == null
                        && (before.contains(cmpId)
                            || cmp.getAfter().contains(id))) {
                        firstBefore = ih2;
                    } 
                    if (cmp.getBefore().contains(id) 
                        || (cmpId != null && after.contains(cmpId))) {
                        lastAfter = ih2;
                    }
                    if (!force && cmpId.equals(id)) {
                        // 已经存在
                        return;
                    }
                    ih2 = ih2.next;
                }
                //后面没有节点,前面包含了所有的.此拦截器插入第一个节点的前面
                if (lastAfter == null && before.contains("*")) {
                    firstBefore = heads[phase];
                }
                
            }else if (!force) {
                Holder ih2 = heads[phase];
                while (ih2 != tails[phase].next) {
                    if (ih2.interceptor.getId().equals(id)) {
                        return;
                    }
                    ih2 = ih2.next;
                }
            }
            hasAfters[phase] |= !after.isEmpty();
            if (firstBefore == null
                && lastAfter == null
                && !before.isEmpty()
                && after.isEmpty()) {
                //前后都没有节点,单前面不为空,后面为空,.此拦截器插入第一个节点的前面
                firstBefore = heads[phase];
            }
            //该阶段最后节点
            if (firstBefore == null) {
                //just add new interceptor at the end
                ih.prev = tails[phase];
                ih.next = tails[phase].next;
                tails[phase].next = ih;
                
                if (ih.next != null) {
                    ih.next.prev = ih;
                }
                tails[phase] = ih;
            } else {
                ih.prev = firstBefore.prev;
                if (ih.prev != null) {
                    ih.prev.next = ih;
                }
                ih.next = firstBefore;
                firstBefore.prev = ih;
                
                if (heads[phase] == firstBefore) {
                    heads[phase] = ih;
                }
            }
        }
        if (iterator != null) {
            if(LOG.isTraceEnabled()){
                LOG.trace(toString(" chain modified "));
            }
        }
    }
   
    @Override
    public void add(Collection<Interceptor<? extends Message>> is) {
        add(is,false);

    }
    
    public void add(Collection<Interceptor<? extends Message>> is,boolean force) {
        if(is==null){
            return;
        }
        for(Interceptor<? extends Message> i:is){
            add(i,force);
        }
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.interceptor.InterceptorChain#remove(org.solmix.runtime.interceptor.Interceptor)
     */
    @Override
    public void remove(Interceptor<? extends Message> i) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.interceptor.InterceptorChain#doIntercept(org.solmix.runtime.exchange.Message)
     */
    @Override
    public boolean doIntercept(Message message) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.interceptor.InterceptorChain#doInterceptAfter(org.solmix.runtime.exchange.Message, java.lang.String)
     */
    @Override
    public boolean doInterceptAfter(Message message, String inteceptorId) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.interceptor.InterceptorChain#doInterceptAt(org.solmix.runtime.exchange.Message, java.lang.String)
     */
    @Override
    public boolean doInterceptAt(Message message, String inteceptorId) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.interceptor.InterceptorChain#listIterator()
     */
    @Override
    public ListIterator<Interceptor<? extends Message>> listIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public synchronized State getState() {
        return state;
    }

   
    @Override
    public void reset() {
        updateIterator();
        if (state == State.COMPLETE) {
            state = State.EXECUTING;
            iterator.reset();
        } else {
            iterator.reset();
        }
    }
    @Override
    public String toString() {
        return toString(""); 
    }

    private String toString(String message) {
        StringBuilder chain = new StringBuilder();
        chain.append("Chain ").append(super.toString()).append(message).append(
            ". Current flow:\n");

        for (int x = 0; x < phases.length; x++) {
            if (heads[x] != null) {
                chain.append("  ");
                chain.append(phases[x].getName()).append(" [");
                Holder i = heads[x];
                boolean first = true;
                while (i != tails[x].next) {
                    if (first) {
                        first = false;
                    } else {
                        chain.append(", ");
                    }
                    String nm = i.interceptor.getClass().getSimpleName();
                    chain.append(nm);
                    i = i.next;
                }
                chain.append("]\n");
            }
        }
        return chain.toString();
    }
    private void updateIterator() {
        if (iterator == null) {
            iterator = new PhaseIterator(heads);
            outputChainToLog(false);
            //System.out.println(toString());
        }
    }
    
    /**内部迭代器*/
    static final class PhaseIterator implements ListIterator<Interceptor<? extends Message>> {
        Holder heads[];
        /**前一个*/
        Holder prev;
        Holder first;
        
        public PhaseIterator(Holder h[]) {
            heads = h;
            first = findFirst();
        }
        /**
         * 
         */
        public void reset() {
            // TODO Auto-generated method stub
            
        }
        /** 起始位置*/
        private Holder findFirst() {
            for (int x = 0; x < heads.length; x++) {
                if (heads[x] != null) {
                    return heads[x];
                }
            }
            return null;
        }
       
        @Override
        public boolean hasNext() {
            if (prev == null) {
                return first != null;
            }
            return prev.next != null;
        }

        @Override
        public Interceptor<? extends Message> next() {
            if (prev == null) {
                if (first == null) {
                    throw new NoSuchElementException();
                }
                prev = first;
            } else {
                if (prev.next == null) {
                    throw new NoSuchElementException();
                }
                prev = prev.next;
            }
            return prev.interceptor;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.util.ListIterator#hasPrevious()
         */
        @Override
        public boolean hasPrevious() {
            // TODO Auto-generated method stub
            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.util.ListIterator#previous()
         */
        @Override
        public Interceptor<? extends Message> previous() {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.util.ListIterator#nextIndex()
         */
        @Override
        public int nextIndex() {
            // TODO Auto-generated method stub
            return 0;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.util.ListIterator#previousIndex()
         */
        @Override
        public int previousIndex() {
            // TODO Auto-generated method stub
            return 0;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.util.ListIterator#remove()
         */
        @Override
        public void remove() {
            // TODO Auto-generated method stub
            
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.util.ListIterator#set(java.lang.Object)
         */
        @Override
        public void set(Interceptor<? extends Message> e) {
            // TODO Auto-generated method stub
            
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.util.ListIterator#add(java.lang.Object)
         */
        @Override
        public void add(Interceptor<? extends Message> e) {
            // TODO Auto-generated method stub
            
        }
        
    }
    static final class Holder {
        PhaseInterceptor<? extends Message> interceptor;
        Holder next;
        Holder prev;
        int phaseIdx;
        
        Holder(PhaseInterceptor<? extends Message> i, int p) {
            interceptor = i;
            phaseIdx = p;
        }
        Holder(Holder p) {
            interceptor = p.interceptor;
            phaseIdx = p.phaseIdx;
        }
    }
}
