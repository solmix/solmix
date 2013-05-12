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
package com.solmix.fmk.datasource;

import com.solmix.api.exception.SlxException;
import com.solmix.api.pool.ObjectCacheFactory;
import com.solmix.api.types.Texception;
import com.solmix.api.types.Tmodule;


/**
 * 
 * @author Administrator
 * @version 110035  2011-4-1
 */

public class SimpleDSCacheFactory implements ObjectCacheFactory
{

   private long stalenessCheckPeriod = 5000L;

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.api.pool.ObjectCacheFactory#create()
    */
   @Override
   public Object create( Object key ) throws SlxException
   {
      return DataSourceProvider.forName( key.toString() );
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.api.pool.ObjectCacheFactory#destory()
    */
   @Override
   public boolean destory() throws SlxException
   {
      throw new SlxException( Tmodule.DATASOURCE, Texception.NO_SUPPORT, "[" + SimpleDSCacheFactory.class.getName()
         + "] no support destory() method at this version." );
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.api.pool.ObjectCacheFactory#getStalenessCheckPeriod()
    */
   @Override
   public long getStalenessCheckPeriod()
   {
      return stalenessCheckPeriod;
   }

   public void setStalenessCheckPeriod( long stalenessCheckPeriod )
   {
      this.stalenessCheckPeriod = stalenessCheckPeriod;
   }
}
