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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Tfield;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-3-23
 */
@SuppressWarnings("unchecked")
public class SQLOrderClause
{

   private static Logger log = LoggerFactory.getLogger(SQLOrderClause.class.getName());

   private final List<SQLDataSource> dataSources;

   private final Map remapTable;

   private Map column2TableMap;

   private final Map valueMaps;

   private final List<String> sortBy;

   private final boolean qualifyColumnNames;

   private List<String> customValueFields;

   public SQLOrderClause(DSRequest request, List dataSources, boolean qualifyColumnNames)
   {
      column2TableMap = new HashMap();
      customValueFields = null;
      this.dataSources = dataSources;
      this.qualifyColumnNames = qualifyColumnNames;
      sortBy = new ArrayList<String>();
      List realStorBy = request.getContext().getSortByFields();
      String _field;
      if (realStorBy != null)
      {
         for (Object order : realStorBy)
         {
            _field = (String) order;
            if (_field.startsWith("_"))
               _field = _field.substring(1);
            sortBy.add(_field);
         }
      }
      remapTable = SQLDataSource.getField2ColumnMap(dataSources);
      if (dataSources.size() > 1)
         column2TableMap = SQLDataSource.getColumn2TableMap(dataSources);
      valueMaps = SQLDataSource.getCombinedValueMaps(dataSources, sortBy);
   }

   public List getCustomValueFields()
   {
      return customValueFields;
   }

   public void setCustomValueFields(List fields)
   {
      customValueFields = fields;
   }

   public String getSQLString() throws SlxException
   {
      if (sortBy == null)
      {
         log.debug("no order data; returning empty string");
         return "";
      }
      StringBuffer __result = new StringBuffer();
      boolean __descending = false;
      SQLDriver driver = dataSources.get(0).getDriver();
      for (String fieldName : sortBy)
      {
         boolean customCheck = false;
         if (fieldName.startsWith("_"))
         {
            fieldName = fieldName.substring(1);
            if (this.customValueFields == null)
               continue;
            for (String field : customValueFields)
            {
               if (fieldName.equals(field))
                  customCheck = true;
            }
            __descending = true;
         }
         Tfield __f = null;
         String overrideTableName = null;
         boolean exclude = false;
         for (SQLDataSource ds : dataSources)
         {
            __f = ds.getContext().getField(fieldName);
            if (__f != null)
            {
               if (customCheck && __f.isCustomSQL())
                  exclude = true;
               if (__f.getTableName() != null)
                  overrideTableName = __f.getTableName();

               if (!exclude)
               {
                  String columnName = (String) remapTable.get(fieldName);
                  Map valueMap = (Map) valueMaps.get(fieldName);
                  if (columnName == null)
                     columnName = fieldName;
                  String tableName = overrideTableName;
                  if (tableName == null)
                     tableName = (String) column2TableMap.get(fieldName);
                  columnName = driver.escapeColumnName(columnName);
                  if (tableName == null && qualifyColumnNames)
                  {
                     DataSource firstDS = dataSources.get(0);
                     if (firstDS instanceof SQLDataSource)
                        tableName = ((SQLDataSource) firstDS).getTable().getName();
                  }
                  String unqualifiedColumnName = columnName;
                  if (!qualifyColumnNames && overrideTableName != null)
                     columnName = (new StringBuilder()).append(overrideTableName).append(".").append(columnName).toString();
                  else if (tableName != null)
                     columnName = (new StringBuilder()).append(tableName).append(".").append(columnName).toString();
                  if (__result.length() != 0)
                     __result.append(", ");
                  if (__f != null && (__f.getCustomSelectExpression() != null))
                     __result.append(unqualifiedColumnName);
                  else
                     __result.append(driver.getExpressionForSortBy(columnName, valueMap));
                  __result.append(__descending ? " DESC" : "");
               }
            }
         }
      }
      return __result.toString();
   }

   public int size()
   {
      return (sortBy != null) ? 1 : 0;
   }
}
