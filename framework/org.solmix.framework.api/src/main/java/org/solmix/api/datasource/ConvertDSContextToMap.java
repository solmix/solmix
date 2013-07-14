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

package org.solmix.api.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.solmix.api.jaxb.TdataSource;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.Tfields;
import org.solmix.api.jaxb.ToperationBinding;
import org.solmix.api.jaxb.ToperationBindings;
import org.solmix.api.jaxb.Tvalidator;
import org.solmix.api.jaxb.Tvalidators;
import org.solmix.api.jaxb.Tvalue;
import org.solmix.api.jaxb.TvalueMap;

/**
 * 
 * @author ffz
 * @version 2012-3-31
 * @since 0.0.4
 */

public class ConvertDSContextToMap
{

    public static Map<String, ?> toClientValueMap(TdataSource tds) {
        Map<String, Object> tdsm = new HashMap<String, Object>();
        tdsm.put("ID", tds.getID());
        if (tds.getAddGlobalId() != null)
            tdsm.put("addGlobalId", tds.getAddGlobalId());
        if (tds.isAutoCacheAllData() != null)
            tdsm.put("autoCacheAllData", tds.isAutoCacheAllData());
        if (tds.isCacheAllData() != null)
            tdsm.put("cacheAllData", tds.isCacheAllData());
        if (tds.getCacheData() != null)
            tdsm.put("cacheData", tds.getCacheData());
        if (tds.getCacheMaxAge() != null)
            tdsm.put("cacheMaxAge", tds.getCacheMaxAge());
        if (tds.getDataProtocol() != null)
            tdsm.put("dataProtocol", tds.getDataProtocol().value());
        if (tds.getDataURL() != null)
            tdsm.put("dataURL", tds.getDataURL());
        if (tds.getDropUnknownCriteria() != null)
            tdsm.put("dropUnknownCriteria", tds.getDropUnknownCriteria());
        if (tds.getDataTransport() != null)
            tdsm.put("dataTransport", tds.getDataTransport().value());
        if (tds.getRecordXPath() != null)
            tdsm.put("recordXPath", tds.getRecordXPath());
        if (tds.getTagName() != null)
            tdsm.put("tagName", tds.getTagName());
        if (tds.isUseHttpProxy() != null)
            tdsm.put("useHttpProxy", tds.isUseHttpProxy());
        if (tds.getDataFormat() != null)
            tdsm.put("dataFormat", tds.getDataFormat().value());
        if (tds.getTestFileName() != null)
            tdsm.put("testFileName", tds.getTestFileName());
        if (tds.getTitleField() != null)
            tdsm.put("titleField", tds.getTitleField());
        if (tds.getIconField() != null)
            tdsm.put("iconField", tds.getIconField());
        if (tds.getInfoField() != null)
            tdsm.put("infoField", tds.getInfoField());
        if (tds.getDataField() != null)
            tdsm.put("dataField", tds.getDataField());
        if (tds.getDescriptionField() != null)
            tdsm.put("descriptionField", tds.getDescriptionField());
        if (tds.isClientOnly() != null)
            tdsm.put("clientOnly", tds.isClientOnly());
        if (tds.isShowPrompt() != null)
            tdsm.put("showPrompt", tds.isShowPrompt());
        if (tds.getInheritsFrom() != null)
            tdsm.put("inheritsFrom", tds.getInheritsFrom());
        if (tds.isUseFlatFields() != null)
            tdsm.put("useFlatFields", tds.isUseFlatFields());
        if (tds.getCallbackParam() != null)
            tdsm.put("callbackParam", tds.getCallbackParam());
        //
        if (tds.getFields() != null) {
            List<Object> fields = getFiledsMap(tds.getFields());
            tdsm.put("fields", fields);
        }
        if (tds.getOperationBindings() != null) {
            List<Object> ops = getOperationBindingsMap(tds.getOperationBindings());
            tdsm.put("operationBindings", ops);
        }
        return tdsm;

    }

    private static Object getValueMap(TvalueMap vm) {
        List<Tvalue> vs= vm.getValue();
        Map<String,String> m = new HashMap<String,String>();
       for(Tvalue v:vs){
           m.put(v.getId(), v.getName());
       }
         return m;

    }

    private static List<Object> getValidatorsMap(Tvalidators vd) {
        List<Object> _return = new ArrayList<Object>();
        List<Tvalidator> vs = vd.getValidator();
        for (Tvalidator v : vs) {
            if (v.isClientOnly()) {
                Map<String, Object> vm = new HashMap<String, Object>();
                _return.add(vm);
                vm.put("clientOnly", v.isClientOnly());
                if (v.getType() != null)
                    vm.put("type", v.getType());
                if (v.getErrorMessage() != null)
                    vm.put("errorMessage", v.getErrorMessage());
                if (v.getMax() != null)
                    vm.put("max", v.getMax());
                if (v.getMin() != null)
                    vm.put("min", v.getMin());
                if (v.isExclusive())
                    vm.put("exclusive", v.isExclusive());
                if (v.getMask() != null)
                    vm.put("mask", v.getMask());
                if (v.getPrecision() != null)
                    vm.put("precision", v.getPrecision());
                if (v.getExpression() != null)
                    vm.put("expression", v.getExpression());
                if (v.getSubstring() != null)
                    vm.put("substring", v.getSubstring());
                if (v.getOperator() != null)
                    vm.put("operator", v.getOperator());
                if (v.getCount() != null)
                    vm.put("count", v.getCount());
            }
        }
        return _return;

    }

    private static List<Object> getFiledsMap(Tfields fields) {
        List<Object> _return = new ArrayList<Object>();
        List<Tfield> fs = fields.getField();
        for (Tfield f : fs) {
            Map<String, Object> fm = new HashMap<String, Object>();
            _return.add(fm);
            fm.put("name", f.getName());
            if (f.isHidden()) {
                fm.put("hidden", f.isHidden());
            }
            if (f.isIgnore() != null)
                fm.put("ignore", f.isIgnore());
            if (f.getTitle() != null)
                fm.put("title", f.getTitle());
            if (f.getType() != null)
                fm.put("type", f.getType().value());
            if (f.isRequired() != null)
                fm.put("required", f.isRequired());
            if (f.getNativeName() != null)
                fm.put("nativeName", f.getNativeName());
            if (f.isCanEdit() != null)
                fm.put("canEdit", f.isCanEdit());
            if (f.isCanExport() != null)
                fm.put("canExport", f.isCanExport());

            if (f.isCanFilter() != null)
                fm.put("canFilter", f.isCanFilter());
            if (f.isCanSave() != null)
                fm.put("canSave", f.isCanSave());
            if (f.isCanSortClientOnly() != null)
                fm.put("canSortClientOnly", f.isCanSortClientOnly());
            if (f.isCanView() != null)
                fm.put("canView", f.isCanView());
            if (f.isDetail() != null)
                fm.put("detail", f.isDetail());
            if (f.isEscapeHTML() != null)
                fm.put("escapeHTML", f.isEscapeHTML());
            if (f.getExportTitle() != null)
                fm.put("exportTitle", f.getExportTitle());
            if (f.getPluralTitle() != null)
                fm.put("pluralTitle", f.getPluralTitle());
            if (f.getPrompt() != null)
                fm.put("prompt", f.getPrompt());
            if (f.isMultiple() != null)
                fm.put("multiple", f.isMultiple());
            if (f.getDateFormat() != null)
                fm.put("dateFormat", f.getDateFormat());
            if (f.getImageHeight() != null)
                fm.put("imageHeight", f.getImageHeight());
            if (f.getImageSize() != null)
                fm.put("imageSize", f.getImageSize());
            if (f.getImageWidth() != null)
                fm.put("imageWidth", f.getImageWidth());
            if (f.getLength() != null)
                fm.put("length", f.getLength());
            if (f.getMaxFileSize() != null)
                fm.put("maxFileSize", f.getMaxFileSize());
            if (f.isPrimaryKey())
                fm.put("primaryKey", f.isPrimaryKey());
            if (f.getForeignKey() != null)
                fm.put("foreignKey", f.getForeignKey());
            if (f.getRootValue() != null)
                fm.put("rootValue", f.getRootValue());
            if (f.getIncludeFrom() != null)
                fm.put("includeFrom", f.getIncludeFrom());
            // valuemap
            if (f.getValueMap() != null) {
                fm.put("valueMap", getValueMap(f.getValueMap()));
            }
            // validators
            if (f.getValidators() != null) {
                List<Object> vs = getValidatorsMap(f.getValidators());
                if (vs.size() > 0)
                    fm.put("validators", vs);
            }

        }
        return _return;

    }

    private static List<Object> getOperationBindingsMap(ToperationBindings ops) {

        List<Object> _return = new ArrayList<Object>();
        List<ToperationBinding> os = ops.getOperationBinding();
        for (ToperationBinding o : os) {
            Map<String, Object> om = new HashMap<String, Object>();
            _return.add(om);
            if (o.getDataProtocol() != null)
                om.put("dataProtocol", o.getDataProtocol());
            if (o.isAllowMultiUpdate() != null)
                om.put("allowMultiUpdate", o.isAllowMultiUpdate());
            if (o.getOperationType() != null)
                om.put("operationType", o.getOperationType());
            if (o.getOperationId() != null)
                om.put("operationId", o.getOperationId());
            if (o.getCallbackParam() != null)
                om.put("callbackParam", o.getCallbackParam());

        }
        return _return;

    }
}
