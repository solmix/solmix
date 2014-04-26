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

package org.solmix.api.serialize;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import javax.xml.bind.JAXBContext;

import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.Tsolmix;
import org.solmix.api.jaxb.request.Request;
import org.solmix.commons.io.SlxFile;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2011-2-7 solmix-api
 */
public interface XMLParser
{

    String getImplName();

    /**
     * @param is
     * @param jc
     * @return
     * @throws SlxException
     */
    Object unmarshal(Reader is, JAXBContext jc) throws SlxException;

    /**
     * @param is
     * @param jc
     * @return
     * @throws SlxException
     */
    Object unmarshal(InputStream is, JAXBContext jc) throws SlxException;

    /**
     * @param out
     * @param jaxbObject
     * @param formatted
     * @param jc
     * @throws SlxException
     */
    void marshal(Writer out, Object jaxbObject, boolean formatted, JAXBContext jc) throws SlxException;

    /**
     * @param file
     * @return
     * @throws SlxException
     */
    Tsolmix unmarshalDS(SlxFile file) throws SlxException;

    /**
     * @param is
     * @return
     * @throws SlxException
     */
    Tsolmix unmarshalDS(Reader is) throws SlxException;

    /**
     * @param is
     * @return
     * @throws SlxException
     */
    Tsolmix unmarshalDS(InputStream is) throws SlxException;

    /**
     * @param out
     * @param jaxbObject
     * @throws SlxException
     */
    void marshalDS(Writer out, Object jaxbObject) throws SlxException;
    
    void toXML(Writer out, Object object) throws SlxException;

    /**
     * @param is
     * @return
     * @throws SlxException
     */
    Request unmarshalReq(InputStream is) throws SlxException;

    /**
     * @param is
     * @return
     * @throws SlxException
     */
    Request unmarshalReq(Reader is) throws SlxException;

    /**
     * @param file
     * @return
     * @throws SlxException
     */
    Request unmarshalReq(SlxFile file) throws SlxException;

}
