/*
 * Copyright 2013 The Solmix Project
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

package org.solmix.mybatis;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.SystemContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年6月13日
 */

public class LocalSqlSessionFactoryProvider extends AbstractSqlSessionFactoryProvider
{


    private final Logger LOG = LoggerFactory.getLogger(LocalSqlSessionFactoryProvider.class.getName());

    public LocalSqlSessionFactoryProvider(final SystemContext sc)
    {
        super(sc);
    }

    /**
     * {@inheritDoc}
     * @throws IOException 
     * 
     * @see org.solmix.mybatis.AbstractSqlSessionFactoryProvider#getConfigAsStream(java.lang.String)
     */
    @Override
    protected InputStream getConfigAsStream(String environment,String configLocation) throws IOException {
        return Resources.getResourceAsStream(configLocation);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.mybatis.AbstractSqlSessionFactoryProvider#getMapperResources(java.lang.String)
     */
    @Override
    protected Map<String, InputStream> getMapperResources(String environment,String mapperLocations) throws IOException{
        Map<String, InputStream> mapperResources=new HashMap<String,InputStream>();
        List<org.springframework.core.io.Resource> resources = new ArrayList<org.springframework.core.io.Resource>();
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
                Thread.currentThread().getContextClassLoader());

            Collections.addAll(resources,
                resolver.getResources(mapperLocations));
            for(org.springframework.core.io.Resource r:resources){
                mapperResources.put(r.toString(), r.getInputStream());
            }
        } catch (IOException ex) {
            // ignore
        }
        return mapperResources;
    }
    
}
