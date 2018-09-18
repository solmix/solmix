/*
 * Copyright 2015 The Solmix Project
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

package org.solmix.commons.expr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.solmix.commons.expr.jexl.JexlExpressionFactory;
import org.solmix.commons.util.DataUtils;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年8月8日
 */

public class JexlTest
{

    JexlExpressionFactory jf = new JexlExpressionFactory();

    @Test
    public void test1() {
    	
    }
    public  void testexp() throws ExpressionParseException, InterruptedException {
        MappedContext mc = new MappedContext();
        mc.put("host", "127.0.0.1");
        mc.put("name", "1");
        Expression start = jf.createExpression("name>2? 'a' : 'b'");
        Object s = start.evaluate(mc);
        assertTrue(s.equals("b"));
    }
    public void test() throws ExpressionParseException, InterruptedException {
        MappedContext mc = new MappedContext();
        mc.put("a", 566);
        mc.put("name", "Dx6");
        mc.put("date", "2015-08-11");
        mc.put("start", "2015-08-01");
        mc.put("end", "2015-08-30");
        assertTrue(expre("a>1", mc));

        assertTrue(expre("name.startsWith(\"D\")", mc));
        assertFalse(expre("a<1", mc));
        assertTrue(expre("a%5==1", mc));
        assertTrue(expre("date>='2015-08-11'", mc));
        assertTrue(expre("date>'2015-08-1' && date<'2015-08-30'", mc));
        assertTrue(expre("a==566?name.substring(1)=='x6':name.substring(2)=='6'", mc));
        Thread.sleep(1000);
    }

    private boolean expre(String str, MappedContext mc) throws ExpressionParseException {
        Expression start = jf.createExpression(str);
        Object s = start.evaluate(mc);
        return DataUtils.asBoolean(s);
    }
}
