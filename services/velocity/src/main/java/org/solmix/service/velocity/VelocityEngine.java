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
package org.solmix.service.velocity;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeInstance;
import org.solmix.service.template.TemplateEngine;
import org.solmix.service.template.TemplateException;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年7月28日
 */

public interface VelocityEngine extends TemplateEngine
{

    RuntimeInstance getRuntimeInstance();
    
    String mergeTemplate( String templateName, Context context, String encoding)throws TemplateException,  IOException;
    
    void mergeTemplate(String template, Context context, OutputStream ostream, String inputEncoding, String outputEncoding) throws TemplateException, IOException;

    void mergeTemplate(String template, Context context, Writer writer, String inputEncoding) throws TemplateException,IOException;
}
