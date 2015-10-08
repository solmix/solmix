/*
 * Copyright 2014 The Solmix Project
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
package org.solmix.exchange.support;

import javax.activation.DataHandler;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Test;
import org.solmix.exchange.model.SerializationInfo;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2014年10月14日
 */

public class DefaultMessageTest extends Assert
{
    private IMocksControl control;
    @Test
    public void testAttachments() {
        control = EasyMock.createNiceControl();
        DataHandler dh = control.createMock(DataHandler.class);
    }
    
    @Test
    public void testContents() {
        DefaultMessage message = new DefaultMessage();
        String str="xxxxxxx";
        message.setContent(String.class, str);
        message.setContent(Integer.class, new Integer(12));
        assertSame(str, message.getContent(String.class));
        assertNotNull(message.getContent(Integer.class));
        message.removeContent(String.class);
        assertNull(message.getContent(String.class));
        
    }
    @Test
    public void testContentOverload() {
        DefaultMessage message = new DefaultMessage();
        String str="xxxxxxx";
        String yyy="yyyyyyy";
        message.setContent(String.class, str);
        message.setContent(String.class, yyy);
        assertSame(yyy, message.getContent(String.class));
        
    }
    
    @Test
    public void performansTest(){
        DefaultMessage message = new DefaultMessage();
        long mark = System.currentTimeMillis();
        for(int i=0;i<1000;i++){
//            DefaultMessage MSG = new DefaultMessage();
//            String str="xxxxxxx";
//            MSG.setContent(String.class, str);
            SerializationInfo info = new SerializationInfo();
        }
        System.out.println(System.currentTimeMillis()-mark);
        mark = System.currentTimeMillis();
        for(int i=0;i<1000;i++){
            String str="xxxxxxx";
            message.put(String.class, str);
        }
        System.out.println(System.currentTimeMillis()-mark);
        
        mark = System.currentTimeMillis();
        for(int i=0;i<1000;i++){
            long l =182l;
            message.setId(l);
        }
        System.out.println(System.currentTimeMillis()-mark);
    }
}
