/*
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
package org.solmix.service.jackson;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.List;

import org.solmix.exchange.Service;
import org.solmix.exchange.data.DataProcessor;
import org.solmix.exchange.data.ObjectReader;
import org.solmix.exchange.data.ObjectWriter;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月17日
 */

public class JacksonDataProcessor implements DataProcessor
{
    private ObjectMapper objectMapper;
    private XmlMapper xmlMapper;
    private boolean omitNullValues = true;
    public boolean prettyPrint ;
    List<JsonSerializer<?>> jsonSerializers;
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.data.DataProcessor#initialize(org.solmix.exchange.Service)
     */
    @Override
    public void initialize(Service service) {
        initializeObjectMapper();
        initializeXmlMapper();
    }

    private void initializeXmlMapper() {
        xmlMapper= new XmlMapper();
        if(prettyPrint){
            objectMapper.writerWithDefaultPrettyPrinter();
        }
        JacksonXmlModule module = new JacksonXmlModule();
        if(jsonSerializers!=null){
            for(JsonSerializer<?> serializer:jsonSerializers){
                module.addSerializer(serializer);
            }
        }
        objectMapper.registerModule(module);
        objectMapper.getSerializationConfig().with(SerializationFeature.INDENT_OUTPUT);
        objectMapper.getSerializationConfig().with(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.getSerializationConfig().without(SerializationFeature.WRITE_NULL_MAP_VALUES);
        objectMapper.getFactory().enable(Feature.AUTO_CLOSE_TARGET);
    }

    
    private void initializeObjectMapper() {
        objectMapper= new ObjectMapper();
        if(prettyPrint){
            objectMapper.writerWithDefaultPrettyPrinter();
        }
        SimpleModule module = new SimpleModule("SolmixJS", new Version(0, 6, 1, "alpha","org.solmix.service","solmix-service-jackson"));
        if(jsonSerializers!=null){
            for(JsonSerializer<?> serializer:jsonSerializers){
                module.addSerializer(serializer);
            }
        }
        objectMapper.registerModule(module);
        objectMapper.getSerializationConfig().with(SerializationFeature.INDENT_OUTPUT);
        objectMapper.getSerializationConfig().with(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.getSerializationConfig().without(SerializationFeature.WRITE_NULL_MAP_VALUES);
        objectMapper.getFactory().enable(Feature.AUTO_CLOSE_TARGET);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ObjectReader<T> createReader(Class<T> cls) {
        ObjectReader<T> dr = null;
        if (cls == InputStream.class) {
            dr = (ObjectReaderImpl<T>)new ObjectReaderImpl<InputStream>(this);
        } else if (cls == Reader.class) {
            dr = (ObjectReaderImpl<T>)new ObjectReaderImpl<Reader>(this);
        } else if (cls == String.class) {
            dr = (ObjectReaderImpl<T>)new ObjectReaderImpl<String>(this);
        }else if (cls == File.class) {
            dr = (ObjectReaderImpl<T>)new ObjectReaderImpl<File>(this);
        }else if (cls == URL.class) {
            dr = (ObjectReaderImpl<T>)new ObjectReaderImpl<URL>(this);
        }
        return dr;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ObjectWriter<T> createWriter(Class<T> cls) {
        ObjectWriter<T> dw = null;
        if (cls == OutputStream.class) {
            dw = (ObjectWriterImpl<T>)new ObjectWriterImpl<OutputStream>(this);
        } else if (cls == Writer.class) {
            dw = (ObjectWriterImpl<T>)new ObjectWriterImpl<Writer>(this);
        }else if (cls == File.class) {
            dw = (ObjectWriterImpl<T>)new ObjectWriterImpl<File>(this);
        }

        return dw;
    }

    @Override
    public Class<?>[] getSupportedReaderFormats() {
        return new Class[]{InputStream.class,Reader.class,String.class,URL.class,File.class};
    }

    @Override
    public Class<?>[] getSupportedWriterFormats() {
        return new Class[]{OutputStream.class,Writer.class,File.class};
    }

    
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * @return
     */
    public XmlMapper getXmlMapper() {
        return xmlMapper;
    }
   
    public boolean isOmitNullValues() {
        return omitNullValues;
    }
    public void setOmitNullValues(boolean omitNullValues) {
        this.omitNullValues = omitNullValues;
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    
    public List<JsonSerializer<?>> getJsonSerializers() {
        return jsonSerializers;
    }

    
    public void setJsonSerializers(List<JsonSerializer<?>> jsonSerializers) {
        this.jsonSerializers = jsonSerializers;
    }
    
}
