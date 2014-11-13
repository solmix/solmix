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

import org.solmix.runtime.exchange.Endpoint;
import org.solmix.runtime.exchange.Exchange;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.MessageUtils;
import org.solmix.runtime.exchange.Pipeline;
import org.solmix.runtime.exchange.PipelineSelector;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年11月13日
 */

public abstract class AbstractPipelineSelector implements PipelineSelector, Closeable
{
    protected static final String KEEP_PIPELINE_ALIVE = "KeepPipelineAlive";
    
    protected List<Pipeline> pipelines = new CopyOnWriteArrayList<Pipeline>();
   
    
    protected Endpoint endpoint;
    public AbstractPipelineSelector(){
        
    }
    public AbstractPipelineSelector(Pipeline pipeline){
        if(pipeline!=null){
            pipelines.add(pipeline);
        }
    }
    
    @Override
    public void close() throws IOException {
       for(Pipeline p:pipelines){
           p.close();
       }
       pipelines.clear();
    }

   
    @Override
    public Pipeline select(Message message) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.PipelineSelector#complete(org.solmix.runtime.exchange.Exchange)
     */
    @Override
    public void complete(Exchange exchange) {
        //保持活动
       if(MessageUtils.isTrue(exchange.get(KEEP_PIPELINE_ALIVE))){
           return;
       }
       
       try{
          if( exchange.getIn()!=null){
              Pipeline pl=   exchange.getOut().get(Pipeline.class);
              if(pl==null){
                  getSelectedPipeline(exchange.getIn()).close(exchange.getIn());;
              }else{
                  pl.close(exchange.getIn());
              }
          }
       }catch (IOException e) {
           //IGNORE
       }

    }
    
    /**
     * @param in
     */
    private Pipeline getSelectedPipeline(Message msg) {
        Pipeline pl= findAdaptePipeline(msg);
        return null;
        // TODO Auto-generated method stub
        
    }
   
    protected Pipeline findAdaptePipeline(Message msg) {
        
        Pipeline pl= msg.get(Pipeline.class);
        //found in out.
        if(pl==null&&msg.getExchange()!=null&&msg.getExchange().getOut()!=null
            &&msg.getExchange().getOut()!=msg){
            pl=msg.getExchange().getOut().get(Pipeline.class);
        }
        if(pl!=null){
            return pl;
        }
        for(Pipeline p:pipelines){
            if(p.getAddress()==null)
                continue;
            String pipeAdd=p.getAddress();
            
           String eiAdd= endpoint.getEndpointInfo().getAddress();
           String msgAdd=(String) msg.get(Message.ENDPOINT_ADDRESS);
           if(msgAdd!=null){
               eiAdd=msgAdd;
           }
           int idx = pipeAdd.indexOf(':');
           pipeAdd = idx == -1 ? "" : pipeAdd.substring(0, idx);
           idx = eiAdd.indexOf(':');
           eiAdd = idx == -1 ? "" : eiAdd.substring(0, idx);
           if(pipeAdd.equalsIgnoreCase(eiAdd)){
               return p;
           }
        }
        //找不到...
        for(Pipeline p:pipelines){
            if(p.getAddress()==null)
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
        this.endpoint=endpoint;

    }

}
