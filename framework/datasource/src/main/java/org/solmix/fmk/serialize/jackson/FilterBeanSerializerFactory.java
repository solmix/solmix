/*
 * Copyright 2012 The Solmix Project
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

package org.solmix.fmk.serialize.jackson;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

//import org.codehaus.jackson.map.AnnotationIntrospector;
//import org.codehaus.jackson.map.SerializationConfig;
//import org.codehaus.jackson.map.introspect.AnnotatedClass;
//import org.codehaus.jackson.map.introspect.BasicBeanDescription;
//import org.codehaus.jackson.map.ser.BeanPropertyWriter;
//import org.codehaus.jackson.map.ser.BeanSerializerFactory;
//import org.codehaus.jackson.map.util.ArrayBuilders;
import org.solmix.commons.util.DataUtil;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.util.ArrayBuilders;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2011-2-22 solmix-ds
 */
public class FilterBeanSerializerFactory extends BeanSerializerFactory
{

    /**
     * 
     */
    private static final long serialVersionUID = -3470138389144231980L;

    /**
     * @param config
     */
    protected FilterBeanSerializerFactory(SerializerFactoryConfig config)
    {
        super(config);
    }

    /**
     * Add bean property to filter-property list which would be remove from output stream
     * 
     * @param filterProperty
     */
    public void addFilterProperty(String[] filterProperty) {
        if (filterProperty == null)
            return;
        if (filterProperty.length == 1 && filterProperty[0].equals(""))
            return;
        ONLY_SERVER_PORP = DataUtil.arrayAdd(ONLY_SERVER_PORP, filterProperty);
    }

    private String[] ONLY_SERVER_PORP = { "__autoConstruct" };

    private HashSet<String> SERVER_SET;

    /**
     * Override for filter some special Bean properties
     */
//    @Override
    protected List<BeanPropertyWriter> filterBeanProperties(SerializationConfig config, BasicBeanDescription beanDesc, List<BeanPropertyWriter> props) {
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        AnnotatedClass ac = beanDesc.getClassInfo();
        String[] ignored = intr.findPropertiesToIgnore(ac);
        if (ignored != null)
            DataUtil.arrayAdd(ignored, ONLY_SERVER_PORP);
        else
            ignored = ONLY_SERVER_PORP;
        if (ignored != null && ignored.length > 0) {
            if (SERVER_SET == null)
                SERVER_SET = ArrayBuilders.arrayToSet(ignored);

            Iterator<BeanPropertyWriter> it = props.iterator();
            while (it.hasNext()) {
                if (SERVER_SET.contains(it.next().getName())) {
                    it.remove();
                }
            }
        }
        return props;
    }
}
