
package org.solmix.commons.unit;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

public class ApproxDurationFormatter extends DurationFormatter
{

    protected FormattedNumber format(BigDecimal baseTime, int granularity, int milliDigits, Locale locale) {
        TimeBreakDown tbd = breakDownTime(baseTime);
        String res;

        // We work in a few different time ranges depending on the
        // magnitude of the duration. Quite hardcoded
        if (granularity == GRANULAR_YEARS) {
            res = tbd.nYears + (tbd.nYears == 1 ? " year " : " years ");
        } else if (granularity == GRANULAR_DAYS) {
            long nDays = tbd.nYears * 365 + tbd.nDays;
            res = nDays + (nDays == 1 ? " day " : " days ");
        } else if (granularity == GRANULAR_HOURS) {
            long hours = tbd.nYears * 365 * 24 + tbd.nDays * 24 + tbd.nHours;
            res = hours + (hours == 1 ? " hour " : " hours ");
        } else if (granularity == GRANULAR_MINS) {
            long minutes = tbd.nYears * 365 * 24 * 60 + tbd.nDays * 24 * 60 + tbd.nHours * 60 + tbd.nMins;
            res = minutes + (minutes == 1 ? " minute " : " minutes ");
        } else if (granularity == GRANULAR_SECS || granularity == GRANULAR_MILLIS) {
            res = tbd.nSecs + (tbd.nSecs == 1 ? " second " : " seconds ");
        } else {
            throw new IllegalStateException("Unexpected granularity");
        }

        return new FormattedNumber(res.trim(), "");
    }

    public UnitNumber parse(String val, Locale locale, ParseSpecifics specifics) throws ParseException {
        throw new ParseException("ApproxDurationFormatter does not support parsing of values", 0);
    }
}
