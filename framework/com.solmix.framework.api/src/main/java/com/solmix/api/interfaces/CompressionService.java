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
package com.solmix.api.interfaces;

import java.io.ByteArrayOutputStream;


/**
 * 
 * @author solomon
 * @version 110035  2011-6-7
 */

public interface CompressionService extends SingletonInstance
{
   boolean canCompress(Object requestContext);
   
   boolean compressIfPossible(Object requestContext)throws Exception;
   void completeResponse(Object requestContext)throws Exception;
   void compressAndSend(Object requestContext,ByteArrayOutputStream byteArrayOutputStream)throws Exception;
   void compressAndSend(Object requestContext,ByteArrayOutputStream byteArrayOutputStream,int size)throws Exception;
}
