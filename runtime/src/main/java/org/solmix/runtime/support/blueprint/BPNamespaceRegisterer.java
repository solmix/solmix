
package org.solmix.runtime.support.blueprint;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.aries.blueprint.NamespaceHandler;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BPNamespaceRegisterer
{

    private static final Logger LOG = LoggerFactory.getLogger(BPNamespaceRegisterer.class);

    private BPNamespaceRegisterer()
    {
    }

    public static void register(BundleContext bc, BPNamespaceFactory factory, String... namespaces) {
        try {
            Object handler = factory.createHandler();
            for (String namespace : namespaces) {
                Dictionary<String, String> properties = new Hashtable<String, String>();
                properties.put("osgi.service.blueprint.namespace", namespace);
                bc.registerService(NamespaceHandler.class.getName(), handler, properties);
                LOG.info("Registered blueprint namespace handler for " + namespace);
            }
        } catch (Throwable e) {
            LOG.warn( "Aries Blueprint packages not available. So namespaces will not be registered", e);
        }
    }

}
