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
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Tsolmix;
import org.solmix.api.jaxb.request.Request;
import org.solmix.api.serialize.XMLParser;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.io.SlxFile;
import org.solmix.commons.util.IOUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2011-2-14 solmix-ds
 */
public class JaxbXMLParserImpl implements XMLParser
{

    private static JAXBContext dataSourceJC;

    private static JAXBContext requestJC;

    @Override
    public Tsolmix unmarshalDS(InputStream is) throws SlxException {
        initDataSourceJaxbContext();
        Object obj = unmarshal(is, dataSourceJC);
        if (obj instanceof JAXBElement<?>)
            return (Tsolmix) ((JAXBElement<?>) obj).getValue();
        else
            return (Tsolmix) obj;

    }

    @Override
    public Request unmarshalReq(InputStream is) throws SlxException {
        initReqJaxbContext();
        Object obj = unmarshal(is, requestJC);
        if (obj instanceof JAXBElement<?>)
            return (Request) ((JAXBElement<?>) obj).getValue();
        else
            return (Request) obj;
    }

    @Override
    public Request unmarshalReq(Reader is) throws SlxException {
        initReqJaxbContext();
        Object obj = unmarshal(is, requestJC);
        if (obj instanceof JAXBElement<?>)
            return (Request) ((JAXBElement<?>) obj).getValue();
        else
            return (Request) obj;
    }

    @Override
    public Request unmarshalReq(SlxFile file) throws SlxException {
        InputStream is = null;
        try {
            is = file.getInputStream();
            return unmarshalReq(is);
        } catch (IOException e) {
            throw new SlxException(Tmodule.XML, Texception.DS_DSFILE_NOT_FOUND, "load ds config file failed", e);
        } finally {
            IOUtils.closeQuitely(is);
        }
    }

    @Override
    public Tsolmix unmarshalDS(Reader is) throws SlxException {
        initDataSourceJaxbContext();
        return (Tsolmix) unmarshal(is, dataSourceJC);
    }

    @Override
    public Tsolmix unmarshalDS(SlxFile file) throws SlxException {
        InputStream is = null;
        try {
            is = file.getInputStream();
            return unmarshalDS(is);
        } catch (IOException e) {
            throw new SlxException(Tmodule.XML, Texception.DS_DSFILE_NOT_FOUND, "load ds config file failed", e);
        } finally {
            IOUtils.closeQuitely(is);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.serialize.XMLParser#getImplName()
     */
    @Override
    public String getImplName() {
        return "jaxb";
    }

    @Override
    public void marshalDS(Writer out, Object jaxbObject) throws SlxException {
        initDataSourceJaxbContext();
        marshal(out, jaxbObject, true, dataSourceJC);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.serialize.XMLParser#marshal(java.io.Writer, java.lang.Object, boolean)
     */
    @Override
    public void marshal(Writer out, Object jaxbObject, boolean formatted, JAXBContext jc) throws SlxException {
        try {
            Marshaller marshaller = jc.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(formatted));
            marshaller.marshal(jaxbObject, out);
        } catch (Exception e) {
            throw new SlxException(Tmodule.XML, Texception.XML_JAXB_MARSHAL, null, e);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.serialize.XMLParser#unmarshal(java.io.InputStream)
     */
    @Override
    public Object unmarshal(InputStream is, JAXBContext jc) throws SlxException {
        try {
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            return unmarshaller.unmarshal(is);
        } catch (JAXBException e) {
            throw new SlxException(Tmodule.XML, Texception.XML_JAXB_UNMARSHAL, null, e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.serialize.XMLParser#unmarshal(java.io.Reader)
     */
    @Override
    public Object unmarshal(Reader is, JAXBContext jc) throws SlxException {
        try {
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            // unmarshaller.setListener(new FilterListener());
            return unmarshaller.unmarshal(new StreamSource(is));
        } catch (JAXBException e) {
            throw new SlxException(Tmodule.XML, Texception.XML_JAXB_UNMARSHAL, null, e);
        }
    }

    protected synchronized void initReqJaxbContext() throws SlxException {
        try {
            if (requestJC == null)
                requestJC = JAXBContext.newInstance(Request.class);
        } catch (JAXBException e) {
            throw new SlxException(Tmodule.XML, Texception.XML_JAXB_UNMARSHAL, "Can not instance request JAXBContext.\n exception:", e);
        }
    }

    protected synchronized void initDataSourceJaxbContext() throws SlxException {
        try {
            if (dataSourceJC == null)
                dataSourceJC = JAXBContext.newInstance(Tsolmix.class);
        } catch (JAXBException e) {
            throw new SlxException(Tmodule.XML, Texception.XML_JAXB_UNMARSHAL, "Can not instance Datasource JAXBContext.\n exception:", e);
        }
    }

    @Override
    public void toXML(Writer out, Object object) throws SlxException {
        XmlMapper xml = new XmlMapper();
        try {
            xml.writeValue(out, object);
        } catch (JsonGenerationException e) {
            throw new SlxException(Tmodule.XML,Texception.XML_CREATE_DOCUMENT,e);
        } catch (JsonMappingException e) {
            throw new SlxException(Tmodule.XML,Texception.XML_CREATE_DOCUMENT,e);
        } catch (IOException e) {
            throw new SlxException(Tmodule.XML,Texception.XML_CREATE_DOCUMENT,e);
        }
        
    }

}
