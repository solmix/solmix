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

package com.solmix.fmk.javassist;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jxpath.JXPathContext;
import com.solmix.fmk.datasource.JaxPathMapFactory;

/**
 * 
 * @version 110035 2011-3-9 Administrator
 */

public class SampleT
{

    public String a = "1";

    public static void main(String[] args) {
        Map<String, Object> _return = new HashMap<String, Object>();
        // _return.put("xx", new HashMap<String, Object>());
        JXPathContext jxpc = JXPathContext.newContext(_return);
        jxpc.setFactory(new JaxPathMapFactory());
        jxpc.createPathAndSetValue("/xx/ss", "sdf");
        // System.out.println(((Map) _return.get("xx")).get("ss"));
        System.out.println(jxpc.getValue("/xx/ss"));
    }
}
