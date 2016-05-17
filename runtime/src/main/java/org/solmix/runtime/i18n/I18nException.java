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

package org.solmix.runtime.i18n;

import java.io.IOException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年7月19日
 */

public class I18nException extends RuntimeException
{

    private static final long serialVersionUID = 1235405790865720786L;

    public I18nException(String string, Throwable e)
    {
        super(string, e);
    }

    public I18nException(String string)
    {
        super(string);
    }

    public I18nException(IOException e)
    {
        super(e);
    }
}
