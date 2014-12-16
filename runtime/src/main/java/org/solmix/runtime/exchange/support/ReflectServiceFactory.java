/**
 * Copyright (c) 2014 The Solmix Project
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

package org.solmix.runtime.exchange.support;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.exchange.Endpoint;
import org.solmix.runtime.exchange.ProtocolFactoryManager;
import org.solmix.runtime.exchange.Service;
import org.solmix.runtime.exchange.event.ServiceFactoryEvent;
import org.solmix.runtime.exchange.invoker.FactoryInvoker;
import org.solmix.runtime.exchange.invoker.Invoker;
import org.solmix.runtime.exchange.invoker.SingletonFactory;
import org.solmix.runtime.exchange.model.EndpointInfo;
import org.solmix.runtime.exchange.model.NamedID;
import org.solmix.runtime.exchange.model.NamedIDPolicy;
import org.solmix.runtime.exchange.model.ServiceInfo;
import org.solmix.runtime.exchange.serialize.Serialization;
import org.solmix.runtime.interceptor.phase.PhasePolicy;
import org.solmix.runtime.interceptor.support.FaultOutInterceptor;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月24日
 */

public abstract class ReflectServiceFactory extends AbstractServiceFactory {

    public static final String METHOD_PARAM_ANNOTATIONS = "method.parameters.annotations";

    public static final String METHOD_ANNOTATIONS = "method.return.annotations";

    public static final String PARAM_ANNOTATION = "parameter.annotations";

    public static final String METHOD = "operation.method";

    public static final String ENDPOINT_CLASS = "endpoint.class";

    public static final String GENERIC_TYPE = "generic.type";

    public static final String RAW_CLASS = "rawclass";

    private static final Logger LOG = LoggerFactory.getLogger(ReflectServiceFactory.class);

    protected NamedID serviceName;

    protected NamedID endpointName;

    protected Class<?> serviceClass;
    
    protected Map<Type, Map<String, Class<?>>> parameterizedTypes;

    protected ParameterizedType serviceType;
    
    protected NamedIDPolicy namedIDPolicy;

    private Map<String, Object> properties;

    private Invoker invoker;

    private final List<String> ignoredClasses = new ArrayList<String>();

    private final List<Method> ignoredMethods = new ArrayList<Method>();

    private Executor executor;
    
    public ReflectServiceFactory() {
        NamedIDPolicy np = new NamedIDPolicy();
        np.setServiceFactory(this);
        setNamedIDPolicy(np);
    }

    @Override
    public Service create() {
        // 重置service,重新创建.
        resetFactory();
        pulishEvent(ServiceFactoryEvent.START_CREATE);
        // 构建service
        buildService();
        // 初始化拦截器
        initDefaultInterceptor();

        if (invoker != null) {
            getService().setInvoker(invoker);
        } else {
            getService().setInvoker(createInvoker());
        }

        if (getExecutor() != null) {
            getService().setExecutor(getExecutor());
        }
        
        if (getSerialization() != null) {
            getService().setSerialization(getSerialization());
        }

        //TODO method dispacher
        createEndpoints();
        Service serv = getService();
        pulishEvent(ServiceFactoryEvent.SERVER_CREATED_END, serv);
        return serv;
    }

    protected void createEndpoints() {
        Service service = getService();
        ProtocolFactoryManager pfm = getContainer().getExtension(
            ProtocolFactoryManager.class);
        for (ServiceInfo inf : service.getServiceInfos()) {
            for (EndpointInfo ei : inf.getEndpoints()) {

                try {
                    pfm.getProtocolFactory(ei.getProtocol().getProtocolId());
                } catch (Exception e1) {
                    continue;
                }
                Endpoint ep = createEndpoint(ei);

                service.getEndpoints().put(ei.getName(), ep);
            }
        }
    }

    protected Invoker createInvoker() {
        Class<?> cls = getServiceClass();
        if (cls.isInterface()) {
            return null;
        }
        return new FactoryInvoker(new SingletonFactory(getServiceClass()));
    }

    @Override
    protected void initDefaultInterceptor() {
        super.initDefaultInterceptor();
        initializeFaultInterceptors();
    }

    protected void initializeFaultInterceptors() {
        getService().getOutFaultInterceptors().add(new FaultOutInterceptor());
    }

    protected abstract void buildService();

    @Override
    protected Serialization defaultSerialization() {
        // 通过service class 注解
        // 通过container参数加载
        return null;
    }

    public void resetFactory() {
        if (!serializeSetted) {
            setSerialization(null);
        }
        setService(null);
    }

    /**   */
    public Class<?> getServiceClass() {
        return serviceClass;
    }

    /**   */
    public void setServiceClass(Class<?> serviceClass) {
        this.serviceClass = serviceClass;
    }

    /**   */
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**   */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**   */
    public Invoker getInvoker() {
        return invoker;
    }

    /**   */
    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    /**
     * @param info
     * @return
     */
    public Endpoint createEndpoint(EndpointInfo info) {
        Endpoint ep = new DefaultEndpoint(getContainer(), getService(), info, getPhasePolicy());
        pulishEvent(ServiceFactoryEvent.ENDPOINT_CREATED, info, ep,
            getServiceClass());
        return ep;
    }

    /**
     * @return
     */
    protected abstract PhasePolicy getPhasePolicy();

    /**   */
    public void setServiceName(NamedID serviceName) {
        this.serviceName = serviceName;
    }

    /**   */
    public NamedID getEndpointName() {
        return endpointName;
    }

    /**   */
    public void setEndpointName(NamedID endpointName) {
        this.endpointName = endpointName;
    }

    /**   */
    public List<String> getIgnoredClasses() {
        return ignoredClasses;
    }

    /**   */
    public List<Method> getIgnoredMethods() {
        return ignoredMethods;
    }

    
    /**   */
    public Executor getExecutor() {
        return executor;
    }
    
    /**   */
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    /**
     * @return
     */
    public String getServiceNamespace() {
        if (serviceName != null) {
            return serviceName.getServiceNamespace();
        }
        if (namedIDPolicy != null) {
            return namedIDPolicy.getServiceNamespace();
        }
        throw new IllegalStateException("Service namespace must be not null!");
    }

    /**   */
    public NamedID getServiceName() {
        return getServiceName(true);
    }

    public NamedID getServiceName(boolean lookup) {
        if (serviceName == null && lookup) {
            serviceName = new NamedID(getServiceNamespace(), getServiceName0());
        }
        return serviceName;
    }

    protected String getServiceName0() {
        return namedIDPolicy.getServiceName();
    }

    protected NamedID getInterfaceName() {
        return namedIDPolicy.getInterfaceName();
    }

    /**   */
    public NamedIDPolicy getNamedIDPolicy() {
        return namedIDPolicy;
    }

    /**   */
    public void setNamedIDPolicy(NamedIDPolicy namedIDPolicy) {
        this.namedIDPolicy = namedIDPolicy;
    }
    protected void processParameterizedTypes() {
        parameterizedTypes = new HashMap<Type, Map<String, Class<?>>>();
        if (serviceClass.isInterface()) {
            processTypes(serviceClass, serviceType);
        } else {
            final Class<?>[] interfaces = serviceClass.getInterfaces();
            final Type[] genericInterfaces = serviceClass.getGenericInterfaces();
            for (int x = 0; x < interfaces.length; x++) {
                processTypes(interfaces[x], genericInterfaces[x]);
            }
            processTypes(serviceClass.getSuperclass(),
                serviceClass.getGenericSuperclass());
        }
    }

    protected void processTypes(Class<?> sc, Type tp) {
        if (tp instanceof ParameterizedType) {
            ParameterizedType ptp = (ParameterizedType) tp;
            Type c = ptp.getRawType();
            Map<String, Class<?>> m = new HashMap<String, Class<?>>();
            parameterizedTypes.put(c, m);
            final Type[] ptpActualTypeArgs = ptp.getActualTypeArguments();
            final TypeVariable<?>[] scTypeArgs = sc.getTypeParameters();
            for (int x = 0; x < ptpActualTypeArgs.length; x++) {
                Type t = ptpActualTypeArgs[x];
                TypeVariable<?> tv = scTypeArgs[x];
                if (t instanceof Class) {
                    m.put(tv.getName(), (Class<?>) t);
                }
            }
        }
    }
}
