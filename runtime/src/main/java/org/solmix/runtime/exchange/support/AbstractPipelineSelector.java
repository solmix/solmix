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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.exchange.Endpoint;
import org.solmix.runtime.exchange.Exchange;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.MessageUtils;
import org.solmix.runtime.exchange.Pipeline;
import org.solmix.runtime.exchange.PipelineFactory;
import org.solmix.runtime.exchange.PipelineFactoryManager;
import org.solmix.runtime.exchange.PipelineSelector;
import org.solmix.runtime.exchange.Processor;
import org.solmix.runtime.exchange.model.EndpointInfo;
import org.solmix.runtime.interceptor.Fault;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月13日
 */

public abstract class AbstractPipelineSelector implements PipelineSelector,
    Closeable {

    protected static final String KEEP_PIPELINE_ALIVE = "KeepPipelineAlive";

    protected List<Pipeline> pipelines = new CopyOnWriteArrayList<Pipeline>();

    protected Endpoint endpoint;

    public AbstractPipelineSelector() {

    }

    public AbstractPipelineSelector(Pipeline pipeline) {
        if (pipeline != null) {
            pipelines.add(pipeline);
        }
    }

    @Override
    public void close() throws IOException {
        for (Pipeline p : pipelines) {
            p.close();
        }
        pipelines.clear();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.PipelineSelector#complete(org.solmix.runtime.exchange.Exchange)
     */
    @Override
    public void complete(Exchange exchange) {
        // 保持活动
        if (MessageUtils.isTrue(exchange.get(KEEP_PIPELINE_ALIVE))) {
            return;
        }
        try {
            final Message inMsg = exchange.getIn();
            if (inMsg != null) {
                Pipeline pl = exchange.getOut().get(Pipeline.class);
                if (pl == null) {
                    getSelectedPipeline(inMsg).close(inMsg);
                } else {
                    pl.close(exchange.getIn());
                }
            }
        } catch (IOException e) {
            // IGNORE
        }
    }

    /**
     * @param in
     */
    protected Pipeline getSelectedPipeline(Message msg) {
        Pipeline pl = findAdaptePipeline(msg);
        if (pl == null) {
            Exchange ex = msg.getExchange();
            EndpointInfo info = endpoint.getEndpointInfo();
            final String transportID = info.getTransporter();
            try {
                PipelineFactoryManager pfm = ex.getContainer().getExtension(
                    PipelineFactoryManager.class);

                if (pfm != null) {
                    PipelineFactory factory = pfm.getFactory(transportID);
                    if (factory != null) {
                        pl = createPipeline(msg, ex, factory);
                    } else {
                        getLogger().warn(
                            "PipelineFactory not found for:"
                                + info.getAddress());
                    }
                } else {
                    getLogger().warn("PipelineFactoryManager not found");
                }
            } catch (Exception e) {
                throw new Fault(e);
            }
        }
        if(pl!=null&&pl.getAddress()!=null){
            replaceEndpointAddressIfNeeded(msg, pl.getAddress(), pl);
        }
        msg.put(Pipeline.class, pl);
        return pl;
    }

    /**
     * @param msg
     * @param address
     * @param pl
     */
    protected boolean replaceEndpointAddressIfNeeded(Message msg, String address,
        Pipeline pl) {
       return false;
    }

    /**
     * @param msg
     * @param ex
     * @param factory
     */
    protected Pipeline createPipeline(Message msg, Exchange ex,
        PipelineFactory factory) throws IOException {
        Pipeline pl = null;
        synchronized (endpoint) {
            if (!pipelines.isEmpty()) {
                pl = findAdaptePipeline(msg);
                if (pl != null) {
                    return pl;
                }
            }

            EndpointInfo info = endpoint.getEndpointInfo();
            String msgAdd = (String) msg.get(Message.ENDPOINT_ADDRESS);
            if (StringUtils.isEmpty(msgAdd) || msgAdd.equals(info.getAddress())) {
                pl = factory.getPipeline(info, ex.getContainer());
                replaceEndpointAddressIfNeeded(msg, info.getAddress(), pl);
            } else {
                pl = factory.getPipeline(info, msgAdd, ex.getContainer());
            }

            Processor processor = ex.get(Processor.class);
            if (processor != null) {
                pl.setProcessor(processor);
            } else {
                getLogger().warn("Message Processor not found!");
            }
            pipelines.add(pl);
        }
        return pl;
    }

    protected abstract Logger getLogger();

    protected Pipeline findAdaptePipeline(Message msg) {

        Pipeline pl = msg.get(Pipeline.class);
        // found in out.
        if (pl == null && msg.getExchange() != null
            && msg.getExchange().getOut() != null
            && msg.getExchange().getOut() != msg) {
            pl = msg.getExchange().getOut().get(Pipeline.class);
        }
        if (pl != null) {
            return pl;
        }
        for (Pipeline p : pipelines) {
            if (p.getAddress() == null)
                continue;
            String pipeAdd = p.getAddress();

            String eiAdd = endpoint.getEndpointInfo().getAddress();
            String msgAdd = (String) msg.get(Message.ENDPOINT_ADDRESS);
            if (msgAdd != null) {
                eiAdd = msgAdd;
            }
            int idx = pipeAdd.indexOf(':');
            pipeAdd = idx == -1 ? "" : pipeAdd.substring(0, idx);
            idx = eiAdd.indexOf(':');
            eiAdd = idx == -1 ? "" : eiAdd.substring(0, idx);
            if (pipeAdd.equalsIgnoreCase(eiAdd)) {
                return p;
            }
        }
        // 找不到...
        for (Pipeline p : pipelines) {
            if (p.getAddress() == null)
                return p;
        }
        return null;
    }

    @Override
    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;

    }

}
