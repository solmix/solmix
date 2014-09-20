/*
 * Copyright 2012 The Solmix Project
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package org.solmix.commons.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @version 110035
 */
public final class DateUtils
{

    private static final Logger LOG = LoggerFactory.getLogger(DataUtils.class);

    public static final long DAY_MS_TIME = 24 * 60 * 60 * 1000;

    public static String simpleDateFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);

    }

    public static String getFirstDayofMouth(String date, String inPattern, String outPattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(inPattern);
        String __return = "";
        try {
            Date d = sdf.parse(date);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            int start = c.getActualMinimum(Calendar.DAY_OF_MONTH);
            c.set(Calendar.DAY_OF_MONTH, start);
            sdf = new SimpleDateFormat(outPattern);
            __return = sdf.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return __return;
    }

    public static String getEndDayofMouth(String date, String inPattern, String outPattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(inPattern);
        String __return = "";
        try {
            Date d = sdf.parse(date);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            int start = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            c.set(Calendar.DAY_OF_MONTH, start);
            sdf = new SimpleDateFormat(outPattern);
            __return = sdf.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return __return;
    }

    public static Date getDateFromString(String date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date __return = null;
        try {
            __return = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return __return;
    }

    public static String getCurrentDateStr(String pattern) {
        return getDateString(new Date(), pattern);
    }

    public static String getDateString(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String __return = null;
        try {
            __return = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return __return;
    }

    public static String getDateString(long dateLong, String pattern) {
        Date date = new Date();
        date.setTime(dateLong);
        return getDateString(date, pattern);
    }

    public static String getDateString(long dateLong) {
        Date date = new Date();
        date.setTime(dateLong);
        return getDateString(date, "yyyy-MM-dd");
    }

    public static String getDateString(String dateLongStr) {
        Date date = new Date();
        date.setTime(Long.valueOf(dateLongStr));
        return getDateString(date, "yyyy-MM-dd");
    }

    public static void main(String args[]) {
        System.out.println(DateUtils.getFirstDayofMouth("201105", "yyyyMM", "yyyyMMdd"));
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        String textDate = "2013-04-01T02:27:05";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(DateUtils.getCurrentDateStr("yyyy-MM-dd HH:mm:ss"));
        System.out.println(new Date());
        try {
            System.out.println(sdf.parse(textDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
