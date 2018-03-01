
package org.solmix.runtime.osgi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.DataUtils;
import org.solmix.runtime.bean.ConfiguredBeanProvider;

public class OSGIBeanProvider implements ConfiguredBeanProvider
{

    private static final Logger LOG = LoggerFactory.getLogger(OSGIBeanProvider.class);
    
    private static final String USED_OSGI_BEAN_PROVIDER = "org.solmix.runtime.osgi.USED_OSGI_PROVIDER";

    public static final String SERVICE_NAME="service.name";
    final ConfiguredBeanProvider orign;

    final BundleContext context;

    private boolean checkCompatibleProvider;
    

    public OSGIBeanProvider(ConfiguredBeanProvider provider, BundleContext defaultContext)
    {
        this.orign = provider;
        this.context = defaultContext;
        Object check = context.getProperty(USED_OSGI_BEAN_PROVIDER);
        checkCompatibleProvider = check == null || DataUtils.asBoolean(check);
       
        
    }

    @Override
    public List<String> getBeanNamesOfType(Class<?> type) {
        return orign.getBeanNamesOfType(type);
    }

    @Override
    public <T> T getBeanOfType(String name, Class<T> type) {
        return orign.getBeanOfType(name, type);
    }

    @Override
    public <T> T getBeanOfType(Class<T> type) {
        return orign.getBeanOfType(type);
    }

    @Override
    public <T> Collection<? extends T> getBeansOfType(Class<T> type) {
        Collection<? extends T> ret = orign.getBeansOfType(type);
        if (ret == null || ret.isEmpty()) {
            return getBeansFromOsgiService(type);
        } else {
            return ret;
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private <T> List<T> getBeansFromOsgiService(Class<T> type) {
        List<T> list = new ArrayList<T>();
        try {
            ServiceReference refs[] = context.getServiceReferences(type.getName(), null);
            if (refs != null) {
                for (ServiceReference r : refs) {
                    if (type == ClassLoader.class && checkCompatibleProvider) {
                        continue;
                    }
                    list.add((T)(context.getService(r)));
                }
            }
        } catch (Exception ex) {
            LOG.info("Tried to find the Bean with type:" + type + " from OSGi services and get error: " + ex);
        }
        return list;
    }
    @Override
    public <T> boolean loadBeansOfType(Class<T> type, BeanLoaderListener<T> listener) {
        return orign.loadBeansOfType(type, listener);
    }

    @Override
    public boolean hasBeanOfName(String name) {
        return orign.hasBeanOfName(name);
    }

}
