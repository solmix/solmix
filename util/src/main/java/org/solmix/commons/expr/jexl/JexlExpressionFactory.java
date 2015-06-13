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

import org.apache.commons.jexl2.JexlEngine;
import org.solmix.commons.expr.Expression;
import org.solmix.commons.expr.ExpressionContext;
import org.solmix.commons.expr.ExpressionFactory;
import org.solmix.commons.expr.ExpressionParseException;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年6月13日
 */

public class JexlExpressionFactory implements ExpressionFactory
{
    private final JexlEngine engine = new JexlEngine();

    /** 是否支持context变量，就是用小数点分隔的变量名。 */
    private boolean supportContextVariables = true;

    /**
     * 是否支持context变量，就是用小数点分隔的变量名。
     *
     * @return 如果支持，则返回<code>true</code>
     */
    public boolean isSupportContextVariables() {
        return supportContextVariables;
    }

    /**
     * 设置支持context变量。
     *
     * @param supportContextVariables 是否支持context变量
     */
    public void setSupportContextVariables(boolean supportContextVariables) {
        this.supportContextVariables = supportContextVariables;
    }

    /**
     * 创建表达式。
     *
     * @param expr 表达式字符串
     * @return 表达式
     */
    public Expression createExpression(final String expr) throws ExpressionParseException {
        final Expression jexlExpression;

        try {
            jexlExpression = new JexlExpression(engine.createExpression(expr));
        } catch (Exception e) {
            throw new ExpressionParseException(e);
        }

        if (isSupportContextVariables() && isValidContextVariableName(expr)) {
            return new Expression() {
                /**
                 * 取得表达式字符串表示。
                 *
                 * @return 表达式字符串表示
                 */
                public String getExpressionText() {
                    return expr;
                }

                /**
                 * 在指定的上下文中计算表达式。
                 *
                 * @param context <code>ExpressionContext</code>上下文
                 * @return 表达式的计算结果
                 */
                public Object evaluate(ExpressionContext context) {
                    // 首先执行jexl表达式
                    Object value = jexlExpression.evaluate(context);

                    // 如果jexl表达式结果为null，则从context中直接取值
                    if (value == null) {
                        value = context.get(expr);
                    }

                    return value;
                }
            };
        }

        return jexlExpression;
    }

    /**
     * 判断是否为context变量。
     *
     * @return 如果是，则返回<code>true</code>
     */
    protected boolean isValidContextVariableName(String varName) {
        for (int i = 0; i < varName.length(); i++) {
            char ch = varName.charAt(i);

            if (Character.isWhitespace(ch) || ch == '[') {
                return false;
            }
        }

        return true;
    }

}
