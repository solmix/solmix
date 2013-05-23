package com.solmix.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;


public interface EMFProvider
{
   /**
    * Return the JPA EntityManagerFactory.
    * @return
    */
   EntityManagerFactory getEntityManagerFactory();
   /**
    * Return the JPA EntityManager.
    * @return
    * @throws PersistenceException
    */
   EntityManager getEntityManager() throws Exception;
   void returnEntityManager(EntityManager em) throws Exception;
   /**
    * Return the transaction Object,and start transact.
    * @param em
    * @return
    * @throws Exception
    */
   Object getTransaction(EntityManager em)throws Exception;
   void commitTansaction(Object tx)throws Exception;
   void rollbackTransaction(Object tx);

}
