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

/**
 * 
 * @author solomon
 * @version $Id$ 2011-6-6
 */

public class EMHooker
{

   private final JPADataSource jpaDS;

   private EntityManager em;

   private Object tx;

   private int opCount;

   EMHooker(JPADataSource ds, EntityManager em)
   {
      this.jpaDS = ds;
      this.em = em;
      this.tx = em.getTransaction();
      opCount = 0;
   }
   EMHooker(JPADataSource ds, EntityManager em,Object tx)
   {
      this.jpaDS = ds;
      this.em = em;
      this.tx = tx;
      opCount = 0;
   }
   /**
    * @return the em
    */
   public EntityManager getEm()
   {
      return em;
   }

   /**
    * @param em the em to set
    */
   public void setEm(EntityManager em)
   {
      this.em = em;
   }

   /**
    * @return the tx
    */
   public Object getTx()
   {
      return tx;
   }

   /**
    * @param tx the tx to set
    */
   public void setTx(Object tx)
   {
      this.tx = tx;
   }

   /**
    * @return the opCount
    */
   public int getOpCount()
   {
      return opCount;
   }

   /**
    * @param opCount the opCount to set
    */
   public void setOpCount(int opCount)
   {
      this.opCount = opCount;
   }
   public synchronized void increaseOpCount(){
      opCount++;
   }

}
