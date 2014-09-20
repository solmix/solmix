/*
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

package org.solmix.runtime.support.spring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.solmix.runtime.Container;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-4
 */

public class ContainerPostProcessor implements BeanFactoryPostProcessor
{

    private Container container;

     String name;

    public ContainerPostProcessor()
    {

    }

    public ContainerPostProcessor(Container container)
    {
        this.container = container;
    }

    public ContainerPostProcessor(String name)
    {
        this.name = name;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Object inject = container;
        if(inject==null){
            inject = getContainerByName(Container.DEFAULT_CONTAINER_ID, beanFactory, true, null);
        }else{
            if (!beanFactory.containsBeanDefinition(Container.DEFAULT_CONTAINER_ID)
                && !beanFactory.containsSingleton(Container.DEFAULT_CONTAINER_ID)) {
                beanFactory.registerSingleton(Container.DEFAULT_CONTAINER_ID, container);
            }
        }
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
          Object p=  beanDefinition.getAttribute(AbstractBeanDefinitionParser.WIRE_CONTAINER_ATTRIBUTE);
           if(p==null)
               continue;
           String name = (String)beanDefinition.getAttribute(AbstractBeanDefinitionParser.WIRE_CONTAINER_NAME);
           String create = (String)beanDefinition
               .getAttribute(AbstractBeanDefinitionParser.WIRE_CONTAINER_CREATE);
           Object inj = inject;
           if (name != null) {
               if (container != null) {
                   continue;
               }
               inj = getContainerByName(name, beanFactory, create != null, create);
           }
           beanDefinition.removeAttribute(AbstractBeanDefinitionParser.WIRE_CONTAINER_NAME);
           beanDefinition.removeAttribute(AbstractBeanDefinitionParser.WIRE_CONTAINER_ATTRIBUTE);
           beanDefinition.removeAttribute(AbstractBeanDefinitionParser.WIRE_CONTAINER_CREATE);
           if (create == null) {
               if (Boolean.valueOf(p.toString())) {
                   beanDefinition.getPropertyValues()
                       .addPropertyValue("container", inj);
               } else  {
                   ConstructorArgumentValues constructorArgs = beanDefinition.getConstructorArgumentValues();
                   insertConstructorArg(constructorArgs, inj);
               }
           }
        }//end loop bean.

    }

    private void insertConstructorArg(
        ConstructorArgumentValues constructorArgs, Object valueToInsert) {
        List<ValueHolder> genericArgs = new ArrayList<ValueHolder>(
            (constructorArgs.getGenericArgumentValues()));
        Map<Integer, ValueHolder> indexedArgs = new HashMap<Integer, ValueHolder>(
            constructorArgs.getIndexedArgumentValues());

        constructorArgs.clear();
        for (ValueHolder genericValue : genericArgs) {
            constructorArgs.addGenericArgumentValue(genericValue);
        }
        for (Map.Entry<Integer, ValueHolder> entry : indexedArgs.entrySet()) {
            constructorArgs.addIndexedArgumentValue(entry.getKey() + 1,
                entry.getValue());
        }
        constructorArgs.addIndexedArgumentValue(0, valueToInsert);
    }
    
    private Object getContainerByName(String name, ConfigurableListableBeanFactory factory, boolean create, String cn) {
        if (!factory.containsBeanDefinition(name) && (create || Container.DEFAULT_CONTAINER_ID.equals(name))) {
            DefaultListableBeanFactory df = (DefaultListableBeanFactory)factory;
            RootBeanDefinition rbd = new RootBeanDefinition(SpringContainer.class);
            if (cn != null) {
                rbd.setAttribute("config", new RuntimeBeanReference(cn));
            }
            df.registerBeanDefinition(name, rbd);
        } else if (cn != null) {
            BeanDefinition bd = factory.getBeanDefinition(name);
            bd.getPropertyValues().addPropertyValue("config", new RuntimeBeanReference(cn));
        }
        return new RuntimeBeanReference(name);
    }

}
