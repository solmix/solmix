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

package com.solmix.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.solmix.commons.logs.Logger;
import com.solmix.sql.internal.SQLConfigManager;

/**
 * 
 * @author solomon
 * @version 110035 2011-9-9
 */

public class SQLMetaData
{

    public SQLMetaData()
    {
        this(null, null);
    }

    public SQLMetaData(Connection conn)
    {
        this("derivedFromConnection", conn);
    }

    public SQLMetaData(String database)
    {
        this(database, null);
    }

    public SQLMetaData(String database, Connection conn)
    {
        this.database = database;
        if (database == null)
            this.database = SQLConfigManager.defaultDatabase;
        this.conn = conn;
    }

    public Connection conn() throws SQLException {
        if (conn == null || conn.isClosed())
            try {
                conn = ConnectionManager.getConnection(database);
            } catch (Exception e) {
                throw new SQLException(e.toString());
            }
        return conn;
    }

    protected void finalize() throws Throwable {
        conn.close();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return conn().getMetaData();
    }

    public List<?> mapValuesAsList(List<Map<String, ?>> list) throws SQLException {
        List results = new ArrayList();
        Map map;
        for (Iterator i = list.iterator(); i.hasNext(); results.add(map.values().iterator().next())) {
            map = (Map) i.next();
            if (map.size() != 1)
                throw new SQLException((new StringBuilder()).append("Expected single key/value pair - map size is: ").append(map.size()).toString());
        }

        return results;
    }

    public List mapValuesAsListForKey(Object key, List list) throws SQLException {
        List results = new ArrayList();
        Object result;
        for (Iterator i = list.iterator(); i.hasNext(); results.add(result)) {
            Map map = (Map) i.next();
            if (map.size() < 1)
                throw new SQLException(
                    (new StringBuilder()).append("Expected at least one  key/value pair - map size is: ").append(map.size()).toString());
            result = map.get(key);
            if (result == null)
                result = map.get(key.toString().toLowerCase());
            if (result == null)
                result = map.get(key.toString().toUpperCase());
            if (result == null)
                throw new SQLException((new StringBuilder()).append("Unable to get value for key: ").append(key.toString()).toString());
        }

        return results;
    }

    public List mapValuesAsListForKeySet(Set keys, List list) throws SQLException {
        List results = new ArrayList();
        Map resultMap;
        label0: for (Iterator i = list.iterator(); i.hasNext(); results.add(resultMap)) {
            Map map = (Map) i.next();
            if (map.size() < 1)
                throw new SQLException(
                    (new StringBuilder()).append("Expected at least one  key/value pair - map size is: ").append(map.size()).toString());
            resultMap = new HashMap();
            Iterator j = keys.iterator();
            do {
                if (!j.hasNext())
                    continue label0;
                String key = j.next().toString();
                Object result = map.get(key);
                if (result == null)
                    result = map.get(key.toLowerCase());
                if (result == null)
                    result = map.get(key.toUpperCase());
                if (result != null)
                    resultMap.put(key, result);
            } while (true);
        }

        return results;
    }

    public Map<String, String> getProductNameAndVersion() throws SQLException {
        String name = getMetaData().getDatabaseProductName();
        String version = getMetaData().getDatabaseProductVersion();
        Map<String, String> map = new HashMap<String, String>();
        map.put("productName", name);
        map.put("productVersion", version);
        return map;
    }

    public List getCatalogs() throws SQLException {
        return mapValuesAsList(SQLTransform.toListOfMaps(getMetaData().getCatalogs()));
    }

    public List getSchemas() throws SQLException {
        return mapValuesAsList(SQLTransform.toListOfMaps(getMetaData().getSchemas()));
    }

    public List getTableTypes() throws SQLException {
        return mapValuesAsList(SQLTransform.toListOfMaps(getMetaData().getTableTypes()));
    }

    public List getViews(String catalog) throws SQLException {
        return getTablesOfType(catalog, "VIEW");
    }

    public List getTables(String catalog) throws SQLException {
        return getTablesOfType(catalog, "TABLE");
    }

    public List getTableNamesAndRemarks(String catalog, String schema, List types) throws SQLException {
        return getBasicTableDetailsForType(catalog, schema, types);
    }

    public List getTablesOfType(String catalog, String type) throws SQLException {
        String types[] = { type };
        List results = SQLTransform.toListOfMaps(getMetaData().getTables(catalog, null, null, types));
        return mapValuesAsListForKey("TABLE_NAME", results);
    }

    public List getBasicTableDetailsForType(String catalog, String schema, List types) throws SQLException {
        String typesArr[] = new String[types.size()];
        for (int i = 0; i < types.size(); i++)
            typesArr[i] = (String) types.get(i);

        List results = SQLTransform.toListOfMaps(getMetaData().getTables(catalog, schema, null, typesArr));
        Set keys = new HashSet();
        keys.add("TABLE_NAME");
        keys.add("REMARKS");
        keys.add("TABLE_SCHEM");
        keys.add("TABLE_TYPE");
        return mapValuesAsListForKeySet(keys, results);
    }

    public List getColumnNames(String catalog, String table) throws SQLException {
        List results = SQLTransform.toListOfMaps(getMetaData().getColumns(catalog, null, table, null));
        return mapValuesAsListForKey("COLUMN_NAME", results);
    }

    public List getPrimaryKeys(String catalog, String table) throws SQLException {
        java.sql.ResultSet rs = getMetaData().getPrimaryKeys(catalog, null, table);
        List results = SQLTransform.toListOfMaps(rs);
        return mapValuesAsListForKey("COLUMN_NAME", results);
    }

    public List getColumnMetaData(String catalog, String schemaName, String table) throws SQLException {
        List results = SQLTransform.toListOfMaps(getMetaData().getColumns(catalog, schemaName, table, null));
        if (results == null || results.size() == 0)
            return new ArrayList();
        else
            return results;
    }

    public Map getColumnMetaData(String catalog, String schemaName, String table, String column) throws SQLException {
        List results = SQLTransform.toListOfMaps(getMetaData().getColumns(catalog, schemaName, table, column));
        if (results == null || results.size() == 0)
            return new HashMap();
        else
            return (Map) results.get(0);
    }

    private static Logger log = new Logger(SQLMetaData.class.getName());

    // static String defaultDatabase;

    public String database;

    public Connection conn;

}
