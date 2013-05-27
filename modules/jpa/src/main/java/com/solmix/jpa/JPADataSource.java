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

package com.solmix.jpa;

import static com.solmix.commons.util.DataUtil.isNotNullAndEmpty;

import java.beans.IntrospectionException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import com.solmix.api.data.DataSourceData;
import com.solmix.api.datasource.DSRequest;
import com.solmix.api.datasource.DSResponse;
import com.solmix.api.datasource.DSResponse.Status;
import com.solmix.api.datasource.DataSource;
import com.solmix.api.datasource.DataSourceGenerator;
import com.solmix.api.exception.SlxException;
import com.solmix.api.jaxb.Efield;
import com.solmix.api.jaxb.EserverType;
import com.solmix.api.jaxb.Tfield;
import com.solmix.api.jaxb.ToperationBinding;
import com.solmix.api.rpc.RPCManager;
import com.solmix.api.rpc.RPCManagerCompletionCallback;
import com.solmix.api.types.Texception;
import com.solmix.api.types.Tmodule;
import org.slf4j.Logger;
import com.solmix.commons.util.DataUtil;
import com.solmix.fmk.base.Reflection;
import com.solmix.fmk.datasource.BasicDataSource;
import com.solmix.fmk.datasource.DSResponseImpl;
import com.solmix.fmk.util.ServiceUtil;
/**
 * JPA datasource.
 * 
 * @author solomon
 * @version $Id$ 2011-6-6
 */

public class JPADataSource extends BasicDataSource implements DataSource, RPCManagerCompletionCallback
{

    private final static Logger log = LoggerFactory.getLogger(JPADataSource.class.getName());

    private boolean strictSQLFiltering;

    private boolean useQualifiedClassName;

    private boolean shouldRollBackTransaction;

    protected EMHooker hooker;

    protected EMFProvider provider;

    private int batchInsertSize = 100;

    private EntityManager entityManager;

    private Object transaction;

    String entity = null;

    String entityName = null;

    Class<?> entityClass = null;

    public JPADataSource() throws SlxException
    {

    }

    public JPADataSource(DataSourceData data) throws SlxException
    {
        strictSQLFiltering = false;
        useQualifiedClassName = false;
        this.init(data);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.fmk.datasource.BasicDataSource#getServerType()
     */
    @Override
    public String getServerType() {
        return EserverType.JPA.value();
    }

    @Override
    public void init(DataSourceData data) throws SlxException {
        adaptProvider(data);
        if (provider == null) {
            log.warn("JPADataSource's EMF provider must be not null");
            return;
        }
        try {
            entityManager = provider.getEntityManager();
            provider.returnEntityManager(entityManager);
        } catch (Exception e) {
            log.error("Unexpected exception while initial entityManager", e);
        }
        super.init(data);
    }

    public void destroy() {
        log.trace("JPADataSource:destroy");
    }

    /**
     * @return the entityManager
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * @param entityManager the entityManager to set
     */
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EMFProvider getProvider() {
        return provider;
    }

    public void setProvider(EMFProvider provider) {
        this.provider = provider;
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
        return new JPADataSource(data);
    }

    protected void adaptProvider(DataSourceData context) throws SlxException {
        String unit = context == null ? null : context.getTdataSource() == null ? null
            : context.getTdataSource().getPersistenceUnit();
        if (unit == null)
            return;
        Object[] objs = ServiceUtil.getOSGIServices(EntityManagerFactory.class.getName(), "(osgi.unit.name="
            + unit + ")");
        EntityManagerFactory factory = objs == null ? null : objs.length >= 1 ? (EntityManagerFactory) objs[0] : null;
        if (factory == null) {
            String __info = "Initial DataSource failed,can not find persistence unit:[" + unit + "].";
            log.error(__info);
            throw new SlxException(Tmodule.JPA, Texception.JPA_NO_EMF, __info);
        } else {
            EMFProviderAM provider = new EMFProviderAM();
            provider.setEntityManagerFactory(factory);
            this.setProvider(provider);
        }
    }

    @Override
    public DSResponse execute(DSRequest req) throws SlxException {
        shouldRollBackTransaction = false;
        hooker = null;
        req.registerFreeResourcesHandler(this);
//        req.getContext().setFreeOnExecute(false);
        DataSource __ds = req.getDataSource();
        if (__ds == null)
            throw new SlxException(Tmodule.JPA, Texception.DS_NO_FONUN_DATASOURCE, "must define a datasource");
        /**********************************************************************
         **** Just assume entity class have been generated. *******************
         **********************************************************************/
        DataSource entitySchema = (DataSource) __ds.getContext().getAutoDeriveSchema();
        /**********************************************************************
         * 
         *********************************************************************/
        if (entitySchema != null) {
            entity = entitySchema.getName();
            entityClass = (Class<?>) entitySchema.getContext().getAttribute("_entity_class");
        }
        if (entity == null || entityClass == null) {
            String entityName = __ds.getContext().getTdataSource() == null ? null
                : __ds.getContext().getTdataSource().getSchemaClass();
            if (DataUtil.isNotNullAndEmpty(entityName)) {
                try {
                    entityClass = Reflection.classForName(entityName);
                } catch (Exception e) {
                    throw new SlxException(Tmodule.JPA, Texception.NO_FOUND, e);
                }
                if (entityClass == null || !entityClass.isAnnotationPresent(Entity.class)) {
                    entity = null;
                } else {
                    /** ID */
                    Entity e = entityClass.getAnnotation(Entity.class);
                    entity = e == null ? null : e.name();
                    if (isNullOrEmpty(entity))
                        entity = entityClass.getName().substring(entityClass.getName().lastIndexOf(".") + 1);
                }
            }
        }
        if (entity == null || entityClass == null) {
            throw new SlxException(Tmodule.JPA, Texception.JPA_NO_ENTITY, "A jpa datasource must special a entity");
        }

        entityName = "_" + entity;
        if (req.getRpc() != null && this.shouldAutoJoinTransaction(req)) {
            log.debug("Auto get transaction object!");
            Object obj = this.getTransactionObject(req);
            if (!(hooker instanceof EMHooker)) {
                if (log.isWarnEnabled())
                    log.warn("JPA DataSource transaction hooker should be a com.solmix.jpa.EMHooker instance,but is"
                        + obj.getClass().getName() + " Assume the transaction object is invalid and set it to null");
                hooker = null;
            } else {
                hooker = (EMHooker) obj;
            }
            if (hooker == null) {
                if (shouldAutoStartTransaction(req, false)) {
                    try {
                        entityManager = provider.getEntityManager();
                        transaction = provider.getTransaction(entityManager);
                    } catch (Exception e) {
                        log.error("Unexpected exception while initial entityManager", e);
                    }
                    log.debug("Creating EntityManager, starting transaction and setting it to RPCManager.");
                    hooker = new EMHooker(this, entityManager, transaction);
                    req.getRpc().getContext().setAttribute(this.getTransactionObjectKey(), hooker);
                    req.getRpc().registerCallback(this);
                } else {
                    try {
                        entityManager = provider.getEntityManager();
                        transaction = provider.getTransaction(entityManager);
                    } catch (Exception e) {
                        log.error("Unexpected exception while initial entityManager", e);
                    }
                }
            } else {
                entityManager = hooker.getEm();
                transaction = hooker.getTx();
            }
            req.setPartOfTransaction(true);
        } else {
            try {
                entityManager = provider.getEntityManager();
                transaction = provider.getTransaction(entityManager);
            } catch (Exception e) {
                log.error("Unexpected exception while initial entityManager", e);
            }
        }
        try {
            return super.execute(req);
        } catch (Exception e) {
            markTrnsactionForRollBack(null);
            throw new SlxException(Tmodule.JPA, Texception.JPA_JPAEXCEPTION, e);
        }
    }

    @Override
    public DSResponse executeFetch(DSRequest req) throws SlxException {
        log.debug("Executing fetch.");
        List<Object> parameters = new ArrayList<Object>();
        List<Object> parameterTypes = new ArrayList<Object>();
        int pcount = 0;
        StringBuffer whereClause = new StringBuffer();
        StringBuffer orderClause = new StringBuffer();
        DSResponse __return = new DSResponseImpl();
        __return.getContext().setStatus(Status.STATUS_SUCCESS);
        __return.setDataSource(req.getDataSource());
        Map criteria = req.getContext().getCriteria();
        if (criteria != null) {
            if (isAdvancedCriteria(criteria)) {
                throw new SlxException(Tmodule.JPA, Texception.NO_SUPPORT,
                    "JPA datasource has't supported advance criteria yet");
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
                                whereClause.append(" and ");
                            whereClause.append(entityName).append(".").append(fieldName).append(" is null");

                        } else if (value instanceof List) {
                            List valueList = (List) value;
                            if (valueList.size() > 0) {
                                if (!isNotNullAndEmpty(whereClause))
                                    whereClause.append(" and ");
                                whereClause.append("(");

                                for (Object v : valueList) {
                                    if (!whereClause.toString().endsWith("("))
                                        whereClause.append(" or ");
                                    whereClause.append(entityName).append(".").append(fieldName).append(" = :p").append(
                                        pcount++);
                                    parameters.add(v);
                                    parameterTypes.add(DataUtil.getPropertyType(entityClass, fieldName));
                                }
                                whereClause.append(")");
                            }

                        } else {
                            if (DataUtil.isNotNullAndEmpty(whereClause))
                                whereClause.append(" and ");
                            if (_ft == Efield.TEXT || _ft == Efield.IMAGE || _ft == Efield.PASSWORD
                                || _ft == Efield.LINK) {
                                String matchStyle = null;
                                try {
                                    matchStyle = req.getContext().getRoperation().getTextMatchStyle();
                                } catch (NullPointerException ignore) {
                                }

                                if ("startsWith".equals(matchStyle)) {
                                    whereClause.append(entityName).append(".").append(fieldName).append(" like :p").append(
                                        pcount++);
                                    parameters.add(value != null ? value.toString() : "" + "%");
                                } else if ("substring".equals(matchStyle)) {
                                    whereClause.append(entityName).append(".").append(fieldName).append(" like :p").append(
                                        pcount++);
                                    parameters.add("%" + value != null ? value.toString() : "" + "%");
                                } else {
                                    whereClause.append(entityName).append(".").append(fieldName).append(" = :p").append(
                                        pcount++);
                                    parameters.add(value);
                                }
                            } else {
                                whereClause.append(entityName).append(".").append(fieldName).append(" = :p").append(
                                    pcount++);
                                parameters.add(value);
                            }// END field?
                            parameterTypes.add(DataUtil.getPropertyType(entityClass, fieldName));
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
        List sort = req.getContext().getSortByFields();
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
        StringBuffer jpaQuery = new StringBuffer().append("select ").append(entityName).append(" from ").append(
            useQualifiedClassName ? entityClass.getName() : entityClass.getSimpleName()).append(" ").append(entityName);
        // result count JPAQL string.
        StringBuffer jpaCountQ = new StringBuffer().append("select count (").append(entityName).append(".").append(
            data.getPrimaryKey()).append(") from ").append(
            useQualifiedClassName ? entityClass.getName() : entityClass.getSimpleName()).append(" ").append(entityName);
        if (DataUtil.isNotNullAndEmpty(whereClause)) {
            jpaQuery.append(" where ").append(whereClause);
            jpaCountQ.append(" where ").append(whereClause);
        }
        if (DataUtil.isNotNullAndEmpty(orderClause)) {
            jpaQuery.append(" order by ").append(orderClause);
        }
        if (log.isDebugEnabled())
            log.debug("JPA-Query String:" + jpaQuery);
        Query query = entityManager.createQuery(jpaQuery.toString());
        Query queryCount = entityManager.createQuery(jpaCountQ.toString());
        for (int i = 0; i < pcount; i++) {
            Object parameterValue = parameters.get(i);
            Class<?> parameterType = (Class<?>) parameterTypes.get(i);
            Object typedParameterValue = DataUtil.castValue(parameterValue, parameterType);
            query.setParameter("p" + i, typedParameterValue);
            queryCount.setParameter("p" + i, typedParameterValue);
            if (log.isDebugEnabled())
                log.debug("Query Parameter:[" + typedParameterValue + "]");
        }

        // Control Page.
        int totalRows = -1;
        boolean __canPage = true;
        //
        ToperationBinding __bind = null;
        try {
            __bind = req.getDataSource().getContext().getOperationBinding(req.getContext().getOperationType(),
                req.getContext().getOperation());
        } catch (NullPointerException e) {
        }
        if (!req.getContext().isPaged()
            && getConfigRealm().getSubtree("jpa").getBoolean("customSQLReturnsAllRows", false) && __bind != null
            && (__bind.getCustomSQL() != null)) {
            __canPage = false;
            log.warning("Paging disabled for full custom queries.  Fetching all rows.Set sql.customSQLReturnsAllRows: false in config to change this behavior");
        }
        if (req.getContext().isPaged() && __canPage) {
            if (req.getContext().getEndRow() != -1L
                && req.getContext().getEndRow() - req.getContext().getStartRow() > req.getContext().getBatchSize())
                req.getContext().setBatchSize(req.getContext().getEndRow() - req.getContext().getStartRow());
            Integer rowCount = Integer.valueOf(Integer.parseInt(queryCount.getSingleResult().toString()));
            totalRows = rowCount != null ? rowCount.intValue() : 0;
            query.setFirstResult(req.getContext().getStartRow().intValue());
            query.setMaxResults(req.getContext().getBatchSize().intValue());
        }
        List results = null;
        if (totalRows == 0)
            results = new ArrayList();
        else
            results = query.getResultList();
        if (totalRows == -1L)
            totalRows = results.size();
        __return.getContext().setTotalRows(totalRows);
        Integer startRow = 0;
        Integer endRow = 0;
        if (totalRows != 0L) {
            startRow = req.getContext().getStartRow() == null ? 0: req.getContext().getStartRow();
            endRow = startRow + results.size();
        }
        __return.getContext().setStartRow(startRow);
        __return.getContext().setEndRow(endRow);
        __return.getContext().setData(results);
        increaseOpCount();
        return __return;
    }

    @Override
    public DSResponse executeAdd(DSRequest req) throws SlxException {
        DSResponse __return = new DSResponseImpl();
        __return.getContext().setStatus(Status.STATUS_SUCCESS);
        __return.setDataSource(req.getDataSource());
        Object bean = null;
        try {
            bean = entityClass.newInstance();
        } catch (InstantiationException e) {
            throw new SlxException(Tmodule.JPA, Texception.CAN_NOT_INSTANCE, e);
        } catch (IllegalAccessException e) {
            throw new SlxException(Tmodule.JPA, Texception.ILLEGAL_ACESS, e);
        }
        List records=req.getContext().getValueSets();
        if(records==null)
            return __return;
        //Construct a empty bean and insert it.
        if(records.isEmpty()){
            entityManager.persist(bean);
        }else{
        //batch update.
        int i=0;
        for(Object o:records){
            try {
                DataUtil.setProperties((Map)o, bean,false);
            } catch (Exception e) {
                String __msg = "invoke bean class:[" + bean.getClass().getName() + "] exception";
                throw new SlxException(Tmodule.JPA, Texception.INVOKE_EXCEPTION, __msg);
            }
            entityManager.persist(bean);
            i++;
            if(i%100==0){
                entityManager.flush();
                entityManager.clear();
            }
        }
        }
        __return.getContext().setData(bean);
        increaseOpCount();
        return __return;
    }

    @Override
    public DSResponse executeRemove(DSRequest req) throws SlxException {
        DSResponse __return = new DSResponseImpl();
        __return.getContext().setStatus(Status.STATUS_SUCCESS);
        __return.setDataSource(req.getDataSource());
        String pk = data.getPrimaryKey();
        Tfield pkField = data.getField(pk);
        if (pkField == null) {
            log.error("field:[" + pk + "]  is not defined in datasource");
            throw new SlxException(Tmodule.JPA, Texception.DS_UPDATE_WITHOUT_PK, "field:[" + pk
                + "]  is not defined in datasource");
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
        __return.getContext().setData(req.getContext().getCriteria());
        increaseOpCount();
        return __return;
    }

    @Override
    public DSResponse executeUpdate(DSRequest req) throws SlxException {
        DSResponse __return = new DSResponseImpl();
        __return.getContext().setStatus(Status.STATUS_SUCCESS);
        __return.setDataSource(req.getDataSource());
        String pk = data.getPrimaryKey();
        Tfield pkField = data.getField(pk);
        if (pkField == null) {
            log.error("field:[" + pk + "]  is not defined in datasource");
            throw new SlxException(Tmodule.JPA, Texception.DS_UPDATE_WITHOUT_PK, "field:[" + pk
                + "]  is not defined in datasource");
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
        __return.getContext().setData(record);
        increaseOpCount();
        return __return;

    }

    @Override
    public String getTransactionObjectKey() throws SlxException {
        return "_slx_jpa_entityManager_key";
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.services.rpc.RPCManagerCompletionCallback#onSuccess(com.solmix.services.rpc.RPCManager)
     */
    @Override
    public void onSuccess(RPCManager rpcmanager) throws SlxException {
        Object obj = rpcmanager.getContext().getAttribute(this.getTransactionObjectKey());
        if (obj == null) {
            log.warn("Transaction Object is null !");
            return;
        }
        if (!(obj instanceof EMHooker)) {
            throw new SlxException(Tmodule.JPA, Texception.OBJECT_TYPE_NOT_ADAPTED,
                "rpc manager does not hold instance of EMHooker for jpa datasource");
        }
        EMHooker hook = (EMHooker) obj;
        try {
            log.debug("committing transaction for" + hook.getOpCount() + "queued operation(s)");
            provider.commitTansaction(hook.getTx());
        } catch (Exception e) {
            provider.rollbackTransaction(transaction);
            log.error("Failed to commit transaction,Rolling back", e);
        }
        try {
            provider.returnEntityManager(hook.getEm());
        } catch (Exception e) {
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.services.rpc.RPCManagerCompletionCallback#onFailure(com.solmix.services.rpc.RPCManager, boolean)
     */
    @Override
    public void onFailure(RPCManager rpcmanager, boolean flag) throws SlxException {
        Object obj = rpcmanager.getContext().getAttribute(this.getTransactionObjectKey());
        if (obj == null) {
            log.warn("Transaction Object is null !");
            return;
        }
        if (!(obj instanceof EMHooker)) {
            throw new SlxException(Tmodule.JPA, Texception.OBJECT_TYPE_NOT_ADAPTED,
                "rpc manager does not hold instance of EMHooker for jpa datasource");
        }
        EMHooker hook = (EMHooker) obj;
        try {
            log.debug("rolling back transaction for" + hook.getOpCount() + "queued operation(s)");
            provider.rollbackTransaction(hook.getTx());
            provider.returnEntityManager(hook.getEm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void freeResources() {
        if (hooker == null) {
            if (shouldRollBackTransaction) {
                log.debug("rolling back transaction!");
                provider.rollbackTransaction(transaction);
            } else {
                try {
                    log.debug("committing transaction");
                    provider.commitTansaction(transaction);
                } catch (Exception ex) {
                    provider.rollbackTransaction(transaction);
                    log.error("Failed to commit transaction,Rolling back", ex);
                }
            }
            try {
                provider.returnEntityManager(entityManager);
            } catch (Exception ignore) {
            }
        }
        super.freeResources();
    }

    /**
     * @return the dataSourceGenerator
     */
    @Override
    public DataSourceGenerator getDataSourceGenerator() {
        if (dataSourceGenerator == null)
            dataSourceGenerator = new JPADataSourceGenerator();
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
        if (hooker != null)
            hooker.increaseOpCount();
    }

    public void markTrnsactionForRollBack(DSResponse resp) {
        this.shouldRollBackTransaction = true;
        if (resp != null)
            resp.getContext().setStatus(Status.STATUS_FAILURE);
        log.debug("mark transaction for roll back");
    }
}
