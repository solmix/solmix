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
package org.solmix.fmk.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Test;
import org.solmix.api.exception.SlxException;
import org.solmix.fmk.SlxContext;
import org.solmix.runtime.SystemContext;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-11-7
 */

public class ResourceBundleManagerImplTest
{
    @Test
    public void test() throws SlxException {
        SystemContext sc = SlxContext.getSystemContext();
        ResourceBundleManagerImpl rbm= new ResourceBundleManagerImpl(sc);
        ResourceBundle rb= rbm.getResourceBundle(Locale.CHINA);
        Assert.assertNotNull(rb);
    }
}
