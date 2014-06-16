/*
 *  Copyright 2012 The Solmix Project
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

package org.solmix.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-10-21
 */

public class JPATransaction
{
    
 public static final String ENTITYMANAGER_ATTR_KEY = "_slx_jpa_entityManager_key";
    /**
     * @return
     */
    public static EntityManager getEntityManager(EntityManagerFactory emf) {
        if (emf.isOpen())
            return emf.createEntityManager();
        else
            throw new java.lang.IllegalStateException("EntityManagerFactory is closed");
    }

    /**
     * @param entityManager
     */
    public static void returnEntityManager(EntityManager em) throws PersistenceException {

        if (em != null) {
            if (em.isOpen())
                em.close();
        }
        // clear entityManager.
        em = null;

    }

    /**
     * Get the {@link javax.persistence.EntityTransaction } from entityManager and begin this transaction.
     * 
     * @param entityManager
     * @return
     */
    public static Object getTransaction(EntityManager em) {
        if (em != null) {
            EntityTransaction tx = em.getTransaction();
            if (!tx.isActive())
                tx.begin();
            return tx;
        } else {
            throw new PersistenceException("EntityManager not Provided");
        }
    }

    /**
     * @param transaction
     */
    public static void commitTansaction(Object tx) {
        if (tx != null && ((EntityTransaction) tx).isActive())
            ((EntityTransaction) tx).commit();

    }

    /**
     * @param transaction
     */
    public static  void rollbackTransaction(Object tx) {
        if (tx != null && ((EntityTransaction) tx).isActive())
            ((EntityTransaction) tx).rollback();

    }

}
