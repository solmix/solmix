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
package org.solmix.fmk.xml;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.jaxb.Tobject;
import org.solmix.api.jaxb.Tsolmix;
import org.solmix.fmk.serialize.JaxbXMLParserImpl;
import org.solmix.fmk.util.XMLUtil;
import org.w3c.dom.Element;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-6-21
 */

public class XMLUtilTest
{

    
    @Test
    public void test() throws SlxException, JAXBException{
        Map xmldata = new HashMap();
        List l = new ArrayList();
        l.add(mapdata("aaa","bbb")); l.add(mapdata("aaa","bbb"));
        xmldata.put("aa", l);
        Element e= XMLUtil.toElement("asda", l);
        Tsolmix m = new Tsolmix();
        TdataSource tds = new TdataSource();
        Tobject o = new Tobject();
        o.getAny().add(e);
        tds.setConfiguration(o);
        m.setDataSource(tds);
        JaxbXMLParserImpl j= new JaxbXMLParserImpl();
        StringWriter sw = new StringWriter();
        j.marshalDS(sw, m);
        System.out.println(sw.toString());

    }

    /**
     * @param string
     * @param string2
     * @return
     */
    private Object mapdata(String string, String string2) {
        Map xmldata = new HashMap();
        xmldata.put(string, string2);
        return xmldata;
    }
}
