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

package org.solmix.runtime.helper;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年8月6日
 */
@Resource
public class ClasspathScannerTest
{

    @Test
    public void test() throws ClassNotFoundException, IOException {
        List<String> packages = new ArrayList<String>();
        packages.add("org.solmix.runtime.helper");
            @SuppressWarnings("unchecked")
            Map<Class<? extends Annotation>, Collection<Class<?>>> findClasses 
            = ClasspathScanner.findClasses(packages, Resource.class);
            Assert.assertNotNull(findClasses);
            Collection<Class<?>> clzs= findClasses.get(Resource.class);
            boolean found=false;
            for(Class<?> clz:clzs){
                if(clz==this.getClass()){
                    found=true;
                }
            }
            Assert.assertTrue(found);
    }

}
