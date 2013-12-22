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

package org.solmix.fmk.util;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Perl5Compiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.call.DSCManager;
import org.solmix.api.context.Context;
import org.solmix.api.context.SystemContext;
import org.solmix.api.context.WebContext;
import org.solmix.api.criterion.ErrorMessage;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.i18n.ResourceBundleManager;
import org.solmix.api.jaxb.Tfield;
import org.solmix.api.jaxb.Tservice;
import org.solmix.api.jaxb.Tvalidator;
import org.solmix.api.jaxb.Tvalue;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.util.DataUtil;
import org.solmix.fmk.SlxContext;
import org.solmix.fmk.base.Reflection;
import org.solmix.fmk.base.ReflectionArgument;
import org.solmix.fmk.datasource.DataSourceProvider;
import org.solmix.fmk.datasource.ValidationContext;
import org.solmix.fmk.datasource.Validator;
import org.solmix.fmk.interfaces.ValidatorFunc;
import org.solmix.fmk.rpc.ServiceObject;
import org.solmix.fmk.velocity.Velocity;

/**
 * 
 * @author Administrator
 * @version 110035 2011-3-13
 */
@SuppressWarnings("unchecked")
public class DefaultValidators
{

    static class required implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext validationcontext)
            throws SlxException {
            if (value == null || value.equals(""))
                return new ErrorMessage("%validator_requiredField");
            else
                return null;
        }

        required()
        {
        }
    }

    static class isOneOf implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext validationcontext)
            throws SlxException {
            if (value == null || value.equals(""))
                return null;
            Collection list = validatorParams.getValueMapList();
            if (list == null)
                throw new SlxException(Tmodule.DATASOURCE, Texception.V_CONDITION_DISSATISFY,
                    "isOneOf validator called without valid list for field: " + fieldName);
            boolean _found = false;
            StringBuffer sb =new StringBuffer();
            for (Object o : list) {
                if(o instanceof Tvalue){
                    sb.append(((Tvalue)o).getId()+",");
                    if(((Tvalue)o).getId().equals(value.toString())){
                        _found = true;
                        break;
                    }
                        
                }
            }
            if (!_found)
                return new ErrorMessage(getErrorString(validatorParams, "%validator_notOneOf"), null, sb.toString());
            else
                return null;
        }

    }

    static class isBoolean implements ValidatorFunc
    {

        /**
         * {@inheritDoc}
         * 
         * @see org.solmix.fmk.util.DefaultValidators.ValidatorFunc#validate(java.util.Map, java.lang.Object,
         *      java.lang.String, java.util.Map, org.solmix.fmk.datasource.ValidationContext)
         */
        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals("") || (value instanceof Boolean))
                return null;
            if (value instanceof Number) {
                double number = ((Number) value).doubleValue();
                context.setResultingValue(new Boolean(number != 0.0D));
            } else if (value instanceof String)
                context.setResultingValue(new Boolean((String) value));
            else
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, "%validator_notABoolean"));
            return null;
        }

        isBoolean()
        {
        }

    }

    static class isInteger implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals("") || (value instanceof Integer))
                return null;
            long longValue;
            try {
                longValue = Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, "%validator_notAnInteger"));
            }
            context.setResultingValue(new Long(longValue));
            return null;
        }

        isInteger()
        {
        }
    }

    static class isTime implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || (value instanceof Date))
                return null;
            if (value.equals("")) {
                context.setResultingValue(null);
                return null;
            }
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss Z");
            try {
                Date time = timeFormat.parse((new StringBuilder()).append(value.toString()).append(" -0000").toString());
                context.setResultingValue(time);
                return null;
            } catch (ParseException e) {
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, "%validator_notATime"));
            }
        }

        isTime()
        {
        }
    }

    static class isDate implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || (value instanceof Date))
                return null;
            if (value.equals("")) {
                context.setResultingValue(null);
                return null;
            }
            String dateString = ((String) value).toString();
            SimpleDateFormat dateFormat;
            if (dateString.length() == 19) {
                if (dateString.charAt(10) == 'T')
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
                else
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
                dateString = (new StringBuilder()).append(dateString).append(" -0000").toString();
            } else {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            }
            try {
                Date date = dateFormat.parse(dateString);
                context.setResultingValue(date);
                return null;
            } catch (ParseException e) {
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, "%validator_notADate"));
            }
        }

        isDate()
        {
        }
    }

    static class isFloat implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals(""))
                return null;
            try {
                double num = DataUtil.asDouble(value);
                context.setResultingValue(new Double(num));
            } catch (NumberFormatException e) {
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, "%validator_notADecimal"));
            }
            return null;
        }

        isFloat()
        {
        }
    }

    static class isIdentifier implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals(""))
                return null;
            if (DataUtil.isIdentifier((String) value))
                return null;
            else
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, "%validator_notAnIdentifier"));
        }

        isIdentifier()
        {
        }
    }

    static class isURL implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals(""))
                return null;
            try {
                new URL(value.toString());
            } catch (MalformedURLException e) {
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, e.getMessage()));
            }
            return null;
        }

        isURL()
        {
        }
    }

    static class isString implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals("")) {
                return null;
            } else {
                context.setResultingValue(value.toString());
                return null;
            }
        }

        isString()
        {
        }
    }

    static class isRegexp implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals(""))
                return null;
            try {
                (new Perl5Compiler()).compile(value.toString());
            } catch (MalformedPatternException e) {
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, e.getMessage()));
            }
            return null;
        }

        isRegexp()
        {
        }
    }

    static class isUnique implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            Map params = context.getTemplateContext();
            DataSource ds = (DataSource) params.get("dataSource");
            Tfield field = (Tfield) params.get("field");
            if (field == null) {
                DefaultValidators.log.warn((new StringBuilder()).append("Field ").append(fieldName).append(" - 'isUnique' validation encountered a ").append(
                    "template context where the field was not set.  Unable to  ").append("proceed, assuming false").toString());
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, "Value must be unique"));
            }
            String realFieldName = field.getName();
            if (ds == null) {
                DefaultValidators.log.warn((new StringBuilder()).append("Field ").append(fieldName).append(" - 'isUnique' validation encountered a ").append(
                    "template context where the dataSource was not set.  Unable to  ").append("proceed, assuming false").toString());
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, "%validator_mustBeUnique"));
            }
            try {
                if (ds.hasRecord(realFieldName, value))
                    return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, "%validator_mustBeUnique"));
            } catch (Exception e) {
                return new ErrorMessage(e.getMessage());
            }
            return null;
        }

        isUnique()
        {
        }
    }

    static class integerRange implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals(""))
                return null;
            long num;
            try {
                num = Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, "%validator_notAnInteger"));
            }
            Long min = validatorParams.getMinAsLong();
            Long max = validatorParams.getMaxAsLong();
            if (min == null && max == null)
                throw new SlxException(Tmodule.DATASOURCE, Texception.V_VALIDATION_FAILED, "integerRange validator called without valid min or max");
            if (max != null && num > max.longValue())
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, "%validator_mustBeShorterThan"), max, max);
            if (min != null && num < min.longValue())
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, "%validator_mustBeLongerThan"), min, min);
            else
                return null;
        }

        integerRange()
        {
        }
    }

    private static Long getParamAsLong(Map params, Object key) {
        Long param = null;
        if (!params.containsKey(key))
            return null;
        try {
            param = new Long(params.get(key).toString());
        } catch (NumberFormatException e) {
            param = null;
        }
        return param;
    }

    static class regexp implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals(""))
                return null;
            String expression = validatorParams.getExpression();
            if (expression == null)
                throw new SlxException(Tmodule.DATASOURCE, Texception.V_VALIDATION_FAILED, "regexp validator called without expression");
            Perl5Util util = new Perl5Util();
            expression = (new StringBuilder()).append("/").append(expression).append("/").toString();
            if (!util.match(expression, value.toString()))
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams));
            else
                return null;
        }

        regexp()
        {
        }
    }

    static class lengthRange implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals(""))
                return null;
            Long min = validatorParams.getMinAsLong();
            Long max = validatorParams.getMaxAsLong();
            if (min == null && max == null)
                throw new SlxException(Tmodule.DATASOURCE, Texception.V_VALIDATION_FAILED, "lengthRange validator called without valid min or max");
            String s = value.toString();
            if (max != null && s.length() > max.longValue() || min != null && s.length() < min.longValue())
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams,
                    (new StringBuilder()).append("Must be between ").append(min).append("-").append(max).append(" characters long").toString()));
            else
                return null;
        }

        lengthRange()
        {
        }
    }

    static class matchesField implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            Object otherField = validatorParams.getOtherField();
            if (otherField == null)
                throw new SlxException(Tmodule.DATASOURCE, Texception.V_VALIDATION_FAILED, "matchesField validator called without valid otherField");
            Object otherFieldValue = record.get(otherField);
            if (value == null && otherFieldValue == null || value.equals(otherFieldValue))
                return null;
            else
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams,
                    (new StringBuilder()).append("Does not match value in field '").append(otherField).append("'").toString()));
        }

        matchesField()
        {
        }
    }

    static class contains implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals(""))
                return null;
            String substring = validatorParams.getSubstring();
            if (substring == null)
                throw new SlxException(Tmodule.DATASOURCE, Texception.V_VALIDATION_FAILED, "contains validator called without valid substring");
            if (value.toString().indexOf(substring) == -1)
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams,
                    (new StringBuilder()).append("Must contain '").append(substring).append("'").toString()));
            else
                return null;
        }

        contains()
        {
        }
    }

    static class doesntContain implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals(""))
                return null;
            String substring = validatorParams.getSubstring();
            if (substring == null)
                throw new SlxException(Tmodule.DATASOURCE, Texception.V_VALIDATION_FAILED, "doesntContain validator called without valid substring");
            if (value.toString().indexOf(substring) > -1)
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams,
                    (new StringBuilder()).append("Must not contain '").append(substring).append("'").toString()));
            else
                return null;
        }

        doesntContain()
        {
        }
    }

    static class substringCount implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals(""))
                return null;
            String substring = validatorParams.getSubstring();
            String val = value.toString();
            if (substring == null)
                throw new SlxException(Tmodule.DATASOURCE, Texception.V_VALIDATION_FAILED, "substringCount validator called without valid substring");
            long matchCount = 0L;
            int i = 0;
            do {
                if (i >= val.length())
                    break;
                i = val.indexOf(substring, i);
                if (i <= -1)
                    break;
                matchCount++;
                i++;
            } while (true);
            String operator = validatorParams.getOperator();
            Long countObj = validatorParams.getCount();
            if (operator == null)
                operator = "==";
            long count;
            if (countObj == null)
                count = 0L;
            else
                count = countObj.longValue();
            if (operator.equals("==")) {
                if (matchCount == count)
                    return null;
            } else if (operator.equals("!=")) {
                if (matchCount != count)
                    return null;
            } else if (operator.equals(">")) {
                if (matchCount > count)
                    return null;
            } else if (operator.equals("<")) {
                if (matchCount < count)
                    return null;
            } else if (operator.equals(">=")) {
                if (matchCount >= count)
                    return null;
            } else if (operator.equals("<=")) {
                if (matchCount <= count)
                    return null;
            } else {
                throw new SlxException(Tmodule.DATASOURCE, Texception.V_VALIDATION_FAILED, (new StringBuilder()).append(
                    "in substringCount validator, operator was ").append(operator).append(", must be one of ==, !=, >, <, >= or <=").toString());
            }
            return new ErrorMessage(
                DefaultValidators.getErrorString(
                    validatorParams,
                    (new StringBuilder()).append("Must contain ").append(operator).append(" ").append(count).append(" instances of '").append(
                        substring).append("'").toString()));
        }

        substringCount()
        {
        }
    }

    static class mask implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals(""))
                return null;
            String mask = validatorParams.getMask();
            if (mask == null)
                throw new SlxException(Tmodule.DATASOURCE, Texception.V_VALIDATION_FAILED, "mask validator called without valid mask");
            Perl5Util util = new Perl5Util();
            mask = (new StringBuilder()).append("/").append(mask).append("/").toString();
            if (!util.match(mask, value.toString()))
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams));
            else
                return null;
        }

        mask()
        {
        }
    }

    static class floatLimit implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals(""))
                return null;
            double num;
            try {
                num = Double.valueOf(value.toString()).doubleValue();
            } catch (NumberFormatException e) {
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, "Must be a valid decimal."));
            }
            Double min = validatorParams.getMinAsDouble();
            Double max = validatorParams.getMaxAsDouble();
            if (max != null && num > max.doubleValue())
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams), max);
            if (min != null && num < min.doubleValue())
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams), min);
            Double precision = validatorParams.getPrecision();
            if (precision != null) {
                double multiplier = Math.pow(10D, precision.doubleValue());
                double suggestedValue = (new Long(Math.round(num * multiplier))).doubleValue() / multiplier;
                if (suggestedValue == num) {
                    return null;
                } else {
                    String message = (new StringBuilder()).append("No more than ").append(precision.intValue()).append(
                        " digits after the decimal point.").toString();
                    return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, message), new Double(suggestedValue));
                }
            } else {
                return null;
            }
        }

        floatLimit()
        {
        }
    }

    static class floatPrecision implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals(""))
                return null;
            double num;
            try {
                num = Double.valueOf(value.toString()).doubleValue();
            } catch (NumberFormatException e) {
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, "Must be a valid decimal."));
            }
            Double precision = validatorParams.getPrecision();
            if (precision != null) {
                double multiplier = Math.pow(10D, precision.doubleValue());
                double suggestedValue = (new Long(Math.round(num * multiplier))).doubleValue() / multiplier;
                if (suggestedValue == num) {
                    return null;
                } else {
                    String message = (new StringBuilder()).append("No more than ").append(precision.intValue()).append(
                        " digits after the decimal point.").toString();
                    return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, message), new Double(suggestedValue));
                }
            } else {
                return null;
            }
        }

        floatPrecision()
        {
        }
    }

    static class floatRange implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals(""))
                return null;
            double num;
            try {
                num = Double.valueOf(value.toString()).doubleValue();
            } catch (NumberFormatException e) {
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, "Must be a valid decimal."));
            }
            Double min = validatorParams.getMinAsDouble();
            Double max = validatorParams.getMaxAsDouble();
            if (max != null && num > max.doubleValue())
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams), max);
            if (min != null && num < min.doubleValue())
                return new ErrorMessage(DefaultValidators.getErrorString(validatorParams), min);
            else
                return null;
        }

        floatRange()
        {
        }
    }

    static class integerOrAuto implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            if (value == null || value.equals(""))
                return null;
            if ("auto".equalsIgnoreCase(value.toString()))
                return null;
            if (validatorParams.getErrorMessage() == null)
                validatorParams.put("errorMessage", "Must be a whole number or \"auto\"");
            return DefaultValidators.getBuiltinValidator("isInteger").validate(validatorParams, value, fieldName, record, context);
        }

        integerOrAuto()
        {
        }
    }

    static class hasRelatedRecord implements ValidatorFunc
    {

        @Override
        public ErrorMessage validate(Validator validatorParams, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            Map params = context.getTemplateContext();
            String relatedDS = validatorParams.getRelatedDataSource();
            String relatedField = validatorParams.getRelatedField();
            if (relatedDS == null || relatedField == null) {
                Tfield thisField = (Tfield) params.get("field");
                String fk = thisField.getForeignKey();
                if (fk != null) {
                    String tokens[] = fk.split("[.]");
                    if (tokens.length == 1) {
                        if (relatedDS == null)
                            relatedDS = ((DataSource) params.get("dataSource")).getName();
                        if (relatedField == null)
                            relatedField = tokens[0];
                    } else {
                        if (relatedDS == null)
                            relatedDS = tokens[0];
                        if (relatedField == null)
                            relatedField = tokens[1];
                    }
                }
            }
            if (relatedDS == null || relatedField == null) {
                String error = (new StringBuilder()).append("Field ").append(fieldName).append(" - 'hasRelatedRecord' validation could not derive ").append(
                    "a relation to test - specify 'relatedDataSource' and 'relatedField' on ").append(
                    "the validator, or a foreignKey property in this field's DataSource ").append("definition.  Cannot proceed, assuming false.").toString();
                DefaultValidators.log.warn(error);
                return new ErrorMessage(error);
            }
            DataSource ds;
            try {
                ds = DataSourceProvider.forName(relatedDS);
                if (ds == null) {
                    String error = (new StringBuilder()).append("Field ").append(fieldName).append(" - 'hasRelatedRecord' validation encountered a ").append(
                        "'relatedDataSource' that was not a real DataSource.  Please check ").append(
                        "your validator code.  Unable to proceed, assuming false.").toString();
                    DefaultValidators.log.warn(error);
                    return new ErrorMessage(error);
                }
            } catch (Exception e) {
                return new ErrorMessage(e.getMessage());
            }
            try {
                if (!ds.hasRecord(relatedField, value))
                    return new ErrorMessage(DefaultValidators.getErrorString(validatorParams, "Related record does not exist"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        hasRelatedRecord()
        {
        }
    }

    static class serverCustom implements ValidatorFunc
    {

        /**
         * {@inheritDoc}
         * 
         * @see org.solmix.fmk.util.DefaultValidators.ValidatorFunc#validate(org.solmix.fmk.datasource.Validator,
         *      java.lang.Object, java.lang.String, java.util.Map, org.solmix.fmk.datasource.ValidationContext)
         */
        @Override
        public ErrorMessage validate(Validator validator, Object value, String fieldName, Map record, ValidationContext context) throws SlxException {
            String velocityExpressiion = validator.getServerCondition();
            Object rawResult = null;
            if (velocityExpressiion != null) {
                try {
                    rawResult = evaluateVelocityExpression(validator, value, fieldName, record, context);
                } catch (Exception e) {
                    throw new SlxException(Tmodule.DATASOURCE, Texception.V_VALIDATION_FAILED, e.getMessage());
                }
            } else {
                Tservice srvConfig = validator.getService();
                if (srvConfig == null)
                    throw new SlxException(Tmodule.DATASOURCE, Texception.V_VALIDATION_FAILED,
                        "'serverCustom' validator has neither serverCondition nor serverObject declaration");
                rawResult = callServerObject(validator, value, fieldName, record, context);
            }
            Boolean rtnValue = null;
            if (rawResult instanceof Boolean) {
                rtnValue = (Boolean) rawResult;
            } else {
                if (rawResult.toString().toLowerCase().trim().equals("true"))
                    rtnValue = new Boolean(true);
                if (rawResult.toString().toLowerCase().trim().equals("false"))
                    rtnValue = new Boolean(false);
            }
            if (rtnValue == null)
                DefaultValidators.log.warn((new StringBuilder()).append("Field ").append(fieldName).append(
                    " - 'serverCustom' validation returned null ").append("instead of boolean value.  Assuming false (validation not passed)").toString());
            if (Boolean.TRUE.equals(rtnValue))
                return null;
            else
                return new ErrorMessage(DefaultValidators.getErrorString(validator, "Failed custom validation"));
        }

        public Object callServerObject(Validator validator, Object value, String fieldName, Map record, ValidationContext context)
            throws SlxException {
            Tservice srvConfig = validator.getService();
            // Map srvMapConf = DataUtil.getMapFromBean( srvConfig );
            Context requestContext = context.getRequestContext();
            DataSource ds = context.getCurrentDataSource();
            String contextString = (new StringBuilder()).append("'serverCustom' validator for field ").append(fieldName).append(" on DataSource ").append(
                ds.getName()).toString();
            ServiceObject serverObject;
            try {
                serverObject = new ServiceObject(srvConfig, requestContext, contextString);
            } catch (Exception e) {
                throw new SlxException(Tmodule.DATASOURCE, Texception.V_VALIDATION_FAILED, (new StringBuilder()).append(
                    "Bad serverObject declaration:\n").append(DataTools.prettyPrint(srvConfig)).append("\n..led to exception:\n").append(
                    DataTools.getStackTrace(e)).toString());
            }
            ReflectionArgument requiredArgs[] = { new ReflectionArgument(Object.class, value, false, false),
                new ReflectionArgument(Validator.class, validator, false, false), new ReflectionArgument(String.class, fieldName, false, false),
                new ReflectionArgument(Map.class, record, false, false) };

            Object returnValue = null;
            try {
                String methodName = srvConfig.getMethod();
                if (methodName == null)
                    methodName = "condition";
                Method method = serverObject.getServiceMethod(methodName);
                Object serverObjectInstance = serverObject.getServiceInstance(method);
                if (context instanceof WebContext) {
                    WebContext web = (WebContext) requestContext;
                    ReflectionArgument optionalArgs[] = { new ReflectionArgument(DataSource.class, ds, false, false),
                        new ReflectionArgument(Context.class, requestContext, false, false),
                        new ReflectionArgument(HttpServletRequest.class, web.getRequest(), false, false),
                        new ReflectionArgument(HttpServletResponse.class, web.getResponse(), false, false),
                        new ReflectionArgument(ServletContext.class, web.getServletContext(), false, false),
                        new ReflectionArgument(HttpSession.class, web.getRequest().getSession(true), false, false),
                        new ReflectionArgument(DSCManager.class, context.getRpcManager(), false, false) };
                    returnValue = Reflection.adaptArgsAndInvoke(serverObjectInstance, method, requiredArgs, optionalArgs);
                } else {
                    ReflectionArgument optionalArgs[] = { new ReflectionArgument(DataSource.class, ds, false, false),
                        new ReflectionArgument(Context.class, requestContext, false, false) };
                    returnValue = Reflection.adaptArgsAndInvoke(serverObjectInstance, method, requiredArgs, optionalArgs);
                }
            } catch (Exception e) {
                Throwable t = Reflection.getRealTargetException(e);
                String message = "Validator DMI invocation threw exception: ";
                DefaultValidators.log.warn((new StringBuilder()).append(message).append(DataTools.getStackTrace(t)).toString());
                throw new SlxException(Tmodule.DATASOURCE, Texception.V_VALIDATION_FAILED, new StringBuilder().append(message).append(
                    t.getClass().getName()).append(" with error: ").append(t.getMessage()).toString());
            }
            return returnValue;
        }

        public Object evaluateVelocityExpression(Validator validator, Object value, String fieldName, Map record, ValidationContext context)
            throws Exception {
            String expression = validator.getServerCondition();
            Map params = context.getTemplateContext();
            return Velocity.evaluateBooleanExpression(expression, params, "CustomValidator", context.getCurrentDataSource());
        }

        serverCustom()
        {
        }
    }

    private static Logger log = LoggerFactory.getLogger(DefaultValidators.class.getName());

    private static final Map<String, ValidatorFunc> validatorFunctions = Collections.synchronizedMap(new HashMap<String, ValidatorFunc>());

    private static final List clientOnlyValidators = DataUtil.makeList("requiredIf");

    static final Map<String, ValidatorFunc> defaultValidators;
    static {
        defaultValidators = new HashMap<String, ValidatorFunc>();
        defaultValidators.put("required", new required());
        defaultValidators.put("isOneOf", new isOneOf());
        defaultValidators.put("isBoolean", new isBoolean());
        defaultValidators.put("isInteger", new isInteger());
        defaultValidators.put("isDate", new isDate());
        defaultValidators.put("isTime", new isTime());
        defaultValidators.put("isFloat", new isFloat());
        defaultValidators.put("isIdentifier", new isIdentifier());
        defaultValidators.put("isURL", new isURL());
        defaultValidators.put("isString", new isString());
        defaultValidators.put("isRegexp", new isRegexp());
        defaultValidators.put("isUnique", new isUnique());
        defaultValidators.put("integerRange", new integerRange());
        defaultValidators.put("regexp", new regexp());
        defaultValidators.put("regex", new regexp());
        defaultValidators.put("lengthRange", new lengthRange());
        defaultValidators.put("matchesField", new matchesField());
        defaultValidators.put("contains", new contains());
        defaultValidators.put("doesntContain", new doesntContain());
        defaultValidators.put("substringCount", new substringCount());
        defaultValidators.put("mask", new mask());
        defaultValidators.put("floatLimit", new floatLimit());
        defaultValidators.put("floatPrecision", new floatPrecision());
        defaultValidators.put("floatRange", new floatRange());
        defaultValidators.put("integerOrAuto", new integerOrAuto());
        defaultValidators.put("serverCustom", new serverCustom());
        defaultValidators.put("hasRelatedRecord", new hasRelatedRecord());

    }

    /**
     * @param currentRecord
     * @param fieldName
     * @param validators
     * @param context
     * @param object
     * @param value
     * @return
     * @throws SlxException
     */
    @SuppressWarnings("unchecked")
    public static ErrorReport validateField(Map<Object, Object> currentRecord, String fieldName, List<Object> validators, ValidationContext context,
        ErrorReport errorReport, Object value) throws SlxException {
        if (validators == null || validators.isEmpty())
            return null;
        if (context == null)
            context = ValidationContext.instance();
        context.clearResultingValue();
        boolean _stopIfFalse = false;
        Validator _valParams;
        String _type = "";
        ErrorMessage _error = null;
        boolean _result = false;
        Iterator<Object> i = validators.iterator();
        do {
            if (!i.hasNext())
                break;
            _stopIfFalse = false;
            _valParams = null;
            _type = "";
            _error = null;
            _result = false;
            Object validator = i.next();
            if (validator instanceof String) {
                _type = (String) validator;
                _valParams = new Validator();
            } else if (validator instanceof Map) {
                Map valParamsMap = (Map) validator;
                _valParams = new Validator(valParamsMap);
                _type = _valParams.getType();
                _stopIfFalse = _valParams.isStopIfFalse();
            } else if (validator instanceof Tvalidator) {
                Tvalidator tvalidator = (Tvalidator) validator;
                _valParams = new Validator((Tvalidator) validator);
                _type = tvalidator.getType().value();
                _stopIfFalse = tvalidator.isStopIfFalse();
            }
            Object rawCondition = _valParams.get("applyWhen");
            if (rawCondition != null) {
                if (!(rawCondition instanceof Map)) {
                    log.warn((new StringBuilder()).append("on field: '").append(fieldName).append("' for validator type '").append(_type).append(
                        "', bad 'applyWhen' ignored: ").append(DataTools.prettyPrint(rawCondition)).toString());
                }
                Map condition = (Map) rawCondition;
                if (log.isDebugEnabled())
                    log.debug((new StringBuilder()).append("on field: '").append(fieldName).append("' for validator type '").append(_type).append(
                        "', 'applyWhen' is:\n").append(DataTools.prettyPrint(condition)).append("\nrecord is:\n").append(
                        DataTools.prettyPrint(currentRecord)).toString());

                try {
                    // result = context.getCurrentDataSource().matchesCriteria(record, condition);
                } catch (Exception e) {
                    log.warn((new StringBuilder()).append("on field: '").append(fieldName).append("' for validate type '").append(_type).append(
                        "' evaluation of 'applyWhen' ").append("threw exception, ignoring validator.\n").append(DataTools.getStackTrace(e)).toString());
                }
                if (_result) {
                    if (log.isInfoEnabled())
                        log.info((new StringBuilder()).append("on field: '").append(fieldName).append("' conditional validator of type '").append(
                            _type).append("' is: ").append(_result ? "active" : "inactive").toString());
                }
            }// END (rawCondition!=null).
            if (context != null) {
                context.addToTemplateContext("validator", _valParams);
                context.addToTemplateContext("record", currentRecord);
                context.addToTemplateContext("value", value);
                context.addToTemplateContext("dataSource", context.getCurrentDataSource());
                context.addToTemplateContext(Velocity.getServletContextMap(context.getRpcManager()));
            }
            _error = processValidator(_type, value, fieldName, currentRecord, _valParams, context);

            if (errorReport == null)
                errorReport = new ErrorReport();
            if (_error != null)
                errorReport.addError(fieldName, _error);
        } while (!_stopIfFalse);

        return errorReport;
    }

    private static ErrorMessage processValidator(String validatorName, Object value, String fieldName, Map record, Validator validator,
        ValidationContext context) throws SlxException {
        if (validator != null && validator.isClientOnly())
            return null;
        if (validatorName == null)
            return new ErrorMessage((new StringBuilder()).append("Validator missing type property: ").append(validator).append(
                "\nIf this is a custom validator, set the clientOnly property to true.").toString());
        if (clientOnlyValidators.contains(validatorName))
            return null;
        ValidatorFunc vfunc = getBuiltinValidator(validatorName);
        if (vfunc != null) {
            ErrorMessage error = vfunc.validate(validator, value, fieldName, record, context);

            if (error != null) {
                try {
                    validator.evaluateErrorMessage(error);
                } catch (Exception e) {
                    // throw new ValidatorException(e.getMessage());
                }

                localizedErrorMessage(error, context);
            }
            return error;
        }
        return null;

    }

    private static String getErrorString(Validator params, String defaultMessage) {
        String errorMessage = params.getErrorMessage();
        if (errorMessage == null)
            errorMessage = defaultMessage;
        return errorMessage;
    }

    private static String getErrorString(Validator params) {
        return getErrorString(params, "%validator_failed");
    }

    /**
     * process the localization of error message.
     * 
     * @param error
     * @param msgTool
     * @return
     * @throws SlxException 
     */
    public static ErrorMessage localizedErrorMessage(ErrorMessage error, ValidationContext context) throws SlxException {
        if (error == null)
            return null;
        MessageTools _msgTool = null;
        Object msg = context.get(Constants.MESSAGE_TOOL_IN_CONTEXT);
        if (msg == null) {
            SystemContext sc = SlxContext.getThreadSystemContext();
            ResourceBundleManager rbm= sc.getBean(ResourceBundleManager.class);
            // VirtualManager.getVirtual(context.getRequestContext());
            _msgTool = new MessageTools(rbm.getResourceBundle(SlxContext.getLocale()));
            context.put(Constants.MESSAGE_TOOL_IN_CONTEXT, _msgTool);
        }
        
        String _error_msg = error.getErrorString();
        if (error.getErrorString().startsWith("%")) {
            _error_msg = _error_msg.substring(1);
            if (error.getArgments() == null)
                _error_msg = _msgTool.get(_error_msg);
            else
                _error_msg = _msgTool.get(_error_msg, error.getArgments());
        }
        error.setErrorString(_error_msg);
        return error;

    }

    /**
     * @param validatorName
     * @return
     */
    protected static ValidatorFunc getBuiltinValidator(String validatorName) {
        return defaultValidators.get(validatorName);
    }
}
