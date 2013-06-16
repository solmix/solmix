package com.solmix.sgt.server;


public enum RequestType {
	
	BIN("bin"),EVENT("event");
	
	private String value;
	RequestType(String value){
		this.value=value;
	}
	public String value(){
		return value;
	}
	public RequestType fromValue(String v){
		for(RequestType c:RequestType.values()){
			if(c.value.equals(v))
				return c;
		}
		throw new IllegalArgumentException(v);
	}
}
