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

package org.solmix.fmk.util;

import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.solmix.api.exception.SlxException;
import org.solmix.api.serialize.JSParser;
import org.solmix.api.serialize.JSParserFactory;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.serialize.JSParserFactoryImpl;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-9-4
 */

public class MapperUtil
{

    private static JSParser jsParser;

    public static void records2XML(String tagName, List<Map<Object, Object>> obj, Writer out) throws Exception {
        if (DataUtil.isNullOrEmpty(obj))
            return;
        for (Map<Object, Object> data : obj) {
            if (DataUtil.isNullOrEmpty(tagName)) {
                tagName = "Object";
            }
            out.write("<" + tagName + ">\n");
            for (Object key : data.keySet()) {
                Object value = data.get(key);
                if (value instanceof Boolean || value instanceof String || value instanceof Number || value instanceof Date) {
                    String strValue = value.toString();
                    if (value instanceof String) {
                        value = quoteXMLString((String) value, false);
                        strValue = value.toString();
                    } else if (value instanceof Date) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        // sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        strValue = sdf.format((Date) value);
                    }

                    out.write("<" + key + ">" + strValue + "</" + key + ">\n");
                } else if (value instanceof Map<?, ?>) {
                    records2XML(null, (List<Map<Object, Object>>) DataUtil.makeListIfSingle((Map<Object, Object>) value), out);
                } else {
                    Map<Object, Object> map = DataUtil.getProperties(value);
                    records2XML(null, map, out);
                }
            }
            out.write("</" + tagName + ">\n");
        }
    }

    public static String quoteXMLString(String value, boolean asAttr) throws Exception {
        StringWriter out = new StringWriter();
        quoteXMLString(value, ((Writer) (out)), asAttr);
        return out.toString();
    }

    public static void quoteXMLString(String value, Writer out, boolean asAttr) throws Exception {
        boolean substituting = false;
        int copiedFrom = 0;
        int length = value.length();
        int i = 0;
        do {
            if (i >= length)
                break;
            char quote;
            switch (quote = value.charAt(i)) {
                case 10: // '\n'
                case 34: // '"'
                case 38: // '&'
                case 60: // '<'
                case 62: // '>'
                    substituting = true;
                    out.write(value.substring(copiedFrom, i));
                    switch (quote) {
                        default:
                            break;

                        case 62: // '>'
                            out.write("&gt;");
                            break;

                        case 60: // '<'
                            out.write("&lt;");
                            break;

                        case 38: // '&'
                            out.write("&amp;");
                            break;

                        case 10: // '\n'
                            if (asAttr)
                                out.write("&amp;#010");
                            else
                                out.write(quote);
                            break;

                        case 34: // '"'
                            if (asAttr)
                                out.write("&quot;");
                            else
                                out.write(quote);
                            break;
                    }
                    copiedFrom = i + 1;
                    break;
            }
            i++;
        } while (true);
        if (substituting)
            out.write(value.substring(copiedFrom));
        else
            out.write(value);
    }

    public static void records2XML(String tagName, Map<?, ?> obj, Writer out) throws Exception {
        List list = DataUtil.makeListIfSingle(obj);
        records2XML(tagName, list, out);
    }

    public static void toJSON(Object obj, Writer out) throws SlxException {
        if (jsParser == null) {
            JSParserFactory jsFactory = JSParserFactoryImpl.getInstance();
            jsParser = jsFactory.get();
        }
        jsParser.toJSON(out, obj);
    }
}
