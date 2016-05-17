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
package org.solmix.service.toolkit.support;

import org.solmix.commons.util.UtilsHelper;
import org.solmix.service.toolkit.ToolSetFactory;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月27日
 */

public class UtilsToolSetFactory implements ToolSetFactory
{

   
    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public Iterable<String> getToolNames() {
        return UtilsHelper.getUtils().keySet();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.service.toolkit.ToolSetFactory#createTool(java.lang.String)
     */
    @Override
    public Object createTool(String name) throws Exception {
        return UtilsHelper.getUtils().get(name);
    }

}
