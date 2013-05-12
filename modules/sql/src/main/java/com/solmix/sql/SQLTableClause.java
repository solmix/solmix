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

import java.util.Iterator;
import java.util.List;

import com.solmix.commons.logs.Logger;
import com.solmix.commons.util.DataUtil;
import com.solmix.api.exception.SlxException;

/**
 * 
 * @author solomon
 * @version 110035 2011-3-22
 */

public class SQLTableClause
{

    private static Logger log = new Logger(SQLTableClause.class.getName());

    private List<String> relatedTables;

    /**
     * @return the relatedTables
     */
    public List<String> getRelatedTables() {
        return relatedTables;
    }

    /**
     * @param relatedTables the relatedTables to set
     */
    public void setRelatedTables(List<String> relatedTables) {
        this.relatedTables = relatedTables;
    }

    private final List<SQLDataSource> dataSources;

    public SQLTableClause(SQLDataSource ds)
    {
        this(DataUtil.makeList(ds));
    }

    public SQLTableClause(List<SQLDataSource> dataSources)
    {
        this.dataSources = dataSources;
    }

    public String getSQLString() throws SlxException {
        if (dataSources == null) {
            log.debug("No datasources, can't generate table clause");
            return "";
        }
        StringBuffer _buf = new StringBuffer();
        Iterator<SQLDataSource> i = dataSources.iterator();
        while (i.hasNext()) {
            SQLDataSource ds = i.next();
            String shema = ds.getContext().getTdataSource().getSqlSchema();
            if (shema != null) {
                _buf.append(shema).append(ds.getDriver().getQualifiedSchemaSeparator());
            }
            _buf.append(ds.getTable().getName());
            if (DataUtil.isNotNullAndEmpty(relatedTables))
                for (String table : relatedTables)
                    _buf.append("," + table);
            if (i.hasNext())
                _buf.append(", ");
        }
        return _buf.toString();
    }
}
