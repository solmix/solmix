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

package org.solmix.jpa;

import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.context.SystemContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-10-21
 */
@SuppressWarnings("rawtypes")
public class LocalEntityManagerFactoryProvider implements EntityManagerFactoryProvider
{
    private PersistenceProvider persistenceProvider;
    private final Map<String,EntityManagerFactory> _tempCache= new java.util.concurrent.ConcurrentHashMap<String, EntityManagerFactory>();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private SystemContext sc;
   public LocalEntityManagerFactoryProvider(){
        
    }
    
    public LocalEntityManagerFactoryProvider(final SystemContext sc){
        setSystemContext(sc);
    }
    /**
     * @param systemContext
     */
    @Resource
    public void setSystemContext(final SystemContext systemcontext) {
        this.sc=systemcontext;
        if(sc!=null){
            sc.setBean(this, EntityManagerFactoryProvider.class);
        }
        
    }
    /**
     * @return the persistenceProvider
     */
    public PersistenceProvider getPersistenceProvider() {
        return persistenceProvider;
    }

    /**
     * @param persistenceProvider the persistenceProvider to set
     */
    public void setPersistenceProvider(PersistenceProvider persistenceProvider) {
        this.persistenceProvider = persistenceProvider;
    }

    @Override
    public EntityManagerFactory createEntityManagerFactory(String persistenceUnitName){
        return createEntityManagerFactory(persistenceUnitName,null);
    }
    
    @Override
    public EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, Map jpaProperties){
        if(_tempCache.get(persistenceUnitName)!=null){
            logger.debug("return EntityManagerFactory from locale cache");
            EntityManagerFactory emf= _tempCache.get(persistenceUnitName);
            if(!emf.isOpen()){
                _tempCache.remove(persistenceUnitName);
            }else{
                return emf;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Building JPA EntityManagerFactory for persistence unit '" + persistenceUnitName + "'");
        }
        EntityManagerFactory emf;
        PersistenceProvider provider = getPersistenceProvider();
        if (provider != null) {
            // Create EntityManagerFactory directly through PersistenceProvider.
             emf = provider.createEntityManagerFactory(persistenceUnitName, jpaProperties);
            if (emf == null) {
                throw new IllegalStateException("PersistenceProvider [" + provider + "] did not return an EntityManagerFactory for name '"
                    + persistenceUnitName + "'");
            }
            
        } else {
            // Let JPA perform its standard PersistenceProvider autodetection.
            emf= Persistence.createEntityManagerFactory(persistenceUnitName, jpaProperties);
        }
        _tempCache.put(persistenceUnitName, emf);
        return emf;
    }
}
