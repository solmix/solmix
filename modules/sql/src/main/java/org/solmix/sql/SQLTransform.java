/*
 * Copyright 2012 The Solmix Project
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

package org.solmix.sql;

import java.io.Reader;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.SystemContext;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Efield;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.util.DataUtil;
import org.solmix.commons.util.IOUtil;
import org.solmix.fmk.SlxContext;
import org.solmix.fmk.base.Reflection;
import org.solmix.fmk.event.EventWorker;
import org.solmix.fmk.event.EventWorkerFactory;
import org.solmix.fmk.util.DataTools;
import org.solmix.fmk.util.SLXDate;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-3-30
 */
@SuppressWarnings("unchecked")
public class SQLTransform
{

    private static Logger log = LoggerFactory.getLogger(SQLTransform.class.getName());

    /**
     * @param dbName
     * @return
     */
    public static boolean hasBrokenCursorAPIs(SQLDriver driver) {
        return driver.hasBrokenCursorAPIs();
    }


    public static List<Object> toListOfMapsOrBeans(ResultSet rs, boolean brokenCursorAPIs, List<SQLDataSource> dataSources) throws SQLException,
        SlxException {
        return toListOfMapsOrBeans(rs, -1L, brokenCursorAPIs, dataSources);
    }

    public static List<Object> toListOfMapsOrBeans(ResultSet rs, long numRows, boolean brokenCursorAPIs, List<SQLDataSource> dataSources)
        throws SQLException, SlxException {
        return toListOfMapsOrBeans(rs, numRows, brokenCursorAPIs, dataSources, null);
    }

    public static List<Object> toListOfMapsOrBeans(ResultSet rs, SQLDriver driver, List<SQLDataSource> dataSources, ToperationBinding opConfig)
        throws SQLException, SlxException {
        return toListOfMapsOrBeans(rs, -1L, hasBrokenCursorAPIs(driver), dataSources, opConfig);
    }

    public static List<Object> toListOfMapsOrBeans(ResultSet rs, boolean brokenCursorAPIs, List<SQLDataSource> dataSources, ToperationBinding opConfig)
        throws SQLException, SlxException {
        return toListOfMapsOrBeans(rs, -1L, brokenCursorAPIs, dataSources, opConfig);
    }

    public static List<Object> toListOfMapsOrBeans(ResultSet rs, long numRows, SQLDriver driver, List<SQLDataSource> dataSources,
        ToperationBinding opConfig) throws SQLException, SlxException {
        return toListOfMapsOrBeans(rs, numRows, hasBrokenCursorAPIs(driver), dataSources, opConfig);
    }

    /**
     * @param resultSet
     * @param l
     * @param hasBrokenCursorAPIs
     * @param dataSources
     * @param opConfig
     * @return
     * @throws SQLException
     * @throws SlxException
     */

    private static List<Object> toListOfMapsOrBeans(ResultSet resultSet, long rowNum, boolean hasBrokenCursorAPIs, List<SQLDataSource> dataSources,
        ToperationBinding opConfig) throws SQLException, SlxException {
        List<Object> __return = new ArrayList<Object>();
        Map<String, String> _caseInsensitiveMap = new HashMap<String, String>();
        ResultSetMetaData _rsmd;
        boolean _useColumnLabel = false;
        String _beanClassName = null;
        List<String> _outputs = null;
        if (opConfig != null) {
            // _beanClassName=opConfig.getBeanClassName();
            String outputsString = opConfig.getOutputs();
            if (outputsString != null) {
                _outputs = new ArrayList<String>();
                String outputsArray[] = outputsString.split(",");
                for (int i = 0; i < outputsArray.length; i++)
                    _outputs.add(outputsArray[i].trim());

            }
        }
        /**
         * get bean class name from datasource.
         */
        long _$ = System.currentTimeMillis();
        // If ResultSet is null.
        if (hasBrokenCursorAPIs) {
            if (!resultSet.next())
                return __return;
        } else {
            boolean isBeforeFirst = false;
            boolean isAfterLast = false;
            try {
                isBeforeFirst = resultSet.isBeforeFirst();
                isAfterLast = resultSet.isAfterLast();
            } catch (SQLException ignored) {
                log.debug("isBeforeFirst()/isAfterLast() throwing exceptions .", ignored);
            }
            if ((isBeforeFirst || isAfterLast || resultSet.getRow() == 0) && !resultSet.next())
                return __return;
        }
        _rsmd = resultSet.getMetaData();
        if (dataSources != null) {
            DataSource firstDS = dataSources.get(0);
            if (firstDS instanceof SQLDataSource) {
                SQLDriver driver = ((SQLDataSource) firstDS).getDriver();
                _useColumnLabel = driver.useColumnLabelInMetadata();
            }
            _beanClassName = (String) DataUtil.getProperty("bean", opConfig, firstDS.getContext().getTdataSource());
        }
        if (dataSources != null) {
            int count = _rsmd.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String fieldName = null;
                String columnName;
                if (_useColumnLabel)
                    columnName = _rsmd.getColumnLabel(i);
                else
                    columnName = _rsmd.getColumnName(i);
                for (Object ds : dataSources) {
                    List<String> names = ((DataSource) ds).getContext().getFieldNames();
                    for (String name : names) {
                        if (name.equalsIgnoreCase(columnName)) {
                            fieldName = name;
                            break;
                        }
                    }
                }
                if (fieldName != null)
                    _caseInsensitiveMap.put(columnName, fieldName);
                else
                    _caseInsensitiveMap.put(columnName, columnName);
            }
        }// END ?DS

        long i = 0;
        do {
            if (i >= rowNum && rowNum != -1L)
                break;
            Map map = toAttributeMap(resultSet, dataSources, _rsmd, _useColumnLabel, _caseInsensitiveMap, _outputs);
            if (DataUtil.isNullOrEmpty(_beanClassName))
                __return.add(map);
            else {
                try {
                    Object bean = Reflection.newInstance(_beanClassName);
                    DataUtil.setProperties(map, bean);
                    __return.add(bean);
                } catch (Exception e) {
                    throw new SlxException(Tmodule.DATASOURCE, Texception.CAN_NOT_INSTANCE, e);
                }
            }
            /**
             * java.sql.ResultSet.next() move cursor to new row set.
             */
            if (!resultSet.next())
                break;
            i++;

        } while (true);
        long $_ = System.currentTimeMillis();
        createEventWork().createAndFireTimeEvent($_ - _$, "SQLTransform (" + __return.size() + " rows): ");
        return __return;
    }

    public static Map<String, ?> toAttributeMap(ResultSet resultSet) throws SQLException {
        return toAttributeMap(resultSet, (List<SQLDataSource>) null, null, true, null, null);
    }

    /**
     * @param resultSet
     * @param rsmd
     * @param useColumnLabel
     * @param caseInsensitiveMap
     * @param outputs
     * @return
     * @throws SQLException
     */
    public static Map<String, ?> toAttributeMap(ResultSet resultSet, List<SQLDataSource> dataSources, ResultSetMetaData rsmd, boolean useColumnLabel,
        Map<String, String> caseInsensitiveMap, List<String> outputs) throws SQLException {
        if (rsmd == null)
            rsmd = resultSet.getMetaData();
        int count = rsmd.getColumnCount();
        Map<String, Object> __return = new HashMap<String, Object>();
        for (int colCursor = 1; colCursor <= count; colCursor++) {
            String columnName;
            if (useColumnLabel)
                columnName = rsmd.getColumnLabel(colCursor);
            else
                columnName = rsmd.getColumnName(colCursor);
            if (caseInsensitiveMap != null && caseInsensitiveMap.get(columnName) != null)
                columnName = caseInsensitiveMap.get(columnName);
            Object obj = resultSet.getObject(colCursor);
            if (outputs != null && !outputs.contains(columnName))
                continue;
            if (obj == null) {
                __return.put(columnName, obj);
                continue;
            }
            if (dataSources != null) {
                for (Object o : dataSources) {
                    DataSource ds = (DataSource) o;
                    Tfield field = ds.getContext().getField(columnName);
                    if (field == null)
                        continue;
                    if ((obj instanceof Clob)) {
                        Reader read = resultSet.getCharacterStream(colCursor);
                        StringWriter sw = new StringWriter();
                        try {
                            IOUtil.copyCharacterStreams(read, sw);
                        } catch (Exception e) {
                            throw new SQLException(e.getMessage());
                        }
                        obj = sw.toString();
                    } else if (field.getType() == Efield.BOOLEAN) {
                        String sqlType = field.getSqlStorageStrategy();
                        if ("number".equals(sqlType) || "integer".equals(sqlType) || "singleChar10".equals(sqlType))
                            obj = "1".equals(obj.toString()) ? ((Object) (Boolean.TRUE)) : ((Object) (Boolean.FALSE));
                        else if ("singleCharYN".equals(sqlType))
                            obj = "Y".equals(obj.toString()) ? ((Object) (Boolean.TRUE)) : ((Object) (Boolean.FALSE));
                        else if ("singleCharTF".equals(sqlType))
                            obj = "T".equals(obj.toString()) ? ((Object) (Boolean.TRUE)) : ((Object) (Boolean.FALSE));
                        else
                            obj = Boolean.valueOf(obj.toString());

                    } else if (DataTools.isBinary(field)) {
                        obj = resultSet.getBinaryStream(colCursor);
                    } else if (field.getType() == Efield.DATE || field.getType() == Efield.DATETIME) {
                        String sqlType = field.getSqlStorageStrategy();
                        String dateFormat = field.getDateFormat();
                        if ("number".equals(sqlType) || "text".equals(sqlType) || DataUtil.isNotNullAndEmpty(dateFormat)) {
                            if (DataUtil.isNullOrEmpty(dateFormat))
                                dateFormat = "yyyyMMdd";
                            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                            try {
                                String str = obj.toString();
                                if (str.length() < dateFormat.length()) {
                                    for (int j = str.length(); j < dateFormat.length(); j++)
                                        str = (new StringBuilder()).append("0").append(str).toString();

                                }
                                obj = sdf.parse(str);
                            } catch (ParseException pe) {
                                log.warn((new StringBuilder()).append("Unable to parse a valid date, time or datetime out of value ").append(obj).append(
                                    " using format string ").append(dateFormat).toString());
                            }
                        } else if (obj instanceof Date) {
                            obj = new SLXDate(((Date) obj).getTime());
                        } else {
                            log.warn((new StringBuilder()).append("received a non-java.util.Date class: ").append(obj.getClass().getName()).append(
                                "for date field: ").append(field.getName()).toString());
                        }
                    } else {
                        obj = ds.transformFieldValue(field, obj);
                    }
                }

            }// END ?dataSources.
            __return.put(columnName, obj);
        }
        return __return;
    }

    public static List<Map<String, ?>> toListOfMaps(ResultSet rs) throws SQLException {
        return toListOfMaps(rs, -1L, false);
    }

    public static List<Map<String, ?>> toListOfMaps(ResultSet rs, SQLDriver driver) throws SQLException {
        return toListOfMaps(rs, -1L, hasBrokenCursorAPIs(driver));
    }

    public static List<Map<String, ?>> toListOfMaps(ResultSet rs, boolean brokenCursorAPIs) throws SQLException {
        return toListOfMaps(rs, -1L, brokenCursorAPIs);
    }

    public static List<Map<String, ?>> toListOfMaps(ResultSet rs, long numRows) throws SQLException {
        return toListOfMaps(rs, numRows, false);
    }

    public static List<Map<String, ?>> toListOfMaps(ResultSet rs, long numRows, SQLDriver driver) throws SQLException {
        return toListOfMaps(rs, numRows, hasBrokenCursorAPIs(driver));
    }

    public static List<Map<String, ?>> toListOfMaps(ResultSet resultSet, long numRows, boolean hasBrokenCursorAPIs) throws SQLException {
        List<Map<String, ?>> __return = new ArrayList<Map<String, ?>>(128);
        /**
         * get bean class name from datasource.
         */
        long _$ = System.currentTimeMillis();
        // If ResultSet is null.
        if (hasBrokenCursorAPIs) {
            if (!resultSet.next())
                return __return;
        } else {
            boolean isBeforeFirst = false;
            boolean isAfterLast = false;
            try {
                isBeforeFirst = resultSet.isBeforeFirst();
                isAfterLast = resultSet.isAfterLast();
            } catch (SQLException ignored) {
                log.debug("isBeforeFirst()/isAfterLast() throwing exceptions .", ignored);
            }
            if ((isBeforeFirst || isAfterLast || resultSet.getRow() == 0) && !resultSet.next())
                return __return;
        }
        long i = 0;
        do {
            if (i >= numRows && numRows != -1L)
                break;
            Map<String, ?> map = toAttributeMap(resultSet);
            __return.add(map);
            /**
             * java.sql.ResultSet.next() move cursor to new row set.
             */
            if (!resultSet.next())
                break;
            i++;

        } while (true);
        long $_ = System.currentTimeMillis();
        createEventWork().createAndFireTimeEvent($_ - _$, new StringBuilder().append("SQLTransform (" ).append( __return.size() ).append( " rows): ").toString());
        return __return;

    }

    public static Map<String, List<Object>> toMapOfLists(ResultSet rs) throws SQLException {
        Map<String, List<Object>> result = new HashMap<String, List<Object>>(128);
        ResultSetMetaData header = rs.getMetaData();
        for (int ii = 1; ii <= header.getColumnCount(); ii++)
            result.put(header.getColumnName(ii), new ArrayList<Object>());
        while (rs.next()) {
            int ii = 1;
            while (ii <= header.getColumnCount()) {
                result.get(header.getColumnName(ii)).add(rs.getObject(ii));
                ii++;
            }
        }
        return result;
    }

    public static List<List<Object>> toFormatList(ResultSet results, List<String> column) throws SQLException {

        List<List<Object>> __return = new ArrayList<List<Object>>(128);
        if (results == null)
            return __return;
        ResultSetMetaData header = results.getMetaData();
        List<Object> _tmp;
        boolean writeFlag = false;
        while (results.next()) {
            int i = 1;
            _tmp = new ArrayList<Object>();
            while (i <= header.getColumnCount()) {
                if (!writeFlag) {
                    if (column == null) {
                        column = new ArrayList<String>();
                    }
                    column.add(header.getColumnName(i));
                }
                _tmp.add(results.getObject(i));
                i++;
            }
            writeFlag = true;
            __return.add(_tmp);
        }
        return __return;

    }

    public static List<Object> toValuesList(ResultSet results, String column) throws SQLException {
        List<Object> valuesList = new ArrayList<Object>(128);
        do {
            if (!results.next())
                break;
            Object value = results.getObject(column.toUpperCase());
            if (value != null)
                valuesList.add(value);
        } while (true);
        return valuesList;
    }

    /**
     * @param <T>
     * @param results
     * @param clz
     * @return
     * @throws SQLException
     */
    public static <T> List<T> toListofBeans(ResultSet results, Class<T> clz) throws SQLException {
        List<?> list = toListOfMaps(results);
        List<T> _return = new ArrayList<T>();
        if (list == null)
            return null;
        try {
            for (Object o : list) {
                Map<Object, Object> data = null;
                if (o instanceof Map<?, ?>) {
                    data = (Map<Object, Object>) o;
                } else {
                    continue;
                }
                T obj = Reflection.newInstance(clz);
                DataUtil.setProperties(data, obj, false);
                _return.add(obj);
            }
        } catch (Exception e) {
            log.error("can not transform data to bean object", e);
        }
        return _return;

    }
    public static EventWorker createEventWork( ) {
        return createEventWork(SlxContext.getThreadSystemContext());
    }
    public static EventWorker createEventWork(final SystemContext sc) {
        EventWorkerFactory factory = EventWorkerFactory.getInstance();
        return factory.createWorker(sc);
    }
}
