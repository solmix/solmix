package org.solmix.commons.unit;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitFormat {
	 private static final HashMap<Integer,Formatter> formatters;

	    static {
	        formatters = new HashMap<Integer,Formatter>();

	        formatters.put(new Integer(UnitConstants.UNIT_NONE),
	                       new NoFormatter());
	        formatters.put(new Integer(UnitConstants.UNIT_CURRENCY),
	                       new CurrencyFormatter());
	        formatters.put(new Integer(UnitConstants.UNIT_BYTES),
	                       new BytesFormatter());
	        formatters.put(new Integer(UnitConstants.UNIT_BITS),
	                       new BitRateFormatter());
	        formatters.put(new Integer(UnitConstants.UNIT_BYTES2BITS),
	                       new BytesToBitsFormatter());
	        formatters.put(new Integer(UnitConstants.UNIT_DURATION),
	                       new DurationFormatter());
	        formatters.put(new Integer(UnitConstants.UNIT_DATE),
	                       new DateFormatter());
	        formatters.put(new Integer(UnitConstants.UNIT_PERCENTAGE),
	                       new PercentageFormatter());
	        formatters.put(new Integer(UnitConstants.UNIT_PERCENT),
	                       new PercentFormatter());
	        formatters.put(new Integer(UnitConstants.UNIT_APPROX_DUR),
	                       new ApproxDurationFormatter());
	    }

	    private static Logger log = LoggerFactory.getLogger(UnitFormat.class);

	    public static FormattedNumber format(UnitNumber val){
	        return format(val, Locale.getDefault());
	    }
	    
	    public static FormattedNumber format(UnitNumber val, Locale locale){
	        return format(val, locale, null);
	    }
	    
	    private static Formatter getFormatter(int unitType){
	        Formatter res;

	        res = (Formatter)formatters.get(new Integer(unitType));
	        if(res == null){
	            throw new IllegalStateException("Unhandled unit type: " + 
	                                            unitType);
	        }
	        return res;
	    }


	    public static FormattedNumber format(UnitNumber val, Locale locale, 
	                                         FormatSpecifics specifics)
	    {
	        FormattedNumber res;
	        Formatter formatter;

	        formatter = getFormatter(val.getUnits());

	        res = formatter.format(val, locale, specifics);
	        if(log.isDebugEnabled()){
	            log.debug("format(" + val.getValue() + ") -> " + res);
	        }
	        return res;
	    }

	    public static FormattedNumber[] formatSame(double[] values, int unitType, 
	                                               int scale)
	    {
	        return formatSame(values, unitType, scale, Locale.getDefault());
	    }

	    public static FormattedNumber[] formatSame(double[] values, int unitType, 
	                                               int scale, Locale locale)
	    {
	        return(formatSame(values, unitType, scale, locale, null));
	    }

	    public static FormattedNumber[] formatSame(double[] values, int unitType, 
	                                               int scale, Locale locale, 
	                                               FormatSpecifics specifics)
	    {
	        FormattedNumber[] res;
	        Formatter formatter;

	        formatter = getFormatter(unitType);

	        res = formatter.formatSame(values, unitType, scale, locale, 
	                                   specifics);

	        if(log.isDebugEnabled()){
	            StringBuffer buf = new StringBuffer();
	            
	            buf.append("format({");
	            for(int i=0; i<values.length; i++){
	                buf.append(values[i]);
	                if(i != values.length)
	                    buf.append(", ");
	            }
	            buf.append("}) -> {");

	            for(int i=0; i<res.length; i++){
	                buf.append(res[i].toString());
	                if(i != values.length)
	                    buf.append(", ");
	            }
	            buf.append("}");

	            log.debug(buf.toString());
	        }
	        return res;
	    }

	    public static BigDecimal getBaseValue(double value, int unitType, int scale){
	        return getFormatter(unitType).getBaseValue(value, scale);
	    }

	    public static BigDecimal getScaledValue(BigDecimal baseValue, int unitType, 
	                                     int scale)
	    {
	        return getFormatter(unitType).getScaledValue(baseValue, scale);
	    }

	    public static UnitNumber parse(String value, int unitType)
	        throws ParseException 
	    {
	        return parse(value, unitType, null);
	    }

	    public static UnitNumber parse(String value, int unitType, 
	                                   ParseSpecifics specifics)
	        throws ParseException 
	    {
	        return parse(value, unitType, Locale.getDefault(), specifics);
	    }

	    public static UnitNumber parse(String value, int unitType, Locale locale,
	                                   ParseSpecifics specifics)
	        throws ParseException
	    {
	        Formatter formatter;

	        formatter = getFormatter(unitType);
	        return formatter.parse(value, locale, specifics);
	    }

	    public static void main(String[] args) throws Exception {
	        /*for(int i=0; i<args.length; i++){
	            UnitNumber num = UnitFormat.parse(args[i], 
	                                               UnitConstants.UNIT_BYTES);

	            System.out.println("Raw = " + num.getBaseValue());
	            System.out.println("Fmt = " + 
	                               UnitFormat.format(num, Locale.getDefault()));

	            System.out.println("Scaled to bytes = " +
	                               num.getScaledValue(UnitConstants.SCALE_NONE));
	            System.out.println("Scaled to kilo = " +
	                               num.getScaledValue(UnitConstants.SCALE_KILO));
	            System.out.println("Scaled to giga = " +
	                               num.getScaledValue(UnitConstants.SCALE_GIGA));
	        }*/
	        System.out.print(getBaseValue(1.0,UnitConstants.UNIT_BYTES,UnitConstants.SCALE_KILO));
	    }
}
