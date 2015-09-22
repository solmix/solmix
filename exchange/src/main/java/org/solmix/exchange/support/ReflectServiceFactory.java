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

package org.solmix.exchange.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.exchange.Endpoint;
import org.solmix.exchange.EndpointException;
import org.solmix.exchange.Exchange;
import org.solmix.exchange.ProtocolFactoryManager;
import org.solmix.exchange.Service;
import org.solmix.exchange.ServiceCreateException;
import org.solmix.exchange.data.DataProcessor;
import org.solmix.exchange.event.ServiceFactoryEvent;
import org.solmix.exchange.interceptor.Fault;
import org.solmix.exchange.interceptor.phase.PhasePolicy;
import org.solmix.exchange.interceptor.support.FaultOutInterceptor;
import org.solmix.exchange.invoker.FactoryInvoker;
import org.solmix.exchange.invoker.Invoker;
import org.solmix.exchange.invoker.OperationDispatcher;
import org.solmix.exchange.invoker.SingletonFactory;
import org.solmix.exchange.model.ArgumentInfo;
import org.solmix.exchange.model.EndpointInfo;
import org.solmix.exchange.model.FaultInfo;
import org.solmix.exchange.model.InterfaceInfo;
import org.solmix.exchange.model.MessageInfo;
import org.solmix.exchange.model.NamedID;
import org.solmix.exchange.model.NamedIDPolicy;
import org.solmix.exchange.model.OperationInfo;
import org.solmix.exchange.model.ServiceInfo;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月24日
 */

public  class ReflectServiceFactory extends AbstractServiceFactory {

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

    private Dictionary<String, ?> properties;

    private Invoker invoker;

    private final List<String> ignoredClasses = new ArrayList<String>();

    private final List<Method> ignoredMethods = new ArrayList<Method>();

    private Executor executor;
    
    private PhasePolicy phasePolicy;
    
    private OperationDispatcher operationDispatcher;
    
    public ReflectServiceFactory(PhasePolicy phasePolicy) {
        this();
        setPhasePolicy(phasePolicy);
    }
    public ReflectServiceFactory() {
       
    }

    @Override
    public Service create() {
        // 重置service,重新创建.
        resetFactory();
        pulishEvent(ServiceFactoryEvent.START_CREATE);
        // 构建service
        buildServiceModel();
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
        
        if (getDataProcessor() != null) {
            getService().setDataProcessor(getDataProcessor());
        }

        getService().put(OperationDispatcher.class.getName(), getOperationDispatcher());
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
                try {
                    Endpoint ep = createEndpoint(ei);
                    service.getEndpoints().put(ei.getName(), ep);
                } catch (EndpointException e) {
                    throw new ServiceCreateException(e);
                }
            }
        }
    }

    @Override
    public Invoker createInvoker() {
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

    protected void buildServiceModel() {
        if(Proxy.isProxyClass(getServiceClass())){
            LOG.warn("Used proxy for service :{}"+getServiceClass());
        }
        pulishEvent(ServiceFactoryEvent.CREATE_FROM_CLASS, getServiceClass());
        
    }
    
    /**将OperationDispatcher和配置参数放入Service*/
    protected void setServiceProperties() {
       OperationDispatcher dispatcher = getOperationDispatcher();
       getService().put(OperationDispatcher.class.getName(), dispatcher);
       for (Class<?> c : dispatcher.getClass().getInterfaces()) {
           getService().put(c.getName(), dispatcher);
       }
    }
    
    /**创建InterfaceInfo*/
    protected InterfaceInfo createInterface(ServiceInfo serviceInfo) {
        NamedID interfaceName = getInterfaceName();
        InterfaceInfo  ii = new InterfaceInfo(serviceInfo, interfaceName);
        Method[] methods = getServiceClass().getMethods();
        //Arrays.sort(methods, new MethodComparator());
        for (Method m : methods) {
            if (isValidOperation(m)) {
                createOperation(serviceInfo, ii, m);
            }
        }
        pulishEvent(ServiceFactoryEvent.INTERFACE_CREATED,ii,getServiceClass());
        return ii;
    }
  
    
    /**创建每个Method的OperationInfo*/
    protected OperationInfo createOperation(ServiceInfo serviceInfo, InterfaceInfo intf, Method m) {
        OperationInfo op = intf.addOperation(getOperationName(intf, m));
        final Annotation[] annotations = m.getAnnotations();
        final Annotation[][] parAnnotations = m.getParameterAnnotations();
        op.setProperty(METHOD_ANNOTATIONS, annotations);
        op.setProperty(METHOD_PARAM_ANNOTATIONS, parAnnotations);
        //XXX 将properties中对method的配置加入这里
        createMessageInfos(intf, op, m);
        
        bindOperation(op, m);
        
        pulishEvent(ServiceFactoryEvent.OPERATIONINFO_BOUND,op,m);
        return op;
    }
    
    /**绑定operaitonInfo和method*/
    protected void bindOperation(OperationInfo operation, Method method) {
        getOperationDispatcher().bind(method, operation);
        
    }
    /**创建每个Method的参数为MessageInfo*/
    protected void createMessageInfos(InterfaceInfo intf, OperationInfo op, Method method) {
        final Class<?>[] paramClasses = method.getParameterTypes();
        // Setup the input message
        op.setProperty(METHOD, method);
        MessageInfo inMsg = op.createMessage(getInMessageName(op, method), MessageInfo.Type.INPUT);
        op.setInput(inMsg.getName().getName(), inMsg);
        final Annotation[][] parAnnotations = method.getParameterAnnotations();
        final Type[] genParTypes = method.getGenericParameterTypes();
        for (int j = 0; j < paramClasses.length; j++) {
            if (Exchange.class.equals(paramClasses[j])) {
                continue;
            }
            if(isInParam(method, j)){
               NamedID argumentId= getInArgumentName(op, method, j);
               ArgumentInfo arg= inMsg.addArgument(argumentId);
               
               initializeArgument(arg, paramClasses[j], genParTypes[j]);
               
               arg.setProperty(METHOD_PARAM_ANNOTATIONS, parAnnotations);
               arg.setProperty(PARAM_ANNOTATION, parAnnotations[j]);
               
               arg.setIndex(j);
            }
        }
        pulishEvent(ServiceFactoryEvent.OPERATIONINFO_IN_MESSAGE_SET,op,method,inMsg);
        //返回信息
        boolean hasOut = hasOutMessage(method);
        if (hasOut) {
            MessageInfo outMsg = op.createMessage(getOutMessageName(op, method), MessageInfo.Type.OUTPUT);
            op.setOutput(outMsg.getName().getName(), outMsg);
            final Class<?> returnType = method.getReturnType();
            if (!returnType.isAssignableFrom(void.class)) {
                final NamedID outId = getOutArgumentName(op, method,-1);
                ArgumentInfo arg= inMsg.addArgument(outId);
                initializeArgument(arg, method.getReturnType(), method.getGenericReturnType());
                final Annotation[] annotations = method.getAnnotations();
                arg.setProperty(METHOD_ANNOTATIONS, annotations);
                arg.setProperty(PARAM_ANNOTATION, annotations);
                
                arg.setIndex(0);
            }
            //isHolder
            //支持将输入参数作为输出 method(in,out)
            //XXX
            /*for (int j = 0; j < paramClasses.length; j++) {
                if (Exchange.class.equals(paramClasses[j])) {
                    continue;
                }
            }*/
            pulishEvent(ServiceFactoryEvent.OPERATIONINFO_IN_MESSAGE_SET,op,method,outMsg);
        }
        if(hasOut){
            initializeFaults(intf, op, method);
        }
    }

    /**处理抛出的Exception*/
    protected void initializeFaults(final InterfaceInfo service, final OperationInfo op, final Method method) {
        // Set up the fault messages
        final Class<?>[] exceptionClasses = method.getExceptionTypes();
        for (int i = 0; i < exceptionClasses.length; i++) {
            Class<?> exClazz = exceptionClasses[i];
            if (Fault.class.isAssignableFrom(exClazz) || exClazz.equals(RuntimeException.class) || exClazz.equals(Throwable.class)) {
                continue;
            }
            createFaultInfo(service, op, exClazz);
        }
    }
    
    /**创建exception信息*/
    protected FaultInfo createFaultInfo(final InterfaceInfo service, final OperationInfo op,
        Class<?> exClass) {
        if(exClass==null){
            return null;
        }
        String faultMsgName =null;
        faultMsgName= namedIDPolicy.getFaultMessageName(op, exClass, exClass);
        if(faultMsgName==null){
            faultMsgName=exClass.getSimpleName();
        }
        NamedID faultName = getFaultName(service,op,exClass,exClass);
        FaultInfo fi = op.addFault(new NamedID(op.getName().getServiceNamespace(),faultMsgName),
                                   new NamedID(op.getName().getServiceNamespace(),faultMsgName));
        fi.setProperty(Class.class.getName(), exClass);
        ArgumentInfo faultArg=fi.addArgument(new NamedID(faultName.getServiceNamespace(),faultMsgName));
        faultArg.setTypeClass(exClass);
        pulishEvent(ServiceFactoryEvent.OPERATIONINFO_FAULT,op,exClass,fi);
        return fi;
    }
    
    protected void initializeArgument(ArgumentInfo arg, Class<?> rawClass, Type type) {
        if(type instanceof TypeVariable){
            if (parameterizedTypes == null) {
                processParameterizedTypes();
            }
            TypeVariable<?> var = (TypeVariable<?>)type;
            final Object gd = var.getGenericDeclaration();
            Map<String, Class<?>> mp = parameterizedTypes.get(gd);
            if (mp != null) {
                Class<?> c = parameterizedTypes.get(gd).get(var.getName());
                if (c != null) {
                    rawClass = c;
                    type = c;
                    arg.getMessageInfo().setProperty("parameterized", Boolean.TRUE);
                }
            }
        }
        arg.setProperty(GENERIC_TYPE, type);
        
        if (Collection.class.isAssignableFrom(rawClass)) {
            arg.setProperty(RAW_CLASS, rawClass);
        }
        arg.setTypeClass(rawClass);
        
    }
    
    protected void initializeDataProcessors() {
        getDataProcessor().initialize(getService());
        service.setDataProcessor(getDataProcessor());
        pulishEvent(ServiceFactoryEvent.DATAPROCESSOR_INITIALIZED,dataProcessor);
    }
    
    @Override
    protected DataProcessor defaultDataProcessor() {
        // 通过service class 注解
        // 通过container参数加载
        return null;
    }

    public void resetFactory() {
        if (!dataFormatSetted) {
            setDataProcessor(null);
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
    public Dictionary<String, ?> getProperties() {
        return properties;
    }

    /**   */
    public void setProperties(Dictionary<String, ?> properties) {
        this.properties = properties;
    }

    /**   */
    @Override
    public Invoker getInvoker() {
        return invoker;
    }

    /**   */
    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
        if (LOG.isTraceEnabled()) {
            LOG.trace("Set service invoker :" + invoker.getClass());
        }
    }

    /**
     * @param info
     * @return
     * @throws EndpointException 
     */
    public Endpoint createEndpoint(EndpointInfo info) throws EndpointException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Creating Endpoint " + info.getName());
        }
        Endpoint ep = new DefaultEndpoint(getContainer(), getService(), info, getPhasePolicy());
        pulishEvent(ServiceFactoryEvent.ENDPOINT_CREATED, info, ep, getServiceClass());
        return ep;
    }

    public PhasePolicy getPhasePolicy() {
        return phasePolicy;
    }
    
    /**   */
    public void setPhasePolicy(PhasePolicy phasePolicy) {
        this.phasePolicy = phasePolicy;
    }

    /**   */
    public void setServiceName(NamedID serviceName) {
        this.serviceName = serviceName;
    }

    /**   */
    public NamedID getEndpointName() {
        return getEndpointName(true);
    }

    public NamedID getEndpointName(boolean lookup) {
        if (endpointName != null || !lookup) {
            return endpointName;
        }
        if (endpointName == null) {
            endpointName = getNamedIDPolicy().getEndpointName();
        }
        return endpointName;
    }
    
    /**   */
    public OperationDispatcher getOperationDispatcher() {
        if (operationDispatcher == null) {
            operationDispatcher = new SimpleOperationDispatcher();
        }
        return operationDispatcher;
    }
    
    /**   */
    public void setOperationDispatcher(OperationDispatcher operationDispatcher) {
        this.operationDispatcher = operationDispatcher;
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

    protected NamedID getFaultName(InterfaceInfo service, OperationInfo op,
        Class<?> exClass, Class<?> beanClass){
        return namedIDPolicy.getFaultName(service, op, exClass, beanClass);
    }
    protected NamedID getOutArgumentName(OperationInfo op, Method method, final int j) {
        return namedIDPolicy.getOutArgumentName(op, method, j);
    }
    protected NamedID getOutMessageName(OperationInfo op, Method method) {
        return namedIDPolicy.getOutMessageName(op, method);
    }
    protected boolean isValidOperation(Method m) {
        return namedIDPolicy.isValidOperation(m);
    }
    
    protected boolean isInParam(Method method,int j){
        return namedIDPolicy.isInParam(method, j);
    }
    
    protected NamedID getInArgumentName(OperationInfo op, Method method, int j) {
        return namedIDPolicy.getInArgumentName(op, method, j);
     }
    
    protected NamedID getOperationName(InterfaceInfo intf, Method method) {
        return namedIDPolicy.getOperationName(intf, method);
    }
    
    public NamedID getInterfaceName() {
        return namedIDPolicy.getInterfaceName();
    }
    /**   */
    public NamedIDPolicy getNamedIDPolicy() {
        return namedIDPolicy;
    }
    public NamedID getInMessageName(OperationInfo op, Method method) {
        return namedIDPolicy.getInMessageName(op, method);
    }
    /**   */
    public void setNamedIDPolicy(NamedIDPolicy namedIDPolicy) {
        this.namedIDPolicy = namedIDPolicy;
    }
    protected boolean hasOutMessage(Method method) {
        return namedIDPolicy.hasOutMessage(method);
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
