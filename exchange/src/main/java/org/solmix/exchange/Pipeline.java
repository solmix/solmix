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

import java.io.IOException;

/**
 * {@link Transporter}之间的传输管道.
 * 
 * 为Binding提供传输通道
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月10日
 */

public interface Pipeline extends ProcessorAware {
    
    @Override
    Processor getProcessor();

    /**
     * 设置管道当前的消息处理器,同时激活管道
     */
    @Override
    void setProcessor(Processor processor);

    void prepare(Message message) throws IOException;

    /**
     * 完成message传输.
     * @param message
     * @throws IOException
     */
    void close(Message message) throws IOException;

    String getAddress();

    void close();

}
