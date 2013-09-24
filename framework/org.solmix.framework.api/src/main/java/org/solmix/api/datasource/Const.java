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

package org.solmix.api.datasource;

/**
 * Code.
 * 
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-20 solmix-api
 */
public class Const
{

   public Const()
   {
   }

   public static int FILE_NOT_FOUND = 3;

   public static int SUPPRESS_DEFAULT_RESPONSE = 2;

   public static int UNSET = 1;

   public static int SUCCESS = 0;

   public static int BAD_DATA = -1;

   public static int DB_EXCEPTION = -2;

   public static int AUTHORIZATION_FAILURE = -3;

   public static int VALIDATION_ERROR = -4;

   public static final int LOGIN_INCORRECT = -5;

   public static final int MAX_LOGIN_ATTEMPTS_EXCEEDED = -6;

   public static final int LOGIN_REQUIRED = -7;

   public static final int LOGIN_SUCCESS = -8;

   public static final int UPDATE_WITHOUT_PK = -9;

   public static final int TRANSACTION_FAILED = -10;

   public static int UNKNOWN_ERROR = -9999;
}
