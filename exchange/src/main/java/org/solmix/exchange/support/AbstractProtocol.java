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

import org.solmix.exchange.Endpoint;
import org.solmix.exchange.Protocol;
import org.solmix.exchange.Transporter;
import org.solmix.exchange.interceptor.support.InterceptorProviderSupport;
import org.solmix.exchange.processor.InChainInitProcessor;
import org.solmix.runtime.Container;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年9月26日
 */

public abstract class AbstractProtocol extends InterceptorProviderSupport implements Protocol
{
    private static final long serialVersionUID = -2566099817251621079L;
    private Container container;

    public AbstractProtocol(Container container) {
        this.container = container;
    }
    @Override
    public void addListener(Transporter transporter, Endpoint endpoint) {
        InChainInitProcessor cip = new InChainInitProcessor(endpoint, container);
        //设置Transporter的协议
        transporter.setProtocol(this);
         //设置Transporter的处理器，激活Transporter
        transporter.setProcessor(cip);
    }

}
