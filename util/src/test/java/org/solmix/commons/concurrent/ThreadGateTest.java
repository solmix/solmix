/**
 * Copyright 2015 The Solmix Project
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
package org.solmix.commons.concurrent;

import org.junit.Assert;
import org.junit.Test;



/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2016年5月8日
 */

public class ThreadGateTest
{

    @Test
    public void test() throws InterruptedException {
        final ThreadGate gate = new ThreadGate();
        new Thread(){
            @Override
            public void run(){
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                }
                gate.setMessage("messagex");
                gate.open();
            }
        }.start();
        gate.await(0);
        Assert.assertEquals("messagex", gate.getMessage());
    }

}
