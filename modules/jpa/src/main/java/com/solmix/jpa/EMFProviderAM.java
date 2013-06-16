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
package com.solmix.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Administrator
 * @version $Id$  2011-6-21
 */

public class EMFProviderAM implements EMFProvider
{
   private EntityManagerFactory entityManagerFactory;
   public EMFProviderAM(){
       
   }
   public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory)
   {
      this.entityManagerFactory = entityManagerFactory;
   }

   private static Logger log = LoggerFactory.getLogger(EMFProviderAM.class.getName());

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.jpa.EMFProvider#getEntityManagerFactory()
    */
   @Override
   public EntityManagerFactory getEntityManagerFactory()
   {
      return entityManagerFactory;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.jpa.EMFProvider#getEntityManager()
    */
   @Override
   public EntityManager getEntityManager() throws Exception
   {
      return entityManagerFactory.createEntityManager();
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.jpa.EMFProvider#returnEntityManager(javax.persistence.EntityManager)
    */
   @Override
   public void returnEntityManager(EntityManager em) throws Exception
   {
      if(em!=null){
         try
         {
            if(em.isOpen())
               em.close();
         } catch (PersistenceException e)
         {
            log.warn("Failed to close EntityManager",e);
         }
      }
      //clear entityManager.
      em=null;
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.jpa.EMFProvider#getTransaction(javax.persistence.EntityManager)
    */
   @Override
   public Object getTransaction(EntityManager em) throws Exception
   {
     if(em!=null){
        EntityTransaction tx = em.getTransaction();
        if(!tx.isActive())
           tx.begin();
        return tx;
     }else{
        throw new PersistenceException("EntityManager not Provided");
     }
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.jpa.EMFProvider#commitTansaction(java.lang.Object)
    */
   @Override
   public void commitTansaction(Object tx) throws Exception
   {
      if(tx!=null &&((EntityTransaction)tx).isActive())
         ((EntityTransaction)tx).commit();

   }

   /**
    * {@inheritDoc}
    * 
    * @see com.solmix.jpa.EMFProvider#rollbackTransaction(java.lang.Object)
    */
   @Override
   public void rollbackTransaction(Object tx)
   {
      if(tx!=null &&((EntityTransaction)tx).isActive())
         try
         {
            ((EntityTransaction)tx).rollback();
         } catch (Exception e)
         {
          log.warn("Unexcepted exception while rolling back transaction ",e);
         }
   }

}
