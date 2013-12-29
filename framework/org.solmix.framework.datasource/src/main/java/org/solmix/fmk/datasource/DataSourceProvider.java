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

package org.solmix.fmk.datasource;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.context.SystemContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceData;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.api.datasource.ParserHandler;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.EserverType;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.SlxContext;
import org.solmix.fmk.cache.StructCache;
import org.solmix.fmk.event.EventWorker;
import org.solmix.fmk.event.EventWorkerFactory;
import org.solmix.fmk.internal.DatasourceCM;

/**
 * Initialize Datasource.all datasource initialized thought {@link #forName(String)}
 * 
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-26 solmix-ds
 */
public class DataSourceProvider
{

    private static ParserHandler parser;

    private static Logger log = LoggerFactory.getLogger(DataSourceProvider.class.getName());

    public static AtomicLong totalInitialDS = new AtomicLong();

    private static AtomicLong maxInitialTime = new AtomicLong();

    /**
     * <b><li>Manager Method:</b> the number of the ds initialized with runtime.
     * 
     * @return
     */
    public static long getTotalInitialDS() {
        return totalInitialDS.longValue();
    }

    /**
     * <b><li>Manager Method:</b>
     * 
     * @return
     */
    public static long getMaxInitialTime() {
        return maxInitialTime.longValue();
    }

    private static void setMaxInitialTime(long time) {
        if (maxInitialTime.longValue() < time)
            maxInitialTime.set(time);

    }

    public static DataSource forName(String dsName) throws SlxException {
       
        return forName(null,dsName, null, null);
    }

    public static DataSource forName(SystemContext sc,String dsName, DSRequest request) throws SlxException {
        return forName(sc,dsName, null, request);
    }

    /**
     * @param dsName Name with group name.
     * @param string
     * @return
     * @throws SlxException
     */
    public static DataSource forName(SystemContext sc,String dsName, String repoName, DSRequest request) throws SlxException {
       if(sc==null){
           sc= SlxContext.getThreadSystemContext();
       }
        DataSource _datasource = null;
        /**
         * find datasource without configure file.
         */
        Object obj = StructCache.getCacheObject(dsName);
        if (obj instanceof DataSource) {
            return (DataSource) obj;
        }
        /**
         * Parser ds configuration file Before initial the datasource.
         */
        ParserHandler _parser = getParserHander(sc);
        long __start = new Date().getTime();
        DataSourceData data = (DataSourceData) _parser.parser(repoName, dsName, ParserHandler.DS_SUFFIX, request);
        if (data == null) {
            if (log.isErrorEnabled())
                log.error("can't parser datasource:" + dsName + " from datasource  from  " + (repoName==null?"all":repoName) + " repository:. the request will breakup");
            return null;
        }
        EserverType _dsType = data.getServerType();
        if (_dsType == null) {
            String tableName = data.getTdataSource().getTableName();
            if (DataUtil.isNotNullAndEmpty(tableName))
                _dsType = EserverType.SQL;
        }
        // no set, default is basic
        if (_dsType == null) {
            if (log.isWarnEnabled())
                log.warn("NO set server-type used the default build in basic  server-type");
            _dsType = EserverType.BASIC;
        }

        List<DataSource> dsList = sc.getBean(DataSourceManager.class).getProviders();
        for (DataSource provider : dsList) {
            if (provider.getServerType().equals(_dsType.value())) {
                _datasource = provider.instance(data);
                break;
            }
        }
        if (_datasource == null) {
            log.warn("Can not found corresponding server-type implemention for [" + _dsType + "] used the build in implementation(BasicDataSource)");
            _datasource = new BasicDataSource().instance(data);
        }

        /**
         * validation the ds.
         */
        boolean isPass = ValidationContext.instance().validate(_datasource);
        long __end = new Date().getTime();
        fireTimeEvent(__end - __start, "initial datasource:" + data.getName());
        setMaxInitialTime(__end - __start);
        if (isPass)// passed validation,continue
        {
            totalInitialDS.incrementAndGet();
            return _datasource;

        } else
            // interrupt ,return null
            return null;
    }
    private static void fireTimeEvent(long time,String msg){
        EventWorkerFactory factory= EventWorkerFactory.getInstance();
        EventWorker worker= factory.createWorker(SlxContext.getThreadSystemContext());
        worker.createAndFireTimeEvent(time, msg);
    }

    /**
     * Get the Single datasource ParserHandler instance.
     * @return Single instance.
     */
    public synchronized static ParserHandler getParserHander(SystemContext sc) {
        if (parser == null) {
            String defaultParser =DatasourceCM.DEFAULT_PARSER;
            if (defaultParser.equals("default")) {
                parser = new DefaultParser(sc);
            }
            // XXX Extension point.
        }
        return parser;
    }

    /**
     * @param parser the parser to set
     */
    public synchronized static void setParser(ParserHandler parser) {
        DataSourceProvider.parser = parser;
    }

    
}
