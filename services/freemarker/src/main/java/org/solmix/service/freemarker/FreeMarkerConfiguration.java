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

import static freemarker.core.Configurable.OUTPUT_ENCODING_KEY;
import static freemarker.core.Configurable.TEMPLATE_EXCEPTION_HANDLER_KEY;
import static freemarker.template.Configuration.CACHE_STORAGE_KEY;
import static freemarker.template.Configuration.DEFAULT_ENCODING_KEY;
import static freemarker.template.Configuration.LOCALIZED_LOOKUP_KEY;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ObjectUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.resource.ResourceManager;
import org.solmix.service.freemarker.support.TemplateContextWrapper;
import org.solmix.service.freemarker.support.TemplateLoaderAdapter;
import org.solmx.service.template.TemplateException;

import freemarker.cache.StrongCacheStorage;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年9月13日
 */

public class FreeMarkerConfiguration
{

   public static final  String DEFAULT_CHARSET = "UTF-8";

    private final Logger LOG;

    private final Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

    private final Map<String, String> properties = new HashMap<String, String>();

    private boolean productionMode = true;

    private TemplateLoader templateLoader;
    private ResourceManager resourceManager;
    private String path;

    private String charset;

    private FreeMarkerPlugin[] plugins;

    public FreeMarkerConfiguration(Logger log)
    {
        this.LOG = log;
    }

    public void init(ResourceManager resourceManager) throws Exception  {
        this. resourceManager=resourceManager;
        removeReservedProperties();
        initProperties();
        initPlugins();
        initWrapper();
        
    }
    
    private void initProperties() {
        Assert.assertNotNull(resourceManager, "resourceManager");

        // 模板字符集编码
        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }

        path =ObjectUtils.defaultIfNull(path, "/templates");
        templateLoader = new TemplateLoaderAdapter(resourceManager, path);

        configuration.setTemplateLoader(templateLoader);

        // 默认使用StrongCacheStorage
        setDefaultProperty(CACHE_STORAGE_KEY, StrongCacheStorage.class.getName());

        // 异常处理器
        setDefaultProperty(TEMPLATE_EXCEPTION_HANDLER_KEY, "rethrow");

        // 其它默认选项
        setDefaultProperty(DEFAULT_ENCODING_KEY, charset);
        setDefaultProperty(OUTPUT_ENCODING_KEY, DEFAULT_CHARSET);
        setDefaultProperty(LOCALIZED_LOOKUP_KEY, "false");

        // 设置选项
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = StringUtils.trimToNull(entry.getValue());

            if (value != null) {
                try {
                    configuration.setSetting(key, value);
                } catch (freemarker.template.TemplateException e) {
                    throw new TemplateException("invalid key and value: " + key + " = " + value, e);
                }
            }
        }
    }
    
    /** 取得用于装载模板的loader。 */
    public TemplateLoader getTemplateLoader() {
        return templateLoader;
    }

    /** 取得freemarker的配置。 */
    public Configuration getConfiguration() {
        return configuration;
    }

    public boolean isProduction() {
        return productionMode;
    }

    /** 设置生产模式。默认为<code>true</code>。 */
    public void setProduction(boolean productionMode) {
        this.productionMode = productionMode;
    }
    
    /** 设置搜索模板的根目录。默认为<code>/templates</code>。 */
    public void setPath(String path) {
        this.path = StringUtils.trimToNull(path);
    }

    /** 设置模板的字符集编码。 */
    public void setTemplateEncoding(String charset) {
        this.charset = StringUtils.trimToNull(charset);
    }

    /** 设置高级配置。 */
    public void setAdvancedProperties(Map<String, String> configuration) {
        this.properties.clear();
        this.properties.putAll(configuration);
    }

    /** 设置plugins。 */
    public void setPlugins(FreeMarkerPlugin[] plugins) {
        this.plugins = plugins;
    }

    /** 删除保留的properties，这些properties用户不能修改。 */
    private void removeReservedProperties() {
        Set<String> keysToRemove = new HashSet<String>();

        keysToRemove.add(DEFAULT_ENCODING_KEY);
        keysToRemove.add(LOCALIZED_LOOKUP_KEY);

        // do removing
        for (String key : keysToRemove) {
            if (properties.containsKey(key)) {
                LOG.warn("Removed reserved property: {} = {}", key, properties.get(key));
                properties.remove(key);
            }
        }
    }
    
    private void initPlugins() {
        if (plugins != null) {
            for (FreeMarkerPlugin plugin : plugins) {
                plugin.init(this);
            }
        }
    }

    private void initWrapper() {
        // 设置ObjectWrapper，使之支持TemplateContext对象
        configuration.setObjectWrapper(new TemplateContextWrapper(configuration.getObjectWrapper()));
    }

    /** 设置默认值。如果值已存在，则不覆盖。 */
    private void setDefaultProperty(String key, String value) {
        if (properties.get(key) == null) {
            properties.put(key, value);
        }
    }
}
