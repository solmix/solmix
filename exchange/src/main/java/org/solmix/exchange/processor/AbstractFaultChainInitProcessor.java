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

package org.solmix.exchange.processor;

import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.ClassLoaderUtils;
import org.solmix.commons.util.ClassLoaderUtils.ClassLoaderHolder;
import org.solmix.exchange.Endpoint;
import org.solmix.exchange.Exchange;
import org.solmix.exchange.ExchangeException;
import org.solmix.exchange.Message;
import org.solmix.exchange.Processor;
import org.solmix.exchange.interceptor.Fault;
import org.solmix.exchange.interceptor.FaultType;
import org.solmix.exchange.interceptor.phase.Phase;
import org.solmix.exchange.interceptor.phase.PhaseInterceptorChain;
import org.solmix.exchange.interceptor.phase.PhasePolicy;
import org.solmix.exchange.support.DefaultMessage;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月18日
 */

public abstract class AbstractFaultChainInitProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractFaultChainInitProcessor.class);

    /** 切面步骤定义 */
    protected final PhasePolicy phasePolicy;

    private final Container container;

    private ClassLoader loader;

    public AbstractFaultChainInitProcessor(Container c, PhasePolicy phasePolicy) {
        this.container = c;
        this.phasePolicy = phasePolicy;
        if (container != null) {
            loader = container.getExtension(ClassLoader.class);
        }
    }

    @Override
    public void process(Message message) throws ExchangeException {
        assert message != null;
        Container orig = ContainerFactory.getAndSetThreadDefaultContainer(container);
        ClassLoaderHolder origLoader = null;
        try {
            if (loader != null) {
                origLoader = ClassLoaderUtils.setThreadContextClassloader(loader);
            }
            Exchange exchange = message.getExchange();

            Message faultMessage = null;
            if (isOutMessage()) {
                Exception ex = message.getContent(Exception.class);
                if (!(ex instanceof Fault)) {
                    ex = new Fault(ex);
                }
                FaultType type = message.get(FaultType.class);
                faultMessage = exchange.getOut();
                if (faultMessage == null) {
                    faultMessage = new DefaultMessage();
                    faultMessage.setExchange(exchange);
                    faultMessage = exchange.get(Endpoint.class).getProtocol().createMessage(
                        faultMessage);
                }
                faultMessage.setContent(Exception.class, ex);
                if (type != null) {
                    faultMessage.put(FaultType.class, type);
                }
                exchange.setOut(null);
                exchange.setOutFault(faultMessage);
            } else {
                faultMessage = message;
                exchange.setIn(null);
                exchange.setInFault(faultMessage);
            }
            PhaseInterceptorChain chain = new PhaseInterceptorChain(getPhases());
            // 初始化链
            initializeInterceptors(faultMessage.getExchange(), chain);

            faultMessage.setInterceptorChain(chain);
            try {
                // 流转
                chain.doIntercept(faultMessage);
            } catch (Exception exc) {
                LOG.error("Error occurred during error handling, give up!", exc);
                throw new RuntimeException(exc);
            }
        } finally {
            if (orig != container) {
                ContainerFactory.setThreadDefaultContainer(orig);
            }
            if (origLoader != null) {
                origLoader.reset();
            }
        }

    }

    /**
     * return container
     */
    public Container getContainer() {
        return container;
    }

    protected void initializeInterceptors(Exchange exchange,
        PhaseInterceptorChain chain) {
    }

    /**
     * @return
     */
    protected abstract SortedSet<Phase> getPhases();

    /**
     * @return
     */
    protected abstract boolean isOutMessage();

}
