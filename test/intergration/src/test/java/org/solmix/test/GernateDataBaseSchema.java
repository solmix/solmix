/*
 *  Copyright 2012 The Solmix Project
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
package org.solmix.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.solmix.sql.SQLTransform;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013年12月17日
 */

public class GernateDataBaseSchema 
{




    @Test
    public void test2() throws IOException {
        try {
            Connection conn= DriverManager.getConnection("jdbc:mysql://localhost:3306/solmix", "root", "solmix");
            String catalog=conn.getCatalog();
            DatabaseMetaData dmd= conn.getMetaData();
           ResultSet rs= dmd.getTables(catalog, null, null, null);
           List<Map<String, ?>> lms= /*SQLTransform.*/toListOfMaps(rs);
           StringBuffer sb = new StringBuffer();
           for(Map<String, ?> lm:lms){
               if(lm.get("TABLE_NAME")!=null&&"TABLE".equals(lm.get("TABLE_TYPE"))){
                   sb.append("表名,"+lm.get("TABLE_NAME")+"\n");
                   List<Map<String, ?>> cls= SQLTransform.toListOfMaps(dmd.getColumns(catalog, null, lm.get("TABLE_NAME").toString(), null));
                   sb.append("Data Item /Column Name ,  Data Type / Length  ,    Description, Valid/ Default Value ,   Null Allowed? (Y/N)\n");
                   for(Map<String, ?> cl:cls){
                       sb.append(cl.get("TABLE_NAME")+"/"+cl.get("COLUMN_NAME")).append(",");
                      
                       sb.append(cl.get("TYPE_NAME")+"/"+cl.get("COLUMN_SIZE")).append(",");
                       sb.append(" ,");
                       sb.append((cl.get("COLUMN_DEF")==null?"":cl.get("COLUMN_DEF"))).append(",");
                       sb.append(cl.get("IS_NULLABLE")).append(",");
                       sb.append("\n");
                   }
                   sb.append("\n");
               }
           }
           File f= new File("a.csv");
           if(!f.exists()){
               f.createNewFile();
           }else{
               f.delete();
               f.createNewFile();
           }
           FileOutputStream fos= new FileOutputStream(f);
           fos.write(sb.toString().getBytes());
           fos.close();
           
            System.out.println(catalog);
            Assert.assertNotNull(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param rs
     * @return
     * @throws SQLException 
     */
    private List<Map<String, ?>> toListOfMaps(ResultSet resultSet) throws SQLException {
        List<Map<String, ?>> __return = new ArrayList<Map<String, ?>>(128);
        long i = 0;
        do {
            if (!resultSet.next())
                break;
            Map<String, ?> map = toAttributeMap(resultSet);
            __return.add(map);
            /**
             * java.sql.ResultSet.next() move cursor to new row set.
             */
            
            i++;

        } while (true);
        return __return;
    }


    /**
     * @param resultSet
     * @return
     * @throws SQLException 
     */
    private Map<String, ?> toAttributeMap(ResultSet resultSet) throws SQLException {
        ResultSetMetaData  rsmd = resultSet.getMetaData();
        int count = rsmd.getColumnCount();
        Map<String, Object> __return = new HashMap<String, Object>();
        for (int colCursor = 1; colCursor <= count; colCursor++) {
            String columnName = rsmd.getColumnLabel(colCursor);
            Object obj = resultSet.getObject(colCursor);
                __return.put(columnName, obj);
        }
        return __return;
    }

}
