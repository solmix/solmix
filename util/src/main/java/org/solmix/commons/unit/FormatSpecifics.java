package org.solmix.commons.unit;

public class FormatSpecifics {
	public static final int PRECISION_MAX = 1;

    private int precision;

    public FormatSpecifics(){
        this.precision = PRECISION_MAX;
    }
    
    public int getPrecision(){
        return this.precision;
    }

    public void setPrecision(int precision){
        if(precision != PRECISION_MAX)
            throw new IllegalArgumentException("Unknown precision '" + 
                                               precision + "'");

        this.precision = precision;
    }
}
