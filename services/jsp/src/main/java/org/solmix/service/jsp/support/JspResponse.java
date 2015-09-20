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
package org.solmix.service.jsp.support;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;


/**
 *  避免在JSP中修改content type、locale和charset。作为模板系统，JSP不应该控制content type和输出字符集。
 * @author solmix.f@gmail.com
 * @version $Id$  2015年9月13日
 */

public class JspResponse extends HttpServletResponseWrapper
{
    public JspResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void setContentType(String contentType) {
        // do nothing
    }

    @Override
    public void setLocale(Locale locale) {
        // do nothing
    }

    @Override
    public void setCharacterEncoding(String charset) {
        // do nothing
    }
}
