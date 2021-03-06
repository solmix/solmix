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

package org.solmix.fusionchart.client;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.plugins.Flashlet;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2012-12-6
 */

public class FusionChartOld extends Flashlet
{

    private static int count = 0;

    private final String swfId;

    public FusionChartOld(String src, int width, int height, String dataUrl)
    {
        this(src, width + "", height + "", dataUrl);
    }

    public FusionChartOld(String src, String width, String height, String dataUrl)
    {
        super();

        setCodeBase("http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0");
        setClassID("clsid:d27cdb6e-ae6d-11cf-96b8-444553540000");
        setPluginsPage("http://www.macromedia.com/go/getflashplayer");

        swfId = "fusionChartId_" + count;
        ++count;
        setID("w_"+swfId);
        setName(swfId);
        setSrc(GWT.getModuleName()+"/fchart/" + src);
        setSize(width, height);

        HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("id", swfId);
        hashMap.put("flashvars", "&id=" + swfId + "&chartWidth=" + width + "&chartHeight=" + height + "&registerWithJS=1" + "&debugMode=0"
            + "&dataURL=data/bin/fetch/" + dataUrl);

        hashMap.put("allowscriptaccess", "always");
        hashMap.put("bgcolor", "#ffffff");
        hashMap.put("quality", "high");

        // If you embed the chart into your web page, and the page has layers such as drop-down menus or
        // drop-down forms, and you want them appear above the chart, you need to add the following line
        // to your code.
         hashMap.put("wmode", "opaque" );

        setParams(hashMap);
        // setCanSelectText(true);
    }

    public String getSwfID() {
        return this.swfId;
    }

    public native void updateChartXML(String xmlData) /*-{
		try {
		var chartid=this.@org.solmix.fusionchart.client.FusionChartOld::getSwfID()();
	      $wnd.updateChartXML(chartid,xmlData);
		} catch (e) {
		$wnd.alert(e);
		}
    }-*/;

}
