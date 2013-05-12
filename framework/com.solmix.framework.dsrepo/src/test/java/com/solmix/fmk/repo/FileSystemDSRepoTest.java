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

package com.solmix.fmk.repo;

import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import com.solmix.commons.io.SlxFile;

/**
 * 
 * @version 110035
 */
public class FileSystemDSRepoTest
{

    private URL location;

    @Before
    public void init() {
        location = getClass().getResource("NewFile.xml");

    }

    @Test
    public void pathTest() throws IOException {
        SlxFile newfile = new SlxFile(location);
        Assert.assertTrue(true);
    }
}
