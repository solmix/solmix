package org.solmix.sgt.client.im.event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.core.JsObject;
import com.smartgwt.client.util.JSOHelper;

public class IMEvent extends JsObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1678651569931690467L;
	
	public IMEvent(){
		 super(JSOHelper.createObject());
	}
	public IMEvent(String topic){
		this();
		JSOHelper.setAttribute(jsObj, "topic", topic);
	}
	public String getTopic() {
		return JSOHelper.getAttribute(jsObj, "topic");
	}
	public void setTopic(String topic) {
		JSOHelper.setAttribute(jsObj, "topic", topic);
	}
	
	public void setProperties(Map<Object,Object> map){
		JSOHelper.setAttribute(jsObj, "data", map);
		
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public void addAttribute(String key,Object value){
		Map data =JSOHelper.getAttributeAsMap(jsObj, "data");
		if(data==null){
			data = new HashMap();
		}
		data.put(key, value);
		setProperties(data);
	}
	public IMEvent withAttribute(String key,Object value){
		addAttribute(key, value);
		return this;
		
	}
	@SuppressWarnings({ "rawtypes" })
	public Map getProperties(){
		return JSOHelper.getAttributeAsMap(jsObj, "data");
	}
	

}
