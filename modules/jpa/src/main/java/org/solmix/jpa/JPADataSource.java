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

package org.solmix.jpa;

import static org.solmix.commons.util.DataUtil.isNotNullAndEmpty;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.call.DSCall;
import org.solmix.api.call.DSCallCompleteCallback;
import org.solmix.api.context.SystemContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSRequestData;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DSResponse.Status;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceData;
import org.solmix.api.datasource.DataSourceGenerator;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Efield;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.EserverType;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.base.Reflection;
import org.solmix.fmk.datasource.BasicDataSource;
import org.solmix.fmk.datasource.BasicGenerator;
import org.solmix.fmk.datasource.DSResponseImpl;
import org.solmix.fmk.datasource.DefaultDataSourceManager;
import org.solmix.fmk.util.DataTools;

/**
 * JPA datasource.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2011-6-6
 */

public class JPADataSource extends BasicDataSource implements DataSource, DSCallCompleteCallback
{

    private final static Logger log = LoggerFactory.getLogger(JPADataSource.class.getName());

    public static final String SERVICE_PID = "org.solmix.modules.jpa";


    String entityName = null;

    Class<?> entityClass = null;

    private boolean useQualifiedClassName;

    private boolean shouldRollBackTransaction;

    protected EntityManagerHolder holder;

    private int batchInsertSize = 100;

    private EntityManager entityManager;

    private Object transaction;

    private EntityManagerFactoryProvider entityManagerFactoryProvider;

    private enum QueryType
    {
        ENTITY , NATIVE_QUERY , OTHER;
    }

    /**
     * This Constructor used by inject,such as spring guice.Coding instance call
     * {@link org.solmix.jpa.JPADataSource#instance(DataSourceData)}
     * 
     * @throws SlxException
     */
    public JPADataSource() throws SlxException
    {

    }

    /**
     * This construction is always called by injector,such as spring, blueprint etc. Please use
     * {@link org.solmix.jpa.JPADataSource#instance(DataSourceData) instance } to init datasource.
     * 
     * @param sc
     */
    public JPADataSource(SystemContext sc)
    {
        setSystemContext(sc);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.fmk.datasource.BasicDataSource#getServerType()
     */
    @Override
    public String getServerType() {
        return EserverType.JPA.value();
    }

    /**
     * @return the entityManagerFactoryProvider
     */
    public EntityManagerFactoryProvider getEntityManagerFactoryProvider() {
        return entityManagerFactoryProvider;
    }

    /**
     * @param entityManagerFactoryProvider the entityManagerFactoryProvider to set
     */
    public void setEntityManagerFactoryProvider(EntityManagerFactoryProvider entityManagerFactoryProvider) {
        this.entityManagerFactoryProvider = entityManagerFactoryProvider;
    }

    @Override
    public void init(DataSourceData data) throws SlxException {
        super.init(data);
        String entityBean = getContext().getTdataSource().getBean();
        if (entityBean != null) {
            try {
                entityClass = BasicGenerator.loadClass(entityBean);
                Entity e = entityClass.getAnnotation(Entity.class);
                entityName = e == null ? null : e.name();
                if (DataUtil.isNullOrEmpty(entityName))
                    entityName = entityClass.getName().substring(entityClass.getName().lastIndexOf(".") + 1);
                entityName=new StringBuilder().append('_').append(entityName).toString();
            } catch (Exception e) {
                throw new SlxException(Tmodule.JPA, Texception.NO_FOUND, e);
            }
        }

    }

    protected EntityManagerFactory getEmf(DataSourceData data) throws SlxException {
        String persistenceUnit = data.getTdataSource() == null ? null : data.getTdataSource().getPersistenceUnit();
        if (persistenceUnit == null)
            persistenceUnit = getConfig().getString(JpaCM.P_DEFAULT_UNIT, "default");

        DataTypeMap pconfig = getConfig().getSubtree(new StringBuilder().append(JpaCM.P_UNIT_PREFIX).append(persistenceUnit).toString());
        if (pconfig.isEmpty()) {
            return this.entityManagerFactoryProvider.createEntityManagerFactory(persistenceUnit);
        } else {
            return this.entityManagerFactoryProvider.createEntityManagerFactory(persistenceUnit, pconfig);
        }

    }

    public void destroy() {
        if (log.isTraceEnabled())
            log.trace("JPADataSource:" + this.getContext().getName() + " destroying!");
    }

    /**
     * @return the entityManager
     * @throws SlxException
     */
    public synchronized EntityManager getEntityManager() throws SlxException {
        if (entityManager == null || !entityManager.isOpen()) {
            entityManager = JPATransaction.getEntityManager(getEmf(data));
        }
        return entityManager;
    }

    /**
     * @param entityManager the entityManager to set
     */
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * @return the batchInsertSize
     */
    public int getBatchInsertSize() {
        return batchInsertSize;
    }

    /**
     * @param batchInsertSize the batchInsertSize to set
     */
    public void setBatchInsertSize(int batchInsertSize) {
        this.batchInsertSize = batchInsertSize;
    }

    @Override
    public DataSource instance(DataSourceData data) throws SlxException {
        JPADataSource ds = new JPADataSource(sc);
        if (this.getEntityManagerFactoryProvider() != null) {
            ds.setEntityManagerFactoryProvider(getEntityManagerFactoryProvider());
        }
        ds.init(data);
        return ds;
    }

  /*  protected void adaptProvider(DataSourceData context) throws SlxException {
        String unit = context == null ? null : context.getTdataSource() == null ? null : context.getTdataSource().getPersistenceUnit();
        if (unit == null) {
            unit = getConfig().getString("default.persistenceUnit", "default");
        }
        Object[] objs = ServiceUtil.getOSGIServices(EntityManagerFactory.class.getName(), "(osgi.unit.name=" + unit + ")");
        EntityManagerFactory factory = objs == null ? null : objs.length >= 1 ? (EntityManagerFactory) objs[0] : null;
    }
*/
    @Override
    public DSResponse execute(DSRequest req) throws SlxException {
        req.registerFreeResourcesHandler(this);
        shouldRollBackTransaction = false;
        holder = null;
        Eoperation _opType = req.getContext().getOperationType();
        DSResponse __return = null;
        if (isJpaOperation(_opType)) {
            DSResponse validationFailure = validateDSRequest(req);
            if (validationFailure != null) {
                return validationFailure;
            }
            //when used FetchType.LAZY,the transaction must be commit after data send to client.
            if(DataUtil.booleanValue(req.getContext().getIsClientRequest())){
                req.setFreeOnExecute(false);
            }
            // if DSRequest not have a DataSource with it,use this by default.
            if (req.getDataSource() == null && req.getDataSourceName() == null) {
                req.setDataSource(this);
            }
            req.setRequestStarted(true);
            Object dsObject = null;
            Object datasources = req.getContext().getDataSourceNames();
            // may be have other datasource.if just one,used as SQL datasource.
            if (datasources != null && (datasources instanceof List<?> && ((List<?>) datasources).size() > 1)) {
                dsObject = datasources;
            } else {
                dsObject = this;
            }
            __return = executeJpaDataSource(req, dsObject);
        } else {
            __return = super.execute(req);
        }

        return __return;
    }

    private static JPADataSource[] getDataSources(List<?> list) throws SlxException {
        List<JPADataSource> _return = new ArrayList<JPADataSource>();
        if (list == null)
            return null;
        for (Object ds : list) {
            if (ds instanceof JPADataSource) {
                _return.add((JPADataSource) ds);
            } else {
                DataSource datasource = DefaultDataSourceManager.getDataSource((String) ds);
                if (datasource instanceof JPADataSource) {
                    _return.add((JPADataSource) datasource);
                } else {
                    log.warn("the datasource [" + ds.toString() + "] cannot processed by JPA DataSource.");
                }
            }
        }
        return _return.toArray(new JPADataSource[_return.size()]);
    }
    /**
     * @param _opType
     * @return
     */
    private boolean isJpaOperation(Eoperation operationType) {
        return DataTools.isFetch(operationType) || DataTools.isAdd(operationType) || DataTools.isRemove(operationType)
            || DataTools.isUpdate(operationType) || DataTools.isReplace(operationType);
    }


    @Override
    protected String getPID() {
        return SERVICE_PID;
    }

    @Override
    public DSResponse executeRemove(DSRequest req) throws SlxException {
        DSResponse __return = new DSResponseImpl();
        __return.setStatus(Status.STATUS_SUCCESS);
        __return.setDataSource(req.getDataSource());
        String pk = data.getPrimaryKey();
        Tfield pkField = data.getField(pk);
        if (pkField == null) {
            log.error("field:[" + pk + "]  is not defined in datasource");
            throw new SlxException(Tmodule.JPA, Texception.DS_UPDATE_WITHOUT_PK, "field:[" + pk + "]  is not defined in datasource");
        }
        Serializable id = (Serializable) req.getContext().getFieldValue(pk);
        String xPath = pkField.getValueXPath();
        if (xPath != null)
            pk = xPath.replace('/', '.');
        Object p = null;
        try {
            p = DataUtil.castValue(id, DataUtil.getPropertyType(entityClass, pk));
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        Object record = entityManager.find(entityClass, p);
        entityManager.remove(record);
        __return.setRawData(req.getContext().getCriteria());
        increaseOpCount();
        return __return;
    }

    @Override
    public DSResponse executeUpdate(DSRequest req) throws SlxException {
        DSResponse __return = new DSResponseImpl();
        __return.setStatus(Status.STATUS_SUCCESS);
        __return.setDataSource(req.getDataSource());
        String pk = data.getPrimaryKey();
        Tfield pkField = data.getField(pk);
        if (pkField == null) {
            log.error("field:[" + pk + "]  is not defined in datasource");
            throw new SlxException(Tmodule.JPA, Texception.DS_UPDATE_WITHOUT_PK, "field:[" + pk + "]  is not defined in datasource");
        }
        Serializable id = (Serializable) req.getContext().getFieldValue(pk);
        String xPath = pkField.getValueXPath();
        if (xPath != null)
            pk = xPath.replace('/', '.');
        Object p = null;
        try {
            p = DataUtil.castValue(id, DataUtil.getPropertyType(entityClass, pk));
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        Object record = entityManager.find(entityClass, p);
        try {
            DataUtil.setProperties(req.getContext().getValues(), record);
        } catch (Exception e) {
            e.printStackTrace();
        }
        __return.setRawData(record);
        increaseOpCount();
        return __return;

    }

    /**
     * Jpa Transaction Object Key is <code>_slx_jpa_entityManager_key</code>
     */
    @Override
    public String getTransactionObjectKey() throws SlxException {
        return "_slx_jpa_entityManager_key";
    }

   
    @Override
    public void onSuccess(DSCall rpcmanager) throws SlxException {
        Object obj = rpcmanager.getAttribute(getTransactionObjectKey());
        if (obj == null) {
            log.warn("Transaction Object is null !");
            return;
        }
        if (!(obj instanceof EntityManagerHolder)) {
            throw new SlxException(Tmodule.JPA, Texception.OBJECT_TYPE_NOT_ADAPTED,
                "rpc manager does not hold instance of EntityManagerHolder for jpa datasource");
        }
        EntityManagerHolder holder = (EntityManagerHolder) obj;
        try {
            log.debug("committing transaction for" + holder.getOpCount() + "queued operation(s)");
            JPATransaction.commitTansaction(holder.getTransaction());
        } catch (Exception e) {
            JPATransaction.rollbackTransaction(transaction);
            log.error("Failed to commit transaction,Rolling back", e);
        }
        try {
            JPATransaction.returnEntityManager(holder.getEntityManager());
        } catch (Exception e) {
        }
    }

    @Override
    public void onFailure(DSCall rpcmanager, boolean flag) throws SlxException {
        Object obj = rpcmanager.getAttribute(getTransactionObjectKey());
        if (obj == null) {
            log.warn("Transaction Object is null !");
            return;
        }
        if (!(obj instanceof EntityManagerHolder)) {
            throw new SlxException(Tmodule.JPA, Texception.OBJECT_TYPE_NOT_ADAPTED,
                "rpc manager does not hold instance of EMHooker for jpa datasource");
        }
        EntityManagerHolder holder = (EntityManagerHolder) obj;
        try {
            if (log.isTraceEnabled())
                log.trace("rolling back transaction for" + holder.getOpCount() + "queued operation(s)");
            JPATransaction.rollbackTransaction(holder.getTransaction());
            JPATransaction.returnEntityManager(holder.getEntityManager());
        } catch (Exception e) {
            throw new SlxException(Tmodule.JPA, Texception.JPA_JPAEXCEPTION, e);
        }
    }

    @Override
    public void freeResources() {
        if (holder == null) {
            if (shouldRollBackTransaction) {
                if (log.isTraceEnabled())
                    log.trace("rolling back transaction!");
                JPATransaction.rollbackTransaction(transaction);
            } else {
                try {
                    if (log.isTraceEnabled())
                        log.trace("committing transaction");
                    JPATransaction.commitTansaction(transaction);
                } catch (Exception ex) {
                    JPATransaction.rollbackTransaction(transaction);
                    log.error("Failed to commit transaction,Rolling back", ex);
                }
            }
            try {
                JPATransaction.returnEntityManager(entityManager);
            } catch (Exception ignore) {
            }
        }
        super.freeResources();
    }

    /**
     * @return the dataSourceGenerator
     */
    @Override
    public synchronized DataSourceGenerator getDataSourceGenerator() {
        if (dataSourceGenerator == null)
            dataSourceGenerator = new JPADataSourceGenerator(this);
        return dataSourceGenerator;
    }

    /**
     * @param dataSourceGenerator the dataSourceGenerator to set
     */
    @Override
    public void setDataSourceGenerator(DataSourceGenerator dataSourceGenerator) {
        this.dataSourceGenerator = dataSourceGenerator;
    }

    public void increaseOpCount() {
        if (holder != null)
            holder.increaseOpCount();
    }

    public void markTrnsactionForRollBack(DSResponse resp) {
        this.shouldRollBackTransaction = true;
        if (resp != null)
            resp.setStatus(Status.STATUS_FAILURE);
        log.debug("mark transaction for roll back");
    }

    /**
     * @return the useQualifiedClassName
     */
    public boolean isUseQualifiedClassName() {
        return useQualifiedClassName;
    }

    /**
     * @param useQualifiedClassName the useQualifiedClassName to set
     */
    public void setUseQualifiedClassName(boolean useQualifiedClassName) {
        this.useQualifiedClassName = useQualifiedClassName;
    }

    /**
     * Execute Sql operation,default is Fetch/Add/Replace/Update/Remove
     * 
     * @param req
     * @param dsObject
     * @return
     * @throws SlxException
     */
    private DSResponse executeJpaDataSource(DSRequest req, Object dsObject) throws SlxException {
        JPADataSource[] datasources;
        if (dsObject instanceof JPADataSource) {
            datasources = new JPADataSource[1];
            datasources[0] = (JPADataSource) dsObject;
        } else if ((dsObject instanceof String)) {
            datasources = getDataSources(DataUtil.makeListIfSingle(dsObject));
        } else if (dsObject instanceof List<?>) {
            datasources = getDataSources((List<?>) dsObject);
        } else {
            throw new SlxException(Tmodule.DATASOURCE, Texception.DS_DSCONFIG_ERROR,
                "in the app operation config, datasource must be set to a string or list");
        }
        if (req.getDSCall() != null && this.shouldAutoJoinTransaction(req)) {
            log.debug("Auto get transaction object!");
            Object obj = this.getTransactionObject(req);
            if (!(holder instanceof EntityManagerHolder)) {
                if (log.isWarnEnabled())
                    log.warn("JPA DataSource transaction holer should be a org.solmix.jpa.EntityManagerHolder instance,but is"
                        + obj.getClass().getName() + " Assume the transaction object is invalid and set it to null");
                holder = null;
            } else {
                holder = (EntityManagerHolder) obj;
            }
            if (holder == null) {
                if (shouldAutoStartTransaction(req, false)) {
                    try {
                        transaction = JPATransaction.getTransaction(getEntityManager());
                    } catch (Exception e) {
                        log.error("Unexpected exception while initial entityManager", e);
                    }
                    log.debug("Creating EntityManager, starting transaction and setting it to DSCall.");
                    holder = new EntityManagerHolder(this, entityManager, transaction);
                    req.getDSCall().setAttribute(getTransactionObjectKey(), holder);
                    req.getDSCall().registerCallback(this);
                } else {
                    try {
                        transaction = JPATransaction.getTransaction(getEntityManager());
                    } catch (Exception e) {
                        log.error("Unexpected exception while initial entityManager", e);
                    }
                }
            } else {
                entityManager = holder.getEntityManager();
                transaction = holder.getTransaction();
            }
            req.setJoinTransaction(true);
        } else {
            try {
                transaction = JPATransaction.getTransaction(getEntityManager());
            } catch (Exception e) {
                log.error("Unexpected exception while initial entityManager", e);
            }
        }
        DSRequestData __requestCX = req.getContext();
        Eoperation _req = __requestCX.getOperationType();
        DSResponse __return = null;
        switch (_req) {
            case ADD:
                __return = addEntity(req, datasources);
                break;
            case FETCH:
                __return = fetchEntity(req, datasources);
                break;
            case REMOVE:
                __return = removeEntity(req, datasources);
                break;
            case UPDATE:
                __return = updateEntity(req, datasources);
                break;
            default:
                break;

        }
        return __return;
    }

    private DSResponse updateEntity(DSRequest req, JPADataSource[] datasources) throws SlxException {
        JPADataSource __firstDS = datasources[0];
        DSRequestData __requestCX = req.getContext();
        DSResponse __return = new DSResponseImpl();
        __return.setStatus(Status.STATUS_SUCCESS);
        __return.setDataSource(req.getDataSource());
        Object criteria = req.getContext().getRawValues();
        int batchsize = __requestCX.getBatchSize();
        // batch size.
        batchsize = batchsize < 100 ? 100 : batchsize;
        if (isEntityPresent(criteria)) {
            List<?> records = DataUtil.makeListIfSingle(criteria);
            Object result = updateBean(batchsize, records);
            __return.setRawData(result);
            return __return;
        }
        // check jpql configured
        ToperationBinding _op = __firstDS.getContext().getOperationBinding(req);
        if (_op != null && _op.getQueryClauses() != null && _op.getQueryClauses().getCustomQL() != null) {
            // velocity exp
            return __return;
        }
        if (isEntityClass(entityClass)) {
            List<?> records = req.getContext().getValueSets();
            List<Object> beans = new ArrayList<Object>();
            for (Object o : records) {
                Object bean = instance(entityClass);
                try {
                    DataUtil.setProperties((Map<?,?>) o, bean, false);
                } catch (Exception e) {
                    String __msg = "invoke bean class:[" + bean.getClass().getName() + "] exception";
                    throw new SlxException(Tmodule.JPA, Texception.INVOKE_EXCEPTION, __msg);
                }
                beans.add(bean);
            }
            Object result = updateBean(batchsize, beans);
            __return.setRawData(result);
            return __return;

        } else {
            throw new SlxException(Tmodule.JPA, Texception.JPA_NO_ENTITY, "JPA DataSource no configured Entity bean");
        }
    }

    private DSResponse removeEntity(DSRequest req, JPADataSource[] datasources) throws SlxException {
        JPADataSource __firstDS = datasources[0];
        DSRequestData __requestCX = req.getContext();
        DSResponse __return = new DSResponseImpl();
        __return.setStatus(Status.STATUS_SUCCESS);
        __return.setDataSource(req.getDataSource());
        Object criteria = req.getContext().getRawCriteria();
        int batchsize = __requestCX.getBatchSize();
        // batch size.
        batchsize = batchsize < 100 ? 100 : batchsize;
        if (isEntityPresent(criteria)) {
            List<?> records = DataUtil.makeListIfSingle(criteria);
            Object result = removeBean(batchsize, records);
            __return.setRawData(result);
            return __return;
        }
        // check jpql configured
        ToperationBinding _op = __firstDS.getContext().getOperationBinding(req);
        if (_op != null && _op.getQueryClauses() != null && _op.getQueryClauses().getCustomQL() != null) {
            Query query=  entityManager.createQuery(_op.getQueryClauses().getCustomQL());
            query.executeUpdate();
            Map<String, Object> c =req.getContext().getCriteria();
            if(c!=null){
                
            }
            return __return;
        }
        if (isEntityClass(entityClass)) {
            List<?> records = req.getContext().getValueSets();
            List<Object> beans = new ArrayList<Object>();
            for (Object o : records) {
                Object bean = instance(entityClass);
                try {
                    DataUtil.setProperties((Map<?,?>) o, bean, false);
                } catch (Exception e) {
                    String __msg = "invoke bean class:[" + bean.getClass().getName() + "] exception";
                    throw new SlxException(Tmodule.JPA, Texception.INVOKE_EXCEPTION, __msg);
                }
                beans.add(bean);
            }
            Object result = removeBean(batchsize, beans);
            __return.setRawData(result);
            return __return;

        } else {
            throw new SlxException(Tmodule.JPA, Texception.JPA_NO_ENTITY, "JPA DataSource no configured Entity bean");
        }
    }

    private DSResponse fetchEntity(DSRequest req, JPADataSource[] datasources) throws SlxException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        StringBuffer whereClause = new StringBuffer();
        StringBuffer orderClause = new StringBuffer();
        if (!isEntityClass(entityClass)) {
            throw new SlxException(Tmodule.JPA, Texception.JPA_NO_ENTITY, "JPA DataSource no configured Entity bean");
        }
        DSResponse __return = new DSResponseImpl();
        __return.setStatus(Status.STATUS_SUCCESS);
        __return.setDataSource(req.getDataSource());
        Map<String, Object> criteria = req.getContext().getCriteria();
        if (criteria != null) {
            if (isAdvancedCriteria(criteria)) {
                throw new SlxException(Tmodule.JPA, Texception.NO_SUPPORT, "JPA datasource has't supported advance criteria yet");
            }
            for (Object obj : criteria.keySet()) {
                String fieldName = (String) obj;
                Object value = criteria.get(obj);
                Tfield _f = data.getField(fieldName);
                if (_f == null) {
                    log.warn("field:[" + fieldName + "] specified in criteria is not defined in datasource");
                } else {
                    try {
                        Efield _ft = _f.getType();
                        String xpath = _f.getValueXPath();
                        if (xpath != null)
                            fieldName.replace('/', '.');
                        if (value == null) {
                            if (isNotNullAndEmpty(whereClause))
                                whereClause.append(" AND ");
                            whereClause.append(entityName).append(".").append(fieldName).append(" is null");

                        } else if (value instanceof List) {
                            List<?> valueList = (List<?>) value;
                            if (valueList.size() > 0) {
                                if (!isNotNullAndEmpty(whereClause))
                                    whereClause.append(" AND ");
                                whereClause.append("(");
                                Class<?> ftype = DataUtil.getPropertyType(entityClass, fieldName);
                                for (int i = 0; i < valueList.size(); i++) {
                                    Object v = valueList.get(i);
                                    if (!whereClause.toString().endsWith("("))
                                        whereClause.append(" or ");
                                    String pName = fieldName + i;
                                    whereClause.append(entityName).append(".").append(fieldName).append(" = :").append(pName);
                                    parameters.put(pName, DataUtil.castValue(v, ftype));
                                }
                                whereClause.append(")");
                            }

                        } else {
                            if (DataUtil.isNotNullAndEmpty(whereClause))
                                whereClause.append(" AND ");
                            if (_ft == Efield.TEXT || _ft == Efield.IMAGE || _ft == Efield.PASSWORD || _ft == Efield.LINK) {
                                String matchStyle = null;
                                try {
                                    matchStyle = req.getContext().getRoperation().getTextMatchStyle();
                                } catch (NullPointerException ignore) {
                                }

                                if ("startsWith".equals(matchStyle)) {
                                    whereClause.append(entityName).append(".").append(fieldName).append(" like :").append(fieldName);
                                    parameters.put(fieldName, value != null ? value.toString() : "" + "%");
                                } else if ("substring".equals(matchStyle)) {
                                    whereClause.append(entityName).append(".").append(fieldName).append(" like :").append(fieldName);
                                    parameters.put(fieldName, "%" + value != null ? value.toString() : "" + "%");
                                } else {
                                    whereClause.append(entityName).append(".").append(fieldName).append(" = :").append(fieldName);

                                    parameters.put(fieldName, value);
                                }
                            } else {
                                whereClause.append(entityName).append(".").append(fieldName).append(" = :").append(fieldName);
                                parameters.put(fieldName, DataUtil.castValue(value, DataUtil.getPropertyType(entityClass, fieldName)));
                            }// END field?
                        }

                    } catch (IntrospectionException ix) {
                        throw new SlxException(Tmodule.JPA, Texception.DEFAULT, ix);

                    }
                }// END _f == null
            }// end criteria loop
        }
        /**************************************************
         * process store
         **************************************************/
        List<?> sort = req.getContext().getSortByFields();
        if (sort != null) {
            for (Object obj : sort) {
                String fieldName = (String) obj;
                Tfield _f = this.data.getField(fieldName);
                if (_f == null) {
                    log.warn("field:[" + fieldName + "] specified in sortBy is not defined in datasource");
                } else {
                    if (isNotNullAndEmpty(orderClause))
                        orderClause.append(" , ");
                    String xpath = _f.getValueXPath();
                    if (isNotNullAndEmpty(xpath))
                        fieldName = xpath.replace('/', '.');
                    orderClause.append(entityName).append(".").append(fieldName);
                }
            }// END SORT FIELD LOOP
        }
        // query JPAQL string.
        StringBuffer jpaQuery = new StringBuffer().append("SELECT ").append(entityName).append(" FROM ").append(
            useQualifiedClassName ? entityClass.getName() : entityClass.getSimpleName()).append(" ").append(entityName);
        // result count JPAQL string.
        StringBuffer jpaCountQ = new StringBuffer().append("SELECT COUNT (").append(
            data.getPrimaryKey() == null ? entityName : entityName + "." + data.getPrimaryKey()).append(") FROM ").append(
            useQualifiedClassName ? entityClass.getName() : entityClass.getSimpleName()).append(" ").append(entityName);
        if (DataUtil.isNotNullAndEmpty(whereClause)) {
            jpaQuery.append(" WHERE ").append(whereClause);
            jpaCountQ.append(" WHERE ").append(whereClause);
        }
        if (DataUtil.isNotNullAndEmpty(orderClause)) {
            jpaQuery.append(" ORDER BY ").append(orderClause);
        }
        if (log.isTraceEnabled())
            log.trace("JPA-Query String:" + jpaQuery);
        Query query = entityManager.createQuery(jpaQuery.toString());
        for (String key : parameters.keySet()) {
            Object value = parameters.get(key);
            query.setParameter(key, value);
            if (log.isTraceEnabled())
                log.trace("Query Parameter:[" + value + "]");
        }

        // Control Page.
        int totalRows = -1;
        boolean __canPage = true;
        //
        ToperationBinding __bind = null;
        try {
            __bind = getContext().getOperationBinding(req);
        } catch (NullPointerException e) {
        }
        if (!req.getContext().isPaged() && getConfig().getBoolean("customSQLReturnsAllRows", false)
            && DataUtil.isNotNullAndEmpty(DataSourceData.getCustomSQL(__bind))) {
            __canPage = false;
            log.debug("Paging disabled for full custom queries.  Fetching all rows.Set sql.customSQLReturnsAllRows: false in config to change this behavior");
        }
        if (req.getContext().isPaged() && __canPage) {
            int end = req.getContext().getEndRow();
            int start = req.getContext().getStartRow();
            int batch = req.getContext().getBatchSize();
            if (end != -1 && end - start > batch) {
                batch = end - start;
                req.getContext().setBatchSize(batch);
            }
            Query queryCount = entityManager.createQuery(jpaCountQ.toString());
            for (String key : parameters.keySet()) {
                Object value = parameters.get(key);
                queryCount.setParameter(key, value);
                if (log.isTraceEnabled())
                    log.trace("Query Parameter:[" + value + "]");
            }
            Integer rowCount = Integer.valueOf(Integer.parseInt(queryCount.getSingleResult().toString()));
            totalRows = rowCount != null ? rowCount.intValue() : 0;
            query.setFirstResult(start);
            query.setMaxResults(batch);
        }
        List<?> results = null;
        if (totalRows == 0)
            results = Collections.emptyList();
        else
            results = query.getResultList();
        if (totalRows == -1L)
            totalRows = results.size();
        __return.setTotalRows(totalRows);
        Integer startRow = 0;
        Integer endRow = 0;
        if (totalRows != 0L) {
            startRow = req.getContext().getStartRow() == null ? 0 : req.getContext().getStartRow();
            endRow = startRow + results.size();
        }
        __return.setStartRow(startRow);
        __return.setEndRow(endRow);
        __return.setRawData(results);
        increaseOpCount();
        return __return;
    }

    private DSResponse addEntity(DSRequest req, JPADataSource[] datasources) throws SlxException {
        JPADataSource __firstDS = datasources[0];
        DSRequestData __requestCX = req.getContext();
        DSResponse __return = new DSResponseImpl();
        __return.setStatus(Status.STATUS_SUCCESS);
        __return.setDataSource(req.getDataSource());
        Object values = req.getContext().getRawValues();
        int batchsize = __requestCX.getBatchSize();
        // batch size.
        batchsize = batchsize < 100 ? 100 : batchsize;
        // java entity bean values.
        if (isEntityPresent(values)) {
            List<?> records = DataUtil.makeListIfSingle(values);
            Object result = persistBean(batchsize, records);
            __return.setRawData(result);
            return __return;
        }
        // check jpql configured
        ToperationBinding _op = __firstDS.getContext().getOperationBinding(req);
        if (_op != null && _op.getQueryClauses() != null && _op.getQueryClauses().getCustomQL() != null) {
            // velocity exp
            return __return;
        }
        if (isEntityClass(entityClass)) {
            List<?> records = req.getContext().getValueSets();
            List<Object> beans = new ArrayList<Object>();
            for (Object o : records) {
                Object bean = instance(entityClass);
                try {
                    DataUtil.setProperties((Map<?,?>) o, bean, false);
                } catch (Exception e) {
                    String __msg = "invoke bean class:[" + bean.getClass().getName() + "] exception";
                    throw new SlxException(Tmodule.JPA, Texception.INVOKE_EXCEPTION, __msg);
                }
                beans.add(bean);
            }
            Object result = persistBean(batchsize, beans);
            __return.setRawData(result);
            return __return;

        } else {
            throw new SlxException(Tmodule.JPA, Texception.JPA_NO_ENTITY, "JPA DataSource no configured Entity bean");
        }
    }

    private Object instance(Class<?> clz) throws SlxException {
        try {
            return Reflection.newInstance(clz);
        } catch (Exception e) {
            throw new SlxException(Tmodule.JPA, Texception.CAN_NOT_INSTANCE, e);
        }
    }

    private Object persistBean(int batchsize, List<?> records) {
        // batch update.
        int i = 0;
        for (Object o : records) {
            entityManager.persist(o);
            i++;
            if (i % batchsize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
        return records;
    }

    private Object updateBean(int batchsize, List<?> records) throws SlxException {
        // batch update.
        int i = 0;
        List<Object> _return = new ArrayList<Object>();
        for (Object o : records) {
            Object attached = findAttachedBean(o);
            try {
                DataUtil.setProperties(DataUtil.getProperties(o, true), attached);
            } catch (Exception e) {
                e.printStackTrace();
            }
            entityManager.merge(attached);
            _return.add(attached);
            i++;
            if (i % batchsize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
        return _return;
    }

    private Object removeBean(int batchsize, List<?> records) throws SlxException {
        // batch update.
        int i = 0;
        List<Object> _return = new ArrayList<Object>();
        for (Object o : records) {
            Object attached = findAttachedBean(o);
            entityManager.remove(attached);
            _return.add(attached);
            i++;
            if (i % batchsize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
        return _return;
    }

    /**
     * @param o
     * @return
     * @throws SlxException
     */
    private Object findAttachedBean(Object o) throws SlxException {
        try {
            Field[] declaredFields = o.getClass().getDeclaredFields();
            Map<String, PropertyDescriptor> propDes = DataUtil.getPropertyDescriptors(o.getClass());
            Object id = null;
            for (Field field : declaredFields) {
                int modifier = field.getModifiers();
                String propertyName = field.getName();
                if (Modifier.isStatic(modifier))
                    continue;
                if (field.getAnnotation(Id.class) != null) {
                    id = DataUtil.getProperty(propertyName, o);
                    break;
                } else {// AccessType=PROPERTY
                    PropertyDescriptor propDesc = propDes.get(propertyName);
                    Method read = propDesc.getReadMethod();
                    if (read != null && read.getAnnotation(Id.class) != null) {
                        id = read.invoke(o, new Object[0]);
                        break;
                    }
                }

            }
            if (id != null) {
                return entityManager.find(o.getClass(), id);
            } else {
                throw new SlxException(Tmodule.JPA, Texception.JPA_JPAEXCEPTION, "JPA Entity Bean no declear ID field or Method");
            }
        } catch (Exception e) {
            throw new SlxException(Tmodule.JPA, Texception.INVOKE_EXCEPTION, "can not find id");
        }
    }

    private boolean isEntityClass(Class<?> clz) {
        if (clz == null)
            return false;
        return clz.isAnnotationPresent(Entity.class);
    }

    private boolean isEntityPresent(Object o) {
        if (o == null)
            return false;
        if (o instanceof List<?>) {
            List<?> criterias = (List<?>) o;
            if (criterias.size() > 0) {
                Object getOne = criterias.get(0);
                return isEntityPresent(getOne);
            }
        } else if (o.getClass().isArray()) {
            Class<?> ctype = o.getClass().getComponentType();
            return ctype.isAnnotationPresent(Entity.class);
        } else {
            return o.getClass().isAnnotationPresent(Entity.class);
        }
        return false;
    }

}
