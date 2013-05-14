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
import java.util.Map;

import com.solmix.commons.logs.Logger;
import com.solmix.api.datasource.DSRequest;
import com.solmix.api.exception.SlxException;
import com.solmix.api.types.Texception;
import com.solmix.api.types.Tmodule;

/**
 * 
 * @author solomon
 * @version 110035  2011-3-31
 */

public class HSQLDBDriver extends SQLDriver
{

   private static final Logger log = new Logger(HSQLDBDriver.class.getName());
   boolean supportsSQLLimit;

   public HSQLDBDriver(String dbName, SQLTable table) throws SlxException
   {
      super(dbName, table);
      supportsSQLLimit = true;
      init(dbName);
   }

   public HSQLDBDriver(String dbName) throws SlxException
   {
      super(dbName, null);
      supportsSQLLimit = true;
      init(dbName);
   }

   @Override
   public boolean hasBrokenCursorAPIs()
   {
      return true;
   }
   public static SQLDriver instance(String dbName, SQLTable table) throws SlxException
   {
      return new HSQLDBDriver(dbName, table);
   }

   public static SQLDriver instance(String dbName) throws SlxException
   {
      return new HSQLDBDriver(dbName);
   }

   public void init(String s) throws SlxException
   {
   }
   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#escapeValue(java.lang.Object)
    */
   @Override
   public String escapeValue(Object value)
   {
      if (value == null)
         return null;
      else
         return (new StringBuilder()).append("'").append(escapeValueUnquoted(value.toString(), false)).append("'").toString();
   }

   @Override
   public String escapeValueUnquoted(Object value, boolean escapeForFilter)
   {
      if (value == null)
         return null;
      String escaped = globalPerl.substitute("s/'/''/g", value.toString());
      if (escapeForFilter)
      {
         escaped = globalPerl.substitute("s'\\\\'\\\\'g", escaped);
         escaped = globalPerl.substitute("s'%'\\%'g", escaped);
         escaped = globalPerl.substitute("s'_'\\_'g", escaped);
      }
      return escaped;
   }
   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#escapeValueForFilter(java.lang.Object, java.lang.String)
    */
   @Override
   public String escapeValueForFilter(Object value, String filterStyle)
   {
      if (value == null)
         return null;
      String rtn = "'";
      if (!"startsWith".equals(filterStyle))
         rtn = (new StringBuilder()).append(rtn).append("%").toString();
      return (new StringBuilder()).append(rtn).append(escapeValueUnquoted(value, true)).append("%'").toString();
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#fetchLastPrimaryKeys(java.util.Map, java.util.List,
    *      com.solmix.sql.SQLDataSource, com.solmix.api.datasource.DSRequest)
    */
   @Override
   public Map fetchLastPrimaryKeys(Map primaryKeysPresent, List sequencesNotPresent, SQLDataSource ds, DSRequest req) throws SlxException
   {
      if ( log.isDebugEnabled() )
      log.debug((new StringBuilder()).append("fetchLastRow data - primaryKeysPresent: ").append(primaryKeysPresent.toString()).append(
         "sequencesNotPresent: ").append(sequencesNotPresent.toString()).toString());
      if (sequencesNotPresent.size() > 1)
         throw new SlxException("HSQLDB can't handle more than one auto_increment primary_key");
      if (dbConnection == null && req == null)
         throw new SlxException("no connection exists for last row fetch");
      java.sql.Statement sqlStatement = null;
      Map primaryKeys = primaryKeysPresent;
      if (!sequencesNotPresent.isEmpty())
      {
         String sequenceName = (String) sequencesNotPresent.get(0);
         Object obj = getScalarResult("CALL IDENTITY()", dbConnection, dbName, this, req).toString();
         Long sequenceValue = new Long(obj.toString());
         primaryKeys.put(sequenceName, sequenceValue);
      }
      return primaryKeys;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#formatValue(java.lang.Object)
    */
   @Override
   public String formatValue(Object value)
   {
      return value.toString();
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#getExpressionForSortBy(java.lang.String, java.util.Map)
    */
   @Override
   protected String getExpressionForSortBy(String column, Map valueMap)
   {
      if (valueMap == null || valueMap.size() == 0)
      return column;
  else
      return caseExpression(column, valueMap.entrySet().iterator());
}

   private String caseExpression(String column, Iterator entries)
   {
      if (!entries.hasNext())
      {
         return column;
      } else
      {
         java.util.Map.Entry entry = (java.util.Map.Entry) entries.next();
         String actualValue = (String) entry.getKey();
         String displayValue = (String) entry.getValue();
         return (new StringBuilder()).append("CASEWHEN(").append(column).append("='").append(actualValue).append("', '").append(displayValue).append(
            "', ").append(caseExpression(column, entries)).append(")").toString();
      }
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#isSupportsNativeReplace()
    */
   @Override
   public boolean isSupportsNativeReplace()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#limitQuery(java.lang.String, long, long, java.util.List, java.lang.String)
    */
   @Override
   public String limitQuery(String query, long startRow, long totalRows, List outputColumns, String orderClause) throws SlxException
   {
      throw new SlxException(Tmodule.SQL, Texception.NO_SUPPORT, "Not supported");
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#limitQuery(java.lang.String, long, long, java.util.List)
    */
   @Override
   public String limitQuery(String query, long startRow, long batchSize, List outputColumns) throws SlxException
   {
      return (new StringBuilder()).append("SELECT LIMIT ").append(startRow).append(" ").append(batchSize).append(" ").append(
         query.substring("SELECT".length())).toString();
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#sqlOutTransform(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public String sqlOutTransform(String columnName, String remapName, String tableName) throws SlxException
   {
      String output = escapeColumnName(columnName);
      if (remapName != null && !remapName.equals(columnName))
         output = (new StringBuilder()).append(output).append(" AS ").append(escapeColumnName(remapName)).toString();
      if (tableName != null)
         output = (new StringBuilder()).append(tableName).append(".").append(output).toString();
      return output;
   }

   @Override
   public String getNextSequenceValue( String columnName, SQLDataSource ds ) throws SlxException
   {
      return "null";
   }
}