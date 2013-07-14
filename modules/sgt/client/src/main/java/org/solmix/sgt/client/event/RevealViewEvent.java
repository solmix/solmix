/*
 * SOLMIX PROJECT
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

package org.solmix.sgt.client.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.shared.GwtEvent;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.TokenFormatter;
import org.solmix.sgt.client.Action;
import org.solmix.sgt.client.EviewType;

/**
 * 
 * @author Administrator
 * @version 110035 2013-1-6
 */

public class RevealViewEvent extends GwtEvent<RevealViewHandler>
{

    private static Type<RevealViewHandler> TYPE;



    private Map<String, String> params;
    private String placeToken;
    private  PlaceRequest preq;
    
    public RevealViewEvent(PlaceRequest preq){
    	this.preq=preq;
    }
    
    public PlaceRequest toPlaceRequest(){
    	return preq;
    }

    public String toPlaceToken(TokenFormatter tokenFormatter){
    	assert preq != null;
    	return tokenFormatter.toPlaceToken(preq);
    }
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<RevealViewHandler> getAssociatedType() {
        return getType();
    }
    public String getNameToken(){
        assert preq != null;
        return preq.getNameToken();
    }

	public String getAction() {
		assert preq != null;
		String action = preq.getParameter(Action.ACTION,Action.MAIN_PAGE);
		return action;
	}

    public RevealViewEvent with(String name, String value) {
    	assert preq != null;
    	preq=preq.with(name, value);
        return this;
    }

    public static Type<RevealViewHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<RevealViewHandler>();
        }
        return TYPE;
    }

    @Override
    protected void dispatch(RevealViewHandler handler) {
        handler.onRevealView(this);

    }

    public Map<String, String> getParameters() {
    	assert preq != null;
    	Map<String,String> params = new HashMap<String,String>();
    	if(getParameterNames()!=null)
    	for(String str:getParameterNames()){
    		if(str.equals(Action.ACTION)||str.equals(Action.P_MODULE)||str.equals(EviewType.P_VIEW_TYPE))
    			continue;
    		params.put(str, this.getParameter(str, null));
    	}
       return params;
    }

    public String getParameter(String key, String defaultValue) {
    	assert preq != null;
    	return preq.getParameter(key, defaultValue);
      
    }

    public Set<String> getParameterNames() {
    	assert preq!=null;
    	return preq.getParameterNames();
    }
}
