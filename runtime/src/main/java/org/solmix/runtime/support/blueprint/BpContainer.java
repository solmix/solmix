/**
 *  Copyright 2012 The Solmix Project
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

package org.solmix.runtime.support.blueprint;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Set;

import org.apache.aries.blueprint.mutable.MutableBeanMetadata;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.solmix.runtime.Container;
import org.solmix.runtime.bean.BeanConfigurer;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.cm.ConfigureUnitManager;
import org.solmix.runtime.cm.support.OsgiConfigureUnitManager;
import org.solmix.runtime.resource.ResourceInjector;
import org.solmix.runtime.resource.ResourceManager;
import org.solmix.runtime.resource.support.PathMatchingResourceResolver;
import org.solmix.runtime.support.ext.ContainerAdaptor;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-5
 */

public class BpContainer extends ContainerAdaptor
{

    BundleContext bundleContext;

    BlueprintContainer blueprintContainer;

    /**
     * @param bundleContext the bundleContext to set
     */
    public void setBundleContext(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        ClassLoader bundleClassLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {

            @Override
            public ClassLoader run() {
                return new BundleDelegatingClassLoader(bundleContext.getBundle(), this.getClass().getClassLoader());
            }
        });
        super.setExtension(bundleClassLoader, ClassLoader.class);
        super.setExtension(bundleContext, BundleContext.class);
        super.setExtension(new OsgiConfigureUnitManager(bundleContext), ConfigureUnitManager.class);
     }
    @Override
    protected void customResourceManager(ResourceManager rm) {
    }
    /**
     * @param blueprintContainer the blueprintContainer to set
     */
    public void setBlueprintContainer(BlueprintContainer blueprintContainer) {
        this.blueprintContainer = blueprintContainer;
    }
    
    @Override
    protected void doInitializeInternal() {
        super.doInitializeInternal();
        processBeanInBp(true);
    }
    
    private void processBeanInBp(boolean start){
    	ResourceManager rm= getExtension(ResourceManager.class);
    	if(start){
    		rm.addResourceResolver(new PathMatchingResourceResolver(new BundleDelegatingClassLoader(bundleContext.getBundle(), BpContainer.class.getClassLoader())));
            setExtension(new BlueprintConfigurer(blueprintContainer), BeanConfigurer.class);
            setExtension(new BlueprintBeanProvider(getExtension(ConfiguredBeanProvider.class), blueprintContainer, bundleContext),
                ConfiguredBeanProvider.class);
    	}
    	Set<String> ids =blueprintContainer.getComponentIds();
        ResourceInjector injector = new ResourceInjector(rm);
        if(ids!=null){
            for(String id:ids){
                ComponentMetadata meta=  blueprintContainer.getComponentMetadata(id);
                if(meta instanceof BeanMetadata){
                    BeanMetadata bean = (BeanMetadata)meta;
                    
                    //如果factory或者初始化的bean需要依赖container，会循环依赖
                    List<String> depids=bean.getDependsOn();
                    if(depids!=null&&depids.size()>0){
                        boolean dependencyContainer=false;
                        for(String depid:depids){
                            ComponentMetadata dmeta=  blueprintContainer.getComponentMetadata(depid);
                            if(dmeta instanceof MutableBeanMetadata){
                                MutableBeanMetadata dbean = (MutableBeanMetadata)dmeta;
                               if(dbean.getRuntimeClass()==BpContainer.class){
                                   dependencyContainer=true;
                                   break;
                               }
                            }
                        }
                        if(dependencyContainer){
                            continue;
                        }
                    }
                    Object instance =  blueprintContainer.getComponentInstance(id);
                    if(instance instanceof Container){
                        continue;
                    }
                    if(start){
                    	injector.injectAware(instance);
                        if(injectable(instance,id)){
                            injector.inject(instance);
                            injector.construct(instance);
                        }
                    }else{
                    	injector.destroy(instance);
                    }
                }
            }
        }
    }
    @Override
    public void destroyBeans() {
        //支持在bp中的注解配置
    	processBeanInBp(false);
        super.destroyBeans();
    }
    
    
    private boolean injectable(Object bean,String beanId){
        return !"solmix".equals(beanId) && ResourceInjector.processable(bean.getClass(), bean);
    }

    @Override
    public String getId() {
        if (id == null) {
            id = bundleContext.getBundle().getSymbolicName() + "-" + DEFAULT_CONTAINER_ID + "-" + Integer.toString(this.hashCode());
        }
        return id;
    }
}
