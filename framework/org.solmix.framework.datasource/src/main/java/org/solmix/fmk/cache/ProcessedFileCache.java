
package org.solmix.fmk.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.commons.io.SlxFile;

public abstract class ProcessedFileCache
{

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
      cache = Collections.synchronizedMap( new HashMap() );
   }

   public void setStalenessCheckInterval( long millis )
   {
      stalenessCheckInterval = millis;
   }

   public long getStalenessCheckInterval()
   {
      return stalenessCheckInterval;
   }

   public void clearCacheEntry( Object key )
   {
      cache.remove( key );
   }

   public Object getObjectFromFile( SlxFile file ) throws Exception
   {
      return getObjectFromFile( file, null );
   }

   /**
    * Load Object form file.
    * <p>
    * At first,look in cache.if Staleness Check success,directly return the Object.Then no found,load from file.
    * 
    * @param file
    * @param flags file's state table,if the file is staleness put objectWasStale = true.
    * @return
    * @throws Exception
    */
   public Object getObjectFromFile( SlxFile file, Map flags ) throws Exception
   {
      String name = file.getCanonicalPath();
      CacheEntry entry = (CacheEntry) cache.get( name );
      long timeStamp = file.lastModified();
      if ( entry != null )
      {
         long currentTime = System.currentTimeMillis();
         if ( currentTime - entry.lastStalenessCheck <= stalenessCheckInterval )
            return entry.cachedObject;
         if ( timeStamp == 0L )
            log.warn( ( new StringBuilder() ).append( "Can't perform staleness checking for " ).append( name ).toString() );
         if ( entry.timeStamp == timeStamp )
         {
            entry.lastStalenessCheck = currentTime;
            return entry.cachedObject;
         }
         if ( log.isDebugEnabled() )
            log.debug( ( new StringBuilder() ).append( "STALE object for file '" ).append( name ).append( "', reloading " ).append(
               "(file timestamp " ).append( timeStamp ).append( ", cache timestamp " ).append( entry.timeStamp ).append( ")" ).toString() );
         if ( flags != null )
            flags.put( "objectWasStale", Boolean.TRUE );
      }
      Object loadedObject = loadObjectFromFile( file );
      cacheObject( name, loadedObject, timeStamp );
      return loadedObject;
   }

   /**
    * 从配置文件中加载实例
    * 
    * @param fileName 文件名
    * @return
    * @throws Exception
    */
   public Object getObjectFromFile( String fileName ) throws Exception
   {
      return getObjectFromFile( new SlxFile( fileName ) );
   }

   /**
    * Load Object form configuration file.
    * 
    * @param SlxFile config file.
    * @return
    * @throws Exception
    */
   public abstract Object loadObjectFromFile( SlxFile slxFile ) throws Exception;

   /**
    * 把对象放入缓存中，并加上时间戳
    * 
    * @param name
    * @param object
    * @param timeStamp 时间戳
    */
   public void cacheObject( String name, Object object, long timeStamp )
   {
      CacheEntry entry = new CacheEntry();
      entry.timeStamp = timeStamp;
      entry.cachedObject = object;
      entry.lastStalenessCheck = System.currentTimeMillis();
      cache.put( name, entry );
   }

   private static Logger log = LoggerFactory.getLogger( ProcessedFileCache.class.getName() );

   private long stalenessCheckInterval;

   Map cache;

}
