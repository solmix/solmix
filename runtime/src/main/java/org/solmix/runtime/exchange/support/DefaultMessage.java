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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;

import org.solmix.runtime.exchange.Exchange;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.interceptor.InterceptorChain;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月14日
 */
@SuppressWarnings("unchecked")
public class DefaultMessage extends StringTypeMapper implements Message
{
    private static final long serialVersionUID = 913240661839172917L;
    private Exchange exchange;
    private String id;
    private InterceptorChain interceptorChain;
    private Object[] contents = new Object[20];
    private int index;
    
    public DefaultMessage() {
    }
    public DefaultMessage(Message m) {
        super(m);
        if (m instanceof DefaultMessage) {
            DefaultMessage impl = (DefaultMessage)m;
            exchange = impl.getExchange();
            id = impl.id;
            interceptorChain = impl.interceptorChain;
            contents = impl.contents;
            index = impl.index;
        } else {
            throw new RuntimeException("Not a MessageImpl! " + m.getClass());
        }
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        this.id=id;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#getInterceptorChain()
     */
    @Override
    public InterceptorChain getInterceptorChain() {
        return interceptorChain;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#setInterceptorChain(org.solmix.runtime.interceptor.InterceptorChain)
     */
    @Override
    public void setInterceptorChain(InterceptorChain chain) {
        this.interceptorChain=chain;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#getExchange()
     */
    @Override
    public Exchange getExchange() {
        return exchange;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#getContent(java.lang.Class)
     */
    @Override
    public <T> T getContent(Class<T> type) {
        for (int x = 0; x < index; x += 2) {
            if (contents[x] == type) {
                return (T)contents[x + 1];
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#setContent(java.lang.Class, java.lang.Object)
     */
    @Override
    public <T> void setContent(Class<T> type, Object content) {
        for (int x = 0; x < index; x += 2) {
            if (contents[x] == type) {
                contents[x + 1] = content;
                return;
            }
        }
        if (index >= contents.length) {
            /**超过容量扩展*/
            Object tmp[] = new Object[contents.length + 10];
            System.arraycopy(contents, 0, tmp, 0, contents.length);
            contents = tmp;
        }
        contents[index] = type;
        contents[index + 1] = content;
        index += 2;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#getContentType()
     */
    @Override
    public Set<Class<?>> getContentType() {
        Set<Class<?>> c = new HashSet<Class<?>>();
        for (int x = 0; x < index; x += 2) {
            c.add((Class<?>)contents[x]);
        }
        return c;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#removeContent(java.lang.Class)
     */
    @Override
    public <T> void removeContent(Class<T> type) {
        for (int x = 0; x < index; x += 2) {
            if (contents[x] == type) {
                index -= 2;
                if (x != index) {
                    contents[x] = contents[index];
                    contents[x + 1] = contents[index + 1];
                }
                contents[index] = null;
                contents[index + 1] = null;
                return;
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#getAttachment(java.lang.String)
     */
    @Override
    public DataHandler getAttachment(String id) {
        Map<String, DataHandler> attachment= getAttachments();
        return attachment==null?null:attachment.get(id);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#getAttachmentNames()
     */
    @Override
    public Set<String> getAttachmentNames() {
        Map<String, DataHandler> attachment= getAttachments();
        return attachment==null?null:attachment.keySet();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#removeAttachment(java.lang.String)
     */
    @Override
    public void removeAttachment(String id) {
        Map<String, DataHandler> attachment= getAttachments();
       if(attachment!=null&&attachment.containsKey(id)){
           attachment.remove(id);
       }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#addAttachment(java.lang.String, javax.activation.DataHandler)
     */
    @Override
    public void addAttachment(String id, DataHandler content) {
        Map<String, DataHandler> attachment= getAttachments();
        if(attachment==null){
            setAttachments(new LinkedHashMap<String, DataHandler>());
        }
        getAttachments().put(id, content);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#getAttachments()
     */
    @Override
    public Map<String, DataHandler> getAttachments() {
        return (Map<String, DataHandler>)get(ATTACHMENTS);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#setAttachments(java.util.Map)
     */
    @Override
    public void setAttachments(Map<String, DataHandler> attachments) {
        put(ATTACHMENTS, attachments);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#hasAttachments()
     */
    @Override
    public boolean hasAttachments() {
        return getAttachments()!=null&&getAttachments().size()>0;
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Message#setExchange(org.solmix.runtime.exchange.Exchange)
     */
    @Override
    public void setExchange(Exchange e) {
        this.exchange=e;
    }

}
