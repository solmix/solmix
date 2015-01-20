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

package org.solmix.runtime.interceptor.support;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.solmix.runtime.exchange.Endpoint;
import org.solmix.runtime.exchange.Exchange;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.MessageList;
import org.solmix.runtime.exchange.Service;
import org.solmix.runtime.exchange.invoker.Invoker;
import org.solmix.runtime.exchange.support.DefaultMessage;
import org.solmix.runtime.interceptor.Fault;
import org.solmix.runtime.interceptor.phase.Phase;
import org.solmix.runtime.interceptor.phase.PhaseInterceptorChain;
import org.solmix.runtime.interceptor.phase.PhaseInterceptorSupport;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月20日
 */

public class ServiceInvokerInterceptor extends PhaseInterceptorSupport<Message> {

    public ServiceInvokerInterceptor() {
        super(Phase.INVOKE);
    }

    @Override
    public void handleMessage(final Message message) throws Fault {
        final Exchange exchange = message.getExchange();
        final Endpoint endpoint = exchange.get(Endpoint.class);
        final Service service = endpoint.getService();
        final Invoker invoker = service.getInvoker();
        Runnable invocation = new Runnable() {

            @Override
            public void run() {
                Exchange ex = message.getExchange();
                Object args = message.getContent(List.class);
                if (args == null) {
                    args = message.getContent(Object.class);
                }
                Object result = invoker.invoke(exchange, args);
                if (!exchange.isOneWay()) {
                    Endpoint ep = exchange.get(Endpoint.class);

                    Message outMessage = ex.getOut();
                    if (outMessage == null) {
                        outMessage = new DefaultMessage();
                        outMessage.setExchange(exchange);
                        outMessage = ep.getProtocol().createMessage(outMessage);
                        exchange.setOut(outMessage);
                    }
                    if (result != null) {
                        MessageList resList = null;
                        if (result instanceof MessageList) {
                            resList = (MessageList) result;
                        } else if (result instanceof List) {
                            resList = new MessageList((List<?>) result);
                        } else if (result.getClass().isArray()) {
                            resList = new MessageList((Object[]) result);
                        } else {
                            outMessage.setContent(Object.class, result);
                        }
                        if (resList != null) {
                            outMessage.setContent(List.class, resList);
                        }
                    }
                }
            }

        };
        Executor executor = endpoint.getExecutor();
        Executor executor2 = exchange.get(Executor.class);
        // 已经使用executor.
        if (executor == executor2 || executor == null) {
            invocation.run();
        } else {
            exchange.put(Executor.class, executor);
            // The current thread holds the lock on PhaseInterceptorChain.
            // In order to avoid the executor threads deadlocking on any of
            // synchronized PhaseInterceptorChain methods the current thread
            // needs to release the chain lock and re-acquire it after the
            // executor thread is done

            final PhaseInterceptorChain chain = (PhaseInterceptorChain) message.getInterceptorChain();
            final AtomicBoolean contextSwitched = new AtomicBoolean();
            final FutureTask<Object> o = new FutureTask<Object>(invocation, null) {

                @Override
                protected void done() {
                    super.done();
                    if (contextSwitched.get()) {
                        PhaseInterceptorChain.setCurrentMessage(chain, null);
                    }
                    chain.releaseChain();
                }

                @Override
                public void run() {
                    if (PhaseInterceptorChain.setCurrentMessage(chain, message)) {
                        contextSwitched.set(true);
                    }

                    synchronized (chain) {
                        super.run();
                    }
                }
            };
            synchronized (chain) {
                executor.execute(o);
                // the task will already be done if the executor uses the
                // current
                // thread
                // but the chain lock status still needs to be re-set
                chain.releaseAndAcquireChain();
            }
            try {
                o.get();
            } catch (InterruptedException e) {
                throw new Fault(e);
            } catch (ExecutionException e) {
                if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                } else {
                    throw new Fault(e.getCause());
                }
            }
        }
    }

}
