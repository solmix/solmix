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
package org.solmix.service.velocity.support;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.velocity.Template;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.AbstractContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalEventContext;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.util.RuntimeServicesAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ObjectUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.Extension;
import org.solmix.runtime.ProductionAware;
import org.solmix.runtime.resource.ResourceManager;
import org.solmix.service.template.TemplateContext;
import org.solmix.service.template.TemplateException;
import org.solmix.service.template.TemplateNotFoundException;
import org.solmix.service.velocity.VelocityEngine;
import org.solmix.service.velocity.VelocityEngineConfiguration;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年7月28日
 */
@Extension("velocity")
public class DefaultVelocityEngine implements VelocityEngine,ProductionAware
{

    private static final Logger LOG = LoggerFactory.getLogger(DefaultVelocityEngine.class);

    private final VelocityEngineConfiguration info = new VelocityEngineConfiguration(LOG);

    private final VelocityInstance ri = new VelocityInstance();

    private final static String RUNTIME_SERVICES_KEY = "_runtime_services";

    @Resource
    private ResourceManager resourceManager;
    
    @PostConstruct
    public void init() throws Exception{
        info.init(resourceManager);
        LOG.debug("Velocity Engine configration info: {}", info);
        
        ri.setConfiguration(info.getProperties());
        ri.setApplicationAttribute(ResourceManager.class.getName(), resourceManager);
        ri.setProperty(RuntimeConstants.EVENTHANDLER_REFERENCEINSERTION, RuntimeServicesExposer.class.getName());
        ri.init();
        
        CloneableEventCartridge eventCartridge = info.getEventCartridge();
        RuntimeServices rs = Assert.assertNotNull((RuntimeServices) ri.getProperty(RUNTIME_SERVICES_KEY), "RuntimeServices");

        eventCartridge.initOnce(rs);
    }
    @Override
    public String[] getDefaultExtensions() {
        return new String[] { "vm" };
    }
    
    public VelocityEngineConfiguration getVelocityEngineInfo(){
        return info;
    }

    
    @Override
    public boolean exists(String templateName) {
        return ri.getLoaderNameForResource(templateName) != null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.service.template.TemplateEngine#evaluate(java.lang.String, org.solmix.service.template.TemplateContext)
     */
    @Override
    public String evaluate(String templateName, TemplateContext context) throws TemplateException, IOException {
        return mergeTemplate(templateName, new TemplateContextAdapter(context), null);
    }

    
    @Override
    public void evaluate(String templateName, TemplateContext context, OutputStream ostream) throws TemplateException, IOException {
        mergeTemplate(templateName, new TemplateContextAdapter(context), ostream, null, null);
    }

   
    @Override
    public void evaluate(String templateName, TemplateContext context, Writer writer) throws TemplateException, IOException {
        mergeTemplate(templateName, new TemplateContextAdapter(context), writer, null);
    }

   
    @Override
    public RuntimeInstance getRuntimeInstance() {
        return ri;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.service.velocity.VelocityEngine#mergeTemplate(java.lang.String, org.apache.velocity.context.Context, java.lang.String)
     */
    @Override
    public String mergeTemplate(String templateName, Context context, String encoding) throws TemplateException, IOException {
        StringWriter writer = new StringWriter();
        mergeTemplate(templateName, context, writer, encoding);
        return writer.toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.service.velocity.VelocityEngine#mergeTemplate(java.lang.String, org.apache.velocity.context.Context, java.io.OutputStream, java.lang.String, java.lang.String)
     */
    @Override
    public void mergeTemplate(String template, Context context, OutputStream ostream, String inputEncoding, String outputEncoding)
        throws TemplateException, IOException {
        if (StringUtils.isEmpty(outputEncoding)) {
            outputEncoding = getDefaultOutputEncoding();
        }

        OutputStreamWriter writer = null;

        try {
            writer = new OutputStreamWriter(ostream, outputEncoding);
        } catch (UnsupportedEncodingException e) {
            error(template, e);
        }

        mergeTemplate(template, context, writer, inputEncoding);
        writer.flush();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.service.velocity.VelocityEngine#mergeTemplate(java.lang.String, org.apache.velocity.context.Context, java.io.Writer, java.lang.String)
     */
    @Override
    public void mergeTemplate(String templateName, Context context, Writer writer, String inputEncoding) throws TemplateException, IOException {
        if (StringUtils.isEmpty(inputEncoding)) {
            inputEncoding = getDefaultInputEncoding();
        }

        try {
            Context eventContext = attachEventCartridge(context);

            mergeTemplate(templateName, inputEncoding, eventContext, writer);
        } catch (Exception e) {
            error(templateName, e);
        }
    }
    /** 设置搜索模板的根目录。默认为<code>/templates</code>。 */
    public void setPath(String path) {
       info.setPath(path);
    }

    /** 是否开启模板缓存。在生产模式下，该模式将被强行开启。 */
    public void setCacheEnabled(boolean cacheEnabled) {
        info.setCacheEnabled(cacheEnabled);
    }

    /** 设置检查模板被修改的间隔（秒）。默认为2秒。 */
    public void setModificationCheckInterval(int modificationCheckInterval) {
       info.setModificationCheckInterval(modificationCheckInterval);
    }

    /** 设置strict reference模式。默认为<code>true</code>。 */
    public void setStrictReference(boolean strictReference) {
        info.setStrictReference(strictReference);
    }

    /** 设置模板的字符集编码。 */
    public void setTemplateEncoding(String charset) {
       info.setTemplateEncoding(charset);
    }

    /** 设置全局宏的名称，可包含通配符。 */
    public void setGlobalMacros(String[] macros) {
        info.setGlobalMacros(macros);
    }

    /** 设置plugins。 */
    public void setPlugins(Object[] plugins) {
        info.setPlugins(plugins);
    }
    
    
    private Context attachEventCartridge(Context context) {
        Context eventContext;

        if (context instanceof InternalEventContext) {
            eventContext = context;
        } else {
            // 将其包装成EventContext，确保event cartridge可以工作。
            // 模板中的修改将保留在context中。
            eventContext = new EventContext(context);
        }

        // 将event cartridge复制以后（如有必要）附到context中。
        EventCartridge ec = info.getEventCartridge().getRuntimeInstance();

        if (ec != null) {
            Assert.assertTrue(ec.attachToContext(eventContext), "Could not attach EventCartridge to velocity context");
        }

        return eventContext;
    }
    /** Copied from org.apache.velocity.app.VelocityEngine。 */
    private boolean mergeTemplate(String templateName, String encoding, Context context, Writer writer)
            throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
        Template template = ri.getTemplate(templateName, encoding);
        if (template == null) {
            String msg = "VelocityEngine.mergeTemplate() was unable to load template '" + templateName + "'";
            ri.getLog().error(msg);
            throw new ResourceNotFoundException(msg);
        } else {
            template.merge(context, writer);
            return true;
        }
    }
    private String defaultInputEncoding;
    private String defaultOutpuEncoding;
    /** 取得解析模板时的默认编码字符集。 */
    protected String getDefaultInputEncoding() {
        if (defaultInputEncoding == null) {
            defaultInputEncoding =ObjectUtils.defaultIfNull(StringUtils.trimToNull((String) ri.getProperty(RuntimeInstance.INPUT_ENCODING)), VelocityEngineConfiguration.DEFAULT_CHARSET);
        }

        return defaultInputEncoding;
    }

    /** 取得输出模板时的默认编码字符集。 */
    protected String getDefaultOutputEncoding() {
        if (defaultOutpuEncoding == null) {
            defaultOutpuEncoding = ObjectUtils.defaultIfNull(StringUtils.trimToNull((String) ri.getProperty(RuntimeInstance.OUTPUT_ENCODING)), VelocityEngineConfiguration.DEFAULT_CHARSET);
        }

        return defaultOutpuEncoding;
    }
    /** 处理异常，显示额外的信息。 */
    private final void error(String templateName, Throwable e) throws TemplateException {
        String err = "Error rendering Velocity template: " + templateName;

       LOG.error(err + ": " + e.getMessage());

        if (e instanceof ResourceNotFoundException) {
            throw new TemplateNotFoundException(err, e);
        }

        if (e instanceof TemplateException) {
            throw (TemplateException) e;
        }

        throw new TemplateException(err, e);
    }
    
    /** 一个hack，用来取得runtime services实例。 */
    public static class RuntimeServicesExposer implements ReferenceInsertionEventHandler, RuntimeServicesAware {
        @Override
        public Object referenceInsert(String reference, Object value) {
            return value;
        }

        @Override
        public void setRuntimeServices(RuntimeServices rs) {
            rs.getConfiguration().setProperty(RUNTIME_SERVICES_KEY, rs);
        }
    }
    /** 包装任意<code>Context</code>，使之支持EventCartridge。 */
    private static class EventContext extends AbstractContext {
        private final Context context;

        public EventContext(Context context) {
            this.context = Assert.assertNotNull(context, "no context");
        }

        @Override
        public Object internalGet(String key) {
            return context.get(key);
        }

        @Override
        public Object internalPut(String key, Object value) {
            return context.put(key, value);
        }

        @Override
        public boolean internalContainsKey(Object key) {
            return context.containsKey(key);
        }

        @Override
        public Object[] internalGetKeys() {
            return context.getKeys();
        }

        @Override
        public Object internalRemove(Object key) {
            return context.remove(key);
        }

        @Override
        public String toString() {
            return context.toString();
        }
    }
    
    @Override
    public void setProduction(boolean productionMode) {
        info.setProductionMode(productionMode);
    }
}
