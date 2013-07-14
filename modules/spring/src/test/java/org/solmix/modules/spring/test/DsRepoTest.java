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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.solmix.api.repo.DSRepository;
import org.solmix.api.repo.DSRepositoryManager;

/**
 * 
 * @author Administrator
 * @version 110035 2012-11-30
 */

public class DsRepoTest
{

    ApplicationContext ctx;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        System.setProperty("solmix.base", "web-root");
        ctx = new ClassPathXmlApplicationContext("spring-dsrepo.xml");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {

        DSRepository repo = ctx.getBean("fileRepo", DSRepository.class);
        String[] locations = repo.getLocations();
        String[] expected = { "file:web-root/WEB-INF/datasource", "file:web-root/WEB-INF/datasource/system/schema" };
        Assert.assertArrayEquals("", expected, locations);
    }

    @Test
    public void test2() throws Exception {

        DSRepositoryManager repo = ctx.getBean("repositoryManager", DSRepositoryManager.class);
        repo.getRepos();
    }

}
