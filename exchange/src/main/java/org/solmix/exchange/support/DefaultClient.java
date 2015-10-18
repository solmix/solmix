/**
 * Copyright (c) 2014 The Solmix Project
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.ClassLoaderUtils;
import org.solmix.commons.util.ClassLoaderUtils.ClassLoaderHolder;
import org.solmix.exchange.Client;
import org.solmix.exchange.ClientCallback;
import org.solmix.exchange.ClientLifeCycleManager;
import org.solmix.exchange.Endpoint;
import org.solmix.exchange.Exchange;
import org.solmix.exchange.ExchangeRuntimeException;
import org.solmix.exchange.Message;
import org.solmix.exchange.MessageList;
import org.solmix.exchange.MessageUtils;
import org.solmix.exchange.Pipeline;
import org.solmix.exchange.PipelineSelector;
import org.solmix.exchange.Processor;
import org.solmix.exchange.Protocol;
import org.solmix.exchange.Service;
import org.solmix.exchange.data.DataProcessor;
import org.solmix.exchange.interceptor.Fault;
import org.solmix.exchange.interceptor.Interceptor;
import org.solmix.exchange.interceptor.InterceptorChain;
import org.solmix.exchange.interceptor.InterceptorProvider;
import org.solmix.exchange.interceptor.phase.PhaseChainCache;
import org.solmix.exchange.interceptor.phase.PhaseInterceptorChain;
import org.solmix.exchange.interceptor.phase.PhasePolicy;
import org.solmix.exchange.interceptor.support.InterceptorProviderSupport;
import org.solmix.exchange.model.InterfaceInfo;
import org.solmix.exchange.model.MessageInfo;
import org.solmix.exchange.model.OperationInfo;
import org.solmix.exchange.model.ProtocolInfo;
import org.solmix.exchange.model.ServiceInfo;
import org.solmix.exchange.processor.ClientOutFaultProcessor;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年1月5日
 */

public class DefaultClient extends InterceptorProviderSupport implements Client {

    public static final String EXECUTOR_USED = Executor.class.getName() + ".EXECUTOR_USED";

    public static final String THREAD_LOCAL_REQUEST_CONTEXT = DefaultClient.class.getName() + ".THREAD_LOCAL_REQUEST_CONTEXT";

    public static final String FINISHED = "exchange.finished";

    public static final String SYNC_TIMEOUT = "synchronous.timeout";
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultClient.class);
    
    private static final long serialVersionUID = -5525728046581732373L;
    
    protected PhaseChainCache inboundChainCache = new PhaseChainCache();
    
    protected PhaseChainCache outboundChainCache = new PhaseChainCache();
    
    protected Map<Thread, EchoContext> requestContext 
    = Collections.synchronizedMap(new WeakHashMap<Thread, EchoContext>());

    protected Map<Thread, Map<String, Object>> responseContext 
    = Collections.synchronizedMap(new WeakHashMap<Thread, Map<String, Object>>());

    protected Map<String, Object> currentRequestContext = new ConcurrentHashMap<String, Object>(8, 0.75f, 4);

    protected int synchronousTimeout = 60000;
    
    protected Container container;
    
    protected ClientOutFaultProcessor clientOutFaultProcessor;
    
    protected PipelineSelector pipelineSelector;
    
    protected Executor executor;

    public DefaultClient(Container container, Endpoint endpoint) {
        this(container, endpoint, (PipelineSelector) null);
    }

    public DefaultClient(Container container, Endpoint endpoint, Pipeline pipeline) {
        this(container, endpoint, new PipelineWrappedSelector(pipeline));
    }
    
    public DefaultClient(Container container, Endpoint endpoint, PipelineSelector pipelineSelector) {
        this.container = container;
        clientOutFaultProcessor = new ClientOutFaultProcessor(container,
            endpoint.getPhasePolicy());
        getPipelineSelector(pipelineSelector).setEndpoint(endpoint);
        fireLifeCycleManager();
    }

    protected void fireLifeCycleManager() {
        ClientLifeCycleManager mgr = container.getExtension(ClientLifeCycleManager.class);
        if (null != mgr) {
            mgr.clientCreated(this);
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public void process(Message message) throws ExchangeRuntimeException {
        Exchange exchange = message.getExchange();
        Endpoint endpoint = exchange.getEndpoint();
        if (endpoint == null) {
            endpoint = getPipelineSelector().getEndpoint();
            message.getExchange().put(Endpoint.class, endpoint);
        }
        
        message = endpoint.getProtocol().createMessage(message);
        message.getExchange().setIn(message);
        message.setInbound(true);
        message.setRequest(false);
        
        List<Interceptor<? extends Message>> i1 = getInInterceptors();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Interceptors provided by client: " + i1);
        }
        List<Interceptor<? extends Message>> i2 = endpoint.getInInterceptors();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Interceptors provided by endpoint: " + i2);
        }
        List<Interceptor<? extends Message>> i3 = endpoint.getProtocol().getInInterceptors();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Interceptors provided by protocol: " + i3);
        }
        
        PhaseInterceptorChain chain;
        PhasePolicy policy = endpoint.getPhasePolicy();
        DataProcessor ser = endpoint.getService().getDataProcessor();
        if (ser instanceof InterceptorProvider) {
            InterceptorProvider p = (InterceptorProvider) ser;
            List<Interceptor<? extends Message>> i4 = p.getInInterceptors();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Interceptors provided by serialization: " + i4);
            }
            chain = inboundChainCache.get(policy.getInPhases(), i1, i2, i3, i4);
        } else {
            chain = inboundChainCache.get(policy.getInPhases(), i1, i2, i3);
        }
        message.setInterceptorChain(chain);
        chain.setFaultProcessor(clientOutFaultProcessor);
        adapteChain(chain, message, true);
        adapteChain(chain, message.getExchange().getOut(), true);
        
        Container original = ContainerFactory.getAndSetThreadDefaultContainer(container);
        ClientCallback callback = message.getExchange().get(ClientCallback.class);
        try {
            if (callback != null) {
                if (callback.isCancelled()) {
                    completeExchange(message.getExchange());
                    return;
                }
                callback.start(message);
            }
            String startingAfterID = (String) message.get(InterceptorChain.STARTING_AFTER_ID);
            String startingAtID = (String) message.get(InterceptorChain.STARTING_AT_ID);
            if (startingAfterID != null) {
                chain.doInterceptAfter(message, startingAfterID);
            } else if (startingAtID != null) {
                chain.doInterceptAfter(message, startingAtID);
            } else if (message.getContent(Exception.class) != null) {
                if(message.isInbound()){
                    if(getEndpoint().getInFaultProcessor()!=null){
                        getEndpoint().getInFaultProcessor().process(message);
                    }
                }else{
                    clientOutFaultProcessor.process(message);
                }
                
            } else {
                callback = message.getExchange().get(ClientCallback.class);
                if (callback != null) {
                    try {
                        chain.doIntercept(message);
                    } catch (Throwable error) {
                        message.getExchange().setIn(message);
                        Map<String, Object> resCtx =(Map<String, Object>) message
                            .getExchange().getOut().get(Message.INVOCATION_CONTEXT);
                        resCtx = (Map<String, Object>)resCtx.get(RESPONSE_CONTEXT);
                        if(resCtx!=null){
                            responseContext.put(Thread.currentThread(), resCtx);
                        }
                        callback.handleException(resCtx, error);
                    }

                } else {
                    chain.doIntercept(message);
                }
            }
            callback = message.getExchange().get(ClientCallback.class);
            if (callback != null) {
                message.getExchange().setIn(message);
                Map<String, Object> resCtx =(Map<String, Object>) message
                    .getExchange().getOut().get(Message.INVOCATION_CONTEXT);
                resCtx = (Map<String, Object>)resCtx.get(RESPONSE_CONTEXT);
                if (resCtx != null) {
                    responseContext.put(Thread.currentThread(), resCtx);
                }
                try {
                    Object obj[] = processResult(message, message.getExchange(), null, resCtx);

                    callback.handleResponse(resCtx, obj);
                } catch (Throwable ex) {
                    callback.handleException(resCtx, ex);
                }
            }
        } finally {
            if (original != container) {
                ContainerFactory.getAndSetThreadDefaultContainer(original);
            }
            exchange = message.getExchange();
            synchronized(exchange){
                exchange.put(FINISHED, Boolean.TRUE);
                exchange.setIn(message);
                exchange.notifyAll();
            }
            
        }
    }
    
    
    @SuppressWarnings("unchecked")
    protected Object[] processResult(Message message, Exchange exchange,
        OperationInfo operation, Map<String, Object> resCtx) throws Exception {
        Exception ex = null;
        if (!message.isInbound()) {
            ex = message.getContent(Exception.class);
        }
        boolean b = false;
        if (ex != null) {
            completeExchange(exchange);
            b = true;
            if (message.getContent(Exception.class) != null) {
                throw ex;
            }
        }
        ex = message.getExchange().get(Exception.class);
        if (ex != null) {
            if (!b) {
                completeExchange(exchange);
            }
            throw ex;
        }
        //Integer responseCode = (Integer)exchange.get(Message.RESPONSE_CODE);
        if (operation != null && !operation.isOneWay()) {
            waitResponse(exchange);
        }
        Boolean keepPipeAlive = (Boolean)exchange.get(Client.KEEP_PIPELINE_ALIVE);
        if (keepPipeAlive == null || !keepPipeAlive) {
            completeExchange(exchange);
        }
        List<Object> resList = null;
        Message inMsg = exchange.getIn();
        ex = getException(exchange);
        if (ex != null) {
            throw ex;
        }
        if (inMsg != null) {
            if (null != resCtx) {
                resCtx.putAll(inMsg);
                // remove the recursive reference if present
                resCtx.remove(Message.INVOCATION_CONTEXT);
                responseContext.put(Thread.currentThread(), resCtx);
            }
            resList = inMsg.getContent(List.class);
            if(resList==null){
                Object obj=inMsg.getContent(Object.class);
                if(obj!=null){
                   return new Object[]{obj};
                }
            }else{
                return resList.toArray();
            }
        }
        return null;
    }
    
    protected Exception getException(Exchange exchange) {
        if (exchange.getInFault() != null) {
            return exchange.getInFault().getContent(Exception.class);
        } else if (exchange.getOutFault() != null) {
            return exchange.getOutFault().getContent(Exception.class);
        } else if (exchange.getIn() != null) {
            return exchange.getIn().getContent(Exception.class);
        }
        return null;
    }

    protected void waitResponse(Exchange exchange) throws IOException {
        synchronized (exchange) {
            long remaining = synchronousTimeout;
            Long sync = MessageUtils.getLong(exchange.getOut(), SYNC_TIMEOUT);
            if (sync != null) {
                remaining = sync;
            }
            while (!Boolean.TRUE.equals(exchange.get(FINISHED)) && remaining > 0) {
                long start = System.currentTimeMillis();
                try {
                    exchange.wait(remaining);
                } catch (InterruptedException ex) {
                    // ignore
                }
                long end = System.currentTimeMillis();
                remaining -= (int) (end - start);
            }
            if (!Boolean.TRUE.equals(exchange.get(FINISHED))) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Response timeout: {}", exchange.get(OperationInfo.class).getName().toString());
                }
                throw new IOException("Response timeout: " + exchange.get(OperationInfo.class).getName().toString());
            }
        }
    }


    private void completeExchange(Exchange exchange) {
        getPipelineSelector().complete(exchange);
    }

    @SuppressWarnings("unchecked")
    private void adapteChain(InterceptorChain chain, Message m, boolean in) {
        if (m == null) {
            return;
        }
        Collection<InterceptorProvider> providers = 
            (Collection<InterceptorProvider>) m.get(Message.INTERCEPTOR_PROVIDERS);
        if (providers != null) {
            for (InterceptorProvider p : providers) {
                if (in) {
                    chain.add(p.getInInterceptors());
                } else {
                    chain.add(p.getOutInterceptors());
                }
            }
        }
        String key = in ? Message.IN_INTERCEPTORS : Message.OUT_INTERCEPTORS;
        Collection<Interceptor<? extends Message>> is = (Collection<Interceptor<? extends Message>>) (m.get(key));
        if (is != null) {
            chain.add(is);
        }
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.PipelineAware#getPipeline()
     */
    @Override
    public Pipeline getPipeline() {
        Message msg = new DefaultMessage();
        Exchange ex = new DefaultExchange();
        msg.setExchange(ex);
        msg.putAll(getRequestContext());
        setupExchange(ex, getEndpoint(), null);
        return getPipelineSelector().select(msg);
    }
    @Override
    public Map<String, Object> getRequestContext() {
        if (isThreadLocalRequestContext()) {
            if (!requestContext.containsKey(Thread.currentThread())) {
                requestContext.put(Thread.currentThread(), new EchoContext(currentRequestContext));
            }
            return requestContext.get(Thread.currentThread());
        }
        return currentRequestContext;
    }
    @Override
    public Map<String, Object> getResponseContext() {
        if (!responseContext.containsKey(Thread.currentThread())) {
            responseContext.put(Thread.currentThread(), new HashMap<String, Object>());
        }
        return responseContext.get(Thread.currentThread());

    }
    
    public boolean isThreadLocalRequestContext() {
        Object o = currentRequestContext.get(THREAD_LOCAL_REQUEST_CONTEXT);
        if (o != null) {
            boolean local = false;
            if (o instanceof Boolean) {
                local = ((Boolean)o).booleanValue();
            } else {
                local = Boolean.parseBoolean(o.toString());
            }
            return local;
        }
        return false;
    }
    public void setThreadLocalRequestContext(boolean b) {
        currentRequestContext.put(THREAD_LOCAL_REQUEST_CONTEXT, b);
    }
    
    protected PhaseInterceptorChain setupOutInterceptorChain(Endpoint endpoint) {
        List<Interceptor<? extends Message>> i1 = getOutInterceptors();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Interceptors provided by client: " + i1);
        }
        List<Interceptor<? extends Message>> i2 = endpoint.getOutInterceptors();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Interceptors provided by endpoint: " + i2);
        }
        List<Interceptor<? extends Message>> i3 = endpoint.getProtocol().getOutInterceptors();
        if (LOG.isTraceEnabled()) {
            LOG.trace("Interceptors provided by protocol: " + i3);
        }
        
        PhaseInterceptorChain chain;
        PhasePolicy policy = endpoint.getPhasePolicy();
        DataProcessor ser = endpoint.getService().getDataProcessor();
        if (ser instanceof InterceptorProvider) {
            InterceptorProvider p = (InterceptorProvider) ser;
            List<Interceptor<? extends Message>> i4 = p.getOutInterceptors();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Interceptors provided by serialization: " + i4);
            }
            chain = outboundChainCache.get(policy.getOutPhases(), i1, i2, i3, i4);
        } else {
            chain = outboundChainCache.get(policy.getOutPhases(), i1, i2, i3);
        }
        return chain;
    }
    
    protected void setupExchange(Exchange ex, 
                                 Endpoint endpoint,
                                 OperationInfo oi) {
        ex.put(Container.class, container);
        ex.put(Client.class, this);

        if (endpoint != null) {
            ex.put(Endpoint.class, endpoint);
            ex.put(Service.class, endpoint.getService());
            if (endpoint.getService().getServiceInfo() != null) {
                ex.put(ServiceInfo.class, endpoint.getService().getServiceInfo());
                ex.put(InterfaceInfo.class, endpoint.getService().getServiceInfo().getInterface());
            }
            ex.put(Protocol.class, endpoint.getProtocol());
            ex.put(ProtocolInfo.class, endpoint.getEndpointInfo().getProtocol());
        }
        if (oi != null) {
            ex.put(OperationInfo.class, oi);
        }
        if (ex.isSync() || executor == null) {
            ex.put(Processor.class, this);
        } else {
            ex.put(Executor.class, executor);
            ex.put(Processor.class, new Processor() {

                @Override
                public void process(final Message message) throws ExchangeRuntimeException {
                    if (!message.getExchange().containsKey(EXECUTOR_USED)) {
                        executor.execute(new Runnable() {
                            
                            @Override
                            public void run() {
                                DefaultClient.this.process(message);
                            }
                        });
                    } else {
                        DefaultClient.this.process(message);
                    }
                }

            });
        }

    }
   
    @Override
    public void destroy() {
        if (container == null) {
            return;
        }
        for (Closeable c : getEndpoint().getCleanupHooks()) {
            try {
                c.close();
            } catch (IOException e) {
                // ignore
            }
        }
        ClientLifeCycleManager mgr = container.getExtension(ClientLifeCycleManager.class);
        if (null != mgr) {
            mgr.clientDestroyed(this);
        }
        if (pipelineSelector != null) {
            if (pipelineSelector instanceof Closeable) {
                try {
                    ((Closeable) pipelineSelector).close();
                } catch (IOException e) {
                }
            } else {
                getPipeline().close();
            }
        }
        container = null;
        pipelineSelector = null;
        clientOutFaultProcessor = null;
        outboundChainCache = null;
        inboundChainCache = null;
        currentRequestContext = null;
        requestContext.clear();
        requestContext = null;
        responseContext.clear();
        responseContext = null;
        executor = null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Client#getContainer()
     */
    @Override
    public Container getContainer() {
        return container;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Client#getEndpoint()
     */
    @Override
    public Endpoint getEndpoint() {
        return getPipelineSelector().getEndpoint();
    }


    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Client#getPipelineSelector()
     */
    @Override
    public PipelineSelector getPipelineSelector() {
        return getPipelineSelector(null);
    }

    protected PipelineSelector getPipelineSelector(PipelineSelector override) {
        if (pipelineSelector == null) {
            setPipelineSelector(override);
        }
        return pipelineSelector;
    }

    @Override
    public synchronized void setPipelineSelector(PipelineSelector selector) {
        if (selector == null) {
            pipelineSelector = new PreparedPipelineSelector();
        } else {
            pipelineSelector = selector;
        }
    }
    
    @Override
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
    
    /** 同步超时 */
    public int getSynchronousTimeout() {
        return synchronousTimeout;
    }
    
    /** 同步超时  */
    public void setSynchronousTimeout(int synchronousTimeout) {
        this.synchronousTimeout = synchronousTimeout;
    }
    

    
    @Override
    public Object[] invoke(OperationInfo oi, Object... params) throws Exception {
        return invoke(oi, params, null);
    }

   
    @Override
    public Object[] invoke(OperationInfo oi, 
                           Object[] params,
                           Map<String, Object> context) throws Exception {
        try {
            return invoke(oi, params, context, (Exchange) null);
        } finally {
            if (context != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> res = (Map<String, Object>) context.get(RESPONSE_CONTEXT);
                if (res != null) {
                    responseContext.put(Thread.currentThread(), res);
                }
            }
        }
    }
    
    @Override
    public Object[] invoke(OperationInfo oi, Object[] params, Exchange exchange)
        throws Exception {
        Map<String, Object> context = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();
        Map<String, Object> req = new HashMap<String, Object>(getRequestContext());
        context.put(RESPONSE_CONTEXT, resp);
        context.put(REQUEST_CONTEXT, req);
        try {
            return invoke(oi, params, context, exchange);
        } finally {
            responseContext.put(Thread.currentThread(), resp);
        }
    }

    
    @Override
    public Object[] invoke(OperationInfo oi, Object[] params,
        Map<String, Object> context, Exchange exchange) throws Exception {
        return doInvoke(null, oi, params, context, exchange);
    }

    
    @Override
    public void invoke(ClientCallback callback, OperationInfo oi,
        Object... params) throws Exception {
        invoke(callback, oi, params, null, null);
    }

    @Override
    public void invoke(ClientCallback callback, OperationInfo oi,
        Object[] params, Map<String, Object> context) throws Exception {
        invoke(callback, oi, params, context, null);
    }

    @Override
    public void invoke(ClientCallback callback, OperationInfo oi,
        Object[] params, Map<String, Object> context, Exchange exchange)
        throws Exception {
        doInvoke(callback, oi, params, context, exchange);

    }

    @Override
    public void invoke(ClientCallback callback, OperationInfo oi,
        Object[] params, Exchange exchange) throws Exception {
        invoke(callback, oi, params, null, exchange);
    }
    
    @SuppressWarnings("unchecked")
    private Object[] doInvoke(final ClientCallback callback,
                              OperationInfo oi,
                              Object[] params,
                              Map<String, Object> context,
                              Exchange exchange) throws Exception {
        Container original = ContainerFactory.getAndSetThreadDefaultContainer(container);
        ClassLoaderHolder origLoader = null;
        try {
            ClassLoader loader = container.getExtension(ClassLoader.class);
            if (loader != null) {
                origLoader = ClassLoaderUtils.setThreadContextClassloader(loader);
            }
            if (exchange == null) {
                exchange = new DefaultExchange();
            }
            exchange.setSync(callback == null);
            Endpoint endpoint = getEndpoint();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Invoke, operation info: {}, params: {}", oi,
                    Arrays.toString(params));
            }
            Message msg = endpoint.getProtocol().createMessage();
            Map<String, Object> reqContext = null;
            Map<String, Object> resContext = null;
            if (context == null) {
                context = new HashMap<String, Object>();
            }
            reqContext = (Map<String, Object>) context.get(REQUEST_CONTEXT);
            resContext = (Map<String, Object>) context.get(RESPONSE_CONTEXT);
            if (reqContext == null) {
                reqContext = new HashMap<String, Object>(getRequestContext());
                context.put(REQUEST_CONTEXT, reqContext);
            }
            if (resContext == null) {
                resContext = new HashMap<String, Object>();
                context.put(RESPONSE_CONTEXT, resContext);
            }

            msg.put(Message.INVOCATION_CONTEXT, context);
            if (reqContext != null) {
                msg.putAll(reqContext);
                exchange.putAll(reqContext);
            }
            msg.setContent(List.class, new MessageList(params));

            if (oi != null) {
                exchange.setOneWay(oi.getOutput() == null);
            }
            setupOutMessage(msg,endpoint,oi);
            exchange.setOut(msg);
            exchange.put(ClientCallback.class, callback);

            //setup message
            msg.setRequest(true);
            msg.setInbound(false);
            if (oi != null) {
                msg.put(MessageInfo.class, oi.getInput());
            }
            //setup exchange
            setupExchange(exchange, endpoint, oi);
            
            PhaseInterceptorChain chain = setupOutInterceptorChain(endpoint);
            msg.setInterceptorChain(chain);
            
            if (callback == null) {
                chain.setFaultProcessor(clientOutFaultProcessor);
            } else {
                chain.setFaultProcessor(new Processor() {

                    @Override
                    public void process(Message message)
                        throws ExchangeRuntimeException {
                        Exception ex = message.getContent(Exception.class);
                        if (ex != null) {
                            completeExchange(message.getExchange());
                            // complete exception
                            if (message.getContent(Exception.class) == null) {
                                List<Object> resList = null;
                                Message inMsg = message.getExchange().getIn();
                                Map<String, Object> ctx = responseContext.get(Thread.currentThread());
                                resList = inMsg.getContent(List.class);
                                Object[] result = resList == null ? null : resList.toArray();
                                callback.handleResponse(ctx, result);
                                return;
                            }
                        }
                        clientOutFaultProcessor.process(message);
                    }
                });
            }
            //准备数据管道
            preparePipelineSelector(msg);
            
            adapteChain(chain, msg, false);
            
            try {
                chain.doIntercept(msg);
            } catch (Fault fault) {
                throw fault;
            }
            if (callback != null) {
                return null;
            } else {
                return processResult(msg, exchange, oi, resContext);
            }
        } finally {
            if (origLoader != null) {
                origLoader.reset();
            }
            if (original != container) {
                ContainerFactory.setDefaultContainer(original);
            }
        }
    }

    protected void setupOutMessage(Message msg, Endpoint endpoint, OperationInfo oi) {
    }

    protected  void preparePipelineSelector(Message msg) {
        getPipelineSelector().prepare(msg);
        msg.getExchange().put(PipelineSelector.class, getPipelineSelector());
    }

    public static class EchoContext extends HashMap<String, Object> {
        private static final long serialVersionUID = 5199023273052841289L;
        final Map<String, Object> shared;
        public EchoContext(Map<String, Object> sharedMap) {
            super(sharedMap);
            shared = sharedMap;
        }

        @Override
        public Object put(String key, Object value) {
            shared.put(key, value);
            return super.put(key, value);
        }

        @Override
        public void putAll(Map<? extends String, ? extends Object> t) {
            shared.putAll(t);
            super.putAll(t);
        }

        @Override
        public Object remove(Object key) {
            shared.remove(key);
            return super.remove(key);
        }

        public void reload() {
            super.clear();
            super.putAll(shared);
        }
    }


}
