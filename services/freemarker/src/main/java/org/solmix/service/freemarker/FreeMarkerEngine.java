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
package org.solmix.service.freemarker;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.solmx.service.template.TemplateEngine;
import org.solmx.service.template.TemplateException;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年9月13日
 */

public interface FreeMarkerEngine extends TemplateEngine
{
    /** 渲染模板，并以字符串的形式取得渲染的结果。 */
    String mergeTemplate(String templateName, Object context, String inputCharset) throws TemplateException,
                                                                                          IOException;

    /** 渲染模板，并将渲染的结果送到字节输出流中。 */
    void mergeTemplate(String templateName, Object context, OutputStream ostream, String inputCharset,
                       String outputCharset) throws TemplateException, IOException;

    /** 渲染模板，并将渲染的结果送到字符输出流中。 */
    void mergeTemplate(String templateName, Object context, Writer out, String inputCharset) throws TemplateException,IOException;
             
}
