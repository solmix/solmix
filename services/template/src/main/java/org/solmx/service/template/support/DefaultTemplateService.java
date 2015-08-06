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

package org.solmx.service.template.support;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.FileUtils;
import org.solmix.runtime.Container;
import org.solmix.runtime.extension.ExtensionLoader;
import org.solmx.service.template.TemplateContext;
import org.solmx.service.template.TemplateEngine;
import org.solmx.service.template.TemplateException;
import org.solmx.service.template.TemplateNotFoundException;
import org.solmx.service.template.TemplateService;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年7月28日
 */

public class DefaultTemplateService implements TemplateService
{

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTemplateService.class);

    private ExtensionLoader<TemplateEngine> loader;

    private boolean init;

    private Map<String, TemplateEngine> engineMappings;

    private Container container;

    private TemplateSearchingStrategy[] strategies;

    private String defaultExtension;

    private boolean searchExtensions;

    private Map<TemplateKey, TemplateMatchResult> matchedTemplates;

    private Boolean cacheEnabled =  true;

    public DefaultTemplateService(Container container)
    {
        setContainer(container);
    }

    public void setContainer(Container container) {
        this.container = container;
        this.container.setExtension(this, TemplateService.class);
    }

    @PostConstruct
    public synchronized void init() {
        init = true;
        loader = container.getExtensionLoader(TemplateEngine.class);
        
        if (cacheEnabled) {
            matchedTemplates = new ConcurrentHashMap<TemplateKey, TemplateMatchResult>();
        }
        engineMappings = new TreeMap<String, TemplateEngine>();
        for (String extension : loader.getLoadedExtensions()) {
            TemplateEngine engine = loader.getExtension(extension);
            String[] exts = engine.getDefaultExtensions();
            for (String ext : exts) {
                ext = FileUtils.normalizeExtension(ext);
                Assert.assertNotNull(ext, "default extensions for engine: %s", engine);
                engineMappings.put(ext, engine);
                LOG.trace("Template Name \"*.{}\" mapped to Template Engine: {}", ext, extension);
            }
        }

        List<TemplateSearchingStrategy> strategyList = new LinkedList<TemplateSearchingStrategy>();
        if (defaultExtension != null) {
            strategyList.add(new DefaultExtensionStrategy(defaultExtension));
        }

        if (searchExtensions) {
            strategyList.add(new SearchExtensionsStrategy(getSupportedExtensions()));
        }
        strategies = strategyList.toArray(new TemplateSearchingStrategy[strategyList.size()]);
    }

    public void setDefaultExtension(String defaultExtension) {
        this.defaultExtension = defaultExtension;
    }

    public Boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(Boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public void setSearchExtensions(boolean searchExtensions) {
        this.searchExtensions = searchExtensions;
    }

    @Override
    public String[] getSupportedExtensions() {
        return engineMappings.keySet().toArray(new String[engineMappings.size()]);
    }

    @Override
    public TemplateEngine getTemplateEngine(String extension) {
        if (extension == null) {
            return null;
        }
        return engineMappings.get(extension);
    }

    @Override
    public boolean exists(String templateName) {
        try {
            findTemplate(templateName);
            return true;
        } catch (TemplateNotFoundException e) {
            return false;
        }
    }

    protected TemplateMatchResult findTemplate(String templateName) {
        ensureInit();
        TemplateKey key = new TemplateKey(templateName, strategies);
        TemplateMatchResult result;

        if (cacheEnabled) {
            result = matchedTemplates.get(key);

            if (result != null) {
                return result;
            }
        }

        TemplateMatcher matcher = new TemplateMatcher(key) {

            private int i;

            @Override
            public boolean findTemplate() {
                boolean found = false;

                // 保存状态，假如没有匹配，则恢复状态
                String savedTemplateNameWithoutExtension = getTemplateNameWithoutExtension();
                String savedExtension = getExtension();
                TemplateEngine savedEngine = getEngine();
                int savedStrategyIndex = i;

                try {
                    if (i < strategies.length) {
                        found = strategies[i++].findTemplate(this);
                    } else {
                        found = findTemplateInTemplateEngine(this);
                    }
                } finally {
                    if (!found) {
                        // 恢复状态，以便尝试其它平级strategies
                        setTemplateNameWithoutExtension(savedTemplateNameWithoutExtension);
                        setExtension(savedExtension);
                        setEngine(savedEngine);
                        i = savedStrategyIndex;
                    }
                }

                return found;
            }
        };

        if (!matcher.findTemplate()) {
            throw new TemplateNotFoundException("Could not find template \"" + matcher.getOriginalTemplateName() + "\"");
        }

        if (cacheEnabled) {
            result = new TemplateMatchResultImpl(matcher.getTemplateName(), matcher.getEngine());
            matchedTemplates.put(key, result);
        } else {
            result = matcher;
        }

        return result;
    }

    private boolean findTemplateInTemplateEngine(TemplateMatcher matcher) {
        TemplateEngine engine = getTemplateEngine(matcher.getExtension());

        matcher.setEngine(engine);

        if (engine == null) {
            return false;
        }

        String templateName = matcher.getTemplateName();

        LOG.trace("Searching for template \"{}\" using {}", templateName, engine);

        return engine.exists(templateName);
    }

    private void ensureInit() {
        if (!init) {
            init();
        }
    }

    @Override
    public String evaluate(String templateName, TemplateContext context) throws TemplateException, IOException {
        TemplateMatchResult result = findTemplate(templateName);
        TemplateEngine engine = Assert.assertNotNull(result.getEngine(), "templateEngine");

        return engine.evaluate(result.getTemplateName(), context);
    }

    @Override
    public void evaluate(String templateName, TemplateContext context, OutputStream ostream) throws TemplateException, IOException {
        TemplateMatchResult result = findTemplate(templateName);
        TemplateEngine engine = Assert.assertNotNull(result.getEngine(), "templateEngine");

        engine.evaluate(result.getTemplateName(), context, ostream);
    }

    @Override
    public void evaluate(String templateName, TemplateContext context, Writer writer) throws TemplateException, IOException {
        TemplateMatchResult result = findTemplate(templateName);
        TemplateEngine engine = Assert.assertNotNull(result.getEngine(), "templateEngine");

        engine.evaluate(result.getTemplateName(), context, writer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getTemplateEngine(Class<? extends TemplateEngine> engineImplement) {
        return (T) container.getExtension(engineImplement);
    }

}
