/*
 * Copyright 2015 The Solmix Project
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

import java.io.Closeable;
import java.io.IOException;

import org.solmix.exchange.Endpoint;
import org.solmix.exchange.Exchange;
import org.solmix.exchange.Message;
import org.solmix.exchange.Pipeline;
import org.solmix.exchange.PipelineSelector;
import org.solmix.exchange.Processor;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月19日
 */

public class PreexistingPipelineSelector implements PipelineSelector, Closeable
{
    private Pipeline pipeline;
    private Endpoint  endpoint;

    public PreexistingPipelineSelector(Pipeline pipeline, Endpoint endpoint)
    {
        this.pipeline=pipeline;
        this.endpoint=endpoint;
    }

    @Override
    public void close() throws IOException {
        pipeline.close();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.PipelineSelector#prepare(org.solmix.exchange.Message)
     */
    @Override
    public void prepare(Message message) {
        Processor pro = message.getExchange().get(Processor.class);
        if(pro!=null){
            pipeline.setProcessor(pro);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.PipelineSelector#select(org.solmix.exchange.Message)
     */
    @Override
    public Pipeline select(Message message) {
        return pipeline;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.PipelineSelector#complete(org.solmix.exchange.Exchange)
     */
    @Override
    public void complete(Exchange exchange) {
        try {
            if(exchange.getIn()!=null){
                pipeline.close(exchange.getIn());
            }
        } catch (IOException e) {
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.PipelineSelector#getEndpoint()
     */
    @Override
    public Endpoint getEndpoint() {
        return endpoint;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.PipelineSelector#setEndpoint(org.solmix.exchange.Endpoint)
     */
    @Override
    public void setEndpoint(Endpoint endpoint) {
       this.endpoint=endpoint;
    }

}
