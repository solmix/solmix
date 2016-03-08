
package org.solmix.runtime.management.support;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.management.InstanceAlreadyExistsException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.RequiredModelMBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerEvent;
import org.solmix.runtime.ContainerListener;
import org.solmix.runtime.management.ComponentManager;
import org.solmix.runtime.management.ManagedComponent;
import org.solmix.runtime.management.ManagementConstants;

public class DefaultComponentManager implements ComponentManager, ContainerListener
{

    private static final Logger LOG = LoggerFactory.getLogger(DefaultComponentManager.class);

    private Container container;

    private String withContainerId;

    private boolean usePlatformMBeanServer;

    private String mbeanServerName;

    private boolean enabled;

    private boolean threaded;

    private boolean daemon;

    private String jmxServiceURL;

    private boolean createMBServerConnectorFactory = true;

    private ConnectorServerFactory connectorFactory;

    private MBeanServer mserver;

    private Set<ObjectName> beans = new HashSet<ObjectName>();

    private static Map<String, String> mbeanServerIDMap = new HashMap<String, String>();

    private boolean connectFailed;

    public DefaultComponentManager()
    {

    }

    public DefaultComponentManager(Container container)
    {
        readJMXProperties(container);
        this.container = container;

    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        if (this.container == null) {
            readJMXProperties(container);
        } else {
            ComponentManager manager = container.getExtension(ComponentManager.class);
            if (this != manager) {
                container.setExtension(manager, ComponentManager.class);
                try {
                    ManagedContainer mc = new ManagedContainer(container);
                    manager.unregister(mc);
                    if (LOG.isInfoEnabled()) {
                        LOG.info("unregistered " + mc.getObjectName());
                    }
                } catch (JMException e) {
                    // ignore
                }
            }
        }
        this.container = container;
    }

    @PostConstruct
    public void init() {
        if (container != null && container.getExtension(MBeanServer.class) != null) {
            enabled = true;
            createMBServerConnectorFactory = false;
            mserver = container.getExtension(MBeanServer.class);
        }
        if (isEnabled()) {
            if (usePlatformMBeanServer) {
                mserver = ManagementFactory.getPlatformMBeanServer();
            }
            if(StringUtils.isEmpty(mbeanServerName)){
                mserver = ManagementFactory.getPlatformMBeanServer();
            }else{
                String mbeanServerID = mbeanServerIDMap.get(mbeanServerName);
                List<MBeanServer> servers = null;
                if (mbeanServerID != null) {
                    servers = MBeanServerFactory.findMBeanServer(mbeanServerID);
                }
                if (servers == null || servers.size() == 0) {
                    mserver = MBeanServerFactory.createMBeanServer(mbeanServerID);
                    try {
                        mbeanServerID = (String) mserver.getAttribute(getDelegateName(), "MBeanServerId");
                        mbeanServerIDMap.put(mbeanServerName, mbeanServerID);
                    } catch (JMException e) {
                        // ignore
                    }
                } else {
                    mserver = servers.get(0);
                }
            }
            if (createMBServerConnectorFactory) {
                connectorFactory = ConnectorServerFactory.getInstance();
                connectorFactory.setMBeanServer(mserver);
                connectorFactory.setThreaded(isThreaded());
                connectorFactory.setDaemon(isDaemon());
                connectorFactory.setServiceUrl(getJMXServiceURL());
                try {
                    connectorFactory.createConnector();
                } catch (IOException ex) {
                    connectFailed = true;
                    LOG.info("START_CONNECTOR_FAILURE_MSG", new Object[] { ex });
                }
            }
            if (!connectFailed && null != container) {            
                try {
                    ManagedContainer mc = new ManagedContainer(container);                    
                    register(mc);
                } catch (JMException jmex) {
                    LOG.info("REGISTER_FAILURE_MSG", new Object[]{container, jmex});
                }
            }

        }
        if (container != null) {
            container.setExtension(this, ComponentManager.class);
            container.addListener(this);
        }
    }

    public String getJMXServiceURL() {
        if (jmxServiceURL == null) {
            return "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
        } else {
            return jmxServiceURL;
        }
    }

    private ObjectName getDelegateName() throws JMException {
        try {
            return (ObjectName) MBeanServerDelegate.class.getField("DELEGATE_NAME").get(null);
        } catch (Throwable t) {
            // ignore, likely on Java5
        }
        try {
            return new ObjectName("JMImplementation:type=MBeanServerDelegate");
        } catch (MalformedObjectNameException e) {
            JMException jme = new JMException(e.getMessage());
            jme.initCause(e);
            throw jme;
        }
    }

    private void readJMXProperties(Container c) {
        if (c != null) {
            withContainerId = getContainerProperty(c, "c.jmx.withContainerId", withContainerId);
            mbeanServerName = getContainerProperty(c, "c.jmx.serverName", mbeanServerName);
            usePlatformMBeanServer = getContainerProperty(c, "c.jmx.usePlatformMBeanServer", usePlatformMBeanServer);
            createMBServerConnectorFactory = getContainerProperty(c, "c.jmx.createMBServerConnectorFactory", createMBServerConnectorFactory);
            daemon = getContainerProperty(c, "c.jmx.daemon", daemon);
            threaded = getContainerProperty(c, "c.jmx.threaded", threaded);
            enabled = getContainerProperty(c, "c.jmx.enabled", enabled);
            jmxServiceURL = getContainerProperty(c, "c.jmx.JMXServiceURL", jmxServiceURL);
        }
    }

    private static String getContainerProperty(Container c, String key, String dflt) {
        String v = (String) c.getProperty(key);
        return v != null ? v : dflt;
    }

    private static boolean getContainerProperty(Container c, String key, boolean dflt) {
        Object v = c.getProperty(key);
        if (v instanceof Boolean) {
            return (Boolean) v;
        }
        return v != null ? Boolean.valueOf(v.toString()) : dflt;
    }

    @Override
    public ObjectName register(ManagedComponent component) throws JMException {
       ObjectName name = component.getObjectName();
       register(component,name);
       return name;
    }

    @Override
    public ObjectName register(ManagedComponent component, boolean forceRegistration) throws JMException {
        ObjectName name = component.getObjectName();
        register(component,name,forceRegistration);
        return name;
    }

    @Override
    public void register(Object obj, ObjectName name) throws JMException {
        register(obj,name,false);

    }

    @Override
    public void register(Object obj, ObjectName name, boolean forceRegistration) throws JMException {
        if (!isEnabled() || connectFailed) {
            return;           
        }
        
        try {
            registerMBeanWithServer(obj, persist(name), forceRegistration);           
        } catch (NotCompliantMBeanException e) {        
            AssembleModelMBeanSupport assembler = new AssembleModelMBeanSupport();
            ModelMBeanInfo mbi = assembler.getModelMbeanInfo(obj.getClass());
            register(obj, name, mbi, forceRegistration);
        }          

    }
    
    private void register(Object obj, ObjectName name, ModelMBeanInfo mbi, boolean forceRegistration) 
        throws JMException {                  
        RequiredModelMBean rtMBean = (RequiredModelMBean)mserver.instantiate("javax.management.modelmbean.RequiredModelMBean");
        rtMBean.setModelMBeanInfo(mbi);
        try {
            rtMBean.setManagedResource(obj, "ObjectReference");
        } catch (InvalidTargetObjectTypeException itotex) {
            throw new JMException(itotex.getMessage());
        }
        registerMBeanWithServer(rtMBean, persist(name), forceRegistration);
    }
    
    private void registerMBeanWithServer(Object obj, ObjectName name, boolean forceRegistration) 
        throws JMException {
        ObjectInstance instance = null;
        try {
            instance = mserver.registerMBean(obj, name);  
            if (LOG.isInfoEnabled()) {
                LOG.info("registering MBean " + name + ": " + obj);
            }
        } catch (InstanceAlreadyExistsException e) {            
            if (forceRegistration) {
                mserver.unregisterMBean(name);                
                instance = mserver.registerMBean(obj, name);
            } else {
                throw e;
            }
        }
        
        if (instance != null) {
            beans.add(name);
        }
    }
    
    private ObjectName persist(ObjectName original) throws JMException {
        ObjectName persisted = original;
        if (!(withContainerId == null 
              || "".equals(withContainerId) 
              || withContainerId.startsWith("${"))) {
            String originalStr = original.toString();
            if (originalStr.indexOf(ManagementConstants.CONTAINER_ID_PROP) != -1) {
                String persistedStr = 
                    originalStr.replaceFirst(ManagementConstants.CONTAINER_ID_PROP + "=" + container.getId(), 
                                             ManagementConstants.CONTAINER_ID_PROP + "=" + withContainerId);
                persisted = new ObjectName(persistedStr);
            }
        }
        return persisted;
    }

    @Override
    public void unregister(ManagedComponent component) throws JMException {
        ObjectName name = component.getObjectName();
        unregister(persist(name));
    }

    @Override
    public void unregister(ObjectName name) throws JMException {
        if (!isEnabled() || connectFailed) {
            return;           
        }
        
        beans.remove(name);       
        mserver.unregisterMBean(name); 
    }

    
    public String getMbeanServerName() {
        return mbeanServerName;
    }

    
    public void setMbeanServerName(String mbeanServerName) {
        this.mbeanServerName = mbeanServerName;
    }

    @Override
    public void shutdown() {
        if (!isEnabled()) {
            return;
        }
        if (connectorFactory != null) {
            try {
                connectorFactory.destroy();
            } catch (IOException ex) {
                LOG.error("Stop Mbean conncetor factory.", ex);
            }
        }
        Object[] mBeans = beans.toArray();
        for (Object name : mBeans) {
            beans.remove(name);
            try {
                unregister((ObjectName) name);
            } catch (JMException jmex) {
                LOG.error("unregister mbean failed", jmex);
            }
        }
    }

    @Override
    public MBeanServer getMBeanServer() {
        return mserver;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置enabled属性的值。
     * 
     */
    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public void setServer(MBeanServer server) {
        this.mserver = server;
    }
    /**
     * 获取threaded属性的值。
     * 
     */
    public boolean isThreaded() {
        return threaded;
    }

    /**
     * 设置threaded属性的值。
     * 
     */
    public void setThreaded(boolean value) {
        this.threaded = value;
    }

    /**
     * 获取daemon属性的值。
     * 
     */
    public boolean isDaemon() {
        return daemon;
    }

    /**
     * 设置daemon属性的值。
     * 
     */
    public void setDaemon(boolean value) {
        this.daemon = value;
    }

    @Override
    public void handleEvent(ContainerEvent event) {
        switch (event.getType()) {
            case ContainerEvent.POSTCLOSE:
                this.shutdown();
                break;
            default:
                return;
        }

    }


    
    public void setJMXServiceURL(String jmxServiceURL) {
        this.jmxServiceURL = jmxServiceURL;
    }
    
}
