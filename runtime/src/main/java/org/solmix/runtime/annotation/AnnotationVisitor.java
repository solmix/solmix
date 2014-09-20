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


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年7月27日
 */

public interface AnnotationVisitor
{
    /** set the target object being visited.  Invoked before any of
     * the visit methods.   
     *
     * @see AnnotationProcessor
     *
     * @param target the target object 
     */ 
    void setTarget(Object target);


    /** return the list of annotations this visitor wants to be
     * informed about.
     *
     * @return list of annotation types to be informed about
     *
     */
    List<Class<? extends Annotation>> getTargetAnnotations(); 
    
    /** visit an annotated class. Invoked when the class of an object
     * is annotated by one of the specified annotations.
     * <code>visitClass</code> is called for each of the annotations
     * that matches and for each class. 
     *
     * @param clz the class with the annotation
     * @param annotation the annotation 
     *
     */
    void visitClass(Class<?> clz, Annotation annotation); 

    
    /** visit an annotated field. Invoked when the field of an object
     * is annotated by one of the specified annotations.
     * <code>visitField</code> is called for each of the annotations
     * that matches and for each field. 
     *
     * @param field the annotated field
     * @param annotation the annotation 
     *
     */
    void visitField(Field field, Annotation annotation); 

    /** visit an annotated method. Invoked when the method of an object
     * is annotated by one of the specified annotations.
     * <code>visitMethod</code> is called for each of the annotations
     * that matches and for each method. 
     *
     * @param method the annotated fieldx
     * @param annotation the annotation 
     *
     */
    void visitMethod(Method method, Annotation annotation); 
}
