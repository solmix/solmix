package org.solmix.commons.unit;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

public interface Formatter {
	/**
     * Format a number with the given locale.  
     *
     * @param val    Value to format
     * @param locale Locale that the resultant format should be in
     * @param format Optional format to give specific hints to the
     *               formatter about how the result should look.
     */
    public FormattedNumber format(UnitNumber val, Locale locale,
                                  FormatSpecifics format);

    /**
     * Format a several values at once into the same format.  I.e.
     * no matter the discrepency of range, they all contain the
     * same units (megabytes, etc.)
     *
     * @param values   Values to format
     * @param unitType One of UnitsConstants.UNIT_*
     * @param scale    One of UnitsConstants.SCALE_*
     * @param locale   Locale that the resultant format should be in
     * @param format   Optional format to give specific hints to the
     *                 formatter about how the result should look.
     */
    public FormattedNumber[] formatSame(double[] values, int unitType, 
                                        int scale, Locale locale,
                                        FormatSpecifics format);

    /**
     * Get the base value of a value, given its scale.  
     *
     * E.x.:  getBaseValue(1, SCALE_KILO) -> 1024 (bytes being the base unit)
     *
     * @param value Value to get the base of
     * @param scale Scale of the value -- must be valid for the 
     *              formatter unit type
     */
    public BigDecimal getBaseValue(double value, int scale);

    /**
     * Get a scaled version of a value.  The value in its base format, and
     * the target scale are passed.  The return value will be a number,
     * scaled to the target.
     *
     * E.x.:  getScaledValue(1024, SCALE_NONE) -> 1024
     *        getScaledValue(1024, SCALE_KILO) -> 1 (1 kilobyte)
     *
     * @param value     Value to scale
     * @param targScale Target scale -- must be valid for the 
     *                  formatter unit type
     */
    public BigDecimal getScaledValue(BigDecimal value, int targScale);

    /**
     * Parse a string into a UnitNumber.
     *
     * E.x. parse("34 MB") -> UnitNumber(34, UNIT_BYTES, SCALE_MEGA)
     *
     * @param val    Value to parse
     * @param locale Locale to parse with
     * @param specifics An optional argument which gives the parser 
     *                  more information about parsing
     *
     * @return the number representing the parsed value
     * @throw ParseException if the value could not be parsed 
     */
    public UnitNumber parse(String val, Locale locale, 
                            ParseSpecifics specifics)
        throws ParseException;
}
