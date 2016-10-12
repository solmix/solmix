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
package org.solmix.commons.pager;

import java.util.Arrays;
import java.util.List;

/**
 * A fetcher which uses a static array of strings to page through.
 */

public class StaticPageFetcher<T> extends PageFetcher<T>
{

    private List<T> data;

    public StaticPageFetcher(T[] data)
    {
        this.data = Arrays.asList(data);
    }

    public StaticPageFetcher(List<T> data)
    {
        this.data = data;
    }

    @Override
    public PageList<T> getPage(PageControl control) throws PageFetchException {
        PageList<T> res = new PageList<T>();
        int startIdx, endIdx;

        res.setTotalSize(this.data.size());

        if (control.getPageSize() == PageControl.SIZE_UNLIMITED || control.getPageNum() == -1) {
            res.addAll(this.data);
            return res;
        }

        startIdx = control.getPageFirstIndex();
        endIdx = startIdx + control.getPageSize();

        if (startIdx >= this.data.size())
            return res;

        if (endIdx > this.data.size())
            endIdx = this.data.size();

        res.addAll(this.data.subList(startIdx, endIdx));
        return res;
    }
}
