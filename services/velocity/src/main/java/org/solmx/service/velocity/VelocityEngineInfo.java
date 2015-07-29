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

package org.solmx.service.velocity;

import static org.apache.velocity.runtime.RuntimeConstants.INPUT_ENCODING;
import static org.apache.velocity.runtime.RuntimeConstants.OUTPUT_ENCODING;
import static org.apache.velocity.runtime.RuntimeConstants.PARSER_POOL_SIZE;
import static org.apache.velocity.runtime.RuntimeConstants.RESOURCE_LOADER;
import static org.apache.velocity.runtime.RuntimeConstants.RESOURCE_MANAGER_LOGWHENFOUND;
import static org.apache.velocity.runtime.RuntimeConstants.RUNTIME_LOG;
import static org.apache.velocity.runtime.RuntimeConstants.RUNTIME_LOG_LOGSYSTEM;
import static org.apache.velocity.runtime.RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS;
import static org.apache.velocity.runtime.RuntimeConstants.RUNTIME_REFERENCES_STRICT;
import static org.apache.velocity.runtime.RuntimeConstants.SET_NULL_ALLOWED;
import static org.apache.velocity.runtime.RuntimeConstants.UBERSPECT_CLASSNAME;
import static org.apache.velocity.runtime.RuntimeConstants.VM_ARGUMENTS_STRICT;
import static org.apache.velocity.runtime.RuntimeConstants.VM_LIBRARY;
import static org.apache.velocity.runtime.RuntimeConstants.VM_LIBRARY_AUTORELOAD;
import static org.apache.velocity.runtime.RuntimeConstants.VM_LIBRARY_DEFAULT;
import static org.apache.velocity.runtime.RuntimeConstants.VM_PERM_INLINE_LOCAL;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.app.event.EventHandler;
import org.slf4j.Logger;
import org.solmix.commons.util.ArrayUtils;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.Files;
import org.solmix.commons.util.ObjectUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.resource.InputStreamResource;
import org.solmix.runtime.resource.ResourceManager;
import org.solmx.service.velocity.support.CloneableEventCartridge;
import org.solmx.service.velocity.support.CustomizedUberspectImpl;
import org.solmx.service.velocity.support.RenderableHandler;
import org.solmx.service.velocity.support.Slf4jLogChute;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年7月28日
 */

public class VelocityEngineInfo
{

    public static final String DEFAULT_CHARSET = "UTF-8";

    private final ExtendedProperties properties = new ExtendedProperties();

    private Object[] plugins;

    private boolean strictReference = true;

    // template charset encoding
    private String charset;

    // global macros
    private String[] macros;

    private boolean productionMode = true;

    // resource loader
    private String path;

    private boolean cacheEnabled = true;

    private int modificationCheckInterval = 2;

    private final Logger log;

    private ResourceManager resourceManager;

    private final Map<String, InputStreamResource> preloadedResources = new HashMap<String, InputStreamResource>();

    private final CloneableEventCartridge eventCartridge = new CloneableEventCartridge();

    /**
     * @param log
     */
    public VelocityEngineInfo(Logger log)
    {
        this.log = log;
    }

    public void init(ResourceManager resourceManager) throws Exception {
        this.resourceManager = resourceManager;
        removeReservedProperties();

        initPlugins();
        initLogger();
        initMacros();
        initResourceLoader(); 
        initEventHandlers();
        initMiscs();
    }

    private void initResourceLoader() {
        if (productionMode) {
            cacheEnabled = true;
        }
    }

    private void initEventHandlers() {
        // 准备eventCartridge，并设置默认的handler
        boolean hasRenderableHandler = false;

        if (!ArrayUtils.isEmptyArray(plugins)) {
            for (Object plugin : plugins) {
                if (plugin instanceof RenderableHandler) {
                    hasRenderableHandler = true;
                    break;
                }
            }
        }

        if (!hasRenderableHandler) {
            addHandler(new RenderableHandler());
        }

        if (!ArrayUtils.isEmptyArray(plugins)) {
            for (Object plugin : plugins) {
                if (plugin instanceof EventHandler) {
                    addHandler((EventHandler) plugin);
                }
            }
        }
    }

    private void addHandler(EventHandler handler) {
        Assert.assertTrue(eventCartridge.addEventHandler(handler), "Unknown event handler type: %s", handler.getClass());
    }

    /** 初始化杂项。 */
    private void initMiscs() {
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }

        setDefaultProperty(RESOURCE_MANAGER_LOGWHENFOUND, "false");
        setDefaultProperty(INPUT_ENCODING, charset);
        setDefaultProperty(OUTPUT_ENCODING, DEFAULT_CHARSET);
        setDefaultProperty(PARSER_POOL_SIZE, "50");
        setDefaultProperty(UBERSPECT_CLASSNAME, CustomizedUberspectImpl.class.getName());
        setDefaultProperty(VM_ARGUMENTS_STRICT, "true");
        setDefaultProperty(VM_PERM_INLINE_LOCAL, "true");
        setDefaultProperty(SET_NULL_ALLOWED, "true");

        // 自动加载宏
        if (productionMode) {
            properties.setProperty(VM_LIBRARY_AUTORELOAD, "false");
        } else {
            properties.setProperty(VM_LIBRARY_AUTORELOAD, "true");
        }

        // strict ref
        properties.setProperty(RUNTIME_REFERENCES_STRICT, String.valueOf(strictReference));
    }

    private void setDefaultProperty(String key, Object value) {
        if (!properties.containsKey(key)) {
            properties.setProperty(key, value);
        }
    }

    /** 查找所有全局macros。 */
    private void initMacros() throws Exception {

        if (macros != null) {
            for (String macro : macros) {
                resolveMacro(resourceManager, macro);
            }
        }

        resolveMacro(resourceManager, VM_LIBRARY_DEFAULT);

        // Plugin macros
        if (plugins != null) {
            for (Object plugin : plugins) {
                if (plugin instanceof VelocityPlugin) {
                    addMacroResources(null, ((VelocityPlugin) plugin).getMacros());
                }
            }
        }

        if (!properties.containsKey(VM_LIBRARY)) {
            properties.setProperty(VM_LIBRARY, ObjectUtils.EMPTY_STRING);
        }
    }

    private void resolveMacro(ResourceManager resourceManager, String macro) {
        String path = Files.normalizeAbsolutePath(this.path + "/");
        String pattern = Files.normalizeAbsolutePath(path + macro);
        InputStreamResource[] resources;

        try {
            resources = resourceManager.getResourcesAsStream(pattern);
        } catch (IOException e) {
            resources = null;
        }

        addMacroResources(path, resources);
    }

    private void addMacroResources(String path, InputStreamResource[] resources) {
        if (resources != null) {
            @SuppressWarnings("unchecked")
            Set<String> macros = new HashSet<String>(properties.getVector(VM_LIBRARY));

            for (InputStreamResource resource : resources) {
                if (resource.exists()) {
                    String templateName = resource.getFilename();

                    if (templateName == null) {
                        templateName = getTemplateNameOfPreloadedResource(resource);
                    }

                    if (!macros.contains(templateName)) {
                        properties.addProperty(VM_LIBRARY, templateName);
                        macros.add(templateName);
                    }
                }
            }
        }
    }

    private String getTemplateNameOfPreloadedResource(InputStreamResource resource) {
        URL url;
        try {
            url = resource.getURL();
        } catch (IOException e) {
            url = null;
        }
        String templateNameBase;

        if (url != null) {
            templateNameBase = "globalVMs/" + Files.getFilename(url.getPath());
        } else {
            templateNameBase = "globalVMs/globalVM.vm";
        }

        String templateName = templateNameBase;

        for (int i = 1; preloadedResources.containsKey(templateName) && !resource.equals(preloadedResources.get(templateName)); i++) {
            templateName = templateNameBase + i;
        }

        preloadedResources.put(templateName, resource);

        return templateName;
    }

    /** 初始化日志系统。 */
    private void initLogger() {
        properties.setProperty(RUNTIME_LOG_LOGSYSTEM, new Slf4jLogChute(log));
    }

    private void initPlugins() throws Exception {
        if (plugins != null) {
            for (Object plugin : plugins) {
                if (plugin instanceof VelocityPlugin) {
                    ((VelocityPlugin) plugin).init(this);
                }
            }
        }
    }

    /** 删除保留的properties，这些properties用户不能修改。 */
    private void removeReservedProperties() {
        Set<String> keysToRemove = new HashSet<String>();

        // Remove resource loader settings
        keysToRemove.add(RESOURCE_LOADER);

        for (Iterator<?> i = properties.getKeys(); i.hasNext();) {
            Object key = i.next();

            if (key instanceof String && ((String) key).contains(RESOURCE_LOADER)) {
                keysToRemove.add((String) key);
            }
        }

        // Remove log settings
        keysToRemove.add(RUNTIME_LOG);
        keysToRemove.add(RUNTIME_LOG_LOGSYSTEM);
        keysToRemove.add(RUNTIME_LOG_LOGSYSTEM_CLASS);

        // Remove macros
        keysToRemove.add(VM_LIBRARY);

        // Remove event handlers: 仅移除eventhandler.xxx.class，保留其它参数
        for (Iterator<?> i = properties.getKeys(); i.hasNext();) {
            Object key = i.next();

            if (key instanceof String && ((String) key).startsWith("eventhandler.") && ((String) key).endsWith(".class")) {
                keysToRemove.add((String) key);
            }
        }

        // remove others
        keysToRemove.add(INPUT_ENCODING);
        keysToRemove.add(VM_LIBRARY_AUTORELOAD);
        keysToRemove.add(RUNTIME_REFERENCES_STRICT);

        // do removing
        for (String key : keysToRemove) {
            if (properties.containsKey(key)) {
                log.warn("Removed reserved property: {} = {}", key, properties.get(key));
                properties.clearProperty(key);
            }
        }
    }

    /**
     * @return
     */
    public ExtendedProperties getProperties() {
        return properties;
    }

    /**
     * @return
     */
    public CloneableEventCartridge getEventCartridge() {
        return eventCartridge;
    }

    public void setAdvancedProperties(Map<String, Object> configuration) {
        this.properties.clear();

        for (Map.Entry<String, Object> entry : configuration.entrySet()) {
            this.properties.setProperty(entry.getKey(), entry.getValue());
        }
    }

    /** 设置搜索模板的根目录。默认为<code>/templates</code>。 */
    public void setPath(String path) {
        this.path = StringUtils.trimToNull(path);
    }

    /** 是否开启模板缓存。在生产模式下，该模式将被强行开启。 */
    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    /** 设置检查模板被修改的间隔（秒）。默认为2秒。 */
    public void setModificationCheckInterval(int modificationCheckInterval) {
        this.modificationCheckInterval = modificationCheckInterval;
    }

    public boolean isProductionMode() {
        return productionMode;
    }

    /** 设置生产模式。默认为<code>true</code>。 */
    public void setProductionMode(boolean productionMode) {
        this.productionMode = productionMode;
    }

    /** 设置strict reference模式。默认为<code>true</code>。 */
    public void setStrictReference(boolean strictReference) {
        this.strictReference = strictReference;
    }

    /** 设置模板的字符集编码。 */
    public void setTemplateEncoding(String charset) {
        this.charset = StringUtils.trimToNull(charset);
    }

    /** 设置全局宏的名称，可包含通配符。 */
    public void setGlobalMacros(String[] macros) {
        this.macros = macros;
    }

    /** 设置plugins。 */
    public void setPlugins(Object[] plugins) {
        this.plugins = plugins;
    }

}
