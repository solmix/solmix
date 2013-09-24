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
package org.solmix.sql;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035  2011-3-21
 */
@SuppressWarnings("unchecked")
public class SQLTable
{

   String tableName;

   List primaryKeys;

   Map columnTypes;

   Map sequences;

   Map native2DSColumnMap;

   public String quoteColumnNames;

   public SQLTable(String tableName, List primaryKeys, Map columnTypes, Map native2DSColumnMap, Map sequences, String quoteColumnNames)
   {
      this.tableName = tableName;
      this.primaryKeys = primaryKeys;
      this.columnTypes = columnTypes;
      this.sequences = sequences;
      this.native2DSColumnMap = native2DSColumnMap;
      this.quoteColumnNames = quoteColumnNames;
   }

   public String getName()
   {
      return tableName;
   }

   public List getPrimaryKeys()
   {
      return primaryKeys;
   }

   public Map getColumnTypes()
   {
      return columnTypes;
   }

   public String getColumnType(String columnName)
   {
      return (String) columnTypes.get(columnName);
   }

   public Map getSequences()
   {
      return sequences;
   }

   public String getSequence(String columnName)
   {
      return (String) sequences.get(columnName);
   }

   public Map getNative2DSColumnMap()
   {
      return native2DSColumnMap;
   }
}
