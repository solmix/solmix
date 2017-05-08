package org.solmix.commons.unit;

import java.text.ParseException;


public class BitRateFormatter extends AbstractBinFormatter
{
    protected String getTagName(){
        return "b";
    }

    protected UnitNumber parseTag(double number, String tag, int tagIdx,
                                  ParseSpecifics specifics)
        throws ParseException
    {
        int scale;

        if(tag.equalsIgnoreCase("b") ||
           tag.equalsIgnoreCase("b/sec"))
        {
            scale = UnitConstants.SCALE_BIT;
        } else if(tag.equalsIgnoreCase("k") ||
                  tag.equalsIgnoreCase("kb") ||
                  tag.equalsIgnoreCase("kb/sec"))
        {
            scale = UnitConstants.SCALE_KILO;
        } else if(tag.equalsIgnoreCase("m") ||
                  tag.equalsIgnoreCase("mb") ||
                  tag.equalsIgnoreCase("mb/sec"))
        {
            scale = UnitConstants.SCALE_MEGA;
        } else if(tag.equalsIgnoreCase("g") ||
                  tag.equalsIgnoreCase("gb") ||
                  tag.equalsIgnoreCase("gb/sec"))
        {
            scale = UnitConstants.SCALE_GIGA;
        } else if(tag.equalsIgnoreCase("t") ||
                  tag.equalsIgnoreCase("tb") ||
                  tag.equalsIgnoreCase("tb/sec"))
        {
            scale = UnitConstants.SCALE_TERA;
        } else if(tag.equalsIgnoreCase("p") ||
                  tag.equalsIgnoreCase("pb") ||
                  tag.equalsIgnoreCase("pb/sec"))
        {
            scale = UnitConstants.SCALE_PETA;
        } else {
            throw new ParseException("Unknown bitrate type '" + tag + "'", 
                                     tagIdx);
        }

        return new UnitNumber(number, UnitConstants.UNIT_BITS, scale);
    }

}
