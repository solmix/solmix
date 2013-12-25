/*
 * SOLMIX PROJECT
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
package org.solmix.web.interceptor;

import static org.solmix.commons.util.DataUtil.booleanValue;
import static org.solmix.fmk.util.ServletTools.encodeParameter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.call.DSCall;
import org.solmix.api.call.DSCallWebInterceptor;
import org.solmix.api.call.InterceptorOrder;
import org.solmix.api.context.WebContext;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DSResponse;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.export.IExport;
import org.solmix.api.jaxb.EexportAs;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.export.ExportManagerImpl;
import org.solmix.fmk.util.DataTools;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-12-24
 */

public class ExportInterceptor extends DSCallWebInterceptor
{
    private static final Logger LOG = LoggerFactory.getLogger(ExportInterceptor.class);

    @Override
    public ReturnType postInspect(DSCall dsCall,WebContext context) throws SlxException {
        List<DSRequest> requests = dsCall.getRequests();
        if (requests != null && !requests.isEmpty()) {
            if (requests.size() > 1)
                LOG.warn("DownLoad DSRequest must be single .");
            DSRequest req = requests.get(0);
            if (booleanValue(req.getContext().getIsExport())) {
                DSResponse res= dsCall.getResponse(req);
             // put request export relative info to response.
                processExport(req, res);
                // used filter from datasource.if not ,can used res.getContext().getDataList(Map.class);
                List<Map<Object, Object>> data = res.getRecords();
                Map<String, String> fieldMap = new HashMap<String, String>();
                DataSource ds = res.getDataSource() == null ? req.getDataSource() : res.getDataSource();
                List<String> fieldNames = res.getContext().getExportFields();
                List<String> finalFields = new ArrayList<String>();
                // if no defined export fields used datasource's fields.
                if (fieldNames == null || fieldNames.isEmpty())
                    fieldNames = ds.getContext().getFieldNames();
                EexportAs exportAs = EexportAs.fromValue(res.getContext().getExportAs());
                String separatorChar = res.getContext().getExportTitleSeparatorChar();
                List<String> efields = res.getContext().getExportFields();
                // List<String> efieldName = res.getContext().gete
                if (efields == null) {
                    ToperationBinding __op = DataTools.getOperationBindingFromDSByRequest(ds, req);
                    if (__op != null) {
                        String fields = __op.getExport() != null ? __op.getExport().getExportFields() : null;
                        efields = DataUtil.isNotNullAndEmpty(fields) ? DataUtil.simpleSplit(fields, ",") : null;
                    }
                }
                // loop fieldName.
                for (int i = 0; i < fieldNames.size(); i++) {
                    String fieldName = fieldNames.get(i);
                    String fieldTitle = null;
                    Tfield field = ds.getContext().getField(fieldName);
                    if (field != null && !field.isHidden() && (field.isCanExport() == null || field.isCanExport())) {
                        fieldTitle = field.getTitle();
                        if (fieldTitle == null) {
                            fieldTitle = fieldName;
                        }
                        if (exportAs == EexportAs.XML) {
                            if (separatorChar == null)
                                separatorChar = "";
                            fieldTitle = fieldTitle.replaceAll("[$&<>() ]", separatorChar);
                        }
                        fieldMap.put(fieldName, fieldTitle);
                        finalFields.add(fieldName);
                    }
                }
                if (efields == null) {
                    efields = finalFields;
                }
                int lineBreakStyleId = 4;
                if (res.getContext().getLineBreakStyle() != null) {
                    String lineBreakStyle = res.getContext().getLineBreakStyle().toLowerCase();
                    lineBreakStyleId = lineBreakStyle.equals("mac") ? 1 : ((int) (lineBreakStyle.equals("unix") ? 2
                        : ((int) (lineBreakStyle.equals("dos") ? 3 : 4))));
                }

                String delimiter = res.getContext().getExportDelimiter();
                if (delimiter == null || delimiter == "")
                    delimiter = ",";
                // get export provider.
                Map<String, Object> conf = new HashMap<String, Object>();
                conf.put(IExport.LINE_BREAK_STYLE, lineBreakStyleId);
                conf.put(IExport.EXPORT_DELIMITER, delimiter);
                conf.put(IExport.ORDER, efields);
                String exportHeader = res.getContext().getExportHeader();
                if (exportHeader != null) {
                    conf.put(IExport.EXPORT_HEADER_STRING, exportHeader);
                }
                String exportFooter = res.getContext().getExportFooter();
                if (exportFooter != null) {
                    conf.put(IExport.EXPORT_FOOTER_STRING, exportFooter);
                }
                IExport export = ExportManagerImpl.get(exportAs, conf);
                try {
                    ServletOutputStream os = context.getResponse().getOutputStream();
                    BufferedOutputStream bufferedOS = new BufferedOutputStream(os);
                    String fileNameEncoding = encodeParameter("filename", res.getContext().getExportFilename());
                    if (res.getContext().getExportDisplay().equals("download")) {
                        context.getResponse().addHeader("content-disposition", "attachment;" + fileNameEncoding);
                        String contentType = null;
                        switch (exportAs) {
                            case XML: // '\003'
                                contentType = "unknown";
                                break;

                            case JSON: // '\002'
                                contentType = "application/json";
                                break;

                            case XLS: // '\004'
                                contentType = "application/vnd.ms-excel";
                                break;

                            case OOXML: // '\005'
                                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                                break;

                            case CSV: // '\001'
                                contentType = "text/comma-separated-values";
                                break;

                            default:
                                contentType = "text/csv";
                                break;
                        }
                        context.setContentType(contentType);
                    } else {
                        context.getResponse().addHeader("content-disposition",
                            (new StringBuilder()).append("inline; ").append(fileNameEncoding).toString());
                    }
                    export.exportResultSet(data, fieldMap, bufferedOS);
                    // String streamData = writer.toString(); int contentLength = streamData.length();
                    // context.getResponse().setContentLength(contentLength);
                    bufferedOS.flush();
                    os.flush();
                } catch (IOException e) {
                    throw new SlxException(Tmodule.BASIC, Texception.IO_EXCEPTION, e);
                }
                return ReturnType.CANCELLED;
            }
            
        }
        return ReturnType.CONTINUE;
    }
    private void processExport(DSRequest dsreq, DSResponse dsresp) {
        dsresp.getContext().setIsExport(true);
        if (dsresp.getContext().getExportAs() == null || dsresp.getContext().getExportAs().equals(""))
            dsresp.getContext().setExportAs(dsreq.getContext().getExportAs());
        if (dsresp.getContext().getExportDelimiter() == null || dsresp.getContext().getExportDelimiter().equals(""))
            dsresp.getContext().setExportDelimiter(dsreq.getContext().getExportDelimiter());
        if (dsresp.getContext().getExportDisplay() == null || dsresp.getContext().getExportDisplay().equals(""))
            dsresp.getContext().setExportDisplay(dsreq.getContext().getExportDisplay());
        if (dsresp.getContext().getExportFilename() == null || dsresp.getContext().getExportFilename().equals(""))
            dsresp.getContext().setExportFilename(dsreq.getContext().getExportFilename());
        if (dsresp.getContext().getExportFields() == null || ("".equals(dsresp.getContext().getExportFields())))
            dsresp.getContext().setExportFields(dsreq.getContext().getExportFields());
        if (dsresp.getContext().getLineBreakStyle() == null || dsresp.getContext().getLineBreakStyle().equals(""))
            dsresp.getContext().setLineBreakStyle(dsreq.getContext().getLineBreakStyle());
        if (dsresp.getContext().getExportHeader() == null || dsresp.getContext().getExportHeader().equals(""))
            dsresp.getContext().setExportHeader(dsreq.getContext().getExportHeader());
        if (dsresp.getContext().getExportFooter() == null || dsresp.getContext().getExportFooter().equals(""))
            dsresp.getContext().setExportFooter(dsreq.getContext().getExportFooter());
        if (dsresp.getContext().getExportTitleSeparatorChar() == null || dsresp.getContext().getExportTitleSeparatorChar().equals(""))
            dsresp.getContext().setExportTitleSeparatorChar(dsreq.getContext().getExportTitleSeparatorChar());
    }
    @Override
    public PRIORITY priority() {
        return InterceptorOrder.BEFORE_DEFAULT;
    }
}
