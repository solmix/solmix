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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.solmix.api.context.SystemContext;
import org.solmix.api.serialize.JSParser;
import org.solmix.api.serialize.JSParserFactory;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2011-2-9 solmix-ds
 */
public class JSParserFactoryImpl implements JSParserFactory
{

    private static JSParserFactory instance;

    private JSParser defaultParser;

    private final List<JSParser> parsers = new CopyOnWriteArrayList<JSParser>();

    private final SystemContext sc;

    public synchronized static JSParserFactory getInstance() {
        if (instance == null) {
            instance = new JSParserFactoryImpl();
        }
        return instance;
    }

    public JSParserFactoryImpl()
    {
        this(null);
    }

    public JSParserFactoryImpl(final SystemContext sc)
    {
        this.sc = sc;
    }

    /**
     * @return the defaultParser
     */
    public synchronized JSParser getDefaultParser() {
        if (defaultParser == null)
            defaultParser = new JacksonJSParserImpl();
        return defaultParser;
    }

    /**
     * @param defaultParser the defaultParser to set
     */
    public void setDefaultParser(JSParser defaultParser) {
        this.defaultParser = defaultParser;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.serialize.JSParserFactory#get()
     */
    @Override
    public JSParser get() {

        return getDefaultParser();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.serialize.JSParserFactory#get(java.lang.String)
     */
    @Override
    public JSParser get(String implName) {
        for (JSParser parser : parsers) {
            if (implName.trim().equals(parser.getImplName()))
                return parser;
        }
        return null;
    }

    public void register(JSParser parser) {
        parsers.add(parser);
    }

    public void unregister(JSParser parser) {
        parsers.remove(parser);
    }
}
