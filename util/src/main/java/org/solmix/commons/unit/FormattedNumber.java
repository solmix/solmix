package org.solmix.commons.unit;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FormattedNumber implements Serializable {
	

	public static final String DEFAULT_SEPERATOR = " ";
	    
	    private String value;
	    private String tag;
	    private String seperator;
	    
	    public FormattedNumber(String value, String tag){
	        this(value, tag, DEFAULT_SEPERATOR);
	    }

	    public FormattedNumber(String value, String tag, String seperator) {
	        this.value     = value;
	        this.tag       = tag;
	        this.seperator = seperator;            
	    }
	    
	    public String getValue(){
	        return this.value;
	    }

	    public String getTag(){
	        return this.tag;
	    }

	    public String toString(){
	        String res;

	        res = this.value + this.seperator + this.tag;
	        return res.trim();
	    }
}
