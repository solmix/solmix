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
package org.solmix.runtime.exchange.support;

import org.solmix.runtime.Container;
import org.solmix.runtime.exchange.Endpoint;
import org.solmix.runtime.exchange.Exchange;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.Pipeline;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月14日
 */

public class DefaultExchange extends StringTypeMapper implements Exchange
{

    private static final long serialVersionUID = -2559302429404755489L;

    protected final Container container;
    private Message in;
    private Message out;
    
   public DefaultExchange(DefaultExchange exchange){
       super(exchange);
       this.container=exchange.container;
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Exchange#getIn()
     */
    @Override
    public Message getIn() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Exchange#setIn(org.solmix.runtime.exchange.Message)
     */
    @Override
    public void setIn(Message in) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Exchange#getOut()
     */
    @Override
    public Message getOut() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Exchange#setOut(org.solmix.runtime.exchange.Message)
     */
    @Override
    public void setOut(Message out) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Exchange#getInFault()
     */
    @Override
    public Message getInFault() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Exchange#setInFault(org.solmix.runtime.exchange.Message)
     */
    @Override
    public void setInFault(Message m) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Exchange#getOutFault()
     */
    @Override
    public Message getOutFault() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Exchange#setOutFault(org.solmix.runtime.exchange.Message)
     */
    @Override
    public void setOutFault(Message m) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Exchange#getPipeline()
     */
    @Override
    public Pipeline getPipeline() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Exchange#setPipeline(org.solmix.runtime.exchange.Pipeline)
     */
    @Override
    public void setPipeline(Pipeline pipeline) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Exchange#isOneWay()
     */
    @Override
    public boolean isOneWay() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Exchange#setOneWay(boolean)
     */
    @Override
    public void setOneWay(boolean b) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Exchange#isSync()
     */
    @Override
    public boolean isSync() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Exchange#setSync(boolean)
     */
    @Override
    public void setSync(boolean b) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Exchange#getContainer()
     */
    @Override
    public Container getContainer() {
        return container;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Exchange#getEndpoint()
     */
    @Override
    public Endpoint getEndpoint() {
        // TODO Auto-generated method stub
        return null;
    }

}
