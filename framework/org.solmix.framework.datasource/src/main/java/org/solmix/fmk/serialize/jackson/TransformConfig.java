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
package org.solmix.fmk.serialize.jackson;

import java.util.HashMap;
import java.util.Map;



/**
 * 
 * @author Administrator
 * @version 110035 2011-3-9
 */
public class TransformConfig {

   public class Record {
   private String className;
   private Object value;
   public Record(String clzName,Object v){
      this.setClassName(clzName);
      this.setValue(v);
   }

/**
 * @return the className
 */
public String getClassName() {
   return className;
}

/**
 * @param className the className to set
 */
public void setClassName(String className) {
   this.className = className;
}

/**
 * @return the value
 */
public Object getValue() {
   return value;
}

/**
 * @param value the value to set
 */
public void setValue(Object value) {
   this.value = value;
}
}
   private String[] filterProperties;

   private Map<String, Record> replaceMap;

   /**
    * @return the filterProperties
    */
   public String[] getFilterProperties() {
      return filterProperties;
   }

   /**
    * @param filterProperties the filterProperties to set
    */
   public void setFilterProperties(String[] filterProperties) {
      this.filterProperties = filterProperties;
   }

   /**
    * @return the replaceMap
    */
   public Map<String, Record> getReplaceMap() {
      return replaceMap;
   }

   /**
    * @param replaceMap the replaceMap to set
    */
   public void setReplaceMap(Map<String, Record> replaceMap) {
      this.replaceMap = replaceMap;
   }

   public void addToReplaceMap(String key, Record record) {
      if (replaceMap == null)
         replaceMap = new HashMap<String, Record>();
      replaceMap.put(key, record);
   }
}
