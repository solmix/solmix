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

package org.solmix.service.export.template;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.annotation.Resource;

import org.apache.poi.hwpf.HWPFDocument;
import org.solmix.commons.util.FileUtils;
import org.solmix.runtime.Extension;
import org.solmix.runtime.resource.InputStreamResource;
import org.solmix.runtime.resource.ResourceManager;
import org.solmx.service.template.TemplateContext;
import org.solmx.service.template.TemplateEngine;
import org.solmx.service.template.TemplateException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年9月9日
 */
@Extension(name = "world")
public class HWPFTemplateEngine implements TemplateEngine
{

    private String path = "";

    @Resource
    private ResourceManager resourceManager;

    @Override
    public String[] getDefaultExtensions() {
        return new String[] { "doc"};
    }

    @Override
    public boolean exists(String templateName) {
       
        InputStreamResource stream= getInputStreamResource( templateName);
        if (stream != null && stream.exists()) {
            return true;
        }
        return false;
    }
    
    private InputStreamResource getInputStreamResource(String templateName){
        String abpath = path+ "/"+ templateName;
        abpath = FileUtils.normalizeAbsolutePath(abpath);
        if (resourceManager != null) {
           return resourceManager.getResourceAsStream(abpath);
            
        }
        return null;
    }

    @Override
    public String evaluate(String templateName, TemplateContext context) throws TemplateException, IOException {
        throw new java.lang.UnsupportedOperationException(
            "world template engine not support method:#evaluate(String templateName, TemplateContext context)");
    }

    @Override
    public void evaluate(String templateName, TemplateContext context, OutputStream ostream) throws TemplateException, IOException {
        InputStreamResource stream= getInputStreamResource( templateName);
        HWPFDocument hdt = null;
        try {
        hdt = new HWPFDocument(stream.getInputStream());
        } catch (IOException e1) {
        e1.printStackTrace();
        }
    }

    @Override
    public void evaluate(String templateName, TemplateContext context, Writer writer) throws TemplateException, IOException {
        throw new java.lang.UnsupportedOperationException(
            "world template engine not support method:#evaluate(String templateName, TemplateContext context, Writer writer)");

    }

    public String getPath() {
        return path;
    }

    /**
     * 模板访问的根目录
     * 
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

}
