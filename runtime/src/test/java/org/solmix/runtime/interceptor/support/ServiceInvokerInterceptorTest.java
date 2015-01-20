/**
 * Copyright (c) 2015 The Solmix Project
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
package org.solmix.runtime.interceptor.support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;
import org.solmix.runtime.exchange.Endpoint;
import org.solmix.runtime.exchange.Exchange;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.Service;
import org.solmix.runtime.exchange.invoker.Invoker;
import org.solmix.runtime.exchange.model.ServiceInfo;
import org.solmix.runtime.exchange.support.DefaultExchange;
import org.solmix.runtime.exchange.support.DefaultMessage;
import org.solmix.runtime.exchange.support.DefaultService;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年1月16日
 */

public class ServiceInvokerInterceptorTest extends Assert {
    @Test
    public void testInterceptor() throws Exception {
        ServiceInvokerInterceptor intc = new ServiceInvokerInterceptor();
        
        Message m = new DefaultMessage();
        Exchange exchange = new DefaultExchange();
        m.setExchange(exchange);
        exchange.setIn(m);
        
        exchange.setOut(new DefaultMessage());
        
        TestInvoker i = new TestInvoker();
        Endpoint endpoint = createEndpoint(i);
        exchange.put(Endpoint.class, endpoint);
        Object input = new Object();
        List<Object> lst = new ArrayList<Object>();
        lst.add(input);
        m.setContent(List.class, lst);
        
        intc.handleMessage(m);
        
        assertTrue(i.invoked);
        
        List<?> list = exchange.getOut().getContent(List.class);
        assertEquals(input, list.get(0));
    }
    
    Endpoint createEndpoint(Invoker i) throws Exception {
        IMocksControl control = EasyMock.createNiceControl();
        Endpoint endpoint = control.createMock(Endpoint.class);

        Service service = new DefaultService((ServiceInfo)null);
        service.setInvoker(i);
        service.setExecutor(new SimpleExecutor());
        EasyMock.expect(endpoint.getService()).andReturn(service).anyTimes();
        
        control.replay();

        return endpoint;
    }
    
    static class TestInvoker implements Invoker {
        boolean invoked;
        @Override
        public Object invoke(Exchange exchange, Object o) {
            invoked = true;
            assertNotNull(exchange);
            assertNotNull(o);
            return o;
        }
    }
    
    static class SimpleExecutor implements Executor {

        @Override
        public void execute(Runnable command) {
            command.run();
        }
        
    }
}
