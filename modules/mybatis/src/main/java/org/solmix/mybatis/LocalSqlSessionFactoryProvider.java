/*
 * Copyright 2013 The Solmix Project
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

package org.solmix.mybatis;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.exception.SlxException;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.commons.util.DataUtils;
import org.solmix.runtime.SystemContext;
import org.solmix.runtime.cm.ConfigureUnit;
import org.solmix.runtime.cm.ConfigureUnitManager;
import org.solmix.sql.ConnectionManager;
import org.solmix.sql.SQLDataSource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年6月13日
 */

public class LocalSqlSessionFactoryProvider implements
    SqlSessionFactoryProvider
{

    private final Map<String, SqlSessionFactoryHolder> _tempCache = new java.util.concurrent.ConcurrentHashMap<String, SqlSessionFactoryHolder>();

    private final Logger LOG = LoggerFactory.getLogger(LocalSqlSessionFactoryProvider.class.getName());

    private SystemContext sc;

    public LocalSqlSessionFactoryProvider()
    {

    }

    public LocalSqlSessionFactoryProvider(final SystemContext sc)
    {
        setSystemContext(sc);
    }

    /**
     * @param systemContext
     */
    @Resource
    public void setSystemContext(final SystemContext systemcontext) {
        this.sc = systemcontext;
        if (sc != null) {
            sc.setBean(this, SqlSessionFactoryProvider.class);
        }

    }

    protected DataTypeMap getConfig() throws SlxException {
        ConfigureUnitManager cum = sc.getBean(ConfigureUnitManager.class);
        ConfigureUnit cu = null;
        try {
            cu = cum.getConfigureUnit(MybatisDataSource.SERVICE_PID);
        } catch (IOException e) {
            throw new SlxException(Tmodule.SQL, Texception.IO_EXCEPTION, e);
        }
        if (cu != null)
            return cu.getProperties();
        else
            return new DataTypeMap();
    }

    /**
     * {@inheritDoc}
     * @throws SlxException 
     * 
     * @see org.solmix.mybatis.SqlSessionFactoryProvider#createSqlSessionFactory(java.lang.String)
     */
    @Override
    public SqlSessionFactory createSqlSessionFactory(String environment,
        Map<String, Object> properties) throws SlxException {
        SqlSessionFactoryHolder sessionFactory = _tempCache.get(environment);
        if (sessionFactory != null) {
            return sessionFactory.factory;
        }
        if (LOG.isTraceEnabled())
            LOG.trace("Building Mybatis SqlSessionFactory for environment '"
                + environment + "'");
        SqlSessionFactoryHolder session = null;
            session = createSessionForDb(environment, properties);
        if (session != null)
            _tempCache.put(environment, session);
        return session.factory;
    }
    
    public String getDatabaseType(String dbName){
        ConfigureUnitManager cum = sc.getBean(ConfigureUnitManager.class);
        ConfigureUnit cu = null;
        try {
            cu = cum.getConfigureUnit(SQLDataSource.SERVICE_PID);
        } catch (IOException e) {
            throw new java.lang.IllegalStateException( e);
        }
        if(cu!=null){
            DataTypeMap types= cu.getProperties();
            return types.getString(dbName+".database.type");
        }
        return null;
    }

    /**
     * @param dbName
     * @return
     * @throws SlxException
     */
    private synchronized SqlSessionFactoryHolder createSessionForDb(String environment,
        Map<String, Object> properties) throws SlxException {
        DataTypeMap config = getConfig();
        DataTypeMap dbConfig = config.getSubtree(environment);
        try {
            String configLocation = dbConfig.getString("mybatis.config.file");
            String dbName = dbConfig.getString("mybatis.dbname").trim();
            String dbType = getDatabaseType(dbName);
            String mapperScan = dbConfig.getString("mybatis.mapper.scan.base");
            Configuration configuration;
            XMLConfigBuilder xmlConfigBuilder = null;
            if (DataUtils.isNotNullAndEmpty(configLocation)) {
                InputStream inputStream = Resources.getResourceAsStream(configLocation);
                xmlConfigBuilder = new XMLConfigBuilder(inputStream, null,
                    createPropertiesFromMap(properties));
                configuration = xmlConfigBuilder.getConfiguration();
            } else {
                throw new SlxException(
                    "Failed to instance Mybatis Configuration,because of resource file is null");
            }
            configuration.addInterceptor(new PageInterceptor());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Registered plugin: '" + PageInterceptor.class.getName() + "'");
              }
            if (xmlConfigBuilder != null) {
                try {
                    xmlConfigBuilder.parse();

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Parsed configuration file: '"
                            + configLocation + "'");
                    }
                } catch (Exception ex) {
                    throw new SlxException(Tmodule.DATASOURCE,
                        Texception.DS_DSCONFIG_ERROR,
                        "Failed to parse config resource: " + configLocation,
                        ex);
                } finally {
                    ErrorContext.instance().reset();
                }
            }
            ConnectionManager conn = getConnectionManager(sc);
            DataSource dataSource = conn.getDataSource(dbName);
            Environment env = new Environment(environment,
                new JdbcTransactionFactory(), dataSource);
            configuration.setEnvironment(env);
            List<org.springframework.core.io.Resource> mapperLocations = getMapperResources(mapperScan);
            if (!mapperLocations.isEmpty()) {
                for (org.springframework.core.io.Resource mapperLocation : mapperLocations) {
                    if (mapperLocation == null) {
                        continue;
                    }

                    try {
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(
                            mapperLocation.getInputStream(), configuration,
                            mapperLocation.toString(),
                            configuration.getSqlFragments());
                        xmlMapperBuilder.parse();
                    } catch (Exception e) {
                        throw new IOException(
                            "Failed to parse mapping resource: '"
                                + mapperLocation + "'", e);
                    } finally {
                        ErrorContext.instance().reset();
                    }

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Parsed mapper file: '" + mapperLocation
                            + "'");
                    }
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Property 'mapperLocations' was not specified or no matching resources found");
                }
            }

            SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
            SqlSessionFactory factory = builder.build(configuration);
            return new SqlSessionFactoryHolder(factory, dbType);
        } catch (IOException e) {
            throw new SlxException("Failed to create mybatis sqlSessionFactory",e);
        }
    }

    private List<org.springframework.core.io.Resource> getMapperResources(
        String mapperLocations) {
        List<org.springframework.core.io.Resource> resources = new ArrayList<org.springframework.core.io.Resource>();
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
                Thread.currentThread().getContextClassLoader());

            Collections.addAll(resources,
                resolver.getResources(mapperLocations));

        } catch (IOException ex) {
            // ignore
        }
        return resources;
    }

    protected ConnectionManager getConnectionManager(SystemContext sc) {
        return sc.getBean(ConnectionManager.class);

    }

    public static Properties createPropertiesFromMap(Map<String, Object> propMap) {
        if (propMap == null)
            return null;
        Properties result = new Properties();
        for (Iterator<String> i = propMap.keySet().iterator(); i.hasNext();) {
            String key = i.next();
            Object val = propMap.get(key);
            if (key != null && val != null)
                result.put(key, val);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.mybatis.SqlSessionFactoryProvider#createSqlSessionFactory(java.lang.String,
     *      java.util.Map)
     */
    @Override
    public SqlSessionFactory createSqlSessionFactory(String environment) throws SlxException{
        return createSqlSessionFactory(environment, null);
    }
    
    private class SqlSessionFactoryHolder{
         SqlSessionFactory factory;
         String dbType;
         SqlSessionFactoryHolder(SqlSessionFactory factory,String dbType){
             this.factory=factory;
             this.dbType=dbType;
         }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.mybatis.SqlSessionFactoryProvider#getDbType(java.lang.String)
     */
    @Override
    public String getDbType(String environment) {
        SqlSessionFactoryHolder sessionFactory = _tempCache.get(environment);
        if (sessionFactory != null) {
            return sessionFactory.dbType;
        }
        DataTypeMap config;
        try {
            config = getConfig();
        } catch (SlxException e) {
           throw new java.lang.IllegalStateException("Mybatis config is null",e);
        }
        DataTypeMap dbConfig = config.getSubtree(environment);
        return dbConfig.getString("mybatis.dbtype").trim();
    }
}
