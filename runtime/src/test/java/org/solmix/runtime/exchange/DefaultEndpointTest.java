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

package org.solmix.runtime.exchange;

import java.util.SortedSet;

import org.junit.Assert;
import org.junit.Test;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.exchange.model.EndpointInfo;
import org.solmix.runtime.exchange.support.DefaultEndpoint;
import org.solmix.runtime.exchange.support.DefaultService;
import org.solmix.runtime.interceptor.phase.Phase;
import org.solmix.runtime.interceptor.phase.PhasePolicy;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2014年12月5日
 */

public class DefaultEndpointTest extends Assert {

    @Test
    public void test() throws EndpointException {
        Container c = ContainerFactory.getDefaultContainer();
        EndpointInfo ei = new EndpointInfo();
        ei.setAddress("hola://www.solmix.org/a");
        EndpointInfo ei2 = new EndpointInfo();
        ei2.setAddress("hola://www.solmix.org/a2");
        Service service = new DefaultService();
        PhasePolicy pp = new PhasePolicy() {

            @Override
            public SortedSet<Phase> getOutPhases() {
                return null;
            }

            @Override
            public SortedSet<Phase> getInPhases() {
                return null;
            }
        };

        Endpoint ed1 = new DefaultEndpoint(c, service, ei, pp);

        Endpoint ed2 = new DefaultEndpoint(c, service, ei, pp);
        Endpoint ed3 = new DefaultEndpoint(c, service, ei2, pp);

        assertEquals(ed1.hashCode(), ed2.hashCode());
        assertTrue(ed1.hashCode() != ed3.hashCode());
    }

}
