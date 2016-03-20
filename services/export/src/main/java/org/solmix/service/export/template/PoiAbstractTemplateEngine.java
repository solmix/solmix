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
import java.io.StringWriter;
import java.io.Writer;

import javax.annotation.Resource;

import org.apache.velocity.runtime.RuntimeInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.FileUtils;
import org.solmix.runtime.resource.InputStreamResource;
import org.solmix.runtime.resource.ResourceManager;
import org.solmix.service.export.ExportException;
import org.solmix.service.template.TemplateContext;
import org.solmix.service.template.TemplateEngine;
import org.solmix.service.template.TemplateException;
import org.solmix.service.velocity.VelocityEngine;
import org.solmix.service.velocity.support.TemplateContextAdapter;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年9月9日
 */

public abstract class PoiAbstractTemplateEngine implements TemplateEngine
{
    private static final Logger LOG = LoggerFactory.getLogger(XSSFTemplateEngine.class);

    public static final String VT_TMP_NAME = "_d_vm_result";

    private String path = "";

    @Resource
    private ResourceManager resourceManager;

    /** 需要velocity支持 */
    @Resource
    private VelocityEngine velocity;


    @Override
    public boolean exists(String templateName) {
        InputStreamResource stream = getInputStreamResource(templateName);
        if (stream != null && stream.exists()) {
            return true;
        }
        return false;
    }

    protected InputStreamResource getInputStreamResource(String templateName) {
        String abpath = path + "/" + templateName;
        abpath = FileUtils.normalizeAbsolutePath(abpath);
        if (resourceManager != null) {
            return resourceManager.getResourceAsStream(abpath);

        }
        return null;
    }

   

    public Object evaluateValue(String expression, TemplateContext context) {
        RuntimeInstance instance = velocity.getRuntimeInstance();
        TemplateContextAdapter vContext = new TemplateContextAdapter(context);
        Object result;
        if (expression.startsWith("#")) {
            StringWriter out = new StringWriter();
            instance.evaluate(vContext, out, "VelocityExpression", expression);
            result = out.toString();
        } else {
            try {
                instance.evaluate(vContext, null, "VelocityExpression",
                    new StringBuilder().append("#set($").append(VT_TMP_NAME).append(" = ").append(expression).append(")\n").toString());
            } catch (Exception e) {
                throw new ExportException("Velocity evalute exception:"+expression, e);
            }
            result = vContext.get(VT_TMP_NAME);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("expression[" + expression + "] evaluate to value:" + result);
        }
        return result;
    }

    @Override
    public String evaluate(String templateName, TemplateContext context) throws TemplateException, IOException {
        throw new java.lang.UnsupportedOperationException(
            "excel template engine not support method:#evaluate(String templateName, TemplateContext context)");

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
