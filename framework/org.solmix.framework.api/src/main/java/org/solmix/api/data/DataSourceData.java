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

package org.solmix.api.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import org.solmix.api.VelocityExpression;
import org.solmix.api.criterion.IEvaluator;
import org.solmix.api.criterion.Operator;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.IType;
import org.solmix.api.event.IValidationEvent;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.EserverType;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.commons.logs.SlxLog;
import org.solmix.commons.util.DataUtil;

/**
 * This class used as a datasource context bean,cache ds static configuration , dynamical configuration and runtime
 * variable.
 * <p>
 * <h1>Merge Specification</h1> {@link org.solmix.api.data.DataSourceData DataSourceData} and
 * {@link org.solmix.api.jaxb.TdataSource TdataSource} Merge Specification
 * <table border=1 >
 * <tr>
 * <td COLSPAN="2" width="100%" align="center" style="background-color : #BBFFFF;font-size:12pt">The Type of DataSource
 * configuration</td>
 * </tr>
 * <tr width="100%" >
 * <td>From top to bottom</td>
 * <td>From bottom to top</td>
 * </tr>
 * <tr>
 * <td>
 * <li>
 * <li></td>
 * <td>
 * <li>
 * <li></td>
 * </tr>
 * </table>
 * For <code>From top to bottom<code>:use {@link org.solmix.api.data.DataSourceData DataSourceData}
 * <p>
 * For <code>From bottom to top<code>:use {@link org.solmix.api.jaxb.TdataSource TdataSource}
 * 
 * @author solomon
 * @since 0.0.1
 * @version 110035 2010-12-19 solmix-api
 */
@SuppressWarnings("unchecked")
public class DataSourceData implements Serializable
{

    private Map<String, Tfield> mapFields;

    private Map<Object, Object> derivedClientToServerFieldMap = new LinkedHashMap<Object, Object>();

    private Map<Object, Object> ds2NativeFieldMap;

    private Map<Object, Object> native2DSFieldMap;

    private String superDSName;

    private boolean waitForFree;

    private Map<Object, Object> otherAttributes;

    /**
    * 
    */
    private static final long serialVersionUID = 3894686313931868579L;

    /**
     * <B><li>ANNOTATE:</B> This is DS static configuration form xml config file.
     */
    private TdataSource tdataSource;

    private Map<String, ?> customerConfig;

    private DataSource superDS;

    // private List<Tfield> fields;

    private List<String> primaryKeys;

    private Object autoDeriveSchema;

    /**
     * <B><li>ANNOTATE:</B> This is DS runtime variable
     */
    private long configTimestamp = -1;

    /**
     * <B><li>ANNOTATE:</B> This is DS dynamical config.
     */
    private String dsConfigFile;

    /**
     * <B><li>ANNOTATE:</B> This is DS runtime variable
     */
    private IEvaluator evaluator;

    private VelocityExpression requires;

    private List<String> requiresRoles;

    /**
     * Return this datasource needed resource.if the {@link #requires} is null,used {@link #tdataSource}to initial it.
     * 
     * @return the requires
     */
    public VelocityExpression getRequires() {
        return requires;
    }

    /**
     * Flag for control pooled datasource to return only once.
     * 
     * @return
     */
    public boolean isWaitForFree() {
        return waitForFree;
    }

    /**
     * @param waitForFree the waitForFree to set
     */
    public void setWaitForFree(boolean waitForFree) {
        this.waitForFree = waitForFree;
    }

    /**
     * @param requires the requires to set
     */
    public void setRequires(VelocityExpression requires) {
        this.requires = requires;
    }

    /**
     * Return this datasource needed roles.if the {@link #requiresRoles} is null,used {@link #tdataSource}to initial it.
     * 
     * @return the requiresRoles
     */
    public List<String> getRequiresRoles() {
        return requiresRoles;
    }

    /**
     * @param requiresRoles the requiresRoles to set
     */
    public void setRequiresRoles(List<String> requiresRoles) {
        this.requiresRoles = requiresRoles;
    }

    /**
     * Get auto generated schema object. used for cache auto derived schema.
     * 
     * @return the autoDeriveSchema
     */
    public Object getAutoDeriveSchema() {
        return autoDeriveSchema;
    }

    /**
     * Cache schema object which is auto generated by datasource.
     * 
     * @param autoDeriveSchema the autoDeriveSchema to set
     */
    public void setAutoDeriveSchema(Object autoDeriveSchema) {
        this.autoDeriveSchema = autoDeriveSchema;
    }

    /**
     * @return the mapFields
     */
    public Map<String, Tfield> getMapFields() {
        if (mapFields == null)
            mapFields = new LinkedHashMap<String, Tfield>();
        return mapFields;
    }

    /**
     * @param mapFields the mapFields to set
     */
    public void setMapFields(Map<String, Tfield> mapFields) {
        this.mapFields = mapFields;
    }

    public void addField(Tfield field) {
        this.mapFields.put(field.getName(), field);
    }

    private final Map<String, IType> fieldTypeCache = Collections.synchronizedMap(new HashMap<String, IType>());;

    public IType getCachedFiledType(String fieldName) {
        return fieldTypeCache.get(fieldName);
    }

    public void addCachedFieldType(String fieldName, IType type) {
        fieldTypeCache.put(fieldName, type);
    }

    public void addToPrimaryKeys(String key) {
        if (primaryKeys == null) {
            primaryKeys = new ArrayList<String>();
        }
        if (!primaryKeys.contains(key)) {
            primaryKeys.add(key);
        }
    }

    /**
     * @return the primaryKeys
     */
    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    /**
     * 
     * @return unique primary key.
     */
    public String getPrimaryKey() {
        List<String> keys = getPrimaryKeys();
        if (keys == null || keys.get(0) == null)
            return null;
        else
            return keys.get(0);
    }

    /**
     * @return the fields
     */
    public List<Tfield> getFields() {
        if (getMapFields() == null)
            LoggerFactory.getLogger(SlxLog.GLOBAL).warn("try to CALL getFields() Method ,before datasource initialized");
        List<Tfield> mapFields = new ArrayList<Tfield>();
        for (Tfield f : getMapFields().values())
            mapFields.add(f);

        return mapFields;
    }

    private boolean autoJoinTransaction;

    /**
     * @return the autoJoinTransaction
     */
    public boolean shouldAutoJoinTransaction() {
        return autoJoinTransaction;
    }

    /**
     * @param autoJoinTransaction the autoJoinTransaction to set
     */
    public void setAutoJoinTransaction(boolean autoJoinTransaction) {
        this.autoJoinTransaction = autoJoinTransaction;
    }

    /**
     * The source of superDSName is form {@link org.solmix.api.jaxb.TdataSource}.
     * 
     * @return the superDSName
     */
    public String getSuperDSName() {
        // if(superDSName==null){
        // TdataSource ds =tdataSource.getInheritsFrom();
        // superDSName=ds.getID();
        // }
        return superDSName;
    }

    public Map<Object, Object> getDerivedClientToServerFieldMap() {
        return derivedClientToServerFieldMap;
    }

    public void setDerivedClientToServerFieldMap(Map<Object, Object> derivedClientToServerFieldMap) {
        this.derivedClientToServerFieldMap = derivedClientToServerFieldMap;
    }

    public Map<Object, Object> getDs2NativeFieldMap() {
        return ds2NativeFieldMap;
    }

    /**
     * @return the native2DSFieldMap
     */
    public Map<Object, Object> getNative2DSFieldMap() {
        return native2DSFieldMap;
    }

    /**
     * @param native2dsFieldMap the native2DSFieldMap to set
     */
    public void setNative2DSFieldMap(Map<Object, Object> native2dsFieldMap) {
        native2DSFieldMap = native2dsFieldMap;
    }

    public void setDs2NativeFieldMap(Map<Object, Object> ds2NativeFieldMap) {
        this.ds2NativeFieldMap = ds2NativeFieldMap;
    }

    public void setDs2NativeFieldMap(Object key, Object value) {
        if (ds2NativeFieldMap == null)
            ds2NativeFieldMap = new LinkedHashMap<Object, Object>();
        ds2NativeFieldMap.put(key, value);
    }

    /**
     * The source of superDSName is form {@link org.solmix.api.jaxb.TdataSource},so the <code>set</code> method should
     * update the source value.
     * 
     * @param superDSName the superDSName to set
     */
    public void setSuperDSName(String superDSName) {
        this.superDSName = superDSName;
        // if (tdataSource.getInheritsFrom() == null)
        // tdataSource.setInheritsFrom(superDSName);
    }

    /**
     * @return the superDS
     */
    public DataSource getSuperDS() {
        return superDS;
    }

    /**
     * @param superDS the superDS to set
     */
    public void setSuperDS(DataSource superDS) {
        this.superDS = superDS;
        // if (superDS != null && superDSName == null)
        // this.superDSName = superDS.getName();
    }

    /**
     * <B><li>ANNOTATE:</B> This is DS dynamical runtime variable
     */
    private List<IValidationEvent> validationList;

    /**
     * @return the customerConfig
     */
    public Map<String, ?> getCustomerConfig() {
        return customerConfig;
    }

    /**
     * <B><li>ANNOTATE:</B> This is DS dynamical runtime variable
     * 
     * @param event
     */
    public void addValidationEvent(IValidationEvent event) {
        if (validationList == null)
            validationList = new ArrayList<IValidationEvent>();
        validationList.add(event);
    }

    public List<IValidationEvent> getValidationEvents() {
        return validationList;
    }

    /**
     * @param customerConfig the customerConfig to set
     */
    public void setCustomerConfig(Map<String, ?> customerConfig) {
        this.customerConfig = customerConfig;
    }

    @SuppressWarnings("unused")
    private DataSourceData()
    {

    }

    public DataSourceData(TdataSource datasource)
    {
        this.setTdataSource(datasource);

    }

    /**
     * <B><li>ANNOTATE:</B> This is DS runtime variable
     * 
     * @param op
     */
    public void addSearchOperator(Operator op) {
        getEvaluator().addSearchOperator(op);
    }

    /**
     * Indicate the operation type.
     * <p>
     * <B>
     * <li>ANNOTATE:</B> here for convenience.
     * 
     * @param operation
     * @return
     */
    public boolean isModificationAction(Eoperation operation) {
        if (operation == Eoperation.ADD || operation == Eoperation.REMOVE || operation == Eoperation.UPDATE || operation == Eoperation.REPLACE)
            return true;
        else
            return false;
    }

    /**
     * <p>
     * <B>
     * <li>ANNOTATE:</B> here for convenience.
     * 
     * @param opType
     * @param opId
     * @return
     */
    public boolean isModificationAction(Eoperation opType, String opId) {
        if (isModificationAction(opType))
            return true;
        // if (tdataSource != null) {
        // Taction action= getAction(opType, opId);
        // return (action != null && action.getSqlType() == EsqlType.UPDATE);
        // }
        return false;
    }

    public String getAutoOperationId(Eoperation operationType) {
        return getName() + "_" + operationType.value();
    }

    /**
     * Return the datasource context's name.Any reference to DataSource identification must from this method.not form
     * {@link org.solmix.api.jaxb.TdataSource#getID()}
     * <p>
     * <B>
     * <li>ANNOTATE:</B>
     * <p>
     * This accessor method returns a reference to the <B>group:</B>{@link org.solmix.api.jaxb.TdataSource#getID() ID},
     * not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serviceOrReferenceListOrBean property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getTdataSource().setID(newItem);
     * </pre>
     * 
     * @return
     */
    public String getName() {
        return tdataSource.getID();
    }

    public void setName(String name) {
        tdataSource.setID(name);
    }

    public ToperationBinding getOperationBinding(Eoperation opType) {
        return getOperationBinding(opType, null);

    }

    public ToperationBinding getOperationBinding(DSRequest request) {
        return getOperationBinding(request.getContext().getOperationType(), request.getContext().getOperationId());

    }

    /**
     * <B><li>ANNOTATE:</B>
     * <p>
     * This accessor method returns a reference to the {@link org.solmix.api.jaxb.TdataSource}, not a snapshot.
     * Therefore any modification you make to the returned list will be present inside the JAXB object. This is why
     * there is not a <CODE>set</CODE> method for the serviceOrReferenceListOrBean property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getTdataSource().getActions().setAction(newItem);
     * </pre>
     * 
     * @param opType
     * @param opId
     * @return
     */
    public ToperationBinding getOperationBinding(Eoperation opType, String opId) {
        if (tdataSource == null)
            return null;
        ToperationBinding autoOperationBinding = null;
        boolean operationIdIsAuto = opId != null && opType != null && opId.equals(getAutoOperationId(opType));
        if (tdataSource.getOperationBindings() != null) {
            for (ToperationBinding action : tdataSource.getOperationBindings().getOperationBinding()) {
                if (action.getOperationId() != null && action.getOperationId().equals(opId) && action.getOperationType() == opType)
                    return action;
                if (action.getOperationType() == opType && (operationIdIsAuto || opId == null))
                    autoOperationBinding = action;
            }
        }
        if (autoOperationBinding != null)
            return autoOperationBinding;
        return null;
    }

    /**
     * <B><li>ANNOTATE:</B>
     * <p>
     * This accessor method returns a reference to the {@link org.solmix.api.jaxb.TdataSource}, not a snapshot.
     * Therefore any modification you make to the returned list will be present inside the JAXB object. This is why
     * there is not a <CODE>set</CODE> method for the serviceOrReferenceListOrBean property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getTdataSource().getFields().setField(newItem);
     * </pre>
     * 
     * @param fieldName
     * @return
     */
    public Tfield getField(String fieldName) {
        if (getMapFields() == null || fieldName == null)
            return null;
        return getMapFields().get(fieldName);
    }

    /**
     * @return the tdataSource
     */
    public TdataSource getTdataSource() {
        return tdataSource;
    }

    /**
     * find out the server implements type of datasource.
     * <p>
     * <B>
     * <li>ANNOTATE:</B>
     * <p>
     * This accessor method returns a reference to the {@link org.solmix.api.jaxb.TdataSource}, not a snapshot.
     * Therefore any modification you make to the returned list will be present inside the JAXB object. This is why
     * there is not a <CODE>set</CODE> method for the serviceOrReferenceListOrBean property.
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getTdataSource().setServerType(newItem);
     * </pre>
     * 
     * @return
     */
    public EserverType getServerType() {
        return tdataSource.getServerType();
    }

    /**
     * @param tdataSource the tdataSource to set
     */
    public void setTdataSource(TdataSource tdataSource) {
        this.tdataSource = tdataSource;
    }

    /**
     * <B><li>ANNOTATE:</B> This is DS runtime variable
     * 
     * @return the configTimestamp
     */
    public long getConfigTimestamp() {
        return configTimestamp;
    }

    /**
     * <B><li>ANNOTATE:</B> This is DS runtime variable
     * 
     * @param configTimestamp the configTimestamp to set
     */
    public void setConfigTimestamp(long configTimestamp) {
        this.configTimestamp = configTimestamp;
    }

    /**
     * @return the dsConfigFile
     */
    public String getDsConfigFile() {
        return dsConfigFile;
    }

    /**
     * @param dsConfigFile the dsConfigFile to set
     */
    public void setDsConfigFile(String dsConfigFile) {
        this.dsConfigFile = dsConfigFile;
    }

    /**
     * @return the evaluator
     */
    public IEvaluator getEvaluator() {
        return evaluator;
    }

    /**
     * @param evaluator the evaluator to set
     */
    public void setEvaluator(IEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    /**
     * @return
     */
    public List<String> getFieldNames() {
        List<String> res = new ArrayList<String>();
        for (Tfield field : getFields())
            res.add(field.getName());
        return res;
    }

    /**
     * @return
     */
    public boolean isValidateRecords() {
        if (tdataSource != null)
            return DataUtil.booleanValue(tdataSource.isValidateRecords());
        else
            return false;
    }

    public Map<String, Object> getExpandedDs2NativeFieldMap() {
        Map fieldMap = new LinkedHashMap();
        if (this.getSuperDS() != null) {
            fieldMap = getSuperDS().getContext().getExpandedDs2NativeFieldMap();
        }
        DataUtil.mapMerge(ds2NativeFieldMap, fieldMap);
        return fieldMap;
    }

    public Map getValueMaps() {
        return getValueMaps(getFieldNames());
    }

    public Map<String, Object> getValueMaps(List<String> fieldNames) {
        Map<String, Object> valueMaps = new HashMap<String, Object>();
        if (fieldNames == null || fieldNames.size() == 0)
            return valueMaps;
        for (Object o : fieldNames) {
            String fieldName = (String) o;
            Tfield field = this.getField(fieldName);
            if (field != null && field.getValueMap() != null)
                valueMaps.put(fieldName, field.getValueMap().getValue());
        }
        return valueMaps;
    }

    /**
     * @param columnName
     * @return
     */
    public Object getColumnName(String fieldName) {
        return ds2NativeFieldMap.get(fieldName);
    }

    public static String getCustomSQL(ToperationBinding bind) {
        String _return = null;

        if (bind != null && bind.getQueryClauses() != null) {
            _return = bind.getQueryClauses().getCustomSQL();
        }
        return _return == null ? null : _return.trim();
    }

    public static String getValuesClause(ToperationBinding bind) {
        String _return = null;
        if (bind != null && bind.getQueryClauses() != null) {
            _return = bind.getQueryClauses().getValuesClause();
        }
        return _return == null ? null : _return.trim();
    }

    public static String getSelectClause(ToperationBinding bind) {
        String _return = null;
        if (bind != null && bind.getQueryClauses() != null) {
            _return = bind.getQueryClauses().getSelectClause();
        }
        return _return == null ? null : _return.trim();
    }

    public static String getTableClause(ToperationBinding bind) {
        String _return = null;
        if (bind != null && bind.getQueryClauses() != null) {
            _return = bind.getQueryClauses().getTableClause();
        }
        return _return == null ? null : _return.trim();
    }

    public static String getWhereClause(ToperationBinding bind) {
        String _return = null;
        if (bind != null && bind.getQueryClauses() != null) {
            _return = bind.getQueryClauses().getWhereClause();
        }
        return _return == null ? null : _return.trim();
    }

    /**
     * @param key
     * @return
     */
    public Object getAttribute(String key) {
        if (otherAttributes != null)
            return otherAttributes.get(key);
        return null;
    }

    public void setAttribute(String key, Object value) {
        if (otherAttributes == null)
            otherAttributes = new HashMap<Object, Object>();
        otherAttributes.put(key, value);
    }

    public void removeAttribute(String key) {
        if (otherAttributes != null)
            otherAttributes.remove(key);

    }
}
