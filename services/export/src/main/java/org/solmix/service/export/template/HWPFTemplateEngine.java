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

package org.solmix.service.export.template;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.hwpf.HWPFDocument;
import org.solmix.runtime.Extension;
import org.solmix.runtime.resource.InputStreamResource;
import org.solmix.service.template.TemplateContext;
import org.solmix.service.template.TemplateException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年9月9日
 */
@Extension( "world2007")
public class HWPFTemplateEngine extends PoiAbstractTemplateEngine
{
    @Override
    public void evaluate(String templateName, TemplateContext context, OutputStream ostream) throws TemplateException, IOException {
        InputStreamResource stream= getInputStreamResource( templateName);
        HWPFDocument hdt = null;
        try {
        hdt = new HWPFDocument(stream.getInputStream());
        } catch (IOException e1) {
        e1.printStackTrace();
        }
    }

    @Override
    public String[] getDefaultExtensions() {
        return new String[]{"doc"};
    }


}
