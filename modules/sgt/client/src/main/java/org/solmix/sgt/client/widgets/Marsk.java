package org.solmix.sgt.client.widgets;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.HTMLPane;
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Marsk {
	Dialog marsk;
	HTMLPane content;
	private static Marsk instance;
	private  Marsk(){
		marsk = new Dialog();
		marsk.setAutoCenter(true);
		marsk.setIsModal(true);
		marsk.setMargin(0);
		Map bodyDefaults = new HashMap();  
        bodyDefaults.put("membersMargin", 0);  
        marsk.setBodyDefaults(bodyDefaults);  
		marsk.setShowHeader(false);
		marsk.setShowEdges(false);
		marsk.setShowModalMask(true);
		marsk.setShowShadow(true);
		marsk.setWidth("*");
		marsk.setHeight("*");
		 content= new HTMLPane();
//	    content.setBackgroundColor("#22B6B6");
	    content.setContents("<div style='vertical-align:center;margin: 10px;'><img src='images/loading_2.gif' width='32' height='32' style='margin:8px;float:left;vertical-align:bottom;'/></br><span id='loadingMsg' style='font-size:14px;vertical-align:bottom;'>正在加载应用......</span></div>");
		marsk.addItem(content);
	}
	
	public static Marsk getInstance(){
		if(instance==null)
			instance = new Marsk();
		return instance;
	}
	public void setContent(String message){
		String html="<div style='vertical-align:center;margin: 10px;'><img src='images/loading_2.gif' width='32' height='32' style='margin:8px;float:left;vertical-align:bottom;'/></br><span id='loadingMsg' style='font-size:14px;vertical-align:bottom;'>"+message+"</span></div>";
		content.setContents(html);
	}
	
	public static void show(String message){
		getInstance().setContent(message);
		getInstance().marsk.show();
	}
	public static void hidden(){
		getInstance().marsk.hide();
	}
}
