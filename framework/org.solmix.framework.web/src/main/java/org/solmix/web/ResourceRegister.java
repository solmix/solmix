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
package org.solmix.web;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.solmix.api.servlet.ServletManager;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-6-4
 */

public class ResourceRegister
{
    
    private Map<String,String> resources;
    private String resource;
    private volatile BundleContext managedContext;

    /**
     * The service registration of this service as servlet
     * 
     * @see #dispose()
     */

    private final  List<ServiceRegistration<Servlet>> registrations=new ArrayList<ServiceRegistration<Servlet>>();
    /**
     * @return the resource
     */
    public String getResource() {
        return resource;
    }


    
    /**
     * @param resource the resource to set
     */
    public void setResource(String resource) {
        this.resource = resource;
    }


    public void register(){
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put(Constants.SERVICE_PID, getClass().getName());
        props.put(Constants.SERVICE_DESCRIPTION, "Solmix ServletContext");
        if(resource!=null){
            props.put(ServletManager.SERVLET_NAME, resource);
            props.put(ServletManager.SERVLET_PATTERN, resource);
            Servlet servlet =  createResourceServlet(null,resource);
            ServiceRegistration<Servlet>  registration = managedContext.registerService(Servlet.class, servlet, props);
            registrations.add(registration);
        }
        if(resources!=null&&resources.size()>0){
            for(String alies:resources.keySet()){
                String name=resources.get(alies);
                props.put(ServletManager.SERVLET_NAME, alies);
                props.put(ServletManager.SERVLET_PATTERN, alies);
                Servlet servlet =  createResourceServlet(alies,name);
                ServiceRegistration<Servlet> registration = managedContext.registerService(Servlet.class, servlet, props); 
                registrations.add(registration);
            }
        }
    }
    protected Servlet createResourceServlet(String alies,String name){
        StaticResourceServlet servlet = new StaticResourceServlet(alies,name,managedContext);
        return servlet;
    }

    
    public void unregister(){
        if(registrations!=null&&registrations.size()>0){
            for( ServiceRegistration<Servlet> registration :registrations){
                registration.unregister();
            }
        }
        
        
    }


    
    /**
     * @return the resources
     */
    public Map<String, String> getResources() {
        return resources;
    }


    
    /**
     * @param resources the resources to set
     */
    public void setResources(Map<String, String> resources) {
        this.resources = resources;
    }



    
    /**
     * @return the managedContext
     */
    public BundleContext getManagedContext() {
        return managedContext;
    }



    
    /**
     * @param managedContext the managedContext to set
     */
    public void setManagedContext(BundleContext managedContext) {
        this.managedContext = managedContext;
    }
    
}
