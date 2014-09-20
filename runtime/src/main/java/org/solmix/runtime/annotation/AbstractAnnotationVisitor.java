/*
 * Copyright 2013 The Solmix Project
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
package org.solmix.runtime.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月27日
 */

public abstract class AbstractAnnotationVisitor implements AnnotationVisitor
{
    protected Object target;
    protected Class<?> targetClass;
    

    private final List<Class<? extends Annotation>> targetAnnotations = 
                                 new ArrayList<Class<? extends Annotation>>(); 
    
    
    protected AbstractAnnotationVisitor(Class<? extends Annotation> ann) {
        addTargetAnnotation(ann);
    }
    
    protected AbstractAnnotationVisitor(List<Class<? extends Annotation>> ann) {
        targetAnnotations.addAll(ann);
    }

    protected final void addTargetAnnotation(Class<? extends Annotation> ann) { 
        targetAnnotations.add(ann); 
    } 

    @Override
    public void visitClass(Class<?> clz, Annotation annotation) {
        // complete
    }

    @Override
    public List<Class<? extends Annotation>> getTargetAnnotations() {
        return targetAnnotations;
    }

    @Override
    public void visitField(Field field, Annotation annotation) {
        // complete
    }

    @Override
    public void visitMethod(Method method, Annotation annotation) {
        // complete
    }

    @Override
    public void setTarget(Object object) {
        target = object;
        targetClass = object.getClass();
    }
    public void setTarget(Object object, Class<?> cls) {
        target = object;
        targetClass = cls;
    }
    
    public Object getTarget() { 
        return target;
    } 
    public Class<?> getTargetClass() { 
        return targetClass;
    } 
}
