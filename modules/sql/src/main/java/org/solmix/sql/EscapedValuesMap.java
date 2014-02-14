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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.solmix.commons.util.DataUtil;


/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035  2011-3-26
 */

@SuppressWarnings( { "unchecked", "serial" })
public class EscapedValuesMap extends HashMap
{

   enum Mode
   {
      FITER(2) , EQUAL(3) , SUBSTRING(4);
      private int value;

      Mode(int value)
      {
         this.value = value;
      }
   }

   private List<SQLDataSource> dataSources;

   private Mode mode;

   private SQLDataSource firstDS;

   private boolean isJoin;

   public EscapedValuesMap(Map m, SQLDataSource ds, Mode mode)
   {
      this(m, DataUtil.makeList(ds), mode);
   }

   public EscapedValuesMap(Map m, List<SQLDataSource> dataSources, Mode mode)
   {
      this.dataSources = dataSources;
      this.mode = mode;
      firstDS = (SQLDataSource) dataSources.get(0);
      isJoin = dataSources.size() > 1;
      if (m != null)
         putAll(m);
   }
   public Object get(Object o){
      Object rawValue = super.get(o);
      if(!(rawValue instanceof Number) && !(rawValue instanceof String) && !(rawValue instanceof Date))
          return rawValue;
      if (rawValue == null)
         if (mode == Mode.EQUAL || mode == Mode.SUBSTRING)
            return "('1'='1')";
         else
            return null;
      String column = (String) o;
      SQLDataSource ds = getDSForColumn(column);
      if (ds == null)
         ds = firstDS;
      SQLDriver driver = ds.getDriver();
      String escapedColumnName = ds.escapeColumnName(column);
      if (isJoin)
         escapedColumnName = (new StringBuilder()).append(ds.getTable().getName()).append(".").append(escapedColumnName).toString();
      switch (mode)
      {
         case FITER:
            return ds.valueForWhereClause(rawValue, column, true);
         case EQUAL:
            return (new StringBuilder()).append("(").append(escapedColumnName).append("=").append(ds.valueForWhereClause(rawValue, column)).append(
               ")").toString();
         case SUBSTRING:
            return (new StringBuilder()).append("(LOWER(").append(escapedColumnName).append(")").append(" LIKE ").append(
               ds.escapeValueForFilter(rawValue)).append(")").append(driver.escapeClause()).toString();
         default:
            return ds.valueForWhereClause(rawValue, column);
      }
   }
   private SQLDataSource getDSForColumn(Object columnName)
   {
       if(dataSources == null)
           return null;
       for(Iterator i = dataSources.iterator(); i.hasNext();)
       {
           SQLDataSource ds = (SQLDataSource)i.next();
         if (ds.getContext().getColumnName((String) columnName) != null)
               return ds;
       }

       return null;
   }
}
