/*
 * SOLMIX PROJECT
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

package org.solmix.modules.spring.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solmix.api.i18n.ResourceBundleManager;
import org.solmix.api.repo.DSRepositoryManager;
import org.solmix.api.security.SecurityAdmin;
import org.solmix.fmk.datasource.DefaultDataSourceManager;
import org.solmix.sql.ConnectionManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Administrator
 * @version 110035 2012-11-30
 */

public class DataSourceTest
{

    ApplicationContext ctx;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        System.setProperty("solmix.base", "web-root");
        ctx = new ClassPathXmlApplicationContext("spring-ds.xml");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {

        DSRepositoryManager repo = ctx.getBean("repositoryManager", DSRepositoryManager.class);
        Assert.assertNotNull(repo);
        ResourceBundleManager resourceBundle = ctx.getBean("resourceBundle", ResourceBundleManager.class);
        Assert.assertNotNull(resourceBundle);
        SecurityAdmin security_admin = ctx.getBean("security_admin", SecurityAdmin.class);
        Assert.assertNotNull(security_admin);

    }

    @Test
    public void test2() {

        DefaultDataSourceManager repo = ctx.getBean("ds_manager", DefaultDataSourceManager.class);
        Assert.assertNotNull(repo);
        Assert.assertTrue(repo.getProviders().size() > 1);

    }

    @Test
    public void test3() {

        ConnectionManager cm = ctx.getBean(ConnectionManager.class.getName(), ConnectionManager.class);
        Assert.assertNotNull(cm);

    }

}
