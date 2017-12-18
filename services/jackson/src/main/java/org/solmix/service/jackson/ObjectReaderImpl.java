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
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.solmix.exchange.Message;
import org.solmix.exchange.data.DataProcessorException;
import org.solmix.exchange.data.ObjectReader;
import org.solmix.exchange.model.ArgumentInfo;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年8月17日
 */

public class ObjectReaderImpl<T> implements ObjectReader<T>
{

    private ObjectMapper objectMapper;
    
    private XmlMapper xmlMapper;

    public ObjectReaderImpl(JacksonDataProcessor processor)
    {
        this.objectMapper = processor.getObjectMapper();
        this.xmlMapper= processor.getXmlMapper();
    }

    @Override
    public void setProperty(String prop, Object value) {

    }

    @Override
    public Object read(T input) {
        return read(input, (ArgumentInfo)null);
    }

    @Override
    public Object read(T input, ArgumentInfo ai) {
        Object content_type=ai.getProperty(Message.CONTENT_TYPE);
        if(content_type!=null&&content_type.equals("xml")){
            try {
                if(input instanceof InputStream){
                    return  xmlMapper.readValue((InputStream)input, ai.getTypeClass());
                 }else if(input instanceof Reader){
                     return xmlMapper.readValue((Reader)input, ai.getTypeClass());
                 }else if(input instanceof String){
                     return xmlMapper.readValue((String)input, ai.getTypeClass());
                 }else if(input instanceof URL){
                     return xmlMapper.readValue((URL)input, ai.getTypeClass());
                 }else if(input instanceof File){
                     return xmlMapper.readValue((File)input, ai.getTypeClass());
                 }else{
                     throw new DataProcessorException("Unkonw source:"+input.getClass().getName());
                 }
            } catch (JsonParseException e) {
                throw new DataProcessorException("Json parse exception",e);
            } catch (JsonMappingException e) {
                throw new DataProcessorException("Json mapping exception",e);
            } catch (IOException e) {
                throw new DataProcessorException("Json Mapper io exception",e);
            }
        }else{
            try {
                if(input instanceof InputStream){
                   return  objectMapper.readValue((InputStream)input, ai.getTypeClass());
                }else if(input instanceof Reader){
                    return objectMapper.readValue((Reader)input, ai.getTypeClass());
                }else if(input instanceof String){
                    return objectMapper.readValue((String)input, ai.getTypeClass());
                }else if(input instanceof URL){
                    return objectMapper.readValue((URL)input, ai.getTypeClass());
                }else if(input instanceof File){
                    return objectMapper.readValue((File)input, ai.getTypeClass());
                }else{
                    throw new DataProcessorException("Unkonw source:"+input.getClass().getName());
                }
            } catch (JsonParseException e) {
                throw new DataProcessorException("Json parse exception",e);
            } catch (JsonMappingException e) {
                throw new DataProcessorException("Json mapping exception",e);
            } catch (IOException e) {
                throw new DataProcessorException("Json Mapper io exception",e);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.data.ObjectReader#read(java.lang.Object, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <E> E read(T input, Class<E> ai) {
        ArgumentInfo arg= new ArgumentInfo();
        arg.setTypeClass(ai);
        return (E) read(input, arg);
    }

}
