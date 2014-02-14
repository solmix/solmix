/*
 *  Copyright 2012 The Solmix Project
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

package org.solmix.fmk.engine.internal.request;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.solmix.api.request.SlxHttpServletResponse;

/**
 * 
 * @author Administrator
 * @version 110035 2012-4-16
 */

public class SlxHttpServletResponseImpl extends HttpServletResponseWrapper implements SlxHttpServletResponse
{

    private final RequestData requestData;

    /**
     * @param response
     */
    public SlxHttpServletResponseImpl(RequestData requestData, HttpServletResponse response)
    {
        super(response);
        this.requestData = requestData;
    }

    protected final RequestData getRequestData() {
        return requestData;
    }

    // ---------- Adaptable interface

    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        return getRequestData().adaptTo(this, type);
    }

    // ---------- Redirection support through PathResolver --------------------

    // @Override
    // public String encodeURL(String url) {
    // // make the path absolute
    // url = makeAbsolutePath(url);
    //
    // // resolve the url to as if it would be a resource path
    // url = map(url);
    //
    // // have the servlet container to further encodings
    // return super.encodeURL(url);
    // }
    //
    // @Override
    // public String encodeRedirectURL(String url) {
    // // make the path absolute
    // url = makeAbsolutePath(url);
    //
    // // resolve the url to as if it would be a resource path
    // url = map(url);
    //
    // // have the servlet container to further encodings
    // return super.encodeRedirectURL(url);
    // }

    // ---------- Error handling through Sling Error Resolver -----------------

    // @Override
    // public void sendError(int status) throws IOException {
    // sendError(status, null);
    // }
    //
    // @Override
    // public void sendError(int status, String message) throws IOException {
    // checkCommitted();
    //
    // SLXRequestProcessor eh = getRequestData().getSlingRequestProcessor();
    // eh.handleError(status, message, requestData.getSlingRequest(), this);
    // }
    //
    // // ---------- Internal helper ---------------------------------------------
    //
    // private void checkCommitted() {
    // if (isCommitted()) {
    // throw new IllegalStateException("Response has already been committed");
    // }
    // }
    //
    // private String makeAbsolutePath(String path) {
    // if (path.startsWith("/")) {
    // return path;
    // }
    //
    // String base = getRequestData().getContentData().getResource().getPath();
    // int lastSlash = base.lastIndexOf('/');
    // if (lastSlash >= 0) {
    // path = base.substring(0, lastSlash + 1) + path;
    // } else {
    // path = "/" + path;
    // }
    //
    // return path;
    // }

}
