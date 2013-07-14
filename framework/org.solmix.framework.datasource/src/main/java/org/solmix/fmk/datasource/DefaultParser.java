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

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.api.data.DataSourceData;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.ParserHandler;
import org.solmix.api.event.IValidationEvent.Level;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.EserverType;
import org.solmix.api.jaxb.Module;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.repo.DSRepository;
import org.solmix.api.serialize.JSParser;
import org.solmix.api.serialize.JSParserFactory;
import org.solmix.api.serialize.XMLParser;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.io.SlxFile;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.context.SlxContext;
import org.solmix.fmk.event.EventUtils;
import org.solmix.fmk.serialize.JSParserFactoryImpl;
import org.solmix.fmk.serialize.JaxbXMLParserImpl;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-26 solmix-ds
 */
public class DefaultParser implements ParserHandler
{

    private static final Logger log = LoggerFactory.getLogger(DefaultParser.class.getName());

    public static final String INHERIT_KEY = "_inheritsForm";

    public static final String DEFAULT_REPO = "default";

    public static final String DEFAULT_REPO_SUFFIX = "ds";

    protected DataSourceData data;

    protected static XMLParser xmlParser;

    private static AtomicLong numParsered;

    protected DSRequest dsRequest;

    protected final JSParser jsParser;

    public DefaultParser()
    {
        numParsered = new AtomicLong();
        JSParserFactory jsFactory = JSParserFactoryImpl.getInstance();
        jsParser = jsFactory.get();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.ParserHandler#parser(java.lang.String)
     */
    @Override
    public Object parser(String dsName) throws SlxException {
        return parser(DEFAULT_REPO, dsName, DEFAULT_REPO_SUFFIX, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.ParserHandler#parser(java.lang.String, java.lang.String)
     */
    @Override
    public Object parser(String repoName, String dsName) throws SlxException {

        return parser(repoName, dsName, DEFAULT_REPO_SUFFIX, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @throws SlxException
     * 
     * @see org.solmix.api.datasource.ParserHandler#parser(java.lang.String, java.lang.String,
     *      org.solmix.api.datasource.DSRequest)
     */
    @Override
    public Object parser(String repoName, String dsName, DSRequest request) throws SlxException {
        return parser(repoName, dsName, DEFAULT_REPO_SUFFIX, request);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.datasource.ParserHandler#parser(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Object parser(String repoName, /* String group, */String dsName, String suffix, DSRequest request) throws SlxException {
        long _$ = System.currentTimeMillis();
        String __info = ">>Parser datasource [" + dsName + "]";
        log.debug(__info);
        this.dsRequest = request;
        DSRepository repo = DefaultDataSourceManager.getRepoService().loadDSRepo(repoName);
        //
        String explicitName = DataUtil.isNullOrEmpty(suffix) ? dsName : dsName + "." + suffix.toLowerCase().trim();

        /*********************************************
         * Loading datasource from configuration file.*
         *********************************************/
        Object obj = repo.load(explicitName);

        // begin
        if (obj == null)
            return null;
        else if (obj instanceof SlxFile) {
            SlxFile slx = (SlxFile) obj;
            if (xmlParser == null)
                xmlParser = new JaxbXMLParserImpl();
            Module module = xmlParser.unmarshalDS(slx);
            TdataSource td = module.getDataSource();
            if (numParsered == null)
                numParsered = new AtomicLong();
            numParsered.incrementAndGet();
            String ID = td.getID();
            /************************************
             * construct explicable ID with group name.
             ************************************/
            data = new DataSourceData(td);
            if (ID != null && !ID.equals(dsName)) {
                td.setID(dsName);
                String info = (new StringBuilder()).append("dsName case sensitivity mismatch - looking for: ").append(dsName).append(", but got: ").append(
                    ID).toString();
                EventUtils.createAndFireDSValidateEvent(Level.ERROR, info, new IllegalArgumentException());
            }
            try {
                data.setDsConfigFile(slx.getCanonicalPath());
                data.setConfigTimestamp(slx.lastModified());
            } catch (IOException e) {
                throw new SlxException(Tmodule.DATASOURCE, Texception.DS_DSFILE_NOT_FOUND, null, e);
            }
        } else {
            log.error("ds-config java object error,the return ds-config object should be SlxFile.");
            throw new SlxException(Tmodule.DATASOURCE, Texception.DS_DSCONFIG_OBJECT_TYPE_ERROR,
                "ds-config java object error,the return ds-config object should be SlxFile.");
        }
        preBuild(data);
        SlxContext.getEventManager().postEvent(EventUtils.createTimeMonitorEvent(System.currentTimeMillis() - _$, __info));
        return data;
    }

    protected DataSourceData preBuild(DataSourceData data) throws SlxException {
        /************************************************************
         * customer configuration.DataSource.${serverType}.QName=xxxN.
         ************************************************************/
        /*
         * if (data != null) { EserverType serverType = data.getTdataSource().getServerType(); serverType = serverType
         * == null ? EserverType.BASIC : serverType; DataTypeMap map = OSGIHelper.getCM().getSubtree("DataSource"); Map
         * custconfig = DataUtil.getSubtreePrefixed(serverType.value(), map); data.setCustomerConfig(custconfig); }
         */
        /*********************************************************
         * auto generating datasource schema.
         *********************************************************/
        /*
         * if (DataUtil.booleanValue(data.getTdataSource().isAutoDeriveSchema())) autoGenerateSchema();
         */
        customerValidation();
        return data;
    }

    protected void customerValidation() throws SlxException {
        TdataSource td = data.getTdataSource();
        String dsID = data.getTdataSource().getID();
        if (data.getName() == null) {
            EventUtils.createAndFireDSValidateEvent(Level.WARNING, "Datasource configuration with no ID", null);
        }
        // data.addValidationEvent(new DSValidation(Level.WARNING, "Datasource configuration with no ID"));
        if (td.getServerType() == EserverType.SQL && data.getTdataSource().getTableName() == null) {
            data.getTdataSource().setTableName(dsID);
            String __info = "SQL DataSource with no set TableName try to use ID as tableName";
            EventUtils.createAndFireDSValidateEvent(Level.DEBUG, __info, null);
            // data.addValidationEvent(new DSValidation(Level.DEBUG, __info));
            // if (log.isDebugEnabled())
            // log.debug(__info);
        }
        data.setTdataSource(td);

    }

    /**
     * <b><li>Manager Method:</b>
     * 
     * @return the numParsered
     */
    public static long getNumParsered() {
        return numParsered.longValue();
    }

    /**
     * Only get the super datasource.
     */
    protected void parserSuper() {
        String _superDSName = data.getSuperDSName();
        DataSource _superDS = data.getSuperDS();
        String __vinfo;
        if (_superDSName != null && _superDS == null) {
            try {
                __vinfo = new StringBuilder().append("Looking up superDS of DataSource ").append(data.getName()).append(": '").append(_superDS).append(
                    "'").toString();
                EventUtils.createAndFireDSValidateEvent(Level.DEBUG, __vinfo, null);
                // data.addValidationEvent(new DSValidation(Level.DEBUG, __vinfo));
                _superDS = DefaultDataSourceManager.getDataSource(_superDSName, dsRequest);
                if (_superDS == null) {
                    data.setSuperDSName(null);
                    __vinfo = (new StringBuilder()).append("DataSource ").append(data.getName()).append(" declared to inherit from DataSource ").append(
                        _superDSName).append(" which could not be loaded.set superDSName=null").toString();
                    EventUtils.createAndFireDSValidateEvent(Level.DEBUG, __vinfo, null);
                    // data.addValidationEvent(new DSValidation(Level.WARNING, __vinfo));
                } else {
                    data.setSuperDS(_superDS);
                }
            } catch (Exception e) {
                if (log.isWarnEnabled())
                    log.warn(
                        (new StringBuilder()).append("Exception loading current DataSource [").append(data.getName()).append("]'s super ds [").append(
                            _superDSName).append("]: ").toString(), e);
            } finally {
                if (_superDS != null)
                    DefaultDataSourceManager.freeDataSource(_superDS);
            }
        }
    }

}
