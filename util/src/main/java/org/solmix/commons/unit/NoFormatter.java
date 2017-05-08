package org.solmix.commons.unit;

import java.text.NumberFormat;

public class NoFormatter extends AbstractFormatter {

	 protected int getUnitType(){
	        return UnitConstants.UNIT_NONE;
	    }

	    protected int getUnitScale(){
	        return UnitConstants.SCALE_NONE;
	    }

	    protected FormattedNumber formatNumber(double rawValue, NumberFormat fmt){
	    	return new FormattedNumber(fmt.format(rawValue), "");
	    }

}
