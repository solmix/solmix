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
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.Extension;
import org.solmix.runtime.ProductionAware;
import org.solmix.runtime.resource.ResourceManager;
import org.solmix.service.freemarker.FreeMarkerConfiguration;
import org.solmix.service.freemarker.FreeMarkerEngine;
import org.solmix.service.freemarker.FreeMarkerPlugin;
import org.solmix.service.template.TemplateContext;
import org.solmix.service.template.TemplateException;

import freemarker.core.Environment;
import freemarker.core.ParseException;
import freemarker.template.Template;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年9月13日
 */
@Extension("freemarker")
@SuppressWarnings("deprecation")
public class DefaultFreeMarkerEngine implements FreeMarkerEngine ,ProductionAware
{

    private static final Logger LOG  =LoggerFactory.getLogger(DefaultFreeMarkerEngine.class);
    private final FreeMarkerConfiguration configuration = new FreeMarkerConfiguration(LOG);
    
    static {
        try {
            freemarker.log.Logger.selectLoggerLibrary(freemarker.log.Logger.LIBRARY_SLF4J);
            freemarker.log.Logger.setCategoryPrefix("");
        } catch (Throwable ee) {
        }
    }
    @Resource
    private ResourceManager resourceManager;
    
    @PostConstruct
    public void init() throws Exception{
        configuration.init(resourceManager);
    }
    public FreeMarkerConfiguration getConfiguration(){
        return configuration;
    }
  
    @Override
    public String[] getDefaultExtensions() {
        return new String[] { "ftl" };
    }

 
    @Override
    public boolean exists(String templateName) {
        try {
            return configuration.getTemplateLoader().findTemplateSource(templateName) != null;
        } catch (IOException e) {
            return false;
        }
    }

  
    @Override
    public String evaluate(String templateName, TemplateContext context) throws TemplateException, IOException {
        StringWriter out = new StringWriter();
        render(templateName, context, out, null, null, null);
        return out.toString();
    }
    
    private void render(String templateName, Object context, Writer writer, OutputStream ostream, String inputCharset,
        String outputCharset) throws TemplateException, IOException {
        Locale locale = Locale.getDefault();
        
        if (StringUtils.isEmpty(inputCharset)) {
            inputCharset = configuration.getConfiguration().getDefaultEncoding();
        }
        inputCharset = StringUtils.defaultIfEmpty(inputCharset,FreeMarkerConfiguration. DEFAULT_CHARSET);

        if (StringUtils.isEmpty(outputCharset)) {
            outputCharset = configuration.getConfiguration().getOutputEncoding();
        }

        outputCharset = StringUtils.defaultIfEmpty(outputCharset, FreeMarkerConfiguration.DEFAULT_CHARSET);
        
        if (writer == null) {
            if (ostream == null) {
                throw new IllegalArgumentException("missing output writer");
            }

            writer = new OutputStreamWriter(ostream, outputCharset);
        }

        try {
            Template template = configuration.getConfiguration().getTemplate(templateName, locale, inputCharset);
            Environment env = template.createProcessingEnvironment(context, writer);

            env.setLocale(locale);
            env.setOutputEncoding(outputCharset);

            env.process();
        } catch (freemarker.template.TemplateException e) {
            throw new TemplateException("Error rendering FreeMarker template: " + templateName, e);
        } catch (ParseException e) {
            throw new TemplateException("Error rendering FreeMarker template: " + templateName, e);
        }
    }
    @Override
    public void evaluate(String templateName, TemplateContext context, OutputStream ostream) throws TemplateException, IOException {
        render(templateName, context, null, ostream, null, null);

    }

    @Override
    public void evaluate(String templateName, TemplateContext context, Writer writer) throws TemplateException, IOException {
        render(templateName, context, writer, null, null, null);

    }

 
    @Override
    public String mergeTemplate(String templateName, Object context, String inputCharset) throws TemplateException, IOException {
        StringWriter out = new StringWriter();
        render(templateName, context, out, null, inputCharset, null);
        return out.toString();
    }

   
    @Override
    public void mergeTemplate(String templateName, Object context, OutputStream ostream, String inputCharset, String outputCharset)
        throws TemplateException, IOException {
        render(templateName, context, null, ostream, inputCharset, outputCharset);

    }

   
    @Override
    public void mergeTemplate(String templateName, Object context, Writer out, String inputCharset) throws TemplateException, IOException {
        render(templateName, context, out, null, inputCharset, null);

    }

    @Override
    public void setProduction(boolean productionMode) {
      configuration.setProduction(productionMode);
        
    }
    
    public void setPath(String path){
        configuration.setPath(path);
    }
    
    
    public void setPlugins(FreeMarkerPlugin[] plugins) {
        configuration.setPlugins(plugins);
    }

}
