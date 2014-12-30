/*
 * Copyright 2013 The Solmix Project
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

package org.solmix.runtime.extension;

import junit.framework.Assert;

import org.junit.Test;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerEvent;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.ContainerListener;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年8月4日
 */

public class ContainerListenerTest {

    @Test
    public void test() throws InterruptedException {
        Container c = ContainerFactory.newInstance().createContainer();
        String create = c.getProperty(ContainerListenerImpl.class.getName()).toString();
        Assert.assertEquals(ContainerListenerImpl.CREATED, create);
        c.addListener(new ContainerListener() {

            @Override
            public void handleEvent(ContainerEvent event) {
                if (event.getType() == ContainerEvent.PRECLOSE)
                    Assert.assertEquals(
                        ContainerListenerImpl.PRECLOSE,
                        event.getContainer().getProperty(
                            ContainerListenerImpl.class.getName()));
            }
        });
        c.close();
    }

}
