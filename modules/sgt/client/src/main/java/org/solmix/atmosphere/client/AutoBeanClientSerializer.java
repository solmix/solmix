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
package org.solmix.atmosphere.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-9-27
 */

public class AutoBeanClientSerializer implements ClientSerializer {
    
    private static final Logger logger = Logger.getLogger(AutoBeanClientSerializer.class.getName());
     
     private Map<Class, AutoBeanFactory> beanFactories;
     private AutoBeanFactory activeBeanFactory;
     private Class<Object> activeBeanClass;
     // buffer in order to capture split messages
     private final StringBuffer buffer = new StringBuffer(16100);
    
     public void registerBeanFactory(Class<AutoBeanFactory> factoryClass, Class forBean) {
         registerBeanFactory((AutoBeanFactory)GWT.create(factoryClass), forBean);
     }
     
     public void registerBeanFactory(AutoBeanFactory factory, Class forBean) {
         if (beanFactories == null) {
             beanFactories = new HashMap<Class, AutoBeanFactory>();
         }
         beanFactories.put(forBean, factory);
         if (activeBeanFactory == null) {
             setActiveBeanFactory(forBean);
         }
     }
     
     public void setActiveBeanFactory(Class forBean) {
         if (beanFactories == null) {
             throw new IllegalStateException("No bean factory available");
         }
         AutoBeanFactory factory = beanFactories.get(forBean);
         if (factory == null) {
             throw new IllegalStateException("No bean factory available");
         }
         activeBeanFactory = factory;
         activeBeanClass = forBean;
     }
     
     @Override
     public Object deserialize(String raw) throws SerializationException {
        
        buffer.append(raw); // TODO buffer messages in case we receive a chunked message
        
       // split up in different parts - based on the {}
       // this is necessary because multiple objects can be chunked in one single string
       int brackets = 0;
       int start = 0;
       List<String> messages = new ArrayList<String>();
       for (int i = 0; i < buffer.length(); i++) {

           // detect brackets
           if (buffer.charAt(i) == '{') {
              ++brackets;
           }
           else if (buffer.charAt(i) == '}') --brackets;

           // new message
           if (brackets == 0) {
               messages.add(buffer.substring(start, i + 1));
               start = i + 1;
           }
       }
       buffer.delete(0, start);

       // create the objects
       List<Object> objects = new ArrayList<Object>(messages.size());
       for (String message : messages) {
         try {

            logger.info("Deserialize " + message + " from " + raw);
             Object event = AutoBeanCodex.decode(activeBeanFactory, activeBeanClass, message).as();

             objects.add(event);

         } catch (RuntimeException e) {

             throw new SerializationException(e);

         }
       }
       return objects;
     }
     
     @Override
     public String serialize(Object message) throws SerializationException {
         try {

             AutoBean<Object> bean = AutoBeanUtils.getAutoBean(message);

             return AutoBeanCodex.encode(bean).getPayload();

         } catch (RuntimeException e) {

             throw new SerializationException(e);

         }
     }


 }