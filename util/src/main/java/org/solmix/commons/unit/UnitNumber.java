package org.solmix.commons.unit;

import java.math.BigDecimal;

public class UnitNumber {
	private double value;
    private int    units;
    private int    scale;

    public UnitNumber(double value, int units) {
        this(value, units, UnitConstants.SCALE_NONE);
    }
    
    public UnitNumber(double value, int units, int scale){
        this.value = value;
        this.units = units;
        this.scale = scale;

        UnitUtil.checkValidUnits(units);
        UnitUtil.checkValidScale(scale);
        UnitUtil.checkValidScaleForUnits(units, scale);
    }

    public double getValue(){
        return this.value;
    }

    public int getUnits(){
        return this.units;
    }

    public int getScale(){
        return this.scale;
    }

    public BigDecimal getBaseValue(){
        return UnitFormat.getBaseValue(this.value, this.units, this.scale);
    }

    public BigDecimal getScaledValue(int targScale){
        return UnitFormat.getScaledValue(this.getBaseValue(), this.units, 
                                          targScale);
    }

    public String toString(){
        return Double.toString(value);
    }
}
