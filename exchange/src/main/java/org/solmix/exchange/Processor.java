/**
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

package org.solmix.exchange;

/**
 * 消息处理器 在hola中消息处理其为一个端点,端点接受消息后通过处理器返回,在datax中消息处理器为数据获取服务,将数据获取后返回
 * 在wmix中为请求处理端点,将http请求处理后触发返回.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月13日
 */

public interface Processor {

    void process(Message message) throws ExchangeException;
}
