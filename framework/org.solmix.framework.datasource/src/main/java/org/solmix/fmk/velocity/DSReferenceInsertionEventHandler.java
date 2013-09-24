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

package org.solmix.fmk.velocity;

import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.solmix.api.datasource.DataSource;

/**
 * Reference 'Stream insertion' event handler. Called with object that will be inserted into stream via
 * value.toString(). Please return an Object that will toString() nicely :)
 * 
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110046 
 */
public class DSReferenceInsertionEventHandler implements ReferenceInsertionEventHandler
{

    private static final String ESCAPING_EXCEPTIONS[] = { "$defaultSelectClause", "$defaultTableClause", "$defaultWhereClause",
        "$defaultValuesClause", "$criteria", "$values", "$defaultGroupClause", "$defaultGroupWhereClause", "$defaultOrderClause", "$rawValue",
        "$filter", "$equals", "$substringMatches", "$fields", "$qfields" };

    private static Logger log = LoggerFactory.getLogger(DSReferenceInsertionEventHandler.class.getName());

    private final boolean escapeValues;

    private final DataSource ds;

    private final List<String> externals;

    public Object foundObject;

    public DSReferenceInsertionEventHandler(Context ctx)
    {
        this(ctx, null, false);
    }

    public DSReferenceInsertionEventHandler(Context ctx, DataSource ds, boolean escapeValues)
    {
        externals = new ArrayList<String>();
        this.escapeValues = escapeValues;
        this.ds = ds;
        EventCartridge ec = new EventCartridge();
        ec.addEventHandler(this);
        ec.attachToContext(ctx);
        Object keys[] = ctx.getKeys();
        for (int i = 0; i < keys.length; i++)
            externals.add(keys[i].toString());

    }

    /**
     * A call-back which is executed during Velocity merge before a reference value is inserted into the output stream.
     * All registered ReferenceInsertionEventHandlers are called in sequence. If no ReferenceInsertionEventHandlers are
     * are registered then reference value is inserted into the output stream as is.
     * <p>
     * note: this javadoc is from velocity javadoc for convenience.
     * 
     * @param reference Reference from template about to be inserted.
     * @param data Value about to be inserted (after its toString() method is called).
     * @return Object on which toString() should be called for output
     */
    @Override
    public Object referenceInsert(String reference, Object data) {
        foundObject = data;
        if (escapeValues && ds == null) {
            log.warn("getParameter() called but DataSource has not been set - returning warning");
            return (new StringBuilder()).append("'Unsafe to retrieve ").append(reference).append(" - DataSource has not been set'").toString();
        }
        if (escapeValues) {
            for (int i = 0; i < ESCAPING_EXCEPTIONS.length; i++)
                if (reference.equals(ESCAPING_EXCEPTIONS[i])
                    || reference.startsWith((new StringBuilder()).append(ESCAPING_EXCEPTIONS[i]).append(".").toString())) {
                    return data;
                }
            boolean escape = false;
            for (String str : externals) {
                String external = (new StringBuilder()).append("$").append(str).toString();
                if (reference.equals(external) || reference.startsWith(external + ".")) {
                    escape = true;
                    break;
                }
            }
            if (!escape)
                return ds.escapeValue(data, reference);
            else
                return data;
        } else {
            return data;
        }
    }


}
