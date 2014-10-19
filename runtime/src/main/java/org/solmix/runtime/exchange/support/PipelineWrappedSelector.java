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

import java.io.Closeable;
import java.io.IOException;

import org.solmix.runtime.exchange.Endpoint;
import org.solmix.runtime.exchange.Exchange;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.Pipeline;
import org.solmix.runtime.exchange.PipelineSelector;
import org.solmix.runtime.exchange.Processor;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月17日
 */

public class PipelineWrappedSelector implements PipelineSelector, Closeable
{
    private final Pipeline pipeline;
    private  Endpoint endpoint;
    
    public PipelineWrappedSelector(Pipeline pipeline){
        this(pipeline,null);
    }

    public PipelineWrappedSelector(Pipeline pipeline,Endpoint ed){
        this.pipeline=pipeline;
        this.endpoint=ed;
    }
    
    @Override
    public void close() throws IOException {
        pipeline.close();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.PipelineSelector#prepare(org.solmix.runtime.exchange.Message)
     */
    @Override
    public void prepare(Message message) {
        Processor p= message.getExchange().get(Processor.class);
        if(p!=null){
            pipeline.setProcessor(p);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.PipelineSelector#select(org.solmix.runtime.exchange.Message)
     */
    @Override
    public Pipeline select(Message message) {
        return pipeline;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.PipelineSelector#complete(org.solmix.runtime.exchange.Exchange)
     */
    @Override
    public void complete(Exchange exchange) {
        try {
            if (exchange.getIn() != null) {
                pipeline.close(exchange.getIn());
            }
        } catch (IOException e) { }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.PipelineSelector#getEndpoint()
     */
    @Override
    public Endpoint getEndpoint() {
        return endpoint;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.PipelineSelector#setEndpoint(org.solmix.runtime.exchange.Endpoint)
     */
    @Override
    public void setEndpoint(Endpoint endpoint) {
       this.endpoint=endpoint;
    }

}
