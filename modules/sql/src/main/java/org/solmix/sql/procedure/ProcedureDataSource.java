/*
 * SOLMIX PROJECT
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

package org.solmix.sql.procedure;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
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
import org.solmix.fmk.datasource.DSResponseImpl;
import org.solmix.fmk.velocity.Velocity;
import org.solmix.sql.ConnectionManager;

public final class ProcedureDataSource
{

    private Connection conn;

    public static String INPUT = "in";

    public static String OUTPUT = "out";

    private ConnectionManager connectionManager;
    private static final Logger log = LoggerFactory.getLogger(ProcedureDataSource.class.getName());

    @SuppressWarnings("unchecked")
    public DSResponse update(DSRequest req, DataSource ds) throws SlxException {
        DSResponse __resp = new DSResponseImpl();
        DSResponseData respData = new DSResponseData();
        DataSourceData data = ds.getContext();
        Map<Object, Object> raws = req.getContext().getValues();
        Eoperation _optType = req.getContext().getOperationType();
        String _opID = req.getContext().getOperationId();
        String sql = "";
        String ins = null;
        ToperationBinding _bind = data.getOperationBinding(_optType, _opID);
        if (_bind != null) {
            sql = DataSourceData.getCustomSQL(_bind).trim();
            ins = DataSourceData.getValuesClause(_bind);
        }
        Map context = Velocity.getStandardContextMap(req);
        String explictSQL = Velocity.evaluateAsString(sql, context);
        //XXX
        boolean printSQL = true;
        if (printSQL)
            log.info(explictSQL);

        try {
            conn = connectionManager.get(getDbName(data));
            CallableStatement pre = conn.prepareCall(explictSQL);
            List l = (List) raws.get(INPUT);
            int inputLength = l.size();
            for (int j = 1; j <= inputLength; j++) {
                Object objValue = l.get(j - 1);
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
            List out = (List) raws.get(OUTPUT);
            if (out != null && out.size() == 1) {
                pre.registerOutParameter(inputLength + 1, Types.INTEGER);
            }
            pre.execute();
            respData.setData(pre.getInt(inputLength + 1));
            respData.setStatus(Status.STATUS_SUCCESS);
        } catch (SQLException e) {
            respData.setStatus(Status.STATUS_FAILURE);
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            log.error("[SQLEXCEPTION]" + e.getLocalizedMessage());
        } catch (IOException e) {
            respData.setStatus(Status.STATUS_FAILURE);
            throw new SlxException(Tmodule.SQL, Texception.IO_EXCEPTION, e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null)
                connectionManager.free(conn);

        }
        __resp.setContext(respData);
        return __resp;
    }

    private String getDbName(DataSourceData data) {
        String dbName = data.getTdataSource() != null ? data.getTdataSource().getDbName() : null;
        return dbName;
    }
}
