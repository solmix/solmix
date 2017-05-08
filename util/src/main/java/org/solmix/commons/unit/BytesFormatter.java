package org.solmix.commons.unit;

import java.text.ParseException;


public class BytesFormatter extends AbstractBinFormatter
{

    protected String getTagName(){
        return "B";
    }
    
    protected UnitNumber parseTag(double number, String tag, int tagIdx,
                                  ParseSpecifics specifics)
        throws ParseException
    {
        int scale;

        if(tag.equalsIgnoreCase("b") ||
           tag.equalsIgnoreCase("bytes"))
        {
            scale = UnitConstants.SCALE_NONE;
        } else if(tag.equalsIgnoreCase("k") ||
                  tag.equalsIgnoreCase("kb"))
        {
            scale = UnitConstants.SCALE_KILO;
        } else if(tag.equalsIgnoreCase("m") ||
                  tag.equalsIgnoreCase("mb"))
        {
            scale = UnitConstants.SCALE_MEGA;
        } else if(tag.equalsIgnoreCase("g") ||
                  tag.equalsIgnoreCase("gb"))
        {
            scale = UnitConstants.SCALE_GIGA;
        } else if(tag.equalsIgnoreCase("t") ||
                  tag.equalsIgnoreCase("tb"))
        {
            scale = UnitConstants.SCALE_TERA;
        } else if(tag.equalsIgnoreCase("p") ||
                  tag.equalsIgnoreCase("pb"))
        {
            scale = UnitConstants.SCALE_PETA;
        } else {
            throw new ParseException("Unknown byte type '" + tag + "'", 
                                     tagIdx);
        }

        return new UnitNumber(number, UnitConstants.UNIT_BYTES, scale);
    }

}
