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

package org.solmix.fmk.cm;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.cm.ConfigManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-2
 */
public class SpringManagedService implements BeanNameAware, BeanFactoryAware, BeanFactoryPostProcessor
{

    public static final String XML_FILE_EXTENSION = ".xml";

    private final PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Resource[] locations;

    private ConfigManager[] managers;

    private String fileEncoding;

    private final boolean ignoreResourceNotFound = false;

    private BeanFactory beanFactory;

    private String beanName;
    
    public SpringManagedService(){
    }

    public void setLocation(Resource location) {
        this.locations = new Resource[] { location };
    }

    public void setLocations(Resource[] locations) {
        this.locations = locations;
    }

    public void setManager(ConfigManager manager) {
        this.managers = new ConfigManager[] { manager };

    }

    public void setManagers(ConfigManager[] managers) {
        this.managers = managers;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        importStaticProperties();

    }

    /**
     * 
     */
    protected void importStaticProperties() {
        if (this.managers != null && this.managers.length > 0) {
            for (ConfigManager cm : managers) {
                String pid = cm.getPid();
                Resource res = lookupResourceByPid(pid);
                Properties pop = this.loadProperties(res);
                Enumeration en = pop.keys();
                while (en.hasMoreElements()) {
                    String key = (String) en.nextElement();
                    String value = (String)pop.get(key);
                    if(value.indexOf("${")!=-1){
                        String sysKey=value.substring(value.indexOf("${")+2, value.indexOf("}"));
                       String new_v= System.getProperty(sysKey);
                       if(new_v!=null)
                           value= value.replace("${"+sysKey+"}", new_v);
                           pop.put(key, value);
                    }
                    
                }
                cm.updateConfig(pop);

            }
        }

    }

    private Resource lookupResourceByPid(String pid) {
        if (this.locations == null || this.locations.length <= 0 || pid == null)
            return null;
        for (Resource location : locations) {
            String fileName = location.getFilename();
            if (fileName != null && fileName.startsWith(pid)) {
                return location;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    @Override
    public void setBeanName(String name) {
        this.beanName = name;

    }

    protected Properties loadProperties(Resource location) {
        Properties props = new Properties();
        if (logger.isInfoEnabled()) {
            logger.info("Loading properties file from " + location);
        }
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
            if (this.ignoreResourceNotFound) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Could not load properties from " + location + ": " + ex.getMessage());
                }
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return props;
    }
}
