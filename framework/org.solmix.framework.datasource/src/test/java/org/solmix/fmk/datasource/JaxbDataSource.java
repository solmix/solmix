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

package org.solmix.fmk.datasource;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Module;
import org.solmix.api.jaxb.request.Request;
import org.solmix.api.serialize.JSParser;
import org.solmix.api.serialize.JSParserFactory;
import org.solmix.api.serialize.XMLParser;
import org.solmix.api.serialize.XMLParserFactory;
import org.solmix.commons.io.SlxFile;
import org.solmix.fmk.serialize.JSParserFactoryImpl;
import org.solmix.fmk.serialize.XMLParserFactoryImpl;

/**
 * @author Administrator
 * 
 */
public class JaxbDataSource
{

    /**
     * Figure out between xmlns:ns1="http://www.solmix.com/xmlns/datasource/v1.0.0" and
     * xmlns="http://www.solmix.com/xmlns/datasource/v1.0.0" For usually people would write XML configuration like this
     * "<module type="text"></module>" not "<ns1:module ns1:type="text"></ns1:module>" but jaxb not having default
     * NameSpace set,and JAXB generated code always has Namespace defined like <code>
     * 
     * @XmlAttribute(name = "ID", namespace = "http://www.solmix.com/xmlns/datasource/v1.0.0", required = true)
     *                    protected String id; </code> for our expectation would like this <code>
     * @XmlAttribute(name = "ID", required = true) protected String id; </code>
     * @param args
     * @throws SlxException
     */
    public static void main(String[] args) throws SlxException {
        URL url = JaxbDataSource.class.getResource("ds.xml");
        SlxFile file = new SlxFile(url);
        URL url1 = JaxbDataSource.class.getResource("request.xml");
        SlxFile file1 = new SlxFile(url1);
        XMLParserFactory factory = XMLParserFactoryImpl.getInstance();
        XMLParser parser = factory.get();
        Module module = null;
        Request transaction = null;
        try {
            module = parser.unmarshalDS(file.getInputStream());
            // transaction = parser.unmarshalReq(file1.getInputStream());
        } catch (SlxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // if (module != null)
        // // System.out.println(module.getDataSource().getAutoConstruct());
        // System.out.println(transaction.getBatchNum());
        JSParserFactory jsFactory = JSParserFactoryImpl.getInstance();
        JSParser jsParser = jsFactory.get();
        StringWriter out = new StringWriter();
        jsParser.toJavaScript(out, module);

        Map map = new HashMap();
        map.put("123", "11");
        map.put("113", "11");
        jsParser.toJavaScript(out, map);
        System.out.println(out.toString());
    }

}
