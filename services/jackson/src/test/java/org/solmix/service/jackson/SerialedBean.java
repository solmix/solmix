package org.solmix.service.jackson;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SerialedBean {

	private boolean bool;
	private InternalBean internal ;
	
	private String n;
	
	@JsonIgnore
	private InternalBean ignore ;

	public boolean isBool() {
		return bool;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}

	public InternalBean getInternal() {
		return internal;
	}

	public void setInternal(InternalBean internal) {
		this.internal = internal;
	}

	public InternalBean getIgnore() {
		return ignore;
	}

	public void setIgnore(InternalBean ignore) {
		this.ignore = ignore;
	}
	@JsonIgnore
	public String getName() {
		return n;
	}

	public void setName(String name) {
		this.n = name;
	}
	
	
}
