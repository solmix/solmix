package org.solmix.commons.unit;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.solmix.commons.util.DateUtils;


public class DateFormatter implements Formatter
{
    public static class DateSpecifics
        extends FormatSpecifics 
    {
        private DateFormat df;

        public DateSpecifics(){}
        
        public void setDateFormat(DateFormat df){
            this.df = df;
        }
        
        public DateFormat getDateFormat(){
            return this.df;
        }
    }

    public FormattedNumber format(UnitNumber val, Locale locale){
        DateSpecifics specifics = new DateSpecifics();

        specifics.setDateFormat(this.getDefaultFormat(locale));
        return this.format(val, locale, specifics);
    }

    public FormattedNumber format(UnitNumber val, Locale locale, 
                                  FormatSpecifics specifics)
    {
        BigDecimal dec;
        DateFormat df;

        // We need a value in the milliseconds range
        dec = val.getBaseValue().divide(UnitUtil.FACT_MILLIS,
                                        BigDecimal.ROUND_HALF_EVEN);
        if(specifics == null){
            df = this.getDefaultFormat(locale);
        } else {
            df = ((DateSpecifics)specifics).getDateFormat();
        }
        return new FormattedNumber(df.format(new Date(dec.longValue())), "");
    }

    public FormattedNumber[] formatSame(double[] val, int unitType, int scale,
                                        Locale locale)
    {
        FormattedNumber[] res;

        res = new FormattedNumber[val.length];

        for(int i=0; i<val.length; i++){
            res[i] = this.format(new UnitNumber(val[i], unitType, scale),
                                 locale);
        }
        
        return res;
    }

    public FormattedNumber[] formatSame(double[] val, int unitType, int scale,
                                        Locale locale, 
                                        FormatSpecifics specifics)
    {
        FormattedNumber[] res;

        res = new FormattedNumber[val.length];

        for(int i=0; i<val.length; i++){
            res[i] = this.format(new UnitNumber(val[i], unitType, scale),
                                 locale, specifics);
        }
        
        return res;
    }

    private DateFormat getDefaultFormat(Locale locale){
        return DateFormat.getDateTimeInstance(DateFormat.SHORT,
                                              DateFormat.MEDIUM, locale);
    }

    public BigDecimal getBaseValue(double value, int scale){
        return DateFormatter.getBaseTime(value, scale);
    }

    public BigDecimal getScaledValue(BigDecimal value, int targScale){
        return DateFormatter.getScaledTime(value, targScale);               
    }

    public static BigDecimal getScaledTime(BigDecimal value, int targScale){
        return value.divide(getScaleCoeff(targScale),
                            BigDecimal.ROUND_HALF_EVEN);
    }

    public static BigDecimal getBaseTime(double value, int scale){
        BigDecimal res;

        res = new BigDecimal(value);
        return res.multiply(getScaleCoeff(scale));
    }

    private static BigDecimal getScaleCoeff(int scale){
        switch(scale){
        case UnitConstants.SCALE_NONE:
            return UnitUtil.FACT_NONE;
        case UnitConstants.SCALE_NANO:
            return UnitUtil.FACT_NANOS;
        case UnitConstants.SCALE_MICRO:
            return UnitUtil.FACT_MICROS;
        case UnitConstants.SCALE_MILLI:
            return UnitUtil.FACT_MILLIS;
        case UnitConstants.SCALE_JIFFY:
            return UnitUtil.FACT_JIFFYS;
        case UnitConstants.SCALE_SEC:
            return UnitUtil.FACT_SECS;
        case UnitConstants.SCALE_MIN:
            return UnitUtil.FACT_MINS;
        case UnitConstants.SCALE_HOUR:
            return UnitUtil.FACT_HOURS;
        case UnitConstants.SCALE_DAY:
            return UnitUtil.FACT_DAYS;
        case UnitConstants.SCALE_WEEK:
            return UnitUtil.FACT_WEEKS;
        case UnitConstants.SCALE_YEAR:
            return UnitUtil.FACT_YEARS;
        }
        
        throw new IllegalArgumentException("Value did not have time " +
                                           "based scale");
    }

    public UnitNumber parse(String val, Locale locale, 
                            ParseSpecifics specifics)
        throws ParseException
    {
        long curTime = System.currentTimeMillis();

        return new UnitNumber(DateUtils.parseComplexTime(val, curTime, false),
                              UnitConstants.UNIT_DATE,
                              UnitConstants.SCALE_MILLI);
    }
}