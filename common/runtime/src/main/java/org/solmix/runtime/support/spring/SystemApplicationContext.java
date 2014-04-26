/*
 *  Copyright 2012 The Solmix Project
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

package org.solmix.runtime.support.spring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.ClassLoaderUtil;
import org.solmix.runtime.bean.BeanConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-3
 */

public class SystemApplicationContext extends ClassPathXmlApplicationContext
{

    public static final String DEFAULT_CFG_FILE = "META-INF/solmix/solmix.xml";

    public static final String DEFAULT_EXT_CFG_FILE = "classpath*:META-INF/solmix/solmix.modules";

    private static final Logger log = LoggerFactory.getLogger(SystemApplicationContext.class);

    private String[] cfgFiles;

    private URL[] cfgURLs;

    private final boolean includeDefault;

    public SystemApplicationContext(String[] cfgFiles, ApplicationContext parent, boolean includeDefault)
    {
        super(new String[0], false, parent);
        this.cfgFiles = cfgFiles;
        this.includeDefault = includeDefault;
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
                @Override
                public Boolean run() throws Exception {
                    refresh();
                    return Boolean.TRUE;
                }
                
            });
        } catch (PrivilegedActionException e) {
            if (e.getException() instanceof RuntimeException) {
                throw (RuntimeException)e.getException();
            }
        }
    }

    @Override
    protected Resource[] getConfigResources() {
        List<Resource> resources = new ArrayList<Resource>();
        if (includeDefault) {
            try {
                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader());

                Collections.addAll(resources, resolver.getResources(DEFAULT_CFG_FILE));

                Resource[] exts = resolver.getResources(DEFAULT_EXT_CFG_FILE);
                for (Resource r : exts) {
                    InputStream is = r.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String line = rd.readLine();
                    while (line != null) {
                        if (!"".equals(line)) {
                            resources.add(resolver.getResource(line));
                        }
                        line = rd.readLine();
                    }
                    is.close();
                }

            } catch (IOException ex) {
                // ignore
            }
        }
        boolean usingDefault = false;
        if (cfgFiles == null) {
            String userConfig = System.getProperty(BeanConfigurer.USER_CFG_FILE_PROPERTY_NAME);
            if (userConfig != null) {
                cfgFiles = new String[] { userConfig };
            }
        }
        if (cfgFiles == null) {
            usingDefault=true;
            cfgFiles = new String[] { BeanConfigurer.USER_CFG_FILE };
        }
        for (String cfgFile : cfgFiles) {
            final Resource f = findResource(cfgFile);
            if (f!=null&&f.exists()) {
                resources.add(f);
                log.info("Used configed file {0}",cfgFile);
            }else{
                if(!usingDefault){
                    log.warn("Can't find configure file {0}",cfgFile);
                    throw new ApplicationContextException("Can't find configure file");
                }
            }
        }
        if(cfgURLs!=null){
            for(URL u:cfgURLs){
                UrlResource ur = new UrlResource(u);
                if(ur.exists()){
                    resources.add(ur);
                }else{
                    log.warn("Can't find configure file {0}",u.getPath());
                }
            }
        }
        if(log.isDebugEnabled()){
            log.debug("Creating application context with resources "+resources.size());
        }
        if(0==resources.size()){
            return null;
        }
        Resource[] res = new Resource[resources.size()];
        res=resources.toArray(res);
        return res;

    }

    /**
     * @param cfgFile
     * @return
     */
    public static Resource findResource(final String cfgFile) {
        try {
            return AccessController.doPrivileged(new PrivilegedAction<Resource>() {
                @Override
                public Resource run() {
                    Resource cpr = new ClassPathResource(cfgFile);
                    if (cpr.exists()) {
                        return cpr;
                    }
                    try {
                        //see if it's a URL
                        URL url = new URL(cfgFile);
                        cpr = new UrlResource(url);
                        if (cpr.exists()) {
                            return cpr;
                        }
                    } catch (MalformedURLException e) {
                        //ignore
                    }
                    //try loading it our way
                    URL url = ClassLoaderUtil.getResource(cfgFile, SystemApplicationContext.class);
                    if (url != null) {
                        cpr = new UrlResource(url);
                        if (cpr.exists()) {
                            return cpr;
                        }
                    }
                    cpr = new FileSystemResource(cfgFile);
                    if (cpr.exists()) {
                        return cpr;
                    }
                    return null;
                }
            });
        } catch (AccessControlException ex) {
            //cannot read the user config file
            return null;
        }
    }
}
