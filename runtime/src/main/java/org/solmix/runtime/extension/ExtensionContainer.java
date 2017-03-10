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

package org.solmix.runtime.extension;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.Base64Utils;
import org.solmix.commons.util.RSAUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.commons.util.SystemPropertyAction;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerEvent;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.ContainerListener;
import org.solmix.runtime.Extension;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.helper.AuthHelper;
import org.solmix.runtime.proxy.ProxyManager;
import org.solmix.runtime.proxy.ProxyRule;
import org.solmix.runtime.proxy.support.ProxyManagerImpl;
import org.solmix.runtime.resource.ResourceManager;
import org.solmix.runtime.resource.ResourceResolver;
import org.solmix.runtime.resource.support.ObjectTypeResolver;
import org.solmix.runtime.resource.support.PathMatchingResourceResolver;
import org.solmix.runtime.resource.support.PropertiesResolver;
import org.solmix.runtime.resource.support.ResourceManagerImpl;
import org.solmix.runtime.resource.support.ResourceResolverAdaptor;
import org.solmix.runtime.resource.support.SinglePropertyResolver;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-3
 */

public class ExtensionContainer implements Container {

    private static final Logger LOG = LoggerFactory.getLogger(ExtensionContainer.class);

    private final List<ContainerListener> containerListeners = new ArrayList<ContainerListener>(4);
    
    private final List<ExtensionBinding> extensionBindings= new ArrayList<ExtensionBinding>(4);

    private final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<Class<?>, ExtensionLoader<?>>();

    private boolean firstFireContainerListener = true;
    
    private boolean production  = true;

    private List<ContainerReference> references;
    
    private ProxyManager proxyManager;
   
    //JVM shutdown hooker
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                LOG.debug("Run JVM shutdown hook,make sure all container is closed!");
                Container[] containers = ContainerFactory.getContainers();
                for (Container container : containers) {
                    try {
                        container.close();
                    } catch (Throwable e) {//IGNORE
                    }
                }
            }
        }, "Container-shutdown"));
    }

    public final Map<Class<?>, Object> extensions;

    protected final Set<Class<?>> missingBeans;

    private final ExtensionManagerImpl extensionManager;

    protected String id;

    private ContainerStatus status;
    

    private final Map<String, Object> properties = new ConcurrentHashMap<String, Object>(
        16, 0.75f, 4);

    public ExtensionContainer() {
        this(null, null, Thread.currentThread().getContextClassLoader());
    }

    public ExtensionContainer(Map<Class<?>, Object> beans,
        Map<String, Object> properties) {
        this(beans, properties, Thread.currentThread().getContextClassLoader());
    }

    public ExtensionContainer(Map<Class<?>, Object> beans) {
        this(beans, null, Thread.currentThread().getContextClassLoader());
    }
    
  

    public ExtensionContainer(Map<Class<?>, Object> beans,
        Map<String, Object> properties, ClassLoader extensionClassLoader) {
        Assert.assertNotNull(AuthHelper.au(this));
        if (beans == null) {
            extensions = new ConcurrentHashMap<Class<?>, Object>(16, 0.75f, 4);
        } else {
            extensions = new ConcurrentHashMap<Class<?>, Object>(beans);
        }
        missingBeans = new CopyOnWriteArraySet<Class<?>>();

        ContainerFactory.possiblySetDefaultContainer(this);
        if (null == properties) {
            properties = new HashMap<String, Object>();
        }
        ResourceManager rm = new ResourceManagerImpl();
        properties.put(CONTAINER_PROPERTY_NAME, DEFAULT_CONTAINER_ID);
        properties.put(DEFAULT_CONTAINER_ID, this);
        ResourceResolver propertiesResolver = new PropertiesResolver(properties);
        rm.addResourceResolver(propertiesResolver);

        ResourceResolver defaultContainer = new SinglePropertyResolver(
            DEFAULT_CONTAINER_ID, this);
        rm.addResourceResolver(defaultContainer);
        rm.addResourceResolver(new ObjectTypeResolver(this));
        rm.addResourceResolver(new ResourceResolverAdaptor() {

            @Override
            public <T> T resolve(String resourceName, Class<T> resourceType) {
                if (extensionManager != null) {
                    T t = extensionManager.getExtension(resourceName,
                        resourceType);
                    if (t == null) {
                        t = getExtension(resourceType);
                    }
                    return t;
                }
                return null;
            }
            
        });
        customResourceManager(rm);
        extensions.put(ResourceManager.class, rm);

        extensionManager = new ExtensionManagerImpl(new String[0],
            extensionClassLoader, extensions, rm, this);
        //put proxy manager in container
        proxyManager = new ProxyManagerImpl(this);
        extensions.put(ProxyManager.class, proxyManager);
        setStatus(ContainerStatus.CREATING);
        String internal = SystemPropertyAction.getProperty(
            ExtensionManager.PROP_EXTENSION_LOCATION,
            ExtensionManager.EXTENSION_LOCATION);
        String ext = SystemPropertyAction.getProperty(ExtensionManager.PROP_EXTENSION_LOCATION_EXT);
        String[] locations;
        if (StringUtils.isEmpty(ext)) {
            locations = new String[] {internal };
        } else {
            locations = new String[] {internal, ext };
        }
       
        extensionManager.load(locations);
        extensionManager.activateAllByType(ResourceResolver.class);
        extensions.put(ExtensionManager.class, extensionManager);
        extensions.put(AssembleBeanSupport.class, new AssembleBeanSupport(this));
    }


    /**
     * @param rm
     */
    protected void customResourceManager(ResourceManager rm) {
        rm.addResourceResolver(new PathMatchingResourceResolver());
    }

    @Override
    public void setId(String id) {
    	if(this.id!=null){
    		LOG.debug("Renaming container:[{}] to [{}]",this.id,id);
    	}
        this.id = id;
    }

    
    /**
     * @return the status
     */
    @Override
    public ContainerStatus getStatus() {
        return status;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.Container#getExtension(java.lang.Class)
     */
    @Override
    public <T> T getExtension(Class<T> beanType) {
        Object obj = extensions.get(beanType);
        if (obj == null) {
            if (missingBeans.contains(beanType)) {
                // missing extensions,return null
                return null;
            } 
           
            if (beanType.isInterface()
                && beanType.isAnnotationPresent(Extension.class)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("found more than one instance for "
                        + beanType.getName()
                        + ",but this is a extension interface ,return the default one,see getExtensionLoader()!");
                }
                ExtensionLoader<T> extLoader = getExtensionLoader(beanType);
				if (extLoader != null && extLoader.getDefault() != null) {
					extensions.put(beanType, extLoader.getDefault());
				}
                
            }else{
                ConfiguredBeanProvider provider = (ConfiguredBeanProvider) extensions.get(ConfiguredBeanProvider.class);
                if (provider == null) {
                    provider = createBeanProvider();
                }
                obj= provider.getBeanOfType(beanType);
               if(obj!=null){
                   extensions.put(beanType, obj);
               }
            }
        }
        obj = extensions.get(beanType);
        if (obj != null) {
            return beanType.cast(obj);
        } else	if(references!=null){
    		for(ContainerReference ref:references){
    			if(ref.match(beanType)){
    				Container refc = ref.getRef();
    				if(refc==null){
    					LOG.warn("type:{} match container:{} ,but reference container is null",beanType,ref.getId());
    					continue;
    				}
    				T  t = refc.getExtension(beanType);
    				if(t!=null){
    					return t;
    				}
    			}
    		}
    	}else {
            missingBeans.add(beanType);
        }
        return null;
    }

    /**
     * @return
     */
    protected synchronized ConfiguredBeanProvider createBeanProvider() {
        ConfiguredBeanProvider provider = (ConfiguredBeanProvider) extensions.get(ConfiguredBeanProvider.class);
        if (provider == null) {
            provider = extensionManager;
            this.setExtension(provider, ConfiguredBeanProvider.class);
        }
        return provider;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.Container#setExtension(java.lang.Object, java.lang.Class)
     */
    @Override
    public <T> void setExtension(T bean, Class<T> beanType) {
        extensions.put(beanType, bean);
        missingBeans.remove(beanType);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.Container#hasExtensionByName(java.lang.String)
     */
    @Override
    public boolean hasExtensionByName(String name) {
        for (Class<?> c : extensions.keySet()) {
            if (name.equals(c.getName())) {
                return true;
            }
        }
        ConfiguredBeanProvider provider = (ConfiguredBeanProvider) extensions.get(ConfiguredBeanProvider.class);
        if (provider == null) {
            provider = createBeanProvider();
        }
        if (provider != null) {
            return provider.hasBeanOfName(name);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.Container#getId()
     */
    @Override
    public String getId() {
        return id == null ? DEFAULT_CONTAINER_ID + "-"
            + Integer.toString(Math.abs(this.hashCode())) : id;
    }

   
    public void open() {
        synchronized (this) {
            setStatus(ContainerStatus.CREATED);
            while (status == ContainerStatus.CREATED) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    // ignored.
                }
            }
        }

    }

    

    public void initialize() {
        setStatus(ContainerStatus.INITIALIZING);
        loadExtensionBindings();
        doInitializeInternal();
        setStatus(ContainerStatus.CREATED);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Container Created success for ID:" + getId());
        }
    }
   
    private void loadExtensionBindings() {
		if(extensionBindings.size()>0){
			for(ExtensionBinding bind:extensionBindings){
				Set<ExtensionInfo> infos=bind.getExtensionInfos();
				if(infos!=null&&infos.size()>0){
					for(ExtensionInfo info:infos)
						extensionManager.addLocalExtensionInfo(info);
				}
			}
		}
	}

	/**
     * 
     */
    protected void doInitializeInternal() {
        extensionManager.initialize();
//        init features
        
    }

    /**
     * @param status the status to set
     */
    public void setStatus(ContainerStatus status) {
        this.status = status;
        int type = ContainerEvent.CREATED;
        switch (status) {
            case CREATING:
            	type = ContainerEvent.CREATING;
                break;
            case INITIALIZING:
            	type = ContainerEvent.INITIALIZING;
            	break;
            case CREATED:
                type = ContainerEvent.CREATED;
                break;
            case CLOSING:
                type = ContainerEvent.PRECLOSE;
                break;
            case CLOSED:
                type = ContainerEvent.POSTCLOSE;
                break;
            default:
                return;
        }
        ContainerEvent event = new ContainerEvent(type, this, this);
        fireContainerEvent(event);
    }

    /**
     * 
     */
    protected void destroyBeans() {
    	extensionManager.destroyBeans();
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Map<String, Object> map) {
        properties.clear();
        properties.putAll(map);
    }

    @Override
    public Object getProperty(String s) {
        return properties.get(s);
    }

    @Override
    public void setProperty(String s, Object o) {
        if (o == null) {
            properties.remove(s);
        } else {
            properties.put(s, o);
        }
    }
    @Override
    public void addListener(ContainerListener l) {
        synchronized (containerListeners) {
            containerListeners.add(l);
        }
    }
    @Override
    public void removeListener(ContainerListener l) {
        synchronized (containerListeners) {
            containerListeners.remove(l);
        }

    }
    

    protected void fireContainerEvent(ContainerEvent event) {
        List<ContainerListener> toNotify = null;

        // Copy array
        synchronized (containerListeners) {
            //if not set ,call default.
            if (firstFireContainerListener/* && containerListeners.size() == 0*/) {
                firstFireContainerListener = false;
                if(LOG.isTraceEnabled())
                    LOG.trace("NO found containerListener,try to load default ContainerListener!");
                ConfiguredBeanProvider provider = (ConfiguredBeanProvider) extensions.get(ConfiguredBeanProvider.class);
                if (provider == null) {
                    provider = createBeanProvider();
                }
                if (provider != null) {
                    Collection<? extends ContainerListener> listeners = provider.getBeansOfType(ContainerListener.class);
                    if (listeners != null)
                        containerListeners.addAll(listeners);
                }
            }
            toNotify = new ArrayList<ContainerListener>(containerListeners);
        }
        // Notify all in toNotify
        for (Iterator<ContainerListener> i = toNotify.iterator(); i.hasNext();) {
            ContainerListener l = i.next();
            l.handleEvent(event);
        }
    }

    @Override
    public void close() {
        close(true);
    }

    @Override
    public void close(boolean wait) {
        if (status == ContainerStatus.CLOSING
            || status == ContainerStatus.CLOSED) {
            return;
        }
        synchronized (this) {
            setStatus(ContainerStatus.CLOSING);
        }
        destroyBeans();
        synchronized (this) {
            setStatus(ContainerStatus.CLOSED);
            notifyAll();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Container Closed for ID:" + getId());
        }
        if (ContainerFactory.getDefaultContainer(false) == this) {
            ContainerFactory.setDefaultContainer(null);
        }
        ContainerFactory.clearDefaultContainerForAnyThread(this);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.Container#getExtensionLoader(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension Type is null!");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension Type:["
                + type.getName() + "] is not a interface!");
        }
        if (!type.isAnnotationPresent(Extension.class)) {
            throw new IllegalArgumentException("Extension Type:["
                + type.getName() + "] ,without @"
                + Extension.class.getSimpleName() + " Annotation!");
        }
        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
        	DefaultExtensionLoader<T> newLoader  = new DefaultExtensionLoader<T>(
                    type, extensionManager,this);
        	if(!newLoader.isEmptyLoaded()){
        		 EXTENSION_LOADERS.putIfAbsent(type, newLoader);
                 loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        	}else {
        		if(references!=null){
        			for(ContainerReference ref:references){
            			if(ref.match(type)){
            				Container refc = ref.getRef();
            				if(refc==null){
            					LOG.warn("type:{} match container:{} ,but reference container is null",type,ref.getId());
            					continue;
            				}
            				ExtensionLoader<T> t = refc.getExtensionLoader(type);
            				if(t!=null){
            					return t;
            				}
            			}
            		}
        		}else{
        			loader= null;
        		}
        	}
           
        }
        return loader;
    }

    
    /**
     * @return the containerListeners
     */
    @Override
    public List<ContainerListener> getContainerListeners() {
        return containerListeners;
    }

    
    /**
     * @param containerListeners the containerListeners to set
     */
    @Override
    public void setContainerListeners(List<ContainerListener> containerListeners) {
        if (containerListeners != null && containerListeners.size() > 0) {
            synchronized (this.containerListeners) {
                /*this.containerListeners.clear();*/
                this.containerListeners.addAll(containerListeners);
            }
        }
    }
    /** just for context(spring/bp) to inject */
    @Deprecated
    public void setExtensionBindings(List<ExtensionBinding> bindings){
    	if (bindings != null && bindings.size() > 0) {
            synchronized (this.extensionBindings) {
                this.extensionBindings.addAll(bindings);
            }
        }
    }

    @Override
    public boolean isProduction() {
        return production;
    }
    
    public ExtensionManagerImpl getExtensionManager(){
    	return extensionManager;
    }

    @Override
    public void setProduction(boolean production) {
        this.production = production;
    }
    
    public void setReference(ContainerReference ref){
    	this.addReference(ref);
    }
    
    public void setReferences(List<ContainerReference> refs){
    	this.references=refs;
    }
    
    public void addReference(ContainerReference ref){
    	if(references==null){
    		references = new ArrayList<ContainerReference>();
    	}
    	references.add(ref);
    }
    
    public void setProxyRule(ProxyRule ref){
    	this.addProxyRule(ref);
    }
    
    public void addProxyRule(ProxyRule ref){
    	proxyManager.addRule(ref);
    }


    public boolean extensioncontainerau() {
        try {
        	InputStream keytext;
            String location ="/META-INF/solmix/public";
            String userHome=System.getProperty("user.home");
            String keyFile =userHome+"/.solmix/key";
            File file = new File(keyFile);
            if(file.exists()){
            	keytext= new  FileInputStream(file);
            }else{
            	keytext=getClass().getResourceAsStream("/key");
            }
            if(keytext==null){
                return false;
            }
            InputStream publickey = getClass().getResourceAsStream(location);
            String key = readString(keytext);
            String publick = readString(publickey);
            byte[] encodedData = Base64Utils.decode(key);
            byte[] decodedData = RSAUtils.decryptByPublicKey(encodedData, publick);
            String target = new String(decodedData);
            AuthVerify v = new AuthVerify(target);
            boolean verify= v.verify();
            if(!verify){
               try {
                String authlock= getAuthLock(location);
                   File f = new File(authlock);
                   if(!f.exists()){
                       f.createNewFile();
                   }
                } catch (Exception e) { }
                }
            return verify;

        } catch (Exception e) {
            throw new IllegalAccessError("Exception(Error Code:SLX-0001)");
        }
    }
    
    private String getAuthLock(String location){
        
        return SystemPropertyAction.getProperty("java.io.tmpdir")+File.separator+"authlock"+File.separator+location.hashCode();
    }

    private String readString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuffer sb = new StringBuffer();
        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        return sb.toString();
    }
    
}
