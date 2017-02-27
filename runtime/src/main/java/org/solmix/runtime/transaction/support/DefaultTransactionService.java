/*
 * Copyright 2015 The Solmix Project
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
package org.solmix.runtime.transaction.support;



/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月12日
 */

public class DefaultTransactionService /*implements TransactionService*/
{
   /* private final Map<Object,Transaction> transactions = new HashMap<Object, Transaction>();
    
    
    @Override
    public void rollback() throws TransactionException {
       for(Transaction transaction:transactions.values()){
           transaction.rollback();
       }
        
    }

    @Override
    public void commit() throws TransactionException {
        for(Transaction transaction:transactions.values()){
            transaction.commit();
        }
    }

    @Override
    public void bindResource(Object object, Transaction transaction) {
        if(object==null||transaction==null){
            return;
        }
       transactions.put(object, transaction);
        
    }

    @Override
    public Map<Object, Transaction> getResourceMap() {
        return transactions;
    }

  
    @Override
    public Transaction getResource(Object object) {
        return transactions.get(object);
    }*/

}
