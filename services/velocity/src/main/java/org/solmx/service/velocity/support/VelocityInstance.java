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

package org.solmx.service.velocity.support;

import java.io.Reader;

import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年7月28日
 */

public class VelocityInstance extends org.apache.velocity.runtime.RuntimeInstance
{

    private static final String INTERPOLATION_HACK_KEY = "runtime.interpolate.string.literals.hack";

    private static final Boolean INTERPOLATION_HACK_DEFAULT = true;

    private boolean interpolationHack;

    @Override
    public synchronized void init() {
        super.init();
        interpolationHack = getConfiguration().getBoolean(INTERPOLATION_HACK_KEY, INTERPOLATION_HACK_DEFAULT);
    }

    @Override
    public SimpleNode parse(Reader reader, String templateName, boolean dumpNamespace) throws ParseException {
        SimpleNode node = super.parse(reader, templateName, dumpNamespace);

        if (interpolationHack) {
            node = traversNode(node);
        }

        return node;
    }

    private SimpleNode traversNode(SimpleNode node) {
        int length = node.jjtGetNumChildren();

        for (int i = 0; i < length; i++) {
            Node child = node.jjtGetChild(i);

            if (child instanceof ASTStringLiteral) {
                replaceStringLiteral(node, (ASTStringLiteral) child, i);
            }

            if (child instanceof SimpleNode) {
                traversNode((SimpleNode) child);
            }
        }

        return node;
    }

    private void replaceStringLiteral(SimpleNode parent, ASTStringLiteral strLit, int index) {
        if (!(strLit instanceof ASTStringLiteralEnhanced)) {
            SimpleNodeUtil.jjtSetChild(parent, new ASTStringLiteralEnhanced(strLit), index);
        }
    }

}
