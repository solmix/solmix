
package org.solmix.commons.unit;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class PercentFormatter extends PercentageFormatter
{

    protected double getMultiplier() {
        return 1.0;
    }

    protected int getUnitType() {
        return UnitConstants.UNIT_PERCENT;
    }

    public UnitNumber parse(String val, Locale locale, ParseSpecifics specifics) throws ParseException {
        NumberFormat fmt;

        fmt = NumberFormat.getNumberInstance(locale);
        return new UnitNumber(fmt.parse(val).doubleValue(), this.getUnitType(), this.getUnitScale());
    }
}
