
package org.solmix.commons.unit;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.solmix.commons.util.ArrayUtils;
import org.solmix.commons.util.StringUtils;

public class DurationFormatter implements Formatter
{

    protected static final int GRANULAR_YEARS = 1; // Time > 1 year

    protected static final int GRANULAR_DAYS = 2; // 1 day < time < 1 year

    protected static final int GRANULAR_HOURS = 3; // 1 hour < time < 1 day

    protected static final int GRANULAR_MINS = 4; // 1 min < time < 1 hour

    protected static final int GRANULAR_SECS = 5; // 1 sec < time < 1 min

    protected static final int GRANULAR_MILLIS = 6; // 0 < time < 1 sec

    protected static final int MILLISEC_DIGITS = 3; // Standard 3 digits to the
                                                    // right of the decimal point

    class TimeBreakDown
    {

        long nYears, nDays, nHours, nMins, nSecs, nMilli;
    }

    public FormattedNumber format(UnitNumber val, Locale locale, FormatSpecifics specifics) {
        int granularity;

        granularity = getGranularity(val);
        return format(val.getBaseValue(), granularity, MILLISEC_DIGITS, locale);
    }

    public FormattedNumber[] formatSame(double[] val, int unitType, int scale, Locale locale, FormatSpecifics specifics) {
        FormattedNumber[] res;
        double[] secs;
        UnitNumber tmpNum;
        int maxIdx, granularity, milliDigits;
        TimeBreakDown tbd;
        boolean wholeNum;

        res = new FormattedNumber[val.length];
        if ((maxIdx = ArrayUtils.max(val)) == -1) {
            return res;
        }

        tmpNum = new UnitNumber(val[maxIdx], unitType, scale);
        granularity = getGranularity(tmpNum);

        // Determine the number scale (right of the decimal point) that is
        // needed to ensure that every formatted number is unique and a
        // linear scale
        wholeNum = true;
        secs = new double[val.length];
        for (int i = 0; i < secs.length; i++) {
            tbd = breakDownTime(UnitFormat.getBaseValue(val[i], unitType, scale));
            secs[i] = tbd.nSecs + ((double) tbd.nMilli / 1000);
            if (tbd.nMilli > 0)
                wholeNum = false;
        }

        milliDigits = UnitUtil.getUniqueDigits(secs, locale);

        if (milliDigits == 0 && wholeNum == false)
            milliDigits = 1;

        // Format the numbers
        for (int i = 0; i < val.length; i++) {
            tmpNum = new UnitNumber(val[i], unitType, scale);
            res[i] = format(tmpNum.getBaseValue(), granularity, milliDigits, locale);
        }

        return res;
    }

    protected FormattedNumber format(BigDecimal baseTime, int granularity, int milliDigits, Locale locale) {
        TimeBreakDown tbd;
        String res;

        // We work in a few different time ranges depending on the
        // magnitude of the duration. Quite hardcoded
        if (granularity == GRANULAR_YEARS) {
            tbd = breakDownTime(baseTime);
            res = tbd.nYears + "y " + tbd.nDays + "d";
        } else if (granularity == GRANULAR_DAYS) {
            tbd = breakDownTime(baseTime);
            long nDays = tbd.nYears * 365 + tbd.nDays;
            res = nDays + (nDays == 1 ? " day " : " days ")
                + StringUtils.formatDuration(tbd.nHours * 60 * 60 * 1000 + tbd.nMins * 60 * 1000 + tbd.nSecs * 1000 + tbd.nMilli);
        } else if (granularity == GRANULAR_HOURS || granularity == GRANULAR_MINS || granularity == GRANULAR_SECS) {
            long nMillis = baseTime.divide(UnitUtil.FACT_MILLIS, BigDecimal.ROUND_HALF_EVEN).longValue();
            res = StringUtils.formatDuration(nMillis, milliDigits, granularity == GRANULAR_SECS);

            if (granularity == GRANULAR_SECS)
                res = res + 's';
        } else if (granularity == GRANULAR_MILLIS) {
            // Format into milliseconds
            double dMillis = baseTime.doubleValue() / 1000000;

            NumberFormat fmt = NumberFormat.getInstance();
            fmt.setMinimumIntegerDigits(1);
            fmt.setMaximumIntegerDigits(3);
            fmt.setMinimumFractionDigits(0);
            fmt.setMaximumFractionDigits(milliDigits);

            res = fmt.format(dMillis) + "ms";
        } else {
            throw new IllegalStateException("Unexpected granularity");
        }

        return new FormattedNumber(res.trim(), "");
    }

    protected TimeBreakDown breakDownTime(BigDecimal val) {
        TimeBreakDown r = new TimeBreakDown();

        r.nYears = val.divide(UnitUtil.FACT_YEARS, BigDecimal.ROUND_DOWN).intValue();
        if (r.nYears > 0)
            val = val.subtract(UnitUtil.FACT_YEARS.multiply(new BigDecimal(Long.toString(r.nYears))));

        r.nDays = val.divide(UnitUtil.FACT_DAYS, BigDecimal.ROUND_DOWN).intValue();
        if (r.nDays > 0)
            val = val.subtract(UnitUtil.FACT_DAYS.multiply(new BigDecimal(Long.toString(r.nDays))));

        r.nHours = val.divide(UnitUtil.FACT_HOURS, BigDecimal.ROUND_DOWN).intValue();
        if (r.nHours > 0)
            val = val.subtract(UnitUtil.FACT_HOURS.multiply(new BigDecimal(Long.toString(r.nHours))));

        r.nMins = val.divide(UnitUtil.FACT_MINS, BigDecimal.ROUND_DOWN).intValue();
        if (r.nMins > 0)
            val = val.subtract(UnitUtil.FACT_MINS.multiply(new BigDecimal(Long.toString(r.nMins))));

        r.nSecs = val.divide(UnitUtil.FACT_SECS, BigDecimal.ROUND_DOWN).intValue();
        if (r.nSecs > 0)
            val = val.subtract(UnitUtil.FACT_SECS.multiply(new BigDecimal(Long.toString(r.nSecs))));

        r.nMilli = val.divide(UnitUtil.FACT_MILLIS, BigDecimal.ROUND_DOWN).intValue();
        return r;
    }

    private int getGranularity(UnitNumber val) {
        BigDecimal nanoSecs = val.getBaseValue();

        if (nanoSecs.longValue() > 0) {
            TimeBreakDown tbd = breakDownTime(nanoSecs);

            if (tbd.nYears > 0)
                return GRANULAR_YEARS;
            else if (tbd.nDays > 0)
                return GRANULAR_DAYS;
            else if (tbd.nHours > 0)
                return GRANULAR_HOURS;
            else if (tbd.nMins > 0)
                return GRANULAR_MINS;
            else if (tbd.nSecs > 0)
                return GRANULAR_SECS;
            else
                return GRANULAR_MILLIS;
        } else { // Use original scale as granularity for 0
            switch (val.getScale()) {
                case UnitConstants.SCALE_YEAR:
                    return GRANULAR_YEARS;
                case UnitConstants.SCALE_DAY:
                    return GRANULAR_DAYS;
                case UnitConstants.SCALE_HOUR:
                    return GRANULAR_HOURS;
                case UnitConstants.SCALE_MIN:
                    return GRANULAR_MINS;
                case UnitConstants.SCALE_SEC:
                    return GRANULAR_SECS;
                default:
                    return GRANULAR_MILLIS;
            }
        }

    }

    public BigDecimal getBaseValue(double value, int scale) {
        return DateFormatter.getBaseTime(value, scale);
    }

    public BigDecimal getScaledValue(BigDecimal value, int targScale) {
        return DateFormatter.getScaledTime(value, targScale);
    }

    /**
     * Returns the # of seconds in a string in the form of "xx.xs" or "xx:yy:zz.a"
     */
    protected double parseTimeStr(String duration) throws ParseException {
        double nHours, nMins, nSecs;
        String[] vals = (String[]) StringUtils.split(duration, ":");

        if (vals.length != 3) {
            throw new ParseException(duration, 0);
        }

        try {
            nHours = Double.parseDouble(vals[0]);
            nMins = Double.parseDouble(vals[1]);
            nSecs = Double.parseDouble(vals[2]);
            return nHours * 60 * 60 + nMins * 60 + nSecs;
        } catch (NumberFormatException exc) {
            throw new ParseException(duration, 0);
        }
    }

    /**
     * Parse a string which is of the same format that we output, when outputting durations.
     */
    private UnitNumber parseRegular(String val, Locale locale, ParseSpecifics specifics) throws ParseException {
        String[] vals = (String[]) StringUtils.split(val, " ");

        double value;
        int scale = UnitConstants.SCALE_SEC;
        try {
            if (vals.length == 2 && vals[0].charAt(vals[0].length() - 1) == 'y' && vals[1].charAt(vals[1].length() - 1) == 'd') {
                String yStr = vals[0].substring(0, vals[0].length() - 1);
                String dStr = vals[1].substring(0, vals[1].length() - 1);
                value = Integer.parseInt(yStr) * 365 + Integer.parseInt(dStr);
                scale = UnitConstants.SCALE_DAY;
            } else if (vals.length == 3 && (vals[1].equals("day") || vals[1].equals("days"))) {
                value = Integer.parseInt(vals[0]) * 24 * 60 * 60 + parseTimeStr(vals[2]);
            } else if (vals.length == 1) {
                value = parseTimeStr(vals[0]);
            } else {
                // String not recognized
                throw new ParseException(val, 0);
            }
        } catch (NumberFormatException exc) {
            throw new ParseException(val, 0);
        }

        return new UnitNumber(value, UnitConstants.UNIT_DURATION, scale);
    }

    public UnitNumber parse(String val, Locale locale, ParseSpecifics specifics) throws ParseException {
        NumberFormat fmt = NumberFormat.getInstance(locale);
        double numberPart;
        String tagPart;
        int nonIdx, scale;

        try {
            return parseRegular(val, locale, specifics);
        } catch (ParseException exc) {
            // That's fine, try another format
        }

        nonIdx = UnitUtil.findNonNumberIdx(val, fmt);
        if (nonIdx == -1) {
            throw new ParseException("Number had no units with it", val.length());
        }

        if (nonIdx == 0) {
            throw new ParseException("Invalid number specified", 0);
        }

        numberPart = fmt.parse(val.substring(0, nonIdx)).doubleValue();
        tagPart = val.substring(nonIdx, val.length()).trim();

        if (tagPart.equalsIgnoreCase("y") || tagPart.equalsIgnoreCase("yr") || tagPart.equalsIgnoreCase("yrs") || tagPart.equalsIgnoreCase("year")
            || tagPart.equalsIgnoreCase("years")) {
            scale = UnitConstants.SCALE_YEAR;
        } else if (tagPart.equalsIgnoreCase("w") || tagPart.equalsIgnoreCase("wk") || tagPart.equalsIgnoreCase("wks")
            || tagPart.equalsIgnoreCase("week") || tagPart.equalsIgnoreCase("weeks")) {
            scale = UnitConstants.SCALE_WEEK;
        } else if (tagPart.equalsIgnoreCase("d") || tagPart.equalsIgnoreCase("day") || tagPart.equalsIgnoreCase("days")) {
            scale = UnitConstants.SCALE_DAY;
        } else if (tagPart.equalsIgnoreCase("h") || tagPart.equalsIgnoreCase("hr") || tagPart.equalsIgnoreCase("hrs")
            || tagPart.equalsIgnoreCase("hour") || tagPart.equalsIgnoreCase("hours")) {
            scale = UnitConstants.SCALE_HOUR;
        } else if (tagPart.equalsIgnoreCase("m") || tagPart.equalsIgnoreCase("min") || tagPart.equalsIgnoreCase("mins")
            || tagPart.equalsIgnoreCase("minute") || tagPart.equalsIgnoreCase("minutes")) {
            scale = UnitConstants.SCALE_MIN;
        } else if (tagPart.equalsIgnoreCase("s") || tagPart.equalsIgnoreCase("sec") || tagPart.equalsIgnoreCase("secs")
            || tagPart.equalsIgnoreCase("second") || tagPart.equalsIgnoreCase("seconds")) {
            scale = UnitConstants.SCALE_SEC;
        } else if (tagPart.equalsIgnoreCase("j") || tagPart.equalsIgnoreCase("jif") || tagPart.equalsIgnoreCase("jifs")
            || tagPart.equalsIgnoreCase("jiffy") || tagPart.equalsIgnoreCase("jiffys") || tagPart.equalsIgnoreCase("jifferoonies")) {
            scale = UnitConstants.SCALE_JIFFY;
        } else if (tagPart.equalsIgnoreCase("ms") || tagPart.equalsIgnoreCase("milli") || tagPart.equalsIgnoreCase("millis")
            || tagPart.equalsIgnoreCase("millisecond") || tagPart.equalsIgnoreCase("milliseconds")) {
            scale = UnitConstants.SCALE_MILLI;
        } else if (tagPart.equalsIgnoreCase("us") || tagPart.equalsIgnoreCase("micro") || tagPart.equalsIgnoreCase("micros")
            || tagPart.equalsIgnoreCase("microsecond") || tagPart.equalsIgnoreCase("microseconds")) {
            scale = UnitConstants.SCALE_MICRO;
        } else if (tagPart.equalsIgnoreCase("ns") || tagPart.equalsIgnoreCase("nano") || tagPart.equalsIgnoreCase("nanos")
            || tagPart.equalsIgnoreCase("nanosecond") || tagPart.equalsIgnoreCase("nanoseconds")) {
            scale = UnitConstants.SCALE_NANO;
        } else {
            throw new ParseException("Unknown duration '" + tagPart + "'", nonIdx);
        }

        return new UnitNumber(numberPart, UnitConstants.UNIT_DURATION, scale);
    }
}