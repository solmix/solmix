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
package com.solmix.fmk.repo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.commons.io.SlxFile;


/**
 * 
 * @version 110035
 */
public abstract class ProcessedFileCache
{
   private static Logger log = LoggerFactory.getLogger(ProcessedFileCache.class.getName());

   private long stalenessCheckInterval;

   Map cache;

   static class CacheEntry
   {

      public Object cachedObject;

      public long timeStamp;

      public long lastStalenessCheck;

      CacheEntry()
      {
      }
   }

   public ProcessedFileCache()
   {
      stalenessCheckInterval = 500L;
      cache = Collections.synchronizedMap(new HashMap());
   }

   public void setStalenessCheckInterval(long millis)
   {
      stalenessCheckInterval = millis;
   }

   public long getStalenessCheckInterval()
   {
      return stalenessCheckInterval;
   }

   public void clearCacheEntry(Object key)
   {
      cache.remove(key);
   }

   public Object getObjectFromFile(SlxFile file) throws Exception
   {
      return getObjectFromFile(file, null);
   }

   /**
    * 从配置文件中获取实例，首先在cache中查找，并且时间戳与文件的一样，那么就不用重新加载文件，否则重新加载文件
    * 
    * @param file
    * @param flags
    *           如果为非NULL,标示给文件为有状态文件
    * @return
    * @throws Exception
    */
   public Object getObjectFromFile(SlxFile file, Map flags) throws Exception
   {
      String name = file.getCanonicalPath();
      CacheEntry entry = (CacheEntry) cache.get(name);
      long timeStamp = file.lastModified();
      if (entry != null) {
         long currentTime = System.currentTimeMillis();
         if (currentTime - entry.lastStalenessCheck <= stalenessCheckInterval)
            return entry.cachedObject;
         if (timeStamp == 0L)
            log.warn((new StringBuilder()).append("Can't perform staleness checking for ").append(name).toString());
         if (entry.timeStamp == timeStamp) {
            entry.lastStalenessCheck = currentTime;
            return entry.cachedObject;
         }
         if (log.isDebugEnabled())
            log.debug((new StringBuilder()).append("STALE object for file '").append(name).append("', reloading ")
               .append("(file timestamp ").append(timeStamp).append(", cache timestamp ").append(entry.timeStamp)
               .append(")").toString());
         if (flags != null)
            flags.put("objectWasStale", Boolean.TRUE);
      }
      Object loadedObject = loadObjectFromFile(file);
      cacheObject(name, loadedObject, timeStamp);
      return loadedObject;
   }

   /**
    * 从配置文件中加载实例
    * 
    * @param fileName
    *           文件名
    * @return
    * @throws Exception
    */
   public Object getObjectFromFile(String fileName) throws Exception
   {
      return getObjectFromFile(new SlxFile(fileName));
   }

   /**
    * 把配置文件加载到配置表中。
    * 
    * @param iscfile
    *           文件
    * @return
    * @throws Exception
    */
   public abstract Object loadObjectFromFile(SlxFile iscfile) throws Exception;

   /**
    * 把对象放入缓存中，并加上时间戳
    * 
    * @param name
    * @param object
    * @param timeStamp
    *           时间戳
    */
   public void cacheObject(String name, Object object, long timeStamp)
   {
      CacheEntry entry = new CacheEntry();
      entry.timeStamp = timeStamp;
      entry.cachedObject = object;
      entry.lastStalenessCheck = System.currentTimeMillis();
      cache.put(name, entry);
   }
}
