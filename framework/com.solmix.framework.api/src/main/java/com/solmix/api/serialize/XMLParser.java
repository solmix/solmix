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

package com.solmix.api.serialize;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import javax.xml.bind.JAXBContext;

import com.solmix.api.exception.SlxException;
import com.solmix.api.jaxb.Module;
import com.solmix.api.jaxb.request.Request;
import com.solmix.commons.io.SlxFile;

/**
 * @author solomon
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
    Module unmarshalDS(SlxFile file) throws SlxException;

    /**
     * @param is
     * @return
     * @throws SlxException
     */
    Module unmarshalDS(Reader is) throws SlxException;

    /**
     * @param is
     * @return
     * @throws SlxException
     */
    Module unmarshalDS(InputStream is) throws SlxException;

    /**
     * @param out
     * @param jaxbObject
     * @throws SlxException
     */
    void marshalDS(Writer out, Object jaxbObject) throws SlxException;

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
