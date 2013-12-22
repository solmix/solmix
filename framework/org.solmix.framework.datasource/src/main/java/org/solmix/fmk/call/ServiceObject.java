/*
 * ========THE SOLMIX PROJECT=====================================
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

package org.solmix.fmk.call;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.jxpath.JXPathContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.context.Context;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.EserviceStyle;
import org.solmix.api.jaxb.Tservice;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.base.Reflection;
import org.solmix.fmk.base.ReflectionArgument;
import org.solmix.fmk.util.ServiceUtil;

/**
 * 
 * @author Administrator
 * @version 110035 2011-4-8
 */

public class ServiceObject
{

    private static Logger log = LoggerFactory.getLogger(ServiceObject.class.getName());

    Tservice serverObjectConfig;

    Context context;

    String contextString;

    ReflectionArgument factoryOptionalArgs[];

    EserviceStyle lookupStyle;

    Class<?> serverObjectClass;

    String serverObjectClassName;

    String serverObjectInterface;

    String serverObjectFilter;

    Object serverObjectInstance;

    Method method;

    public ServiceObject(Tservice serverObjectConfig, Context context, String contextString) throws SlxException
    {
        this(serverObjectConfig, context, null, contextString);
    }

    public ServiceObject(Tservice serverObjectConfig, String contextString) throws SlxException
    {
        this(serverObjectConfig, null, null, contextString);
    }

    public ServiceObject(Tservice srvConfig, Context context, ReflectionArgument factoryOptionalArgs[], String contextString) throws SlxException
    {
        this.serverObjectConfig = srvConfig;
        this.context = context;
        this.factoryOptionalArgs = factoryOptionalArgs;
        this.contextString = contextString;
        lookupStyle = srvConfig.getLookupStyle();
        // if ( lookupStyle == null )
        // lookupStyle = lookupStyle;
        switch (lookupStyle) {
            case NEW: {
                serverObjectClassName = srvConfig.getClazz();
                if (serverObjectClassName == null)
                    throw new SlxException(Tmodule.DATASOURCE, Texception.V_CONDITION_DISSATISFY, (new StringBuilder()).append(
                        "No className specified in serverConfig with ").append("declared").append(" lookupStyle=\"new\" used by ").append(
                        contextString).toString());
                try {
                    serverObjectClass = Reflection.classForName(serverObjectClassName);
                    String xpath = serverObjectConfig.getTargetXPath();
                    if (xpath != null && !xpath.equals(""))
                        try {
                            JXPathContext jxpc = JXPathContext.newContext(serverObjectClass.newInstance());
                            serverObjectInstance = jxpc.getValue(xpath);
                            serverObjectClass = serverObjectInstance.getClass();
                        } catch (InstantiationException ie) {
                            throw new SlxException(Tmodule.DATASOURCE, Texception.V_CONDITION_DISSATISFY,
                                (new StringBuilder()).append("Could not create an instance of ").append(serverObjectClassName).append(
                                    " whilst attempting to process targetXPath ").append(xpath).append(" used by ").append(contextString).append(
                                    " - Exception string: ").append(ie.toString()).toString());
                        }
                } catch (Exception e) {
                    throw new SlxException(Tmodule.DATASOURCE, Texception.V_CONDITION_DISSATISFY, (new StringBuilder()).append(
                        "Failed to lookup class by name: ").append(serverObjectClassName).append(" specified in serverConfig used by ").append(
                        contextString).append(" -  Exception string: ").append(e.toString()).toString());
                }
            }
                break;
            case OSGI: {
                serverObjectInterface = srvConfig.getInterface();
                serverObjectFilter = srvConfig.getFilter();
                Object[] objects = ServiceUtil.getOSGIServices(serverObjectInterface, serverObjectFilter);
                if (DataUtil.isNotNullAndEmpty(objects))
                    serverObjectInstance = objects[0];
            }
                break;
            case BEAN: {
                serverObjectInterface = srvConfig.getInterface();
                serverObjectFilter = srvConfig.getFilter();
                serverObjectInstance = ServiceUtil.getOsgiJndiService(serverObjectInterface, serverObjectFilter);
            }
                break;
            default:
                break;
        }
        if (serverObjectClass == null)
            serverObjectClass = serverObjectInstance.getClass();
    }

    public Class<?> getServiceClass() throws Exception {
        return serverObjectClass;
    }

    public Object getServiceInstance() throws Exception {
        return getServiceInstance(null);
    }

    public Object getServiceInstance(Method method) throws Exception {
        if (serverObjectInstance == null && (method == null || !Modifier.isStatic(method.getModifiers())))
            serverObjectInstance = Reflection.newInstance(serverObjectClass);
        return serverObjectInstance;
    }

    public Method getServiceMethod(String methodName) throws Exception {
        if (method == null)
            method = findMethod(serverObjectClass, methodName);
        return method;
    }

    public static Method findMethod(Class<?> serverObjectClass, String methodName) throws Exception {
        Method methods[] = serverObjectClass.getMethods();
        List<Method> candidateMethods = new ArrayList<Method>();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (methodName.equals(method.getName()))
                candidateMethods.add(method);
        }

        if (candidateMethods.size() == 0) {
            log.debug((new StringBuilder()).append("Couldn't find a public method named: ").append(methodName).append(" on class: ").append(
                serverObjectClass.getName()).toString());
            return null;
        }
        if (candidateMethods.size() > 1)
            throw new Exception((new StringBuilder()).append("Class ").append(serverObjectClass.getName()).append(" defines multiple").append(
                " methods named: ").append(methodName).append(" - overloading is not supported - please disambiguate.").toString());
        else
            return candidateMethods.get(0);
    }

    public String getServiceAsString() {
        if (this.lookupStyle != null) {
            switch (lookupStyle) {
                case NEW:
                    return serverObjectClassName;
                case OSGI:
                    return serverObjectInterface + serverObjectFilter;
                case BEAN:
                    return serverObjectInterface;
                default:
                    break;
            }
        }
        return null;
    }
}
