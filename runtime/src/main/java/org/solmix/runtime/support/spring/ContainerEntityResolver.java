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
package org.solmix.runtime.support.spring;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.DelegatingEntityResolver;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.CollectionUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月29日
 */

public class ContainerEntityResolver extends DelegatingEntityResolver
{
    private static final Logger LOG= LoggerFactory.getLogger(ContainerEntityResolver.class);
    private final EntityResolver dtdResolver;
    private final EntityResolver schemaResolver;
    private Map<String, String> schemaMappings;
    private final ClassLoader classLoader;
    
    public ContainerEntityResolver(ClassLoader loader, EntityResolver dr, EntityResolver sr) {
        super(dr, sr);
        classLoader = loader;
        dtdResolver = dr;
        schemaResolver = sr;
        
        try {
            Properties mappings = PropertiesLoaderUtils.loadAllProperties("META-INF/spring.schemas", 
                                                                          classLoader);
            schemaMappings = new ConcurrentHashMap<String, String>(mappings.size());
            CollectionUtils.mergePropertiesIntoMap(mappings, schemaMappings);
        } catch (IOException e) {
            //ignore
        }
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        InputSource source = super.resolveEntity(publicId, systemId);
        if (null == source && null != systemId) {
            // try the schema and dtd resolver in turn, ignoring the suffix in publicId
            LOG.info( "Attempting to resolve systemId {}", systemId);
            source = schemaResolver.resolveEntity(publicId, systemId);                
            if (null == source) {
                source = dtdResolver.resolveEntity(publicId, systemId); 
            }
        }
        String resourceLocation = schemaMappings.get(systemId);
        if (resourceLocation != null && publicId == null) {
            Resource resource = new ClassPathResource(resourceLocation, classLoader);
            if (resource != null && resource.exists()) {
                source.setPublicId(systemId);    
                source.setSystemId(resource.getURL().toString());
            }
        }
        return source;
    }    

}
