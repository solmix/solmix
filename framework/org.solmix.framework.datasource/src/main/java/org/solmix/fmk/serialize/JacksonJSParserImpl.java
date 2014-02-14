/*
 * Copyright 2012 The Solmix Project
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

package org.solmix.fmk.serialize;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.ObjectFactory;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.jaxb.Tsolmix;
import org.solmix.api.serialize.JSParser;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.util.IOUtil;
import org.solmix.fmk.serialize.jackson.ContextualDateSerializer;
import org.solmix.fmk.serialize.jackson.ModuleSerializer;
import org.solmix.fmk.serialize.jackson.ResponseStatusSerializer;
import org.solmix.fmk.serialize.jackson.TdataSourceSerializer;
import org.solmix.fmk.serialize.jackson.TfieldsSerializer;
import org.solmix.fmk.serialize.jackson.ToperationBindingSerializer;
import org.solmix.fmk.serialize.jackson.ToperationBindingsSerializer;
import org.solmix.fmk.serialize.jackson.TvalidatorsSerializer;
import org.solmix.fmk.serialize.jackson.TvalueMapSerializer;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2011-2-14 solmix-ds
 */
public class JacksonJSParserImpl implements JSParser
{

    private static Logger log = LoggerFactory.getLogger(JacksonJSParserImpl.class.getName());

    public  ObjectMapper iscMapper;

    public  ObjectMapper mapper;

    public boolean prettyPrint = true;

    /**
     * @return the prettyPrint
     */
    @Override
    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    /**
     * @param prettyPrint the prettyPrint to set
     */
    @Override
    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    /**
     * @return the omitNullValues
     */
    @Override
    public boolean isOmitNullValues() {
        return omitNullValues;
    }

    /**
     * @param omitNullValues the omitNullValues to set
     */
    @Override
    public void setOmitNullValues(boolean omitNullValues) {
        this.omitNullValues = omitNullValues;
    }

    private boolean omitNullValues = true;

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.serialize.JSParser#getImplName()
     */
    @Override
    public String getImplName() {
        return "jackson";
    }

    @Override
    public <T> T toJavaObject(Reader src, Class<T> valueType) throws SlxException {

        ObjectMapper mapper = initISCMapper();
        try {
            if (log.isTraceEnabled()) {
                StringWriter record = new StringWriter();
                IOUtil.copyCharacterStreams(src, record);
                log.trace("transform json: " + record.toString() + " to javaObject <" + valueType.getName() + ">");
                return mapper.readValue(record.toString(), valueType);
            }

            return mapper.readValue(src, valueType);
        } catch (IOException e) {
            throw new SlxException(Tmodule.JS, Texception.IO_EXCEPTION, e);
        }

    }

    protected void _toIscJS(Writer out, Object obj) throws SlxException {
        if (obj instanceof TdataSource) {
            org.solmix.api.jaxb.ObjectFactory factory = new ObjectFactory();
            Tsolmix module = factory.createTsolmix();
            module.setDataSource((TdataSource) obj);
            obj = module;
        }
        ObjectMapper mapper = initISCMapper();
        try {
            if (log.isTraceEnabled())
                log.trace("transform Object Class: <" + obj.getClass().getName() + "> to JavaScript.");
            mapper.writeValue(out, obj);
        } catch (Exception ignore) {
            try {
                if (log.isTraceEnabled())
                    log.trace("transform js account exception try to restart the objectmapper", ignore);
                mapper = initISCMapper(true);
                mapper.writeValue(out, obj);
            } catch (JsonGenerationException e) {
                throw new SlxException(Tmodule.JS, Texception.JS_JSON_GENERATION_ERROR, e);
            } catch (JsonMappingException e) {
                throw new SlxException(Tmodule.JS, Texception.JS_JSON_MAPPING_ERROR, e);
            } catch (IOException e) {
                throw new SlxException(Texception.IO_EXCEPTION, e);
            }
        }
    }

    public ObjectMapper initISCMapper() {
        return initISCMapper(false);
    }

    public synchronized ObjectMapper initISCMapper(boolean restart) {
        if (iscMapper == null || restart) {
            long _s = System.currentTimeMillis();
            iscMapper = new ObjectMapper();
            AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
            iscMapper.getSerializationConfig().setAnnotationIntrospector(introspector);
            iscMapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
            iscMapper = customConfig(iscMapper, true);
            long s_ = System.currentTimeMillis();
            if (log.isDebugEnabled()) {
               log.debug("time used to initial jackson objectMapper:[" + (s_ - _s) + "]ms");
            }
        }

        return iscMapper;
    }

    public synchronized ObjectMapper initMapper(boolean restart) {
        if (mapper == null || restart) {
            mapper = new ObjectMapper();
            mapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
            mapper = customConfig(mapper, true);
        }
        return mapper;
    }

    @Override
    public void toJSON(Writer out, Object value) throws SlxException {
        if (mapper == null)
            initMapper(false);

        try {
            mapper.writeValue(out, value);
        } catch (JsonGenerationException e) {
            throw new SlxException(Tmodule.JS, Texception.JS_JSON_GENERATION_ERROR, e);
        } catch (JsonMappingException e) {
            throw new SlxException(Tmodule.JS, Texception.JS_JSON_MAPPING_ERROR, e);
        } catch (IOException e) {
            throw new SlxException(Texception.IO_EXCEPTION, e);
        }
    }

    private synchronized ObjectMapper customConfig(ObjectMapper mapper, boolean isIsc) {
        if (prettyPrint)
            mapper.defaultPrettyPrintingWriter();
        if (omitNullValues) {
            mapper.getSerializationConfig().withSerializationInclusion(Inclusion.NON_NULL);
        }

        if (isIsc) {
            SimpleModule module = new SimpleModule("SolmixJS", new Version(0, 1, 0, "alpha"));
            module.addSerializer(org.solmix.api.jaxb.TdataSource.class, new TdataSourceSerializer());
            module.addSerializer(java.util.Date.class, new ContextualDateSerializer("new Date(", ")"));
            module.addSerializer(org.solmix.fmk.util.SLXDate.class, new ContextualDateSerializer("Date.parseServerDate(", ")", true));
            module.addSerializer(org.solmix.api.jaxb.TvalueMap.class, new TvalueMapSerializer());
            module.addSerializer(org.solmix.api.jaxb.Tvalidators.class, new TvalidatorsSerializer());
            module.addSerializer(org.solmix.api.jaxb.Tfields.class, new TfieldsSerializer());
            module.addSerializer(org.solmix.api.jaxb.Tsolmix.class, new ModuleSerializer());
            module.addSerializer(org.solmix.api.datasource.DSResponse.Status.class, new ResponseStatusSerializer());
            module.addSerializer(org.solmix.api.jaxb.ToperationBindings.class, new ToperationBindingsSerializer());
            module.addSerializer(org.solmix.api.jaxb.ToperationBinding.class, new ToperationBindingSerializer());
            mapper.registerModule(module);
        }
        mapper.getSerializationConfig().set(SerializationConfig.Feature.INDENT_OUTPUT, true);
        // avoid exception when can not found the compare serialiazation.
        mapper.getSerializationConfig().set(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        mapper.getJsonFactory().configure(org.codehaus.jackson.JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
        mapper.getJsonFactory().configure(org.codehaus.jackson.JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        // mapper.getJsonFactory().configure(org.codehaus.jackson.JsonGenerator.Feature, false);
        return mapper;

    }

    @Override
    public void toJavaScript(Writer out, Object obj) throws SlxException {
        _toIscJS(out, obj);

    }

    @Override
    public String toJavaScript(Object obj) throws SlxException {
        StringWriter out = new StringWriter();
        toJavaScript(out, obj);
        return out.toString();

    }

    /**
     * {@inheritDoc}
     * 
     * @throws SlxException
     * 
     * @see org.solmix.api.serialize.JSParser#toJavaObject(java.lang.String, java.lang.Class)
     */
    @Override
    public <T> T toJavaObject(String inputString, Class<T> valueType) throws SlxException {
        ObjectMapper mapper = initISCMapper();
        try {
            if (log.isTraceEnabled()) {
                log.trace("transform json: " + inputString + " to javaObject <" + valueType.getName() + ">");
            }
            return mapper.readValue(inputString, valueType);
        } catch (IOException e) {
            throw new SlxException(Tmodule.JS, Texception.IO_EXCEPTION, e);
        }

    }
}
