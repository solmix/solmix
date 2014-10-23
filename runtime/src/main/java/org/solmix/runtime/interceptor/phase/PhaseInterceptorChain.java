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

import java.lang.ref.WeakReference;
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
import org.solmix.runtime.exchange.MessageUtils;
import org.solmix.runtime.exchange.Processor;
import org.solmix.runtime.interceptor.Fault;
import org.solmix.runtime.interceptor.FaultType;
import org.solmix.runtime.interceptor.Interceptor;
import org.solmix.runtime.interceptor.InterceptorChain;
import org.solmix.runtime.interceptor.SuspendedException;


/**
 * 分阶拦截链,分截拦截器可在链中按不同阶段遍历执行.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月12日
 */
/**
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

    private static final String PREVIOUS_MESSAGE =  "_slx."+PhaseInterceptorChain.class.getName() + ".previous";;
    /**分阶队列*/
    private final Phase phases[];
    
    /**阶段名称对应序号*/
    private final Map<String, Integer> nameMap;
    
    /** 拦截链状态   */
    private  State state;
    
    /** 拦截器迭代器   */
    private PhaseIterator iterator;
    
    /**  heads[phase]为该阶段拦截器的第一个节点 heads[phase] 和tails[phase]直接的节点为该阶段(phase)所有的拦截器*/
    private final Holder heads[];
    
    /** tails[phase]为该阶段拦截器的最后一个节点    */
    private final Holder tails[];
    
    private final boolean hasAfters[];
    
    private boolean faultOccurred;
    private boolean chainReleased;

    private Processor faultProcessor;

    private Message pausedMessage;
    
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
    /**克隆*/
    private PhaseInterceptorChain(PhaseInterceptorChain src){
        state = State.EXECUTING;
        
        //immutable, just repoint
        nameMap = src.nameMap;
        phases = src.phases;
        
        int length = phases.length;
        hasAfters = new boolean[length];
        System.arraycopy(src.hasAfters, 0, hasAfters, 0, length);
        
        heads = new Holder[length];
        tails = new Holder[length];
        
        Holder last = null;
        for (int x = 0; x < length; x++) {
            Holder ih = src.heads[x];
            while (ih != null
                && ih.phaseIdx == x) {
                Holder ih2 = new Holder(ih);
                ih2.prev = last;
                if (last != null) {
                    last.next = ih2;
                }
                if (heads[x] == null) {
                    heads[x] = ih2;
                }
                tails[x] = ih2;
                last = ih2;
                ih = ih.next;
            }
        }
    }
    public PhaseInterceptorChain cloneChain() {
        return new PhaseInterceptorChain(this);
    }
    public static Message getCurrentMessage() {
        return CURRENT_MESSAGE.get();
    }
    
/*    public static boolean setCurrentMessage(PhaseInterceptorChain chain, Message m) {
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
        
    }*/
    
    public synchronized void releaseAndAcquireChain() {
        while (!chainReleased) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
                // ignore
            }
        }
        chainReleased = false;
    }
    public synchronized void abort() {
        this.state = InterceptorChain.State.ABORTED;
    }
    public synchronized void releaseChain() {
        this.chainReleased = true;
        this.notifyAll();
    }
    @Override
    public Iterator<Interceptor<? extends Message>> iterator() {
        return listIterator();
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
        if (heads[phase] == null) {//还未设置任何拦截器
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
        }else{//已经设置了拦截器
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
        PhaseIterator it = new PhaseIterator(heads);
        while (it.hasNext()) {
            Holder holder = it.nextHolder();
            if (holder.interceptor == i) {
                remove(holder);
                return;
            }
        }
    }

    /**
     * @param holder
     */
    private void remove(Holder i) {
        if (i.prev != null) {
            i.prev.next = i.next;
        }
        if (i.next != null) {
            i.next.prev = i.prev;
        }
        int ph = i.phaseIdx;
        if (heads[ph] == i) {
            if (i.next != null
                && i.next.phaseIdx == ph) {
                heads[ph] = i.next;
            } else {
                heads[ph] = null;
                tails[ph] = null;
            }
        }
        if (tails[ph] == i) {
            if (i.prev != null
                && i.prev.phaseIdx == ph) {
                tails[ph] = i.prev;
            } else {
                heads[ph] = null;
                tails[ph] = null;
            }
        }
    }
   
    @Override
    public boolean doIntercept(Message message) {
        updateIterator();

        Message oldMessage = CURRENT_MESSAGE.get();
        try {
            CURRENT_MESSAGE.set(message);
            if (oldMessage != null 
                && !message.containsKey(PREVIOUS_MESSAGE)
                && message != oldMessage
                && message.getExchange() != oldMessage.getExchange()) {
                message.put(PREVIOUS_MESSAGE, new WeakReference<Message>(oldMessage));
            }
            while (state == State.EXECUTING && iterator.hasNext()) {
                try {
                    @SuppressWarnings("unchecked")
                    Interceptor<Message> currentInterceptor =(Interceptor<Message>) iterator.next();
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Invoking handleMessage on interceptor " + currentInterceptor);
                    }
                    currentInterceptor.handleMessage(message);
                    if (state == State.SUSPENDED) {
                        throw new SuspendedException();
                    }
                }catch(SuspendedException suspended){
                    if (iterator.hasPrevious()) {
                        iterator.previous();
                    }
                    pause();
                    throw suspended;
                } catch (RuntimeException ex) {
                    
                    if (!faultOccurred) {
                        faultOccurred = true;
                        message.setContent(Exception.class, ex);
                        handleException(message);
                        Exception ex2 = message.getContent(Exception.class);
                        if (ex2 == null) {
                            ex2 = ex;
                        }
                        defaultLogging(message,ex2,"");
                        boolean isOneWay = false;
                        if (message.getExchange() != null) {
                            if (message.getContent(Exception.class) != null) {
                                message.getExchange().put(Exception.class, ex2);
                            }
                            isOneWay = message.getExchange().isOneWay() 
                                && MessageUtils.getBoolean(message, Message.ONEWAY);
                        }
                       
                        if (faultProcessor != null && !isOneWay) {
                            message.getExchange().setOneWay(false);
                            faultProcessor.process(message);
                        }
                    }
                    state = State.ABORTED;
                } 
            }
            if (state == State.EXECUTING) {
                state = State.COMPLETE;
            }
            return state == State.COMPLETE;
        } finally {
            CURRENT_MESSAGE.set(oldMessage);
        }
    }

   
    private void defaultLogging(Message message, Exception ex,
        String description) {
        FaultType type = message.get(FaultType.class);
        if (type == FaultType.CHECKED_APPLICATION_FAULT) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Application " + description
                    + "has thrown exception, unwinding now", ex);
            } else if (LOG.isInfoEnabled()) {
                Throwable t = ex;
                if (ex instanceof Fault && ex.getCause() != null) {
                    t = ex.getCause();
                }
                LOG.info("Application " + description
                    + "has thrown exception, unwinding now: "
                    + t.getClass().getName() + ": " + ex.getMessage());
            }
        } else if (LOG.isWarnEnabled()) {
            if (type == FaultType.UNCHECKED_APPLICATION_FAULT) {
                LOG.warn("Application " + description
                    + "has thrown exception, unwinding now", ex);
            } else {
                LOG.warn("Interceptor for " + description
                    + "has thrown exception, unwinding now", ex);
            }
        }
    }
    /**释放消息*/
    public void handleException(Message message) {
        while (iterator.hasPrevious()) {
            @SuppressWarnings("unchecked")
            Interceptor<Message> currentInterceptor = (Interceptor<Message>)iterator.previous();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Invoking handleException on interceptor " + currentInterceptor);
            }
            try {
                currentInterceptor.handleFault(message);
            } catch (RuntimeException e) {
                LOG.warn( "Exception in handleFault on interceptor " + currentInterceptor, e);
                throw e;
            } catch (Exception e) {
                LOG.warn( "Exception in handleFault on interceptor " + currentInterceptor, e);
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public boolean doInterceptAfter(Message message, String inteceptorId) {
        updateIterator();
        while (state == State.EXECUTING && iterator.hasNext()) {
            PhaseInterceptor<? extends Message> currentInterceptor 
                = (PhaseInterceptor<? extends Message>)iterator.next();
            if (currentInterceptor.getId().equals(inteceptorId)) {
                break;
            }
        }
        return doIntercept(message);
    }

    @Override
    public boolean doInterceptAt(Message message, String inteceptorId) {
        updateIterator();
        while (state == State.EXECUTING && iterator.hasNext()) {
            PhaseInterceptor<? extends Message> currentInterceptor 
                = (PhaseInterceptor<? extends Message>)iterator.next();
            if (currentInterceptor.getId().equals(inteceptorId)) {
                iterator.previous();
                break;
            }
        }
        return doIntercept(message);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.interceptor.InterceptorChain#listIterator()
     */
    @Override
    public ListIterator<Interceptor<? extends Message>> listIterator() {
        return new PhaseIterator(heads);
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
    
    /**初始化迭代器*/
    private void updateIterator() {
        if (iterator == null) {
            iterator = new PhaseIterator(heads);
           if(LOG.isTraceEnabled()){
               LOG.trace(toString("created "));
           }
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
         * @return
         */
        public Holder nextHolder() {
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
            return prev;
        }
        
        /**重置状态*/
        public void reset() {
            prev = null;
            first = findFirst();
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

        @Override
        public boolean hasPrevious() {
            return prev != null;
        }

      
        @Override
        public Interceptor<? extends Message> previous() {
            if (prev == null) {
                throw new NoSuchElementException();
            }
            Holder tmp = prev;
            prev = prev.prev;
            return tmp.interceptor;
        }

        @Override
        public int nextIndex() {
            throw new UnsupportedOperationException();
        }

       
        @Override
        public int previousIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
            
        }

        @Override
        public void set(Interceptor<? extends Message> e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Interceptor<? extends Message> e) {
            throw new UnsupportedOperationException();
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
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.interceptor.InterceptorChain#getFaultProcessor()
     */
    @Override
    public Processor getFaultProcessor() {
        return faultProcessor;
    }
  
    @Override
    public void setFaultProcessor(Processor processor) {
        this.faultProcessor=processor;
        
    }
    @Override
    public synchronized void pause() {
        state = State.PAUSED;
        pausedMessage = CURRENT_MESSAGE.get();
    }
    @Override
    public synchronized void unpause() {
        if (state == State.PAUSED || state == State.SUSPENDED) {
            state = State.EXECUTING;
            pausedMessage = null;
        }
    }
    
    @Override
    public synchronized void suspend() {
        state = State.SUSPENDED;
        pausedMessage = CURRENT_MESSAGE.get();
    }

    @Override
    public synchronized void resume() {
        if (state == State.PAUSED || state == State.SUSPENDED) {
            state = State.EXECUTING;
            Message m = pausedMessage;
            pausedMessage = null;
            doIntercept(m);
        }
    }
}
