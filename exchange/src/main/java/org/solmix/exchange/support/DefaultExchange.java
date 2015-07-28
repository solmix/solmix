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

package org.solmix.exchange.support;

import org.solmix.commons.collections.StringTypeMapper;
import org.solmix.exchange.Endpoint;
import org.solmix.exchange.Exchange;
import org.solmix.exchange.Message;
import org.solmix.exchange.Pipeline;
import org.solmix.exchange.PipelineSelector;
import org.solmix.runtime.Container;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月14日
 */

public class DefaultExchange extends StringTypeMapper implements Exchange {

    private static final long serialVersionUID = -2559302429404755489L;

    protected Container container;

    private Message in;

    private Message out;

    private Message inFault;

    private Message outFault;

    private Endpoint endpoint;

    private boolean oneWay;

    private boolean sync;

    public DefaultExchange(DefaultExchange exchange) {
        super(exchange);
        this.container = exchange.container;
    }

    /**  */
    public DefaultExchange() {
    }

    @Override
    public <T> void put(Class<T> key, T value) {
        super.put(key, value);
        if (key == Container.class) {
            container = (Container) value;
        } else if (key == Endpoint.class) {
            endpoint = (Endpoint) value;
        }
    }

    @Override
    public void clear() {
        super.clear();
        oneWay = false;
        sync = true;
        in = null;
        out = null;
        inFault = null;
        outFault = null;
        container = null;
        endpoint = null;
    }

    @Override
    public Message getIn() {
        return in;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Exchange#setIn(org.solmix.exchange.Message)
     */
    @Override
    public void setIn(Message in) {
        this.in = in;
        if (in != null) {
            in.setExchange(this);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Exchange#getOut()
     */
    @Override
    public Message getOut() {
        return out;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Exchange#setOut(org.solmix.exchange.Message)
     */
    @Override
    public void setOut(Message out) {
        this.out = out;
        if (out != null) {
            out.setExchange(this);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Exchange#getInFault()
     */
    @Override
    public Message getInFault() {
        return inFault;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Exchange#setInFault(org.solmix.exchange.Message)
     */
    @Override
    public void setInFault(Message m) {
        this.inFault = m;
        if (m != null) {
            m.setExchange(this);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Exchange#getOutFault()
     */
    @Override
    public Message getOutFault() {
        return outFault;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Exchange#setOutFault(org.solmix.exchange.Message)
     */
    @Override
    public void setOutFault(Message m) {
        this.outFault = m;
        if (m != null) {
            m.setExchange(this);
        }

    }

    @Override
    public Pipeline getPipeline(Message message) {
        return get(PipelineSelector.class) != null 
            ? get(PipelineSelector.class).select(message)
            : null;
    }

    @Override
    public void setPipeline(Pipeline pipeline) {
        put(PipelineSelector.class, new PipelineWrappedSelector(pipeline,
            getEndpoint()));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Exchange#getContainer()
     */
    @Override
    public Container getContainer() {
        return container;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Exchange#getEndpoint()
     */
    @Override
    public Endpoint getEndpoint() {
        return endpoint;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Exchange#isOneWay()
     */
    @Override
    public boolean isOneWay() {
        return oneWay;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Exchange#setOneWay(boolean)
     */
    @Override
    public void setOneWay(boolean b) {
        this.oneWay = b;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Exchange#isSync()
     */
    @Override
    public boolean isSync() {
        return sync;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Exchange#setSync(boolean)
     */
    @Override
    public void setSync(boolean b) {
        this.sync = b;
    }
}
