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

package com.solmix.fmk.util;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.Assert;

import org.apache.commons.jxpath.JXPathContext;
import org.junit.Before;
import org.junit.Test;

import com.solmix.api.jaxb.ObjectFactory;
import com.solmix.api.jaxb.TdataSource;
import com.solmix.fmk.datasource.JaxPathMapFactory;

/**
 * 
 * @author Administrator
 * @version 110035 2012-3-31
 */

public class MapperUtilTest
{

    TdataSource tds;

    @Test
    public void toClientValueMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("a3", "aa");
        map.put("a2", "bb");
        map.put("b1", "cc");
        for (Iterator iterator = map.values().iterator(); iterator.hasNext();) {
            String name = (String) iterator.next();
            System.out.println(name);
        }

        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("a3", "aa");
        map2.put("a2", "bb");
        map2.put("b1", "cc");
        for (Iterator iterator = map2.values().iterator(); iterator.hasNext();) {
            String name = (String) iterator.next();
            System.out.println(name);
        }
        Map<String, String> map3 = new TreeMap<String, String>(new Comparator<Object>() {

            Collator collator = Collator.getInstance();

            public int compare(Object o1, Object o2) {
                CollationKey key1 = collator.getCollationKey(o1.toString());
                CollationKey key2 = collator.getCollationKey(o2.toString());
                return key1.compareTo(key2);
                // return collator.compare(o1, o2);
            }
        });
        map.put("a3", "aa");
        map.put("a2", "bb");
        map.put("b1", "cc");
        for (Iterator iterator2 = map.values().iterator(); iterator2.hasNext();) {
            String name3 = (String) iterator2.next();
            System.out.println(name3);
        }
    }

    @Test
    public void jaxpathMapFactory() {
        Map<String, Object> _return = new HashMap<String, Object>();
        // _return.put("xx", new HashMap<String, Object>());
        JXPathContext jxpc = JXPathContext.newContext(_return);
        jxpc.setFactory(new JaxPathMapFactory());
        jxpc.createPathAndSetValue("/xx/ss", "sdf");
        Assert.assertEquals(jxpc.getValue("/xx/ss"), "sdf");
    }

    @Before
    public void setUp() {
        ObjectFactory of = new ObjectFactory();
        tds = of.createTdataSource();
        tds.setCacheData("dd");
        tds.setDbName("solmix");
    }
}
