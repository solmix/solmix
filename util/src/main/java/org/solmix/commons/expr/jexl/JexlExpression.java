/*
 * Copyright 2014 The Solmix Project
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

package org.solmix.commons.expr.jexl;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.expr.ExpressionContext;
import org.solmix.commons.util.Assert;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年6月13日
 */

public class JexlExpression implements org.solmix.commons.expr.Expression
{

    private static final Logger log = LoggerFactory.getLogger(JexlExpression.class);

    private Expression expression;

    /**
     * 创建一个Jexl表达式。
     *
     * @param expr jexl表达式对象
     */
    public JexlExpression(Expression expr)
    {
        this.expression = expr;
    }

    /**
     * 取得表达式字符串表示。
     *
     * @return 表达式字符串表示
     */
    public String getExpressionText() {
        return expression.getExpression();
    }

    /**
     * 在指定的上下文中计算表达式。
     *
     * @param context <code>ExpressionContext</code>上下文
     * @return 表达式的计算结果
     */
    public Object evaluate(ExpressionContext context) {
        try {
            JexlContext jexlContext = new JexlContextAdapter(context);

            if (log.isDebugEnabled()) {
                log.debug("Evaluating EL: " + expression.getExpression());
            }

            Object value = expression.evaluate(jexlContext);

            if (log.isDebugEnabled()) {
                log.debug("value of expression: " + value);
            }

            return value;
        } catch (Exception e) {
            log.warn("Caught exception evaluating: " + expression + ". Reason: " + e, e);
            return null;
        }
    }

    /** 将<code>ExpressionContext</code>适配到<code>JexlContext</code>。 */
    private static class JexlContextAdapter implements JexlContext
    {

        private ExpressionContext expressionContext;

        public JexlContextAdapter(ExpressionContext expressionContext)
        {
            this.expressionContext = Assert.assertNotNull(expressionContext, "expressionContext");
        }

        public Object get(String key) {
            return expressionContext.get(key);
        }

        public void set(String key, Object value) {
            expressionContext.put(key, value);
        }

        public boolean has(String key) {
            return expressionContext.get(key) != null;
        }
    }

}
