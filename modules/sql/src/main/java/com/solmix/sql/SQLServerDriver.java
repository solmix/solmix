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

import java.util.List;
import java.util.Map;

import com.solmix.api.datasource.DSRequest;
import com.solmix.api.exception.SlxException;


/**
 * 
 * @author solomon
 * @version 110035  2011-3-26
 */

public class SQLServerDriver extends SQLDriver
{

   /**
    * @param dbName
    * @param table
    * @throws Exception
    */
   public SQLServerDriver(String dbName, SQLTable table) throws Exception
   {
      super(dbName, table);
      // TODO Auto-generated constructor stub
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#escapeValue(java.lang.Object)
    */
   @Override
   public String escapeValue(Object obj)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#escapeValueForFilter(java.lang.Object, java.lang.String)
    */
   @Override
   public String escapeValueForFilter(Object obj, String s)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#fetchLastPrimaryKeys(java.util.Map, java.util.List,
    *      com.solmix.sql.SQLDataSource, com.solmix.api.datasource.DSRequest)
    */
   @Override
   public Map fetchLastPrimaryKeys(Map map, List list, SQLDataSource sqldatasource, DSRequest dsrequest) throws SlxException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#formatValue(java.lang.Object)
    */
   @Override
   public String formatValue(Object obj)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#getExpressionForSortBy(java.lang.String, java.util.Map)
    */
   @Override
   protected String getExpressionForSortBy(String s, Map map)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#isSupportsNativeReplace()
    */
   @Override
   public boolean isSupportsNativeReplace()
   {
      // TODO Auto-generated method stub
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
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#limitQuery(java.lang.String, long, long, java.util.List)
    */
   @Override
   public String limitQuery(String query, long startRow, long totalRows, List outputColumns) throws SlxException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#sqlOutTransform(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public String sqlOutTransform(String s, String s1, String s2) throws SlxException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#escapeValueUnquoted(java.lang.Object, boolean)
    */
   @Override
   protected String escapeValueUnquoted(Object value, boolean b)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.sql.SQLDriver#getNextSequenceValue(java.lang.String, com.solmix.sql.SQLDataSource)
    */
   @Override
   public String getNextSequenceValue( String s, SQLDataSource sqldatasource ) throws SlxException
   {
      // TODO Auto-generated method stub
      return null;
   }

}
