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

package org.solmix.exchange.interceptor;

/**
 * 消息传递中出现的异常.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年10月13日
 */

public class Fault extends RuntimeException {

    private static final long serialVersionUID = 5729880847366668130L;

    private boolean server;

    private String detail;

    public Fault(String message, Throwable t) {
        super(message, t);
    }

    public Fault(String message) {
        super(message);
    }

    /** @param ex */
    public Fault(Throwable ex) {
        super(ex);
        server = true;
    }

    /**   */
    public boolean isServer() {
        return server;
    }

    public void setDetail(String detail) {
        this.detail=detail;
    }
    
    public String getDetail() {
        return detail;
    }

}
