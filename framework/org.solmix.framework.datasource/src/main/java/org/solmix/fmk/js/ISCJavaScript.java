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

package org.solmix.fmk.js;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.jaxb.request.Roperation;
import org.solmix.api.serialize.JSParser;
import org.solmix.api.serialize.JSParserFactory;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.fmk.serialize.JSParserFactoryImpl;

/**
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2011-2-15 solmix-ds
 */
public class ISCJavaScript
{

    public static JSParser jsParser;

    public static String DATASOURCE = "DataSource";
    static {
        JSParserFactory jsFactory = JSParserFactoryImpl.getInstance();
        jsParser = jsFactory.get();
    }

    public static ISCJavaScript instance() {
        return new ISCJavaScript();
    }

    public static ISCJavaScript get() {
        return instance();
    }

    public ISCJavaScript()
    {
        if (jsParser == null) {
            JSParserFactory jsFactory = JSParserFactoryImpl.getInstance();
            jsParser = jsFactory.get();
        }
    }

    public synchronized void toDataSource(Writer out, DataSource ds) throws SlxException {
        toDataSource(out, ds, "DataSource");
    }

    public synchronized void toDataSource(Writer out, DataSource ds, String dsType) throws SlxException {

        if (ds == null)
            return;
        TdataSource data = ds.getContext().getTdataSource();
        String superDsName = data.getInheritsFrom();
        try {
            _iscStartJS(out, dsType);
            jsParser.toJavaScript(out, ds.toClientValueMap());
            _iscEndJS(out);
            if (superDsName != null) {
                DataSource superds = ds.getContext().getSuperDS();
                if (superds != null)
                    toDataSource(out, superds);
                // jsParser.toJavaScript(out, superds.toClientValueMap());
            }

            // if(superobj!=null)
            // JSDataSourceCache.addCacheObject(data.getInheritsFrom(), superobj);
        } catch (Exception e) {
            throw new SlxException(Tmodule.BASIC, Texception.IO_EXCEPTION, e);
        }
    }

    public String toJS(Object obj) throws SlxException {
        Writer out = new StringWriter();
        jsParser.toJavaScript(out, obj);
        return out.toString();

    }

    /**
     * Print the request data.
     * 
     * @param operation
     * @return
     * @throws SlxException
     */
    public synchronized String printRequestData(Roperation operation) throws SlxException {
        if (operation == null)
            return "";
        Writer out = new StringWriter();
        jsParser.toJavaScript(out, operation);
        return out.toString();
    }

    /**
     * Write the Start JavaScript
     * 
     * @param out
     * @param iscType
     * @param operation
     * @throws IOException
     */
    protected void _iscStartJS(Writer out, String iscType, String operation) throws IOException {
        out.write("isc." + iscType + "." + operation + "(");

    }

    protected void _iscStartJS(Writer out, String iscType) throws IOException {
        _iscStartJS(out, iscType, "create");

    }

    protected void _iscEndJS(Writer out) throws IOException {
        out.write(")\r\n");
    }

    private Map<String, ?> toMap(TdataSource tds) {

        return null;

    }

}
