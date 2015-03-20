/*
 *  Copyright 2012 The Solmix Project
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
package org.solmix.sgt.client.advanceds;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;


/**
 * 一个Call包含了多个数据请求操作，这些操作在服务端顺序执行后返回，多个请求具有原子性，既所有成功为成功。
 * @author solmix.f@gmail.com
 * @version $Id$  2013-6-18
 */

public interface JSCallBacks
{

    void execute(RPCResponse[] response, JavaScriptObject rawData, RPCRequest request);
}
