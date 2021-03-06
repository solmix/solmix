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

package org.solmix.fmk.call;

import org.solmix.api.call.RPCResponse;

/**
 * 
 * @version 110035
 */
public class RPCResponseImpl implements RPCResponse
{

   private Object data;

   private int status;

   public RPCResponseImpl()
   {
      setStatus( STATUS_SUCCESS );
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.api.call.RPCResponse#getData()
    */
   @Override
   public Object getData()
   {
      return data;
   }

   public RPCResponseImpl( Object data )
   {
      this();
      setData( data );
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.api.call.RPCResponse#getStatus()
    */
   @Override
   public int getStatus()
   {
      return status;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.api.call.RPCResponse#setData(java.lang.Object)
    */
   @Override
   public void setData( Object data )
   {
      this.data = data;

   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.api.call.RPCResponse#setStatus(int)
    */
   @Override
   public void setStatus( int status )
   {
      this.status = status;

   }

}
