/*
 *  Copyright 2012 The Solmix Project
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

package org.solmix.sgt.client.advanceds;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.core.BaseClass;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.util.JSOHelper;

/**
 * 
 * @author Administrator
 * @version 110035 2012-12-12
 */

public class AdvanceDataSource extends RestDataSource
{

    public static AdvanceDataSource getOrCreateRef(JavaScriptObject jsObj) {
        if (jsObj == null)
            return null;
        BaseClass obj = BaseClass.getRef(jsObj);
        if (obj != null) {
            return (AdvanceDataSource) obj;
        } else {
            return new AdvanceDataSource(jsObj);
        }
    }

    public void setJavaScriptObject(JavaScriptObject jsObj) {
        id = JSOHelper.getAttribute(jsObj, "ID");
    }

    public void setID(String id) {
        id = id.replace('/', '$');
        super.setID(id);
    }

    public AdvanceDataSource()
    {
        scClassName = "AdvanceDataSource";
    }

    public AdvanceDataSource(JavaScriptObject jsObj)
    {
        scClassName = "AdvanceDataSource";
        setJavaScriptObject(jsObj);
    }

    public native JavaScriptObject create()/*-{
		var config = this.@com.smartgwt.client.core.BaseClass::getConfig()();
		var scClassName = this.@com.smartgwt.client.core.BaseClass::scClassName;
		return $wnd.isc.AdvanceDataSource.create(config);
    }-*/;

    public void exportData() {

    }

    public void setViewType(String addDataURL) throws IllegalStateException {
        setAttribute("viewType", addDataURL, false);
    }

    /**
     * Custom operation for datasource request
     * 
     * 
     * @return . See {@link com.smartgwt.client.docs.String String}
     */
    public String getViewType() {
        return getAttributeAsString("viewType");
    }
}
