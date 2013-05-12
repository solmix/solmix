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
package com.solmix.fmk.interfaces;

import java.util.Map;

import com.solmix.api.criterion.ErrorMessage;
import com.solmix.api.exception.SlxException;
import com.solmix.fmk.datasource.ValidationContext;
import com.solmix.fmk.datasource.Validator;


/**
 * 
 * @author Administrator
 * @version 110035  2011-4-8
 */

public interface ValidatorFunc
{

   ErrorMessage validate( Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context ) throws SlxException;
}
