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

package org.solmix.api.rpc;

import org.solmix.api.datasource.Const;

/**
 * 
 * @version 110035
 */
public interface RPCResponse extends ResponseType
{

   public static final int STATUS_SUCCESS = Const.SUCCESS;

   public static final int STATUS_FAILURE = Const.BAD_DATA;

   public static final int STATUS_LOGIN_INCORRECT = -5;

   public static final int STATUS_MAX_LOGIN_ATTEMPTS_EXCEEDED = -6;

   public static final int STATUS_LOGIN_REQUIRED = -7;

   public static final int STATUS_LOGIN_SUCCESS = -8;

   Object getData();

   void setData( Object data );

   void setStatus( int status );

   int getStatus();

}
