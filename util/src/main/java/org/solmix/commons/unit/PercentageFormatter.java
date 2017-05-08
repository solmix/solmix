
package org.solmix.commons.unit;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class PercentageFormatter extends AbstractFormatter
{

    protected int getUnitType() {
        return UnitConstants.UNIT_PERCENTAGE;
    }

    protected int getUnitScale() {
        return UnitConstants.SCALE_NONE;
    }

    protected double getMultiplier() {
        return 100.0;
    }

    protected FormattedNumber formatNumber(double rawValue, NumberFormat fmt) {
        return new FormattedNumber(fmt.format(rawValue * getMultiplier()), "%", "");
    }

    public UnitNumber parse(String val, Locale locale, ParseSpecifics specifics) throws ParseException {
        NumberFormat fmt;

        fmt = NumberFormat.getPercentInstance(locale);
        return new UnitNumber(fmt.parse(val).doubleValue(), this.getUnitType(), this.getUnitScale());
    }

    public FormattedNumber[] formatSame(double[] vals, int unitType, int scale, Locale locale, FormatSpecifics specifics) {
        FormattedNumber[] res;
        NumberFormat fmt;
        // TODO: refactor to rm duplication of validation
        if (unitType != this.getUnitType()) {
            throw new IllegalArgumentException("Invalid unit specified");
        }
        if (scale != this.getUnitScale()) {
            throw new IllegalArgumentException("Invalid scale specified");
        }
        fmt = NumberFormat.getInstance(locale);
        fmt.setMaximumFractionDigits(2);
        res = new FormattedNumber[vals.length];
        for (int i = 0; i < vals.length; i++) {
            res[i] = this.formatNumber(vals[i], fmt);
        }
        return res;
    }

}
