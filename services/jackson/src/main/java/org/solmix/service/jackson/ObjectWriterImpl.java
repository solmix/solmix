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
import java.io.OutputStream;
import java.io.Writer;

import org.solmix.exchange.Message;
import org.solmix.exchange.data.DataProcessorException;
import org.solmix.exchange.data.ObjectWriter;
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

public class ObjectWriterImpl<T> implements ObjectWriter<T>
{

    private ObjectMapper objectMapper;

    private XmlMapper xmlMapper;

    public ObjectWriterImpl(JacksonDataProcessor processor)
    {
        this.objectMapper = processor.getObjectMapper();
        this.xmlMapper = processor.getXmlMapper();
    }

    @Override
    public void setProperty(String prop, Object value) {

    }

    @Override
    public void write(Object obj, T output) {
        write(obj, new ArgumentInfo(), output);
    }

    @Override
    public void write(Object obj, ArgumentInfo ai, T output) {
        Object content_type= ai.getProperty(Message.CONTENT_TYPE);
        if(content_type!=null&&content_type.equals("xml")){
            try {
                if(output instanceof OutputStream){
                     xmlMapper.writeValue((OutputStream)output,obj);
                 }else if(output instanceof Writer){
                     xmlMapper.writeValue((Writer)output,obj);
                 }else if(output instanceof File){
                     xmlMapper.writeValue(( File)output,obj);
                 }else{
                     throw new DataProcessorException("Unkonw  output:"+output.getClass().getName());
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
                if(output instanceof OutputStream){
                    objectMapper.writeValue((OutputStream)output,obj);
                }else if(output instanceof Writer){
                    objectMapper.writeValue((Writer)output,obj);
                }else if(output instanceof File){
                    objectMapper.writeValue(( File)output,obj);
                }else{
                    throw new DataProcessorException("Unkonw  output:"+output.getClass().getName());
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

}
