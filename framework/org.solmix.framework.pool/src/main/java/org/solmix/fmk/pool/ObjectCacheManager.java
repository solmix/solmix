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

package org.solmix.fmk.pool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.api.exception.SlxException;
import org.solmix.api.pool.ObjectCacheFactory;

/**
 * 
 * @author Administrator
 * @version 110035 2011-4-1
 */

public class ObjectCacheManager
{

   /**
    * Generated serial version unique ID
    */
   private static final long serialVersionUID = -8378789581870340957L;

   private static long STALNESS_CHECK_INTERL = 1000L;

   private final Map< String ,CacheEntry > cache = new ConcurrentHashMap< String ,CacheEntry >();

   private long stalenessCheckPeriod;

   private static final Logger log =  LoggerFactory.getLogger( ObjectCacheManager.class.getName() );

   private ObjectCacheFactory factory;

   private ObjectCacheManager()
   {

   }

   public ObjectCacheManager( ObjectCacheFactory factory )
   {
      this.factory = factory;
   }

   /**
    * @return the stalenessCheckPeriod
    */
   public long getStalenessCheckPeriod()
   {
      return stalenessCheckPeriod;
   }

   /**
    * @param stalenessCheckPeriod the stalenessCheckPeriod to set
    */
   public void setStalenessCheckPeriod( long stalenessCheckPeriod )
   {
      this.stalenessCheckPeriod = stalenessCheckPeriod;
   }

   public Object get( Object key ) throws SlxException
   {
      long _currentTime = System.currentTimeMillis();
      if ( stalenessCheckPeriod == 0 )
         stalenessCheckPeriod = factory.getStalenessCheckPeriod();
      if ( stalenessCheckPeriod == 0 )
         stalenessCheckPeriod = STALNESS_CHECK_INTERL;
      CacheEntry entry = cache.get( key );
      if ( entry == null )
      {
         if ( log.isDebugEnabled() )
         log.debug( "Not found object with key [" + key.toString() + "] in the cache,try to create a new object and put it in the cache." );
         Object cacheObject = factory.create( key );
         cache.put( key.toString(), new CacheEntry( cacheObject ) );
         return cacheObject;
      }
      if ( _currentTime - entry.getLastStalenessCheck() < stalenessCheckPeriod )
      {
         cache.get( key ).setLastStalenessCheck( _currentTime );
         return entry;
      } else
      {
         Object cacheObject = factory.create( key );
         cache.put( key.toString(), new CacheEntry( cacheObject ) );
         return cacheObject;
      }
   }

   public void put( Object key, Object cacheObject )
   {
      cache.put( key.toString(), new CacheEntry( cacheObject ) );
   }

   public void put( Object key, CacheEntry entry )
   {
      cache.put( key.toString(), entry );
   }
}
