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

package org.solmix.fmk.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Administrator
 * @version 110035 2011-6-22
 */

public class StructCache
{
   public static final long HOUR=60*60*1000L;
   public static final long MINUTE=60*1000L;
   public static final long SECOND=60*1000L;
   private static long stalenessCheckInterval;

   private static Map<Object,CacheEntry> cache;
   static{
      stalenessCheckInterval = MINUTE;
      cache = Collections.synchronizedMap(new HashMap<Object,CacheEntry>());
   }

   static class CacheEntry
   {

      public Object cachedObject;

      public long timeStamp;

      public long lastStalenessCheck;

      CacheEntry(Object obj,long stamp,long checkcycle)
      {
         this.cachedObject=obj;
         this.timeStamp=stamp;
         this.lastStalenessCheck=checkcycle;
      }
   }

   public StructCache()
   {
     
   }
   public static void clearCacheEntry(Object key)
   {
      cache.remove(key);
   }
   
   /**
    * @return the stalenessCheckInterval
    */
   public static long getStalenessCheckInterval()
   {
      return stalenessCheckInterval;
   }
   
   /**
    * @param stalenessCheckInterval the stalenessCheckInterval to set
    */
   public static void setStalenessCheckInterval(long stalenessCheckInterval)
   {
      StructCache.stalenessCheckInterval = stalenessCheckInterval;
   }
   public static void addCacheObject(String name,Object object){
      CacheEntry entry = new CacheEntry(object,System.currentTimeMillis(),stalenessCheckInterval);
      cache.put(name, entry);
   }
   public static Object getCacheObject(String name){
      CacheEntry entry= cache.get(name);
      if(entry!=null&&entry.cachedObject!=null){
         if((System.currentTimeMillis()-entry.timeStamp)<stalenessCheckInterval){
            entry.lastStalenessCheck=System.currentTimeMillis();
            return entry.cachedObject;
         }else{
            cache.remove(entry);
         }
      }
     return null;
   }
}
