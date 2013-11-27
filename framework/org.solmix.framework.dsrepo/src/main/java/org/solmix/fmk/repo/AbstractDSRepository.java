/*
 * ========THE SOLMIX PROJECT=====================================
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.repo.DSRepository;

/**
 * 
 * @version 110035
 */
public abstract class AbstractDSRepository implements DSRepository
{

    private static final Logger log = LoggerFactory.getLogger(AbstractDSRepository.class.getName());

    private final ObjectType objectType;

    private final ObjectFormat objectFormat;

    public String name;

 
    public AbstractDSRepository(String name, ObjectType objectType, ObjectFormat objectFormat)
    {
        this.name = name;
        this.objectType = objectType;
        this.objectFormat = objectFormat;
    }

    /**
     * @return the objectField
     */
    @Override
    public ObjectType getObjectType() {
        return objectType;
    }

   

    /**
     * @return the objectFormat
     */
    @Override
    public ObjectFormat getObjectFormat() {
        return objectFormat;
    }

  

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.repo.IDSRepo#getName()
     */
    @Override
    public String getName() {
        return name;
    }

}
