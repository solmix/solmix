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

package org.solmix.fmk.serialize;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.solmix.api.serialize.XMLParser;
import org.solmix.api.serialize.XMLParserFactory;

/**
 * @author solomon
 * @since 0.0.1
 * @version 110035 2011-2-14 solmix-ds
 */
public class XMLParserFactoryImpl implements XMLParserFactory
{

    private static XMLParserFactory instance;

    private XMLParser defaultParser;

    private final List<XMLParser> parsers = new CopyOnWriteArrayList<XMLParser>();

    public synchronized static XMLParserFactory getInstance() {
        if (instance == null) {
            instance = new XMLParserFactoryImpl();
        }
        return instance;
    }

    /**
     * Do not instantiate this class. The constructor must be public to use discovery.
     */
    public  XMLParserFactoryImpl()
    {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.serialize.XMLParserFactory#get()
     */
    @Override
    public XMLParser get() {
        return getDefaultParser();
    }

    /**
     * @param defaultParser the defaultParser to set
     */
    public void setDefaultParser(XMLParser defaultParser) {
        this.defaultParser = defaultParser;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.serialize.XMLParserFactory#get(java.lang.String)
     */
    @Override
    public XMLParser get(String implName) {
        // parsers.add(new SgwtRequestXMLParser());
        for (XMLParser parser : parsers) {
            if (implName.trim().endsWith(parser.getImplName()))
                return parser;
        }
        return null;
    }

    public synchronized XMLParser getDefaultParser() {
        if (defaultParser == null)
            return new JaxbXMLParserImpl();
        else
            return defaultParser;
    }

    public void register(XMLParser parser) {
        parsers.add(parser);
    }

    public void unregister(XMLParser parser) {
        parsers.remove(parser);
    }

}
