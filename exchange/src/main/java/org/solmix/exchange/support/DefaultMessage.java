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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.solmix.commons.collections.StringTypeMapper;
import org.solmix.exchange.Attachment;
import org.solmix.exchange.Exchange;
import org.solmix.exchange.Message;
import org.solmix.exchange.interceptor.InterceptorChain;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月14日
 */
@SuppressWarnings("unchecked")
public class DefaultMessage extends StringTypeMapper implements Message {
    private static final long serialVersionUID = 913240661839172917L;
    private Exchange exchange;
    private long id;
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
     * @see org.solmix.exchange.Message#getId()
     */
    @Override
    public long getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Message#setId(java.lang.String)
     */
    @Override
    public void setId(long id) {
        this.id=id;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Message#getInterceptorChain()
     */
    @Override
    public InterceptorChain getInterceptorChain() {
        return interceptorChain;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Message#setInterceptorChain(org.solmix.exchange.interceptor.InterceptorChain)
     */
    @Override
    public void setInterceptorChain(InterceptorChain chain) {
        this.interceptorChain=chain;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Message#getExchange()
     */
    @Override
    public Exchange getExchange() {
        return exchange;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Message#getContent(java.lang.Class)
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
     * @see org.solmix.exchange.Message#setContent(java.lang.Class, java.lang.Object)
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
     * @see org.solmix.exchange.Message#getContentType()
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
     * @see org.solmix.exchange.Message#removeContent(java.lang.Class)
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
     * @see org.solmix.exchange.Message#setExchange(org.solmix.exchange.Exchange)
     */
    @Override
    public void setExchange(Exchange e) {
        this.exchange = e;
    }
    
    public static void copyContent(Message m1, Message m2) {
        for (Class<?> c : m1.getContentType()) {
            m2.setContent(c, m1.getContent(c));
        }
    }
  
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Message#getAttachments()
     */
    @Override
    public Collection<Attachment> getAttachments() {
        return (Collection<Attachment>)get(ATTACHMENTS);
    }
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Message#setAttachments(java.util.Collection)
     */
    @Override
    public void setAttachments(Collection<Attachment> attachments) {
       put(ATTACHMENTS,attachments);
    }
   

}
