/*
 * ========THE SOLMIX PROJECT=====================================
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

package org.solmix.fmk.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.solmix.api.exception.SlxException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 110035 2011-6-7
 */

@SuppressWarnings("rawtypes")
public class ExcelExport extends AbstractExport
{

    private Workbook workbook;

    private final Map<String,Short> colorCache;

    private final Map<String,CellStyle> styleCache;

    private final Map<String,Font> fontCache;

    private final Map<String,DataFormat> formatCache;

    ExcelExport()
    {
        colorCache = new HashMap<String,Short>();
        styleCache = new HashMap<String,CellStyle>();
        fontCache = new HashMap<String,Font>();
        formatCache = new HashMap<String,DataFormat>();
    }

    private CellStyle getCellStyle(Map cellStyleElements, boolean justApplyDateFormatter) {
        Font font = null;
        DataFormat format = null;
        CellStyle style = null;
        String color = null;
        String backgroundColor = null;
        String dateFormatter = null;
        if (cellStyleElements != null) {
            color = (String) cellStyleElements.get("color");
            backgroundColor = (String) cellStyleElements.get("backgroundColor");
            dateFormatter = (String) cellStyleElements.get("dateFormatter");
        }
        String combo = (new StringBuilder()).append(color).append(backgroundColor).append(dateFormatter).toString();
        if (styleCache.get(combo) != null) {
            style = styleCache.get(combo);
        } else {
            font = getFont(cellStyleElements);
            if (dateFormatter != null || justApplyDateFormatter)
                if (formatCache.get(dateFormatter) != null) {
                    format = formatCache.get(dateFormatter);
                } else {
                    format = workbook.createDataFormat();
                    formatCache.put(dateFormatter, format);
                }
            style = workbook.createCellStyle();
            if (backgroundColor != null) {
                style.setFillForegroundColor(getClosestColor(backgroundColor));
                style.setFillPattern((short) 1);
            }
            if (color != null)
                style.setFont(font);
            if (format != null)
                style.setDataFormat(format.getFormat(excelFormatFromSmartClientDateFormatter(dateFormatter)));
            style.setWrapText(true);
            styleCache.put(combo, style);
        }
        return style;
    }

    private String excelFormatFromSmartClientDateFormatter(String scFormatter) {
        String excelFormat;
        if ("toUSShortDate".equals(scFormatter))
            excelFormat = "M/D/YYYY";
        else if ("toUSShortDateTime".equals(scFormatter))
            excelFormat = "M/D/YYYY HH:MM";
        else if ("toEuropeanShortDate".equals(scFormatter))
            excelFormat = "D/M/YYYY";
        else if ("toEuropeanShortDateTime".equals(scFormatter))
            excelFormat = "D/M/YYYY HH:MM";
        else if ("toJapanShortDate".equals(scFormatter))
            excelFormat = "YYYY/M/D";
        else if ("toJapanShortDateTime".equals(scFormatter))
            excelFormat = "YYYY/M/D HH:MM";
        else if ("toSerializeableDate".equals(scFormatter))
            excelFormat = "YYYY-MM-DD HH:MM:SS";
        else if ("toDateStamp".equals(scFormatter))
            excelFormat = "YYYYMMDDTHHMMSS";
        else
            excelFormat = "DDD MMM DD YYYY HH:MM:SS";
        return excelFormat;
    }

    private RichTextString buildRichTextString(List styles, String stringValue) throws Exception {
        RichTextString styledString = null;
        if (workbook instanceof HSSFWorkbook)
            styledString = new HSSFRichTextString(stringValue);
        // else
        // try {
        // styledString = OOXMLDataExport.getXSSFRichTextString(stringValue);
        // } catch (NoClassDefFoundError e) {
        // throw new Exception(
        // "Trouble loading Apache POI OOXML classes - are the poi-ooxml and poi-ooxml-schemas .jar files deployed?  These are required in addition to the base POI .jar if you want to export in Excel 2007 format",
        // e);
        // }
        String firstValue = (String) ((Map) styles.get(0)).get("value");
        int offset = firstValue.length();
        for (int i = 1; i < styles.size(); i++) {
            Map style = (Map) styles.get(i);
            String styledValue = (String) style.get("value");
            style = (Map) style.get("style");
            Font font = getFont(style);
            if (font != null)
                styledString.applyFont(offset, offset + styledValue.length(), font);
            offset += styledValue.length();
        }

        return styledString;
    }

    private Font getFont(Map cellStyleElements) {
        Font font = null;
        if (cellStyleElements != null) {
            String color = (String) cellStyleElements.get("color");
            String fontFamily = (String) cellStyleElements.get("fontFamily");
            String fontWeight = (String) cellStyleElements.get("fontWeight");
            String fontSize = (String) cellStyleElements.get("fontSize");
            String fontStyle = (String) cellStyleElements.get("fontStyle");
            if (color != null || fontFamily != null || fontWeight != null || fontSize != null || fontStyle != null) {
                String key = (new StringBuilder()).append(color).append(fontFamily).append(fontWeight).append(fontSize).append(fontStyle).toString();
                if (fontCache.get(key) != null) {
                    font = fontCache.get(key);
                } else {
                    font = workbook.createFont();
                    if (color != null)
                        font.setColor(getClosestColor(color));
                    if (fontFamily != null)
                        font.setFontName(mapFontFamily(fontFamily));
                    if (fontWeight != null)
                        font.setBoldweight(mapFontWeight(fontWeight));
                    if (fontSize != null)
                        font.setFontHeightInPoints(mapFontSize(fontSize));
                    if (fontStyle != null)
                        font.setItalic(isFontItalic(fontStyle));
                    fontCache.put(key, font);
                }
            }
        }
        return font;
    }

    protected String mapFontFamily(String cssFontName) {
        return cssFontName;
    }

    private short getClosestColor(String targetColor) {
        if (colorCache.get(targetColor) != null)
            return colorCache.get(targetColor).shortValue();
        int red = Integer.parseInt(targetColor.substring(1, 3), 16);
        int green = Integer.parseInt(targetColor.substring(3, 5), 16);
        int blue = Integer.parseInt(targetColor.substring(5), 16);
        Map<Integer, HSSFColor> excelColors = HSSFColor.getIndexHash();
        int smallestDelta = 765;
        short closestColor = 64;
        Iterator i = excelColors.keySet().iterator();
        do {
            if (!i.hasNext())
                break;
            Integer excelColorIndex = (Integer) i.next();
            HSSFColor excelColor = excelColors.get(excelColorIndex);
            short triplet[] = excelColor.getTriplet();
            int redDelta = Math.abs(red - triplet[0]);
            int greenDelta = Math.abs(green - triplet[1]);
            int blueDelta = Math.abs(blue - triplet[2]);
            if (redDelta + greenDelta + blueDelta < smallestDelta) {
                smallestDelta = redDelta + greenDelta + blueDelta;
                closestColor = (short) excelColorIndex.intValue();
            }
        } while (true);
        colorCache.put(targetColor, new Short(closestColor));
        return closestColor;
    }

    protected short mapFontWeight(String fontWeight) {
        return (short) (!"bold".equals(fontWeight) && !"bolder".equals(fontWeight)
            && ("500".compareTo(fontWeight) > 0 || "900".compareTo(fontWeight) < 0) ? 400 : 700);
    }

    protected short mapFontSize(String fontSize) {
        return Short.parseShort(fontSize);
    }

    protected boolean isFontItalic(String fontStyle) {
        return "italic".equals(fontStyle) || "oblique".equals(fontStyle);
    }

    public static void main(String args[]) throws Exception {

    }

    @Override
    public void exportResultSet(List<Map<Object, Object>> list, Map<String, String> columnMap, OutputStream out) throws SlxException {
        workbook = new HSSFWorkbook();
        Sheet worksheet = workbook.createSheet();
        Collection<String> headers;
        Collection<String> columns;
        if (this._getOrder() != null) {
            columns = this._getOrder();
            if (columnMap == null) {
                headers = columns;
            } else {
                headers = new ArrayList<String>();
                for (String str : _getOrder()) {
                    String remapped = columnMap.get(str);
                    if (remapped != null)
                        headers.add(remapped);
                    else
                        headers.add(str);
                }
            }// END columnMap == null?
        } else {
            if (columnMap == null)
                columnMap = this.getColumnNames(list);
            columns = columnMap.keySet();
            headers = columnMap.values();
        }
        int rowIndex = 0;
        int cellIndex = 0;
        Row row = worksheet.createRow(rowIndex++);
        for (String header : headers) {
            if (header.indexOf("$style") == -1) {
                Cell cell = row.createCell(cellIndex, 1);
                cell.setCellValue(header);
                cellIndex++;
            }
        }
        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);

        for (Map<Object, Object> record : list) {
            row = worksheet.createRow(rowIndex++);
            cellIndex = 0;
            Iterator<String> c = columns.iterator();
            while (c.hasNext()) {
                Cell cell = null;
                String columnName = c.next();
                if (columnName.indexOf("$style") == -1) {
                    Object cellValue = record.get(columnName);
                    String finalValue = cellValue == null ? null : cellValue.toString();
                    Object styleObj = record.get(columnName + "$style");
                    List<Object> styles = null;
                    if (styleObj != null)
                        if (styleObj instanceof List<?>) {
                            styles = (List<Object>) styleObj;
                        } else {
                            styles = new ArrayList<Object>();
                            styles.add(styleObj);
                        }
                    Map styleElements = null;
                    if (styles != null)
                        styleElements = (Map) styles.get(0);
                    if (styles != null)
                        styleElements = (Map) styles.get(0);
                    if (cellValue != null || styleElements != null) {
                        if (styleElements != null && styleElements.get("rawValue") != null)
                            cellValue = styleElements.get("rawValue");
                        if (cellValue instanceof String)
                            try {
                                cellValue = new Double((String) cellValue);
                            } catch (NumberFormatException ignored) {
                            }
                        if (cellValue instanceof Number) {
                            cell = row.createCell(cellIndex, 0);
                            cell.setCellValue(((Number) cellValue).doubleValue());
                        } else if (cellValue instanceof Date) {
                            cell = row.createCell(cellIndex, 0);
                            Double excelDateValue = Double.valueOf(DateUtil.getExcelDate((Date) cellValue));
                            cell.setCellValue(excelDateValue.doubleValue());
                        } else {
                            cell = row.createCell(cellIndex, 1);
                            cell.setCellValue(cellValue != null ? cellValue.toString() : null);
                        }
                        if (styleElements != null)
                            cell.setCellStyle(getCellStyle((Map) styleElements.get("style"), false));
                        else if (cellValue instanceof Date)
                            cell.setCellStyle(getCellStyle(null, true));
                        else
                            cell.setCellStyle(wrapStyle);
                        if (styles != null && styles.size() > 1)
                            try {
                                cell.setCellValue(buildRichTextString(styles, finalValue));
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                    }
                    cellIndex++;
                }
            }
        }
        for (int i = 0; i < columns.size(); i++)
            try {
                worksheet.autoSizeColumn(i);
            } catch (Exception e) {

            }
        try {
            workbook.write(out);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
