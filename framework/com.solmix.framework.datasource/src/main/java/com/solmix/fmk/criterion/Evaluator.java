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

package com.solmix.fmk.criterion;

import java.util.Map;

import com.solmix.api.criterion.AdvancedCriteria;
import com.solmix.api.criterion.Criterion;
import com.solmix.api.criterion.IEvaluator;
import com.solmix.api.criterion.Operator;

/**
 * 
 * @version 110035
 */
public class Evaluator implements IEvaluator
{

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.api.criterion.IEvaluator#addSearchOperator(com.solmix.api.criterion.Operator)
    */
   @Override
   public void addSearchOperator( Operator op )
   {
      // TODO Auto-generated method stub

   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.api.criterion.IEvaluator#evaluateCriterion(java.util.Map,
    *      com.solmix.api.criterion.Criterion)
    */
   @Override
   public boolean evaluateCriterion( Map values, Criterion criterion ) throws Exception
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.api.criterion.IEvaluator#getSearchOperator(java.lang.String)
    */
   @Override
   public Operator getSearchOperator( String id )
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.api.criterion.IEvaluator#valuesMatchCriteria(java.util.Map,
    *      com.solmix.api.criterion.AdvancedCriteria)
    */
   @Override
   public boolean valuesMatchCriteria( Map values, AdvancedCriteria ac ) throws Exception
   {
      // TODO Auto-generated method stub
      return false;
   }

}
