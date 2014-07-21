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
package org.solmix.fmk.repo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.exception.SlxException;
import org.solmix.runtime.SystemContext;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013年11月23日
 */

public class ConfiguredFileRepository extends AbstractDSRepository
{
    private final static Logger log = LoggerFactory.getLogger(ConfiguredFileRepository.class.getName());

    private final SystemContext sc;
    public ConfiguredFileRepository(final SystemContext sc){
        super(BUILDIN_FILE, ObjectType.STREAM,ObjectFormat.XML);
        this.sc=sc;
    }
   


    public static final String DEFAULT_DS_FILES = "META-INF/datasource/";
   
   

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.repo.DSRepository#load(java.lang.String)
     */
    @Override
    public Object load(String ds) throws SlxException {
        String path = new StringBuilder().append(DEFAULT_DS_FILES).append(ds).append(".xml").toString();
        Object _return = loadFrom(Thread.currentThread().getContextClassLoader(),path);
        if(_return==null){
            _return=loadFrom(sc.getBean(ClassLoader.class),path);
        }
       
        return _return;
    }
    private Object loadFrom(ClassLoader loader,String path){
        try {
            Enumeration<URL> urls= loader.getResources(path);
            InputStream find=null ;
            while(urls.hasMoreElements()){
                URL tmp=urls.nextElement();
                if(find==null){
                    find= loader.getResourceAsStream(path);
                    if(log.isTraceEnabled())
                        log.trace("used url "+tmp.getPath()+" for datasource "+path);
                } else{
                    if(log.isWarnEnabled())
                        log.warn("find other resource for datasource :"+path);
                }
            }
            return find;
        } catch (IOException e) {
           log.error("find datasource resource error:",e);
        }
        return null;
    }

   
}
