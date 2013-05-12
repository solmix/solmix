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

package com.solmix.fmk.js;

import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;

import com.solmix.api.datasource.DataSource;
import com.solmix.api.exception.SlxException;
import com.solmix.fmk.AbstractSolmixTestCase;
import com.solmix.fmk.mock.DataSourceMock;

/**
 * 
 * @author Administrator
 * @version 110035 2012-3-31
 */

public class ISCJavaScriptTest extends AbstractSolmixTestCase
{

    DataSource ds;

    @Before
    public void setUp() {
        DataSourceMock mock = new DataSourceMock();
        ds = mock.getBasicDataSource();
    }

    @Test
    public void test() throws SlxException {
        StringWriter out = new StringWriter();
        long _s = System.currentTimeMillis();

        ISCJavaScript.get().toDataSource(out, ds);
        long s_ = System.currentTimeMillis();
        System.out.println(s_ - _s);
        System.out.println(out.toString());

    }

}
