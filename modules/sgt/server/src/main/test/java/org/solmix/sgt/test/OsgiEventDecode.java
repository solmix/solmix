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
package org.solmix.sgt.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.osgi.service.event.Event;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-10-7
 */

public class OsgiEventDecode
{

    @Test
    public void test() {
        Map<String,Object> values = new HashMap<String,Object>();
        values.put("aa", "bb");
        Event e = new Event("aaa",values);
       ObjectMapper mapper = new ObjectMapper();
       
        try {
           String strValue= mapper.writeValueAsString(e);
            System.out.println(strValue);
        } catch (JsonGenerationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (JsonMappingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

}
