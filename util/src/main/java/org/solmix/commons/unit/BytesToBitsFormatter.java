package org.solmix.commons.unit;

import java.text.NumberFormat;


public class BytesToBitsFormatter extends BitRateFormatter {

    //(bytes * 8) == bits
    protected FormattedNumber createFormattedValue(double value,
                                                   int scale,
                                                   NumberFormat fmt) {

        return super.createFormattedValue(value * 8, scale, fmt);
    }
}
