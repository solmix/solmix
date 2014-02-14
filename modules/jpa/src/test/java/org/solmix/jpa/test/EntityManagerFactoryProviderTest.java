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

package org.solmix.jpa.test;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solmix.api.context.SystemContext;
import org.solmix.fmk.SlxContext;
import org.solmix.jpa.EntityManagerFactoryProvider;
import org.solmix.jpa.test.entity.AuthUser;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013年12月10日
 */

public class EntityManagerFactoryProviderTest
{
    private  SystemContext sc;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
         sc = SlxContext.getThreadSystemContext();
       
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void nativeUsedEM() {
        EntityManagerFactoryProvider provider = sc.getBean(EntityManagerFactoryProvider.class);
        EntityManagerFactory emf= provider.createEntityManagerFactory("h2");
        EntityManager em= emf.createEntityManager();
        EntityTransaction tx= em.getTransaction();
        tx.begin();
        AuthUser au = new AuthUser();
        au.setUserName("superadmin");
        au.setComments("super administrator ,this user can do anything.");
        au.setCreateDate(new Date());
        em.persist(au);
        tx.commit();
        long userId= au.getUserId();
        Assert.assertTrue(userId!=0);
        AuthUser find=  em.find(AuthUser.class, userId);
        Assert.assertEquals(au.getUserId(), find.getUserId());
        Assert.assertEquals(au.getUserName(), find.getUserName());
        tx.begin();
        em.remove(find);
        tx.commit();
        em.close();
        Assert.assertNotNull(em);
    }
    @Test
    public void nativeUsedEM2() {
        EntityManagerFactoryProvider provider = sc.getBean(EntityManagerFactoryProvider.class);
        EntityManagerFactory emf= provider.createEntityManagerFactory("h2");
        EntityManager em= emf.createEntityManager();
        Assert.assertNotNull(em);
    }
}
