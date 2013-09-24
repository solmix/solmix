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

package org.solmix.fmk.js.jackson;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator.Feature;
import org.codehaus.jackson.impl.WriterBasedGenerator;
import org.codehaus.jackson.io.IOContext;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.util.BufferRecycler;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.EdataFormat;
import org.solmix.api.jaxb.Efield;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.Tfields;
import org.solmix.api.serialize.JSParser;
import org.solmix.api.serialize.JSParserFactory;
import org.solmix.fmk.serialize.JSParserFactoryImpl;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-28 solmix-api
 */
public class ObjectFactory
{

    public static void main(String args[]) throws JsonGenerationException, JsonMappingException, IOException, SlxException {
        ObjectMapper mapper = new ObjectMapper();
        Writer out = new StringWriter();
        org.solmix.api.jaxb.ObjectFactory factory = new org.solmix.api.jaxb.ObjectFactory();
        TdataSource ds = factory.createTdataSource();
        ds.setID("ad");
        ds.setDataFormat(EdataFormat.JSON);
        ds.setDataURL("local");
        ds.setTableName("test");
        // ds.setAutoConstruct("DataSource");

        Tfield f1 = factory.createTfield();
        f1.setName("123");
        f1.setType(Efield.BINARY);
        Tfields f = factory.createTfields();
        f.getField().add(f1);
        f.getField().add(f1);
        ds.setFields(f);
        ds.getFields().getField().add(f1);
        IOContext ctxt = new IOContext(new BufferRecycler(), out, false);
        WriterBasedGenerator jgen = new WriterBasedGenerator(ctxt, Feature.QUOTE_FIELD_NAMES.getMask(), mapper, out);
        jgen.disable(Feature.QUOTE_FIELD_NAMES);
        SerializationConfig config = mapper.getSerializationConfig();
        config.withSerializationInclusion(Inclusion.NON_NULL);
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        mapper.setSerializationConfig(config);
        mapper.getSerializationConfig().withAnnotationIntrospector(introspector);
        // mapper.writeValue(out, ds);

        // mapper.writeValue(jgen, ds);
        JSParserFactory jsFactory = JSParserFactoryImpl.getInstance();
        JSParser jsParser = jsFactory.get();
        jsParser.toJavaScript(out, ds);
        System.out.println(out.toString());
        out = new StringWriter();
        // JAXBUtil.marshal(ds, out, true);
        System.out.println(out.toString());
        // Writer r = new StringReader(out);
    }

}
