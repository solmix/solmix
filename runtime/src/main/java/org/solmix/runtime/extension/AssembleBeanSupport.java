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
package org.solmix.runtime.extension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.solmix.runtime.Container;
import org.solmix.runtime.bean.BeanConfigurer;
import org.solmix.runtime.resource.ResourceInjector;
import org.solmix.runtime.resource.ResourceManager;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月9日
 */

public class AssembleBeanSupport
{

    private Container container;
    public AssembleBeanSupport(Container container){
        this.container=container;
    }
    
    public <T> T assemble(Class<T> assemblyType){
        return assemble(assemblyType,false);
    }
    
    public <T> T assemble(Class<T> assemblyType,boolean setExtension,Object... args){
       T obj = instance(assemblyType, args);
       if (obj == null) {
           return obj;
       }
       obj=assemble(obj);
      if(setExtension){
          container.setExtension(obj, assemblyType);
      }
        return obj;
        
    }
    
    /**
     * 组装对象<br>
     * <li>使用{@link BeanConfigurer}注入配置
     * <li>使用{@link ResourceInjector}注入资源
     * @param obj 需要组装的对象
     * @return 返回组装完成后的对象
     */
    public <T> T assemble(T obj){
       
        return assemble(null,obj);
    }
    
    public <T> T assemble(String name,T obj){
        BeanConfigurer configurer =container.getExtension(BeanConfigurer.class);
        if (null != configurer) {
            if(name!=null){
                configurer.configureBean(name,obj);
            }else{
                configurer.configureBean(obj);
            }
           
        }
        ResourceManager resourceManager= container.getExtension(ResourceManager.class);
        if(resourceManager!=null){
            ResourceInjector injector = new ResourceInjector(resourceManager);
            injector.injectAware(obj);
            injector.inject(obj);
            injector.construct(obj);
        }
        return obj;
    }
    
    private <T> T instance(Class<T> assemblyType, Object... args) {
        T obj = null;
        try {
            try {
                // if there is a container constructor, use it.
                if (container != null && (args==null||args.length==0)) {
                    Constructor<T> con = assemblyType.getConstructor(Container.class);
                    obj = con.newInstance(container);
                    return obj;
                } else if (container != null && args != null) {
                    Constructor<T> con;
                    boolean noc = false;
                    try {
                        con = assemblyType.getConstructor(Container.class, Object[].class);
                    } catch (Exception ex) {
                        con = assemblyType.getConstructor(Object[].class);
                        noc = true;
                    }
                    if (noc) {
                        obj = con.newInstance(args);
                    } else {
                        obj = con.newInstance(container, args);
                    }
                    return obj;
                } else if (args != null) {
                    Constructor<T> con = assemblyType.getConstructor(Object[].class);
                    obj = con.newInstance(args);
                    return obj;
                }
            } catch (InvocationTargetException ex) {
                throw new ExtensionException("Problem Creating Extension Class", ex.getCause());
            } catch (InstantiationException ex) {
                throw new ExtensionException("Problem Creating Extension Class", ex);
            } catch (SecurityException ex) {
                throw new ExtensionException("Problem Creating Extension Class", ex);
            } catch (NoSuchMethodException e) {
                // ignore
            }
            obj = assemblyType.getConstructor().newInstance();
        } catch (Exception e) {
            throw new ExtensionException("PROBLEM_FINDING_CONSTRUCTOR", e);
        }
        return obj;
    }
}
