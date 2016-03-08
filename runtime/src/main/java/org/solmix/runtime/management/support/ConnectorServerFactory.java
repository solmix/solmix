
package org.solmix.runtime.management.support;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectorServerFactory
{

    public static final String DEFAULT_SERVICE_URL = "service:jmx:rmi:///jndi/rmi://localhost:44444/jmxrmi";

    private static final Logger LOG = LoggerFactory.getLogger(ConnectorServerFactory.class);

    private static MBeanServer server;

    private static String serviceUrl = DEFAULT_SERVICE_URL;

    private static Map<String, ?> environment;

    private static boolean threaded;

    private static boolean daemon;

    private static JMXConnectorServer connectorServer;

    private static class ConnectorServerFactoryHolder
    {

        private static final ConnectorServerFactory INSTANCE = new ConnectorServerFactory();
    }

    private static class MBeanServerHolder
    {

        private static final MBeanServer INSTANCE = MBeanServerFactory.createMBeanServer();
    }

    public void destroy() throws IOException {
        connectorServer.stop();
        if (LOG.isInfoEnabled()) {
            LOG.info("JMX connector server stopped: " + connectorServer);
        }
    }

    public void createConnector() throws IOException {

        if (server == null) {
            server = MBeanServerHolder.INSTANCE;
        }

        // Create the JMX service URL.
        JMXServiceURL url = new JMXServiceURL(serviceUrl);

        // if the URL is localhost, start up an Registry
        if (serviceUrl.indexOf("localhost") > -1 && url.getProtocol().compareToIgnoreCase("rmi") == 0) {
            try {
                int port = getURLLocalHostPort(serviceUrl);
                try {
                    LocateRegistry.createRegistry(port);
                } catch (Exception ex) {
                    // the registry may had been created
                    LocateRegistry.getRegistry(port);
                }

            } catch (Exception ex) {
                LOG.info("CREATE_REGISTRY_FAULT_MSG", new Object[] { ex });
            }
        }

        connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, environment, server);

        if (threaded) {
            // Start the connector server asynchronously (in a separate thread).
            Thread connectorThread = new Thread() {

                @Override
                public void run() {
                    try {
                        connectorServer.start();
                    } catch (IOException ex) {
                        LOG.info("START_CONNECTOR_FAILURE_MSG", new Object[] { ex });
                    }
                }
            };

            connectorThread.setName("JMX Connector Thread [" + serviceUrl + "]");
            connectorThread.setDaemon(daemon);
            connectorThread.start();
        } else {
            // Start the connector server in the same thread.
            connectorServer.start();
        }

        LOG.info("JMX connector server started: " + connectorServer);
    }

    public static ConnectorServerFactory getInstance() {
        return ConnectorServerFactoryHolder.INSTANCE;
    }

    public void setMBeanServer(MBeanServer ms) {
        server = ms;
    }

    public void setServiceUrl(String url) {
        serviceUrl = url;
    }

    public void setEnvironment(Map<String, ?> env) {
        environment = env;
    }

    public void setThreaded(boolean fthread) {
        threaded = fthread;
    }

    public void setDaemon(boolean fdaemon) {
        daemon = fdaemon;
    }

    private int getURLLocalHostPort(String url) {
        int portStart = url.indexOf("localhost") + 10;
        int portEnd;
        int port = 0;
        if (portStart > 0) {
            portEnd = indexNotOfNumber(url, portStart);
            if (portEnd > portStart) {
                final String portString = url.substring(portStart, portEnd);
                port = Integer.parseInt(portString);
            }
        }
        return port;
    }

    private static int indexNotOfNumber(String str, int index) {
        int i = 0;
        for (i = index; i < str.length(); i++) {
            if (str.charAt(i) < '0' || str.charAt(i) > '9') {
                return i;
            }
        }
        return -1;
    }
}
