/**
 * Copyright (c) 2014 The Solmix Project
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

package org.solmix.exchange.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.solmix.exchange.model.ArgumentInfo;
import org.solmix.exchange.model.MessageInfo;
import org.solmix.exchange.model.NamedID;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年11月28日
 */

public class MessageInfoTest {

    private MessageInfo messageInfo;

    @Before
    public void setUp() throws Exception {
        messageInfo = new MessageInfo(null, MessageInfo.Type.INPUT, new NamedID(
            "org.solmix.service.space", "sample"));
    }

    @Test
    public void testID() {

        Assert.assertEquals("org.solmix.service.space",
            messageInfo.getName().getServiceNamespace());
        Assert.assertEquals("sample", messageInfo.getName().getName());
    }

    @Test
    public void testArgument() {
        NamedID argId = new NamedID("org.solmix.service.space", "arg");
        messageInfo.addArgument(argId);
        Assert.assertEquals(messageInfo.getArguments().size(), 1);
        ArgumentInfo arg = messageInfo.getArgument(argId);
        Assert.assertEquals(arg.getIndex(), 0);

        Assert.assertEquals(arg.getName().getServiceNamespace(),
            "org.solmix.service.space");
        Assert.assertEquals(arg.getName().getName(), "arg");
    }

}
