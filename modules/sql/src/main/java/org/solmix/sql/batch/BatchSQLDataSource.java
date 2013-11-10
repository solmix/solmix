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

package org.solmix.sql.batch;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.data.DSResponseData;
import org.solmix.api.data.DataSourceData;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DSResponse.Status;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Eoperation;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.datasource.DSResponseImpl;
import org.solmix.fmk.velocity.Velocity;
import org.solmix.sql.ConnectionManager;
import org.solmix.sql.SQLTransform;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-9-17
 */

public final class BatchSQLDataSource
{

    private static final Logger log = LoggerFactory.getLogger(BatchSQLDataSource.class.getName());

    private String dbName;
    private ConnectionManager connectionManager;
    protected String getDbName(DataSourceData data) {
        if (this.dbName == null) {
            dbName = data.getTdataSource() != null ? data.getTdataSource().getDbName() : null;
        }
        return dbName;
    }

    public void freeConnection(Connection connection) throws SlxException {
        connectionManager.free(connection);

    }

    public DSResponse fetch(DSRequest req, DataSource ds) throws SlxException {
        DSResponse __resp = new DSResponseImpl(ds,req);
        DSResponseData respData = new DSResponseData();
        DataSourceData data = ds.getContext();
        Connection conn = null;
        try {
            Map context = Velocity.getStandardContextMap(req);
            conn = connectionManager.get(getDbName(data));
            Eoperation _optType = req.getContext().getOperationType();
            String _opID = req.getContext().getOperationId();
            ToperationBinding _bind = data.getOperationBinding(_optType, _opID);
            String sql = "";
            if (_bind != null)
                sql = DataSourceData.getCustomSQL(_bind);
            String explictSQL = Velocity.evaluateAsString(sql, context);

            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = pstmt.executeQuery(explictSQL);
            List<String> columns = new ArrayList<String>();
            List<List<Object>> result = SQLTransform.toFormatList(rs, columns);
            int row = result.size();

            respData.setStartRow(0);
            respData.setEndRow(row);
            respData.setTotalRows(row);
            // union data.
            List<Object> columnNames = new ArrayList<Object>();
            for (String key : columns) {
                columnNames.add(key);
            }
            result.add(columnNames);
            respData.setData(result);
            if (conn != null) {
                conn.commit();

            }
            respData.setStatus(Status.STATUS_SUCCESS);
        } catch (SQLException e) {
            respData.setStatus(Status.STATUS_FAILURE);
            throw new SlxException(Tmodule.SQL, Texception.SQL_SQLEXCEPTION, e);

        } finally {
            connectionManager.free(conn);
        }
        __resp.setContext(respData);
        return __resp;
    }

    public DSResponse add(DSRequest req, DataSource ds) throws SlxException {
        DSResponse __resp = new DSResponseImpl(ds,req);
        DSResponseData respData = new DSResponseData();
        Connection conn = null;
        List data_holder = null;
        String explictSQL = "";
        List<Object> columnNames = null;
        try {
            DataSourceData data = ds.getContext();

            List<Object> values = req.getContext().getValueSets();
            columnNames = (List<Object>) values.get(values.size() - 1);
            Eoperation _optType = req.getContext().getOperationType();
            String _opID = req.getContext().getOperationId();
            String sql = "";
            String ins = null;
            ToperationBinding _bind = data.getOperationBinding(_optType, _opID);
            if (_bind != null) {
                sql = DataSourceData.getCustomSQL(_bind);
                ins = DataSourceData.getValuesClause(_bind);
            }
            int[] pos = null;
            if (ins != null) {
                List<String> na = DataUtil.simpleSplit(ins, ",");
                pos = new int[na.size()];
                for (int i = 0; i < na.size(); i++) {
                    for (int j = 0; j < columnNames.size(); j++) {
                        if (na.get(i).equalsIgnoreCase(columnNames.get(j).toString())) {
                            pos[i] = j;
                        }
                    }
                }
            }

            Map context = Velocity.getStandardContextMap(req);
            explictSQL = Velocity.evaluateAsString(sql, context);

            conn = connectionManager.get(getDbName(data));

            PreparedStatement pre = conn.prepareStatement(explictSQL);
            int affectRow = 0;
            boolean first = true;
            for (int i = 0; i < values.size() - 1; i++) {
                Object obj = values.get(i);

                if (obj instanceof List) {
                    List map = (List) obj;
                    data_holder = map;
                    if (first) {
                        if (pos == null)
                            pos = new int[map.size()];
                        for (int p = 0; p < map.size(); p++)
                            pos[p] = p;
                        first = false;
                    }

                    for (int j = 1; j <= pos.length; j++) {
                        Object objValue = map.get(pos[j - 1]);
                        if (objValue instanceof String) {
                            pre.setString(j, objValue.toString());
                        } else if (objValue instanceof Double) {
                            pre.setDouble(j, (Double) objValue);
                        } else if (objValue instanceof Long) {
                            pre.setLong(j, (Long) objValue);
                        } else if (objValue instanceof Short) {
                            pre.setShort(j, (Short) objValue);
                        } else if (objValue instanceof Integer) {
                            pre.setInt(j, (Integer) objValue);
                        } else if (objValue instanceof Float) {
                            pre.setFloat(j, (Float) objValue);
                        } else if (objValue instanceof BigDecimal) {
                            pre.setBigDecimal(j, (BigDecimal) objValue);
                        } else if (objValue instanceof java.sql.Timestamp) {
                            pre.setTimestamp(j, (java.sql.Timestamp) objValue);
                        } else if (objValue instanceof InputStream) {
                            InputStream is = (InputStream) pre;
                            pre.setBinaryStream(j, is, is.available());
                        } else if (objValue instanceof StringBuffer) {
                            StringBuffer sb = (StringBuffer) objValue;
                            pre.setCharacterStream(j, new StringReader(sb.toString()), sb.length());
                        } else if (objValue instanceof Date) {
                            java.sql.Date date = new java.sql.Date(((Date) objValue).getTime());
                            pre.setDate(j, date);
                        } else if (objValue instanceof java.sql.Time) {
                            pre.setTime(j, (java.sql.Time) objValue);
                        } else {
                            pre.setNull(j, 2);
                        }
                    }
                    pre.addBatch();
                }
                if ((i + 1) % 100 == 0) {
                    int[] res = pre.executeBatch();
                    affectRow = affectRow + res.length;
                    pre.clearBatch();
                }
                // int[] res= pre.executeBatch();
                // affectRow=affectRow+res.length;
                // pre.clearBatch();
            }
            int[] res = pre.executeBatch();
            affectRow = affectRow + res.length;
            conn.commit();
            respData.setStatus(Status.STATUS_SUCCESS);
            respData.setTotalRows(affectRow);
        } catch (SQLException e) {
            respData.setStatus(Status.STATUS_FAILURE);
            log.error("[BAD-DATA]" + data_holder.toString());
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            log.error("[SQLEXCEPTION]" + e.getLocalizedMessage());
        } catch (IOException e) {
            respData.setStatus(Status.STATUS_FAILURE);
            throw new SlxException(Tmodule.SQL, Texception.IO_EXCEPTION, e);
        } finally {
            if (conn != null)
                connectionManager.free(conn);

        }
        __resp.setContext(respData);
        return __resp;
    }

    public DSResponse update(DSRequest req, DataSource ds) throws SlxException {
        return add(req, ds);
    }
}
