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

package com.solmix.fmk.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.solmix.api.datasource.DSRequest;
import com.solmix.api.datasource.DataSource;
import com.solmix.api.event.IValidationEvent.Level;
import com.solmix.api.event.ValidationEventWrapper;
import com.solmix.api.exception.SlxException;
import com.solmix.api.jaxb.Efield;
import com.solmix.api.jaxb.Eoperation;
import com.solmix.api.jaxb.Tfield;
import com.solmix.api.jaxb.ToperationBinding;
import com.solmix.api.types.Texception;
import com.solmix.api.types.Tmodule;
import com.solmix.commons.util.DataUtil;
import com.solmix.fmk.datasource.ValidationEventFactory;
import com.solmix.fmk.event.EventUtils;
import com.solmix.fmk.js.ISCJavaScript;

public class DataTools
{

    private static final Logger log = LoggerFactory.getLogger(DataTools.class.getName());

    public DataTools()
    {
    }

    public static String prettyPrint(Object obj) {
        try {

            String _tmp = ISCJavaScript.jsParser.toJavaScript(obj);
            if (!ISCJavaScript.jsParser.isPrettyPrint())
                return DataUtil.prettyPrint(_tmp);
            else
                return _tmp;
        } catch (Exception e) {
            return (new StringBuilder()).append("Exception during DataTools.prettyPrint:\n").append(DataUtil.getStackTrace(e)).toString();
        }
    }

    public static ToperationBinding getOperationBindingFromDSByRequest(DataSource ds, DSRequest request) {
        if (request == null || request.getContext() == null)
            return null;
        ToperationBinding __op = ds.getContext().getOperationBinding(request.getContext().getOperationType(), request.getContext().getOperationId());
        return __op;
    }

    public static Map<String, Object> parserRequestValues(List<Object> elementValues) {
        if (elementValues == null || elementValues.size() == 0)
            return null;
        Map<String, Object> map = new HashMap<String, Object>();
        for (Object element : elementValues) {
            if (element instanceof Element) {
                try {
                    Element e = (Element) element;
                    String name = e.getLocalName();
                    if (name == null)
                        name = e.getNodeName();

                    Object value = XMLtoRecord(e);
                    if (value != null || name != null)
                        map.put(name, value);
                } catch (SlxException e1) {
                    EventUtils.createAndFireDSValidateEvent(Level.WARNING, "The request data " + element.toString()
                        + "is not a Element type values,check the Request-Data", null);
                    // if (validation != null)
                    // validation.add(new DSValidation(Level.WARNING, "The request data " + element.toString()
                    // + "is not a Element type values,check the Request-Data"));
                }

            } else {
                EventUtils.createAndFireDSValidateEvent(Level.WARNING, "The request data " + element.toString()
                    + "is not a Element type values,check the Request-Data", null);
                // if (validation != null)
                // validation.add(new DSValidation(Level.WARNING, "The request data " + element.toString()
                // + "is not a Element type values,check the Request-Data"));
            }

        }
        return map;

    }

    public static Object XMLtoRecord(Object xmlObj) throws SlxException {
        if (xmlObj instanceof List<?>) {
            List<Object> _tmpList = null;
            Map<String, Object> _tmpMap = null;
            for (Object xml : (List<?>) xmlObj) {
                if (xml instanceof Element) {
                    Element e = (Element) xml;
                    Node fnode = e.getFirstChild();
                    String nodeName = e.getLocalName();
                    if (nodeName == null)
                        nodeName = e.getNodeName();
                    if (nodeName != null && nodeName.equals("elem")) {
                        if (_tmpList == null)
                            _tmpList = new ArrayList<Object>();
                        _tmpList.add(XMLtoRecord(fnode));
                    } else {
                        if (_tmpMap == null)
                            _tmpMap = new HashMap<String, Object>();
                        _tmpMap.put(nodeName, XMLtoRecord(e));
                    }
                }
            }
            if (_tmpList == null && _tmpMap != null)
                return _tmpMap;
            if (_tmpList != null && _tmpMap == null)
                return _tmpList;
            if (_tmpList != null && _tmpMap != null) {
                _tmpMap.put("elems", _tmpList);
                return _tmpMap;
            }
            return null;
        } else if (xmlObj instanceof Element) {
            Element ele = (Element) xmlObj;
            String _type = getXMLType(ele);
            boolean isSimple = false;
            // if (ele.hasAttributes())
            // {
            // if ((ele.getAttributes()).getLength() == 1 && !ele.hasChildNodes())
            // {
            // isSimple = true;
            // return ele.getAttribute("value");
            // }
            // }else{
            if (ele.hasChildNodes() && (ele.getChildNodes().getLength() == 1) && ele.getFirstChild() instanceof Text) {
                isSimple = true;
                String value = ((Text) ele.getFirstChild()).getData();
                return makeObject(value, _type);
            }
            // }
            // Map attributes = XMLUtil.attributesToMap((Element) xmlObj);
            List<Element> eleList = XMLUtil.getElementChildren(ele);
            if (DataUtil.isNotNullAndEmpty(eleList)) {
                if (_type.equalsIgnoreCase("List") || _type.equalsIgnoreCase("array")) {
                    List<Object> list = new ArrayList<Object>();
                    for (Element e : eleList) {
                        list.add(XMLtoRecord(e));
                    }
                    return list;
                } else if (_type.equalsIgnoreCase("object") || _type.equalsIgnoreCase("map") || _type.equalsIgnoreCase("set")) {
                    Map map = new HashMap();
                    for (Element e : eleList) {
                        String name = e.getLocalName();
                        if (name == null)
                            name = e.getNodeName();
                        map.put(name, XMLtoRecord(e));
                    }
                    return map;
                } else {
                    Map map = new HashMap();
                    for (Element e : eleList) {
                        String name = e.getLocalName();
                        if (name == null)
                            name = e.getNodeName();
                        map.put(name, XMLtoRecord(e));
                        DataUtil.putMultiple(map, name, XMLtoRecord(e));
                    }
                    return map;
                }

            }
            return null;
            // return DataUtil.mapMerge(attributes, _return);

        } else {
            throw new SlxException(Tmodule.XML, Texception.NO_SUPPORT, DataUtil.getNoSupportString(xmlObj));
        }

    }

    public static String getXMLType(Element ele) {
        String xsitype = ele.getAttribute("xsi:type");
        String _type = null;
        if (DataUtil.isNotNullAndEmpty(xsitype) && xsitype.length() >= 4)
            _type = xsitype.substring(4);
        return _type;
    }

    public static Object makeObject(String value, String typeString) {
        Object __return = null;
        if (typeString == null) {
            return value;
        } else if (typeString.equals("long")) {
            __return = Long.parseLong(value);
        } else if (typeString.equals("date")) {
            try {
                __return = (new SimpleDateFormat("yyyy-MM-dd")).parse(value);
            } catch (ParseException e1) {
                log.warn("parse date:" + value + " fialed");
            }
        } else {
            __return = value;
        }
        return __return;
    }

    public static boolean enumEqual(Enum<?> comp1, Enum<?> comp2) {
        if (comp1.name().equals(comp2.name()))
            return true;
        else
            return false;
    }

    /**
     * @param e
     * @return
     */
    public static Object getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        try {
            sw.close();
        } catch (Exception e) {
        }
        return sw.toString();
    }

    public static boolean isModificationRequest(DSRequest req) throws SlxException {
        return isModificationOperation(req.getDataSource(), req.getContext().getOperationType(), req.getContext().getOperationId());
    }

    /**
     * @param dataSource
     * @param operationType
     * @param operation
     * @return
     */
    public static boolean isModificationOperation(DataSource dataSource, Eoperation operationType, String operation) {
        return isModificationOperation(operationType);
    }

    /**
     * @param operationType
     * @return
     */
    public static boolean isModificationOperation(Eoperation operationType) {
        if (isAdd(operationType) || isRemove(operationType) || isReplace(operationType) || isUpdate(operationType))
            return true;
        else
            return false;
    }

    public static boolean isFetch(Eoperation operationType) {
        return operationType == Eoperation.FETCH;

    }

    public static boolean isAdd(Eoperation operationType) {
        return operationType == Eoperation.ADD;

    }

    public static boolean isFilter(Eoperation operationType) {
        return operationType == Eoperation.FETCH;

    }

    public static boolean isCustomer(Eoperation operationType) {
        return operationType == Eoperation.CUSTOM;
    }

    public static boolean isRemove(Eoperation operationType) {
        return operationType == Eoperation.REMOVE;
    }

    public static boolean isUpdate(Eoperation operationType) {
        return operationType == Eoperation.UPDATE;
    }

    public static boolean isDownload(Eoperation operationType) {
        return operationType == Eoperation.DOWNLOAD_FILE || operationType == Eoperation.VIEW_FILE;
    }

    public static boolean isReplace(Eoperation operationType) {
        return operationType == Eoperation.REPLACE;
    }

    public static boolean isValidate(Eoperation operationType) {
        return operationType == Eoperation.VALIDATE;
    }

    public static boolean isBinary(Tfield field) {
        if (field == null)
            return false;
        Efield type = field.getType();
        return type != null && (type == Efield.BINARY || type == Efield.IMAGE || type == Efield.IMAGE_FILE || "clob".equals(type.value()));
    }

    /**
     * @param type
     * @return
     */
    public static boolean typeIsBoolean(String type) {
        return "boolean".equals(type) || "false".equals(type) || "true".equals(type);
    }

    public static boolean typeIsNumeric(String type) {
        return "number".equals(type) || "float".equals(type) || "decimal".equals(type) || "double".equals(type) || "int".equals(type)
            || "intEnum".equals(type) || "integer".equals(type) || "sequence".equals(type);
    }

    /**
     * @param dataSourceName
     * @param opType
     * @return
     */
    public static String autoCreateOperationID(String dataSourceName, Eoperation opType) {
        if (dataSourceName != null && opType != null)
            return dataSourceName + "_" + opType.value();
        return null;
    }

    /**
     * @param _type
     * @return
     */
    public static boolean isBinaryType(Efield type) {
        if (type == null)
            return false;
        if (type == Efield.BINARY || type == Efield.IMAGE || type == Efield.IMAGE_FILE || "clob".equalsIgnoreCase(type.value()))
            return true;
        else
            return false;
    }

    public static Event createValidationEvent(String type, Level level, String msg) throws SlxException {
        ValidationEventFactory factory = ValidationEventFactory.instance();
        factory.setType(type);
        return ValidationEventWrapper.wrapper(factory.create(level, msg));
    }

}
