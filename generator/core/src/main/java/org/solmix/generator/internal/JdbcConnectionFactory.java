/**
 *    Copyright 2006-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.solmix.generator.internal;

import static org.solmix.commons.util.StringUtils.stringHasValue;
import static org.solmix.generator.util.Messages.getString;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import org.solmix.generator.api.ConnectionFactory;
import org.solmix.generator.config.JdbcConnectionInfo;

/**
 * This class assumes that classes are cached elsewhere for performance reasons,
 * but also to make sure that any native libraries are only loaded one time
 * (avoids the dreaded UnsatisfiedLinkError library loaded in another
 * classloader)
 * 
 * @author Jeff Butler
 */
public class JdbcConnectionFactory implements ConnectionFactory {

    private String userId;
    private String password;
    private String connectionURL;
    private String driverClass;
    private Properties otherProperties;

    /**
     * This constructor is called when there is a JDBCConnectionConfiguration
     * specified in the configuration.
     * 
     * @param config the configuration
     */
    public JdbcConnectionFactory(JdbcConnectionInfo config) {
        super();
        userId = config.getUserId();
        password = config.getPassword();
        connectionURL = config.getConnectionURL();
        driverClass = config.getDriverClass();
        otherProperties = config.getProperties();
    }

    /**
     * This constructor is called when this connection factory is specified 
     * as the type in a ConnectionFactory configuration element. 
     */
    public JdbcConnectionFactory() {
        super();
    }

    @Override
    public Connection getConnection() throws SQLException {

        Properties props = new Properties();

        if (stringHasValue(userId)) {
            props.setProperty("user", userId); 
        }

        if (stringHasValue(password)) {
            props.setProperty("password", password); 
        }

        props.putAll(otherProperties);

        Driver driver = getDriver();
        Connection conn = driver.connect(connectionURL, props);

        if (conn == null) {
            throw new SQLException(getString("RuntimeError.7")); 
        }

        return conn;
    }

    private Driver getDriver() {
        Driver driver;

        try {
            Class<?> clazz = ObjectFactory.externalClassForName(driverClass);
            driver = (Driver) clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(getString("RuntimeError.8"), e); 
        }

        return driver;
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        // this should only be called when this connection factory is
        // specified in a ConnectionFactory configuration
        userId = properties.getProperty("userId"); 
        password = properties.getProperty("password"); 
        connectionURL = properties.getProperty("connectionURL"); 
        driverClass = properties.getProperty("driverClass"); 

        otherProperties = new Properties();
        otherProperties.putAll(properties);

        // remove all the properties that we have specific attributes for
        otherProperties.remove("userId"); 
        otherProperties.remove("password"); 
        otherProperties.remove("connectionURL"); 
        otherProperties.remove("driverClass"); 
    }
}
