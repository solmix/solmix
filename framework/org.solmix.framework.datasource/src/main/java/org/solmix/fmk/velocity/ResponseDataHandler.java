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

package org.solmix.fmk.velocity;

import org.solmix.api.call.DSCall;
import org.solmix.api.datasource.DSRequest;

public class ResponseDataHandler
{

   public ResponseDataHandler( DSCall rpc, DSRequest dsRequest )
   {
      this.rpc = rpc;
      this.dsRequest = dsRequest;
   }

   public Object getFirst()
   {
      return first( null, null );
   }

   public Object first( String ds )
   {
      return first( ds, null );
   }

   public Object first( String ds, String operation )
   {
      // return rpc.findResponseData("first", ds, operation, dsRequest);
      return null;
   }

   public Object getLast()
   {
      return last( null, null );
   }

   public Object last( String ds )
   {
      return last( ds, null );
   }

   public Object last( String ds, String operation )
   {
      // return rpc.findResponseData("last", ds, operation, dsRequest);
      return null;
   }

   private DSCall rpc;

   private DSRequest dsRequest;
}
