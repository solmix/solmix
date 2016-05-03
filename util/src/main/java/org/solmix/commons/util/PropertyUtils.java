/**
 * Copyright 2015 The Solmix Project
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
package org.solmix.commons.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.solmix.commons.xml.VariablesParser;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2016年4月15日
 */

public final class PropertyUtils
{
    private static final Pattern PROPERTY_LINE_PATTERN = Pattern.compile("([^=:]*)([=|:])(.*)");
    
   
    /**
     * 展开值
     * var1=ase
     * var2=232
     * var3=${var1}-${var2}
     * @param props
     */
    public static void expandVariables(Map<String,Object> props) {
        for (String key : props.keySet()) {
            props.put(key, expandVariables(key, props));
        }
    }
    
    /**
     * 加载配置文件
     * @param file
     * @return
     */
    public static Properties loadProperties (String file)  {
        FileInputStream fi = null;
        Properties props = new Properties();
        try {
            fi = new FileInputStream(file);
            props.load(fi);
        } catch (Exception exc) {
            throw new IllegalArgumentException(exc);
        } finally {
            IOUtils.closeQuietly(fi);
            
        }
        return props;
    }
    
    public static void storeProperties(String propFilePath, Map<String, String> props)  {
        if (props == null || props.size() < 1) {
            return;
        }
        PrintWriter writer = null;
        FileReader reader = null;
        try {
            reader = new FileReader(propFilePath);
            BufferedReader bufferedReader = new BufferedReader(reader);
            List<String> newLines = new ArrayList<String>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                newLines.add(processLine(line, props));
            }
            for (String key : props.keySet()) {
                newLines.add(key + " = " + props.get(key));
            }

            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(propFilePath), "UTF-8"));
            for (String aLineData : newLines) {
                writer.println(aLineData);
            }
            writer.flush();
            IOUtils.closeQuietly(bufferedReader);
        } catch (IOException exc) {
            String message = "Failed to store properties into the file: " + propFilePath;
            throw new IllegalStateException(message,exc);
        } finally {
            IOUtils.closeQuietly(writer);
            IOUtils.closeQuietly(reader);
        } 
    } 
    
    private static String processLine(String line, Map<String, String> props) {
        String result;

        Matcher matcher = PROPERTY_LINE_PATTERN.matcher(line);
        if (matcher.find()) {
            String key = matcher.group(1).trim().replaceAll("\\\\", "");
            String value = props.remove(key);
            if (value == null) {
                result = line;
            } else {
                result = matcher.group(1) + matcher.group(2) + value;
            }
        } else {
            result = line;
        }

        return result;
    }
    
    private static Object expandVariables(String key,Map<String,Object> props) {
        Object value = props.get(key);
        if(value==null){
            return null;
        }
        int idx = value.toString().indexOf("${");
        if(idx!=-1){
            return  VariablesParser.parse(value.toString(), props);
        }else{
            return props.get(key);
        }
       
    }
}
