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
package org.solmix.runtime.interceptor.support;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.attachment.AttachmentUtils;
import org.solmix.runtime.interceptor.Fault;
import org.solmix.runtime.interceptor.phase.Phase;
import org.solmix.runtime.interceptor.phase.PhaseInterceptorSupport;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月20日
 */

public class AttachmentInInterceptor extends PhaseInterceptorSupport<Message>
{

    /** 
     * 
     */
    public AttachmentInInterceptor()
    {
        super(Phase.RECEIVE);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
       if(message.get(InputStream.class)==null){
           return;
       }
       String contentType = (String) message.get(Message.CONTENT_TYPE);
       if (AttachmentUtils.isTypeSupported(contentType, getSupportedTypes())) {
           //TODO
       }

    }

    /**
     * @return
     */
    protected List<String> getSupportedTypes() {
        return Collections.singletonList("multipart/related");
    }

}
