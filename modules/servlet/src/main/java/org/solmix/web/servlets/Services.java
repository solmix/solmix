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
package org.solmix.web.servlets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.solmix.commons.osgi.OSGIService;
import org.solmix.commons.util.DataUtil;
import org.solmix.osgi.proxy.Proxy;


/**
 * 
 * @author administrator
 * @version 110035  2011-11-15
 */

public class Services
{
    private final static Logger log = LoggerFactory.getLogger(OSGIService.class.getName());
    /**
     * @param interfaceName
     * @param filter
     * @return
     */
    public static Object[] getServices(String interfaceName, String filter)
    {
        BundleContext context = Proxy.getDefault().context;
           Object[] res  ;
           try {
               if(DataUtil.isNotNullAndEmpty(filter)){
               ServiceReference<?>[] ref=  context.getServiceReferences(interfaceName, filter);
               if(ref!=null){
                    res = new Object[ref.length];
                   for(int i=0;i<ref.length;i++){
                       res[i]=context.getService(ref[i]);
                   }
                   return res;
               }
               }else{
                   
                   ServiceReference<?> ref=  context.getServiceReference(interfaceName);
                   if(ref!=null){
                       res = new Object[0];
                       res[0]= context.getService(ref);
                       return res;
                   }
               }
         } catch (InvalidSyntaxException e) {
             e.printStackTrace();
         }
         return null;
    }
    public static  Object getService(String serviceName) {
        BundleContext context = Proxy.getDefault().context;
        if (log.isTraceEnabled())
           log.trace("OSGIService:getService(): service["+ serviceName+"]");
        ServiceReference<?> ref= context.getServiceReference(serviceName);
       return ref==null?null:context.getService(ref);
    }
    public static <S> S getService(Class<S> clz){
        BundleContext context = Proxy.getDefault().context;
        if (log.isTraceEnabled())
           log.trace("OSGIService:getService(): service["+ clz.getName()+"]");
        ServiceReference<S> ref= context.getServiceReference(clz);
       return ref==null?null:context.getService(ref);
    }
    public static  <S> Collection<S> getServices(Class<S> clz, String filter)
    {
        BundleContext context = Proxy.getDefault().context;
        Collection<S> res  ;
           try {
               if(DataUtil.isNotNullAndEmpty(filter)){
               Collection<ServiceReference<S>> ref=  context.getServiceReferences(clz, filter);
               if(ref!=null){
                    res = new ArrayList<S>(ref.size());
                    Iterator<ServiceReference<S>> it =ref.iterator();
                    while(it!=null&&it.hasNext()){
                        ServiceReference<S> s = it.next();
                       res.add(context.getService(s));
                    }
                   return res;
               }
               }else{
                   
                   ServiceReference<S> ref=  context.getServiceReference(clz);
                   if(ref!=null){
                       res = new ArrayList<S>();
                       res.add( context.getService(ref));
                       return res;
                   }
               }
         } catch (InvalidSyntaxException e) {
             e.printStackTrace();
         }
         return null;
    }
}
