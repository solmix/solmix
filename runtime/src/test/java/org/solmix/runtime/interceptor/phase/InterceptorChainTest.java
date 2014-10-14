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

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solmix.commons.collections.SortedArraySet;
import org.solmix.commons.util.Reflection;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.interceptor.Interceptor;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月14日
 */

public class InterceptorChainTest extends Assert
{
    private IMocksControl control;

    private PhaseInterceptorChain chain;

    private Message message;

    @Before
    public void setUp() {

        control = EasyMock.createNiceControl();
        message = control.createMock(Message.class);

        Phase phase1 = new Phase("phase1", 1);
        Phase phase2 = new Phase("phase2", 2);
        Phase phase3 = new Phase("phase3", 3);
        SortedSet<Phase> phases = new TreeSet<Phase>();
        phases.add(phase1);
        phases.add(phase2);
        phases.add(phase3);

        chain = new PhaseInterceptorChain(phases);
    }
    @After
    public void tearDown() {
        control.verify();
    }
    @Test
    public void testAddOne() throws Exception{
        PhaseInterceptorSupport<? extends Message> interceptor=createMockInterceptor("phase1","id1");
        control.replay();
        chain.add(interceptor);
        Iterator<Interceptor<? extends Message>> it = chain.iterator();
        assertSame(interceptor, it.next());
        assertTrue(!it.hasNext());
    }
    /**测试在同一个阶段的先后顺序*/
    @Test
    public void testAddTwoAtSamePhase() throws Exception{
        PhaseInterceptorSupport<? extends Message> p1=createMockInterceptor("phase1","p1");
        Set<String> after = new HashSet<String>();
        after.add("p1");
        PhaseInterceptorSupport<? extends Message> p2=createMockInterceptor("phase1","p2",null,after);
        control.replay();
        chain.add(p2);
        chain.add(p1);
        Iterator<Interceptor<? extends Message>> it = chain.iterator();
        assertSame(p1, it.next());
        assertSame(p2, it.next());
        assertTrue(!it.hasNext());
    }
    /**测试拦截器执行*/
    @Test
    public void testDoInterceptor() throws Exception{
        CountingPhaseInterceptor p1 = new CountingPhaseInterceptor("phase1", "p1");
        CountingPhaseInterceptor p2 = new CountingPhaseInterceptor("phase2", "p2");
        CountingPhaseInterceptor p3 = new CountingPhaseInterceptor("phase3", "p3");
        message.getInterceptorChain();
        EasyMock.expectLastCall().andReturn(chain).anyTimes();

        control.replay();
        chain.add(p1);
        chain.add(p2);
        chain.add(p3);
        chain.doIntercept(message);
        assertEquals(1, p1.invoked);
        assertEquals(1, p2.invoked);
        assertEquals(1, p3.invoked);
    }
    /**测试从指定拦截器开始执行*/
    @Test
    public void testDoInterceptorAfter() throws Exception{
        CountingPhaseInterceptor p1 = new CountingPhaseInterceptor("phase1", "p1");
        CountingPhaseInterceptor p2 = new CountingPhaseInterceptor("phase2", "p2");
        CountingPhaseInterceptor p3 = new CountingPhaseInterceptor("phase3", "p3");
        message.getInterceptorChain();
        EasyMock.expectLastCall().andReturn(chain).anyTimes();

        control.replay();
        chain.add(p1);
        chain.add(p2);
        chain.add(p3);
        chain.doInterceptAfter(message, p2.getId());
        assertEquals(0, p1.invoked);
        assertEquals(0, p2.invoked);
        assertEquals(1, p3.invoked);
        
        chain.reset();
        chain.doInterceptAt(message, p2.getId());
        assertEquals(0, p1.invoked);
        assertEquals(1, p2.invoked);
        assertEquals(2, p3.invoked);
    }
    @Test
    public void testSingleInterceptorFail() throws Exception {
        PhaseInterceptorSupport<Message> p = createMockInterceptor("phase1", "p1");
        setUpPhaseInterceptorInvocations(p, true, true);
        control.replay();
        chain.add(p);
        chain.doIntercept(message);
    }
    @Test
    public void testFault() throws Exception{
        CountingPhaseInterceptor p1 = new CountingPhaseInterceptor("phase1", "p1");
        FaultPhaseInterceptor p2 = new FaultPhaseInterceptor("phase2", "p2");
        CountingPhaseInterceptor p3 = new CountingPhaseInterceptor("phase3", "p3");
        message.getInterceptorChain();
        EasyMock.expectLastCall().andReturn(chain).anyTimes();

        control.replay();
        chain.add(p1);
        chain.add(p2);
        chain.add(p3);
        chain.doIntercept(message);
        assertEquals(1, p1.invoked);
        assertEquals(1, p2.invoked);
        assertEquals(0, p3.invoked);
        assertEquals(1, p1.faultInvoked);
        assertEquals(1, p2.faultInvoked);
    }
    /////////////////////////////////////////////////////////////////////////////////
    void setUpPhaseInterceptorInvocations(PhaseInterceptorSupport<Message> p,
        boolean fail, boolean expectFault) {
    p.handleMessage(message);
    if (fail) {
        EasyMock.expectLastCall().andThrow(new RuntimeException());
        message.setContent(EasyMock.eq(Exception.class),
                           EasyMock.isA(Exception.class));
        EasyMock.expectLastCall();
    } else {
        EasyMock.expectLastCall();
    }
    if (expectFault) {
        p.handleFault(message);
        EasyMock.expectLastCall();
    }
}
    private PhaseInterceptorSupport<Message> createMockInterceptor(
        String phase, String id) throws Exception{
        return createMockInterceptor(phase, id, null, null);
    }
    /**mock interceptor*/
    @SuppressWarnings("unchecked")
    private PhaseInterceptorSupport< Message> createMockInterceptor(
        String phase, String id,Set<String> before,Set<String> after) throws Exception {
        PhaseInterceptorSupport<Message> p = control.createMock(PhaseInterceptorSupport.class);
        
        if (after == null) {
            after = new SortedArraySet<String>();
        }
        if (before == null) {
            before = new SortedArraySet<String>();
        }
        Field f = PhaseInterceptorSupport.class.getDeclaredField("before");
        Reflection.setAccessible(f);
        f.set(p, before);
        
        f = PhaseInterceptorSupport.class.getDeclaredField("after");
        Reflection.setAccessible(f);
        f.set(p, after);

        f = PhaseInterceptorSupport.class.getDeclaredField("phase");
        Reflection.setAccessible(f);
        f.set(p, phase);

        f = PhaseInterceptorSupport.class.getDeclaredField("id");
        Reflection.setAccessible(f);
        f.set(p, id);

        return p;
    }

    public class CountingPhaseInterceptor extends PhaseInterceptorSupport<Message>
    {

        int invoked;
        int faultInvoked;
        public CountingPhaseInterceptor(String phase, String id)
        {
            super(id, phase);
        }

        @Override
        public void handleMessage(Message m) {
            invoked++;
        }
        @Override
        public void handleFault(Message m) {
            faultInvoked++;
        }

    }
    public class FaultPhaseInterceptor extends PhaseInterceptorSupport<Message>
    {

        int invoked;
        int faultInvoked;
        public FaultPhaseInterceptor(String phase, String id)
        {
            super(id, phase);
        }

        @Override
        public void handleMessage(Message m) {
            invoked++;
            throw new RuntimeException();
        }
        @Override
        public void handleFault(Message m) {
            faultInvoked++;
        }

    }

    public class InsertingPhaseInterceptor extends PhaseInterceptorSupport<Message>
    {
        int invoked;

        int faultInvoked;

        private final PhaseInterceptorChain insertionChain;

        private final PhaseInterceptorSupport<? extends Message> insertionInterceptor;

        public InsertingPhaseInterceptor(PhaseInterceptorChain c,
            PhaseInterceptorSupport<? extends Message> i, String phase,
            String id)
        {
            super(id, phase);
            insertionChain = c;
            insertionInterceptor = i;
        }

        @Override
        public void handleMessage(Message m) {
            insertionChain.add(insertionInterceptor);
            invoked++;
        }

        @Override
        public void handleFault(Message m) {
            faultInvoked++;
        }
    }
}
