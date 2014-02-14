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

import javax.persistence.EntityManagerFactory;


/**
 * In OSGI container depend on aries JPA module.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-12-10
 */

public class OsgiEntityManagerFactoryProvider implements EntityManagerFactoryProvider
{

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.jpa.EntityManagerFactoryProvider#createEntityManagerFactory(java.lang.String, java.util.Map)
     */
    @Override
    public EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, Map properties) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.jpa.EntityManagerFactoryProvider#createEntityManagerFactory(java.lang.String)
     */
    @Override
    public EntityManagerFactory createEntityManagerFactory(String persistenceUnitName) {
        // TODO Auto-generated method stub
        return null;
    }

}
