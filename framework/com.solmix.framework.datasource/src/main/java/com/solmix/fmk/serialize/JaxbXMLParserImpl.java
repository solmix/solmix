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

package com.solmix.fmk.serialize;

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

import com.solmix.api.exception.SlxException;
import com.solmix.api.jaxb.Module;
import com.solmix.api.jaxb.request.Request;
import com.solmix.api.serialize.XMLParser;
import com.solmix.api.types.Texception;
import com.solmix.api.types.Tmodule;
import com.solmix.commons.io.SlxFile;
import com.solmix.commons.logs.Logger;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035 2011-2-14 solmix-ds
 */
public class JaxbXMLParserImpl implements XMLParser
{

    private static Logger log = new Logger(JaxbXMLParserImpl.class);

    private static JAXBContext dataSourceJC;

    private static JAXBContext requestJC;

    @Override
    public Module unmarshalDS(InputStream is) throws SlxException {
        try {
            if (dataSourceJC == null)
                dataSourceJC = JAXBContext.newInstance(Module.class);
        } catch (JAXBException e) {
            throw new SlxException(Tmodule.XML, Texception.XML_JAXB_UNMARSHAL, "Can not instance JAXBContext.\n exception:", e);
        }
        Object obj = unmarshal(is, dataSourceJC);
        if (obj instanceof JAXBElement<?>)
            return (Module) ((JAXBElement<?>) obj).getValue();
        else
            return (Module) obj;

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
        try {
            return unmarshalReq(file.getInputStream());
        } catch (IOException e) {
            throw new SlxException(Tmodule.XML, Texception.DS_DSFILE_NOT_FOUND, "load ds config file failed", e);
        }
    }

    @Override
    public Module unmarshalDS(Reader is) throws SlxException {
        try {
            if (dataSourceJC == null)
                dataSourceJC = JAXBContext.newInstance(Module.class);
        } catch (JAXBException e) {
            throw new SlxException(Tmodule.XML, Texception.XML_JAXB_UNMARSHAL, "Can not instance JAXBContext.\n exception:", e);
        }
        return (Module) unmarshal(is, dataSourceJC);
    }

    @Override
    public Module unmarshalDS(SlxFile file) throws SlxException {
        try {
            return unmarshalDS(file.getInputStream());
        } catch (IOException e) {
            throw new SlxException(Tmodule.XML, Texception.DS_DSFILE_NOT_FOUND, "load ds config file failed", e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.serialize.XMLParser#getImplName()
     */
    @Override
    public String getImplName() {
        return "jaxb";
    }

    @Override
    public void marshalDS(Writer out, Object jaxbObject) throws SlxException {
        try {
            if (dataSourceJC == null)
                dataSourceJC = JAXBContext.newInstance(Module.class);
        } catch (JAXBException e) {
            throw new SlxException(Tmodule.XML, Texception.XML_JAXB_UNMARSHAL, "Can not instance JAXBContext.\n exception:", e);
        }
        marshal(out, jaxbObject, true, dataSourceJC);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.serialize.XMLParser#marshal(java.io.Writer, java.lang.Object, boolean)
     */
    @Override
    public void marshal(Writer out, Object jaxbObject, boolean formatted, JAXBContext jc) throws SlxException {
        try {
            Marshaller marshaller = jc.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(formatted));
            // XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance() .createXMLStreamWriter(out);
            // xmlStreamWriter.setDefaultNamespace("http://www.solmix.com/xmlns/datasource/v1.0.0");
            // xmlStreamWriter.setPrefix("slx", "http://www.solmix.com/xmlns/datasource/v1.0.0");
            // marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");
            marshaller.marshal(jaxbObject, out);
        } catch (Exception e) {
            throw new SlxException(Tmodule.XML, Texception.XML_JAXB_MARSHAL, null, e);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.solmix.api.serialize.XMLParser#unmarshal(java.io.InputStream)
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
     * @see com.solmix.api.serialize.XMLParser#unmarshal(java.io.Reader)
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

    protected void initReqJaxbContext() throws SlxException {
        try {
            if (requestJC == null)
                requestJC = JAXBContext.newInstance(Request.class);
        } catch (JAXBException e) {
            throw new SlxException(Tmodule.XML, Texception.XML_JAXB_UNMARSHAL, "Can not instance JAXBContext.\n exception:", e);
        }
    }

}
