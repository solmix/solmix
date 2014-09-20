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
import java.util.List;

import org.solmix.commons.util.Reflection;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月27日
 */

public class AnnotationProcessor
{
    private final Object target; 
    private List<Class<? extends Annotation>> annotationTypes; 
    
    
    public AnnotationProcessor(Object o) {
        if (o == null) {
            throw new IllegalArgumentException(); 
        }
        target = o; 
    }
    
    /** 
     * Visits each of the annotated elements of the object.
     * 
     * @param visitor a visitor 
     * @param claz the Class of the targe object
     */
    public void accept(AnnotationVisitor visitor, Class<?> claz) { 
        
        if (visitor == null) {
            throw new IllegalArgumentException();
        }
        
        annotationTypes = visitor.getTargetAnnotations();
        visitor.setTarget(target);
        //recursively check annotation in super class
        processClass(visitor, claz);
        processFields(visitor, claz); 
        processMethods(visitor, claz);
    } 
    
    public void accept(AnnotationVisitor visitor) {
        accept(visitor, target.getClass());
    }
    
    
    private void processMethods(AnnotationVisitor visitor, Class<? extends Object> targetClass) {

        if (targetClass.getSuperclass() != null) {
            processMethods(visitor, targetClass.getSuperclass());
        }
        for (Method element : Reflection.getDeclaredMethods(targetClass)) {
            for (Class<? extends Annotation> clz : annotationTypes) {
                Annotation ann = element.getAnnotation(clz); 
                if (ann != null) {
                    visitor.visitMethod(element, ann);
                }
            }
        }
    }
    
    private void processFields(AnnotationVisitor visitor, Class<? extends Object> targetClass) { 
        if (targetClass.getSuperclass() != null) {
            processFields(visitor, targetClass.getSuperclass());
        }
        for (Field element : Reflection.getDeclaredFields(targetClass)) {
            for (Class<? extends Annotation> clz : annotationTypes) {
                Annotation ann = element.getAnnotation(clz); 
                if (ann != null) {
                    visitor.visitField(element, ann);
                }
            }
        }
    } 
    
    
    private void processClass(AnnotationVisitor visitor, Class<? extends Object> targetClass) {
        if (targetClass.getSuperclass() != null) {
            processClass(visitor, targetClass.getSuperclass());
        }
        for (Class<? extends Annotation> clz : annotationTypes) {
            Annotation ann = targetClass.getAnnotation(clz); 
            if (ann != null) {
                visitor.visitClass(targetClass, ann);
            }
        }
    }    
}
