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

package org.solmix.runtime;

import org.solmix.commons.util.ObjectUtils;
import org.solmix.commons.util.Reflection;
import org.solmix.runtime.extension.AssembleBeanSupport;
import org.solmix.runtime.extension.ExtensionLoader;

/**
 * Factory and utility methods for {@link Container}
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年8月9日
 */

public class Containers
{

    /**
     * 获取与当前线程关联的Container
     * 
     * @return
     */
    public static Container getThreadDefaultContainer() {
        return ContainerFactory.getThreadDefaultContainer();
    }

    /**
     *  获取与当前线程关联的Container
     *  
     * @param createIfNeeded 为true,当默认不存在时创建.
     * @return
     */
    public static Container getThreadDefaultContainer(boolean createIfNeeded) {
        return ContainerFactory.getThreadDefaultContainer(createIfNeeded);
    }

    /**
     * @return
     */
    public static Container getDefaultContainer() {
        return ContainerFactory.getDefaultContainer();
    }

    /**
     * @param createIfNeeded
     * @return
     */
    public static Container getDefaultContainer(boolean createIfNeeded) {
        return ContainerFactory.getDefaultContainer(createIfNeeded);
    }
    
    /**
     * 使用当前线程关联的Container加载扩展.
     * 
     * @param type
     * @return
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type){
        return getThreadDefaultContainer().getExtensionLoader(type);
    }

    /**
     * Set the Container related current thread.
     * @param object
     */
    public static void setThreadDefaultContainer(Container container) {
        ContainerFactory.setThreadDefaultContainer(container);
    }
    
    /**
     * 返回指定Container中指定类型的实例,如果不存在则自动创建实例
     * @param container
     * @param clz
     * @return
     */
    public static <T> T createExtensionIfNoProvided(Container container,Class<T> clz){
        if(container==null){
            return null;
        }
        T instance = container.getExtension(clz);
        if(instance==null){
            AssembleBeanSupport assemble  = container.getExtension(AssembleBeanSupport.class);
            if(assemble!=null){
                instance= assemble.assemble(clz, true, ObjectUtils.EMPTY_OBJECT_ARRAY);
            }
            instance=container.getExtension(clz);
        }
        return instance;
    }
    /**根据当前Container注入实例*/
    public static void injectResource(Container container,Object o){
        if(container==null){
            return ;
        }
        AssembleBeanSupport assemble  = container.getExtension(AssembleBeanSupport.class);
        if(assemble!=null){
           assemble.assemble(o);
        }
    }
    /**
     * 创建实例,并注入Resource
     * @param container
     * @param clz
     * @return
     */
    public static  <T> T  injectResource(Container container,Class<T> clz){
        return injectResource(container, clz,ObjectUtils.EMPTY_OBJECT_ARRAY);
    }
    /**
     * 创建实例,并注入Resource
     * @param container
     * @param clz
     * @return
     */
    public static  <T> T  injectResource(Container container,Class<T> clz,Object... args){
        if(container==null){
            return null;
        }
        AssembleBeanSupport assemble  = container.getExtension(AssembleBeanSupport.class);
        if(assemble!=null){
            return assemble.assemble(clz, false, args);
        }else{
            try {
                return Reflection.newInstance(clz);
            } catch (Exception e) {
              throw new IllegalStateException(e);
            }
        }
    }
}
