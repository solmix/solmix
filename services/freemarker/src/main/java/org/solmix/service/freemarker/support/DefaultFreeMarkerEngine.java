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
package org.solmix.service.freemarker.support;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.solmix.service.freemarker.FreeMarkerEngine;
import org.solmx.service.template.TemplateContext;
import org.solmx.service.template.TemplateException;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年9月13日
 */

public class DefaultFreeMarkerEngine implements FreeMarkerEngine
{

    /**
     * {@inheritDoc}
     * 
     * @see org.solmx.service.template.TemplateEngine#getDefaultExtensions()
     */
    @Override
    public String[] getDefaultExtensions() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmx.service.template.TemplateEngine#exists(java.lang.String)
     */
    @Override
    public boolean exists(String templateName) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmx.service.template.TemplateEngine#evaluate(java.lang.String, org.solmx.service.template.TemplateContext)
     */
    @Override
    public String evaluate(String templateName, TemplateContext context) throws TemplateException, IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmx.service.template.TemplateEngine#evaluate(java.lang.String, org.solmx.service.template.TemplateContext, java.io.OutputStream)
     */
    @Override
    public void evaluate(String templateName, TemplateContext context, OutputStream ostream) throws TemplateException, IOException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmx.service.template.TemplateEngine#evaluate(java.lang.String, org.solmx.service.template.TemplateContext, java.io.Writer)
     */
    @Override
    public void evaluate(String templateName, TemplateContext context, Writer writer) throws TemplateException, IOException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.service.freemarker.FreeMarkerEngine#mergeTemplate(java.lang.String, java.lang.Object, java.lang.String)
     */
    @Override
    public String mergeTemplate(String templateName, Object context, String inputCharset) throws TemplateException, IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.service.freemarker.FreeMarkerEngine#mergeTemplate(java.lang.String, java.lang.Object, java.io.OutputStream, java.lang.String, java.lang.String)
     */
    @Override
    public void mergeTemplate(String templateName, Object context, OutputStream ostream, String inputCharset, String outputCharset)
        throws TemplateException, IOException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.service.freemarker.FreeMarkerEngine#mergeTemplate(java.lang.String, java.lang.Object, java.io.Writer, java.lang.String)
     */
    @Override
    public void mergeTemplate(String templateName, Object context, Writer out, String inputCharset) throws TemplateException, IOException {
        // TODO Auto-generated method stub

    }

}
