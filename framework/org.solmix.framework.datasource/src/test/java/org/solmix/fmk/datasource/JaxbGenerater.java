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

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Efield;
import org.solmix.api.jaxb.EserverType;
import org.solmix.api.jaxb.ObjectFactory;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.Tfields;
import org.solmix.api.jaxb.Tobject;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.jaxb.ToperationBindings;
import org.solmix.api.jaxb.Tsolmix;
import org.solmix.api.jaxb.request.Request;
import org.solmix.api.jaxb.request.Roperation;
import org.solmix.api.jaxb.request.Roperations;
import org.solmix.api.serialize.JSParser;
import org.solmix.api.serialize.JSParserFactory;
import org.solmix.api.serialize.XMLParser;
import org.solmix.api.serialize.XMLParserFactory;
import org.solmix.fmk.serialize.JSParserFactoryImpl;
import org.solmix.fmk.serialize.XMLParserFactoryImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2011-2-14 solmix-ds
 */
public class JaxbGenerater
{

    /**
     * @param args
     * @throws NotFoundException
     * @throws CannotCompileException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws SlxException
     * @throws ParserConfigurationException 
     */
    public static void main(String[] args) throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException,
        SecurityException, NoSuchFieldException, SlxException, ParserConfigurationException {
        XMLParserFactory xmlFactory = XMLParserFactoryImpl.getInstance();
        XMLParser xmlParser = xmlFactory.get();
        JSParserFactory jsFactory = JSParserFactoryImpl.getInstance();
        JSParser jsParser = jsFactory.get();

        ObjectFactory factory = new ObjectFactory();
        Tsolmix module = factory.createTsolmix();
        TdataSource ds = factory.createTdataSource();
        ToperationBindings bins=factory.createToperationBindings();
        ds.setOperationBindings(bins);
        ToperationBinding bin=factory.createToperationBinding();
        bins.getOperationBinding().add(bin);
        Tobject obj=factory.createTobject();
        bin.setConfiguration(obj);
        {
            ds.setID("button");
            org.solmix.api.jaxb.Tdescription desc = factory.createTdescription();
            desc.getContent().add("datasource");
            desc.getContent().add("test");
            ds.setDescription(desc);
            ds.setServerType(EserverType.SQL);
            ds.setTableName("employeeTable");
            // ds.setRecordName("employee");
            ds.setTestFileName("/examples/shared/ds/test_data/employees.data.xml");
            Tfields fields = factory.createTfields();
            Tfield field = factory.createTfield();
            field.setName("Name");
            field.setTitle("Name");
            field.setType(Efield.TEXT);
            field.setLength(128);
            fields.getField().add(field);
            {
                Tfield field1 = factory.createTfield();
                field1.setName("Name");
                field1.setTitle("Name");
                field1.setType(Efield.TEXT);
                field1.setLength(128);
                /* TvalueMap valueMap = factory.createTvalueMap();
                valueMap.getValue().add("male");
                valueMap.getValue().add("female");
                field1.setValueMap(valueMap);*/
                fields.getField().add(field1);
            }
            ds.setFields(fields);

        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
       dbf.setNamespaceAware(true);
        Document doc=builder.newDocument();
        Element e = doc.createElementNS("ss","BB");
//        e.setAttribute("xmlns:ns", "http://org.solmix.org/ds");
//        e.setAttribute("xmlns", null);
        e.setPrefix("slx");
        doc.appendChild(e);
        obj.getAny().add(doc.getDocumentElement());
        module.setDataSource(ds);
        Writer out = new StringWriter();
        org.solmix.api.jaxb.request.ObjectFactory f = new org.solmix.api.jaxb.request.ObjectFactory();
        Request transaction = f.createRequest();
        transaction.setTransactionNum(5L);
        Roperations op = f.createRoperations();
        Roperation top = f.createRoperation();
        top.setAppID("123");
        // op.getElem().add(top);
        // transaction.setOperations(op);
        // transaction.setOperations(f.createToperations());
        // try {
        // xmlParser.marshalDS(out, module);
        // // xmlParser.marshal(out, transaction, true);
        // } catch (SlxException e) {
        // e.printStackTrace();
        // }
        // try {
        // List list = ds.getFields().getField();
        // ds.getField().addAll(list);
        // // fild.set(tds, ds.getFields().getField());
        // jsParser.toIscJS(out, ds, "otherAttributes", "fields");
        // } catch (SlxException e) {
        // e.printStackTrace();
        // }
        // ISCJavaScript.instance().toDataSource(out, ds);
        System.out.println(out.toString());
        StringBuffer is2 = new StringBuffer("   <transaction xmlns:xsi=\"http://www.w3.org/2000/10/XMLSchema-instance\" xsi:type=\"xsd:Object\">"
            + "" + "<transactionNum xsi:type=\"xsd:long\">5</transactionNum>" + "<operations xsi:type=\"xsd:List\">"
            + "<elem xsi:type=\"xsd:Object\">" + "<criteria xsi:type=\"xsd:Object\"></criteria>" + "<operationConfig xsi:type=\"xsd:Object\">"
            + "<dataSource>employees</dataSource>" + "<operationType>fetch</operationType>" + "<textMatchStyle>exact</textMatchStyle>"
            + "</operationConfig>" + "<startRow xsi:type=\"xsd:long\">0</startRow>" + "<endRow xsi:type=\"xsd:long\">75</endRow>"
            + "<componentId>isc_ListGrid_5</componentId>" + "<appID>builtinApplication</appID>" + "<operation>employees_fetch</operation>"
            + "<oldValues xsi:type=\"xsd:Object\"></oldValues>" + "</elem>" + "</operations>" + "</transaction>");

        String is3 = is2.toString().replace("xmlns:xsi=\"http://www.w3.org/2000/10/XMLSchema-instance\"",
            "xmlns=\"http://www.solmix.org/xmlns/requestdata/v1.0.1\" xmlns:xsi=\"http://www.w3.org/2000/10/XMLSchema-instance\"");

        XMLParserFactory xmlFactory1 = XMLParserFactoryImpl.getInstance();
        XMLParser xmlParser1 = xmlFactory1.get();
        Request aa = xmlParser1.unmarshalReq(new StringReader(is3));
        xmlParser1.marshalDS(out, module);
        System.out.println(out.toString());
        System.out.println(aa.getOperations().getElem().get(0).getStartRow());
    }
}
