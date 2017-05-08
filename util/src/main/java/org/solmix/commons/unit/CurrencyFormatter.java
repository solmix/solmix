
package org.solmix.commons.unit;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter extends AbstractFormatter
{

    protected String getTagName() {
        return "cents";
    }

    protected int getUnitType() {
        return UnitConstants.UNIT_CURRENCY;
    }

    protected int getUnitScale() {
        return UnitConstants.SCALE_NONE;
    }

    protected NumberFormat getNumberFormat(Locale locale) {
        NumberFormat res = NumberFormat.getInstance(locale);

        res.setMinimumFractionDigits(2);
        res.setMaximumFractionDigits(2);
        return res;
    }

    protected FormattedNumber formatNumber(double rawValue, NumberFormat fmt) {
        String num = NumberFormat.getCurrencyInstance(Locale.US).format(rawValue / 100);
        return new FormattedNumber(num, "", " ");
    }
}
