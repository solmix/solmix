/*
 * SOLMIX PROJECT
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

package org.solmix.fmk.cm.spring;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.cm.ConfigureUnit;
import org.solmix.api.cm.ConfigureUnitManager;
import org.solmix.commons.util.DataUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-5
 */

public class SpringConfigureUnitManager implements ConfigureUnitManager
{

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String DEFAULT_CFG_FILES = "classpath*:META-INF/solmix/*.cfg";

    public static final String XML_FILE_EXTENSION = ".xml";

    private Map<String, ConfigureUnit> configCache;

    private final PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

    private String fileEncoding;

    @Override
    public ConfigureUnit createFactoryConfigureUnit(String factoryPid) throws IOException {
        throw new java.lang.RuntimeException("This method is not support by " + this.getClass().getName());

    }

    @Override
    public synchronized ConfigureUnit getConfigureUnit(String pid) throws IOException {
 
        if (configCache == null) {
                configCache = buildConfig();
        }
        return configCache.get(pid);
    }

    private  Map<String, ConfigureUnit> buildConfig() throws IOException {
        Map<String, ConfigureUnit> configs = new java.util.concurrent.ConcurrentHashMap<String, ConfigureUnit>();
        List<Resource> defaultResources = getConfigureResource(DEFAULT_CFG_FILES);
        for (Resource re : defaultResources) {
            String pid = getPidByFileName(re.getFilename());
            configs.put(pid, new ConfigureUnitImpl(pid, loadProperties(re), this));
            logTraceMessage("Loaded default configuration properties  from file:",re);
        }
        String userParttern = System.getProperty(ConfigureUnit.USER_CONFIG_DIR_PROPERTY_NAME);
        if (userParttern == null)
            userParttern = ConfigureUnit.USER_CONFIG_DIR;
        List<Resource> userConfigs = getConfigureResource(userParttern);
        if (userConfigs != null && userConfigs.size() > 0) {
            for (Resource re : userConfigs) {
                String pid = getPidByFileName(re.getFilename());
                if (configs.containsKey(pid)) {
                    ConfigureUnit cu = configs.get(pid);
                    Properties p = loadProperties(re);
                    cu.update(toDictionary(p));
                    logTraceMessage("merge user configured properties to merge system default:",re);
                } else {
                    configs.put(pid, new ConfigureUnitImpl(pid, loadProperties(re), this));
                    logTraceMessage("Loaded user configuration properties  from file:",re);
                }
            }
        }

        return configs;
    }
    private void logTraceMessage(String msg,Resource location){
        if (logger.isTraceEnabled()) {
            try {
                logger.trace(msg + location.getURL().getPath());
            } catch (IOException e) {
            }
        }
    }

    private Dictionary<String, ?> toDictionary(Properties properties) {
        Dictionary<String, Object> dic = new Hashtable<String, Object>();
        if (properties != null) {
            Enumeration<Object> en = properties.keys();
            while (en.hasMoreElements()) {
                Object key = en.nextElement();
                dic.put(key.toString(), properties.get(key));
            }
        }
        return dic;
    }

    protected String getPidByFileName(String fileName) {
        if (fileName.endsWith(".cfg")) {
            return fileName.substring(0, fileName.length() - 4);
        }
        return fileName;
    }

    @Override
    public ConfigureUnit[] listConfigureUnits(String filter) throws IOException {
        ConfigureUnit[] fiter = null;
        if (configCache == null) {
            configCache = buildConfig();
        }
        if (filter == null) {
            Collection<ConfigureUnit> conf = configCache.values();
            fiter = conf.toArray(new ConfigureUnit[] {});
        }
        return fiter;
    }

    public List<Resource> getConfigureResource(String dir) {
        List<Resource> resources = new ArrayList<Resource>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader());
        try {
            Collections.addAll(resources, resolver.getResources(dir));

        } catch (IOException e) {
            logger.error("IOException", e);
        }
        return resources;
    }

    protected Properties loadProperties(Resource location) {
        Properties props = new Properties();
        InputStream is = null;
        try {
            is = location.getInputStream();
            String filename = location.getFilename();
            if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
                this.propertiesPersister.loadFromXml(props, is);
            } else {
                if (this.fileEncoding != null) {
                    this.propertiesPersister.load(props, new InputStreamReader(is, this.fileEncoding));
                } else {
                    this.propertiesPersister.load(props, is);
                }
            }
        } catch (IOException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Could not load properties from " + location + ": " + ex.getMessage());
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        checkSystemProperties(props);
        return props;
    }

    /**
     * @param props
     */
    private void checkSystemProperties(Properties properties) {
        if (properties != null) {
            Enumeration<Object> en = properties.keys();
            while (en.hasMoreElements()) {
                Object key = en.nextElement();
                Object value = properties.get(key);
                if (value != null) {
                    value = DataUtil.getTemplateValue(value.toString());
                }
                properties.put(key, value);
            }
        }

    }

}
