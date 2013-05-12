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

package com.solmix.fmk.datasource;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

/**
 * 
 * @author Administrator
 * @version 110035 2012-3-31
 */

public class JaxPathMapFactory extends AbstractFactory
{

    public boolean createObject(JXPathContext context, Pointer pointer, Object parent, String name, int index) {

        if (parent instanceof Map) {
            Map p = (Map) parent;
            Map<String, Object> child = new HashMap<String, Object>();
            p.put(name, child);
            return true;
        } else {
            return false;
        }
    }
}
