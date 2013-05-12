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

package com.solmix.fmk.mock;

import static org.easymock.EasyMock.expectLastCall;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Test;

import com.solmix.api.event.EventManager;
import com.solmix.fmk.context.SingleSystemContext;
import com.solmix.fmk.context.SlxContext;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-2
 */

public class MockTest
{

    @Test
    public void test() {
        IMocksControl control = EasyMock.createControl();
        EventManager em = control.createMock(EventManager.class);
        em.getProvider();
        expectLastCall().andReturn("ossgi");
        control.replay();

        SingleSystemContext sc = new SingleSystemContext();
        sc.setEventManager(em);
        SlxContext.setContext(sc);
        EventManager e = SlxContext.getEventManager();
        System.out.println(e.getProvider());
    }

}
