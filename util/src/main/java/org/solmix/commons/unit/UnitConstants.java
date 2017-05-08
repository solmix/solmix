package org.solmix.commons.unit;

public interface UnitConstants {
	public static final int UNIT_NONE       = 0;
    public static final int UNIT_CURRENCY   = 1;
    public static final int UNIT_BYTES      = 2;
    public static final int UNIT_BITS       = 3;
    public static final int UNIT_DURATION   = 4;
    public static final int UNIT_DATE       = 5;
    public static final int UNIT_PERCENTAGE = 6;
    public static final int UNIT_PERCENT    = 7;
    public static final int UNIT_APPROX_DUR = 8;
    public static final int UNIT_BYTES2BITS = 9;
    public static final int UNIT_MAX        = 10; //used for checkValidUnits()

    public static final int SCALE_NONE  = 0;

    // Binary based scaling factors
    public static final int SCALE_KILO  = 1;
    public static final int SCALE_MEGA  = 2;
    public static final int SCALE_GIGA  = 3;
    public static final int SCALE_TERA  = 4;
    public static final int SCALE_PETA  = 5;

    // Time based scaling factors
    public static final int SCALE_YEAR  = 6;
    public static final int SCALE_WEEK  = 7;
    public static final int SCALE_DAY   = 8;
    public static final int SCALE_HOUR  = 9;
    public static final int SCALE_MIN   = 10;
    public static final int SCALE_SEC   = 11;
    public static final int SCALE_JIFFY = 12;
    public static final int SCALE_MILLI = 13;
    public static final int SCALE_MICRO = 14;
    public static final int SCALE_NANO  = 15;

    public static final int SCALE_BIT   = 16;
}
