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

package org.solmix.runtime.exchange;

import java.io.IOException;

/**
 * 传输端,在传输层接收消息
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月13日
 */

public interface Transporter extends ProcessorAware {

    /**
     * 关闭端点停止接收消息
     */
    void shutdown();
    
    String getAddress();
    
    /**
     * 消息返回使用的通道
     * @param msg
     * @return
     * @throws IOException
     */
    Pipeline getBackPipeline(Message msg) throws IOException;
    
    /**
     * 默认端口号
     * @return
     */
    int getDefaultPort();
}
