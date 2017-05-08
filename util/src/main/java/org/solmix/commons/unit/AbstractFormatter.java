package org.solmix.commons.unit;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public abstract class AbstractFormatter implements Formatter {


    protected abstract int getUnitType();
    protected abstract int getUnitScale();

    public FormattedNumber format(UnitNumber val, Locale locale, 
                                  FormatSpecifics specifics)
    {
        NumberFormat fmt;

        fmt = this.getNumberFormat(locale);
        return this.formatNumber(val.getValue(), fmt);
    }

    public FormattedNumber[] formatSame(double[] vals, int unitType,
                                        int scale, Locale locale, 
                                        FormatSpecifics specifics)
    {
        FormattedNumber[] res;
        NumberFormat fmt;

        if(unitType != this.getUnitType()){
            throw new IllegalArgumentException("Invalid unit specified");
        }

        if(scale != this.getUnitScale()){
            throw new IllegalArgumentException("Invalid scale specified");
        }

        fmt = UnitUtil.getNumberFormat(vals, locale);
        res = new FormattedNumber[vals.length];

        for(int i=0; i<vals.length; i++){
            res[i] = this.formatNumber(vals[i], fmt);
        }
        return res;
    }

    protected abstract FormattedNumber 
        formatNumber(double rawValue, NumberFormat fmt);

    protected NumberFormat getNumberFormat(Locale locale){
        NumberFormat res = NumberFormat.getInstance(locale);

        res.setMinimumFractionDigits(1);
        res.setMaximumFractionDigits(1);
        return res;
    }

    public BigDecimal getBaseValue(double value, int scale){
        if(scale != this.getUnitScale()){
            throw new IllegalArgumentException("Invalid scale specified");
        }

        return new BigDecimal(value);
    }

    public BigDecimal getScaledValue(BigDecimal value, int targScale){
        if(targScale != this.getUnitScale()){
            throw new IllegalArgumentException("Invalid scale specified");
        }

        return value;
    }

    public UnitNumber parse(String val, Locale locale, 
                            ParseSpecifics specifics)
        throws ParseException
    {
        NumberFormat fmt;

        fmt = NumberFormat.getInstance(locale);
        return new UnitNumber(fmt.parse(val).doubleValue(),
                              this.getUnitType(), this.getUnitScale());
    }

}
