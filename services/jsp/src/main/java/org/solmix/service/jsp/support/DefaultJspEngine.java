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

package org.solmix.service.jsp.support;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.FileUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.resource.InputStreamResource;
import org.solmix.runtime.resource.ResourceManager;
import org.solmix.service.jsp.JspEngine;
import org.solmix.service.template.TemplateContext;
import org.solmix.service.template.TemplateException;
import org.solmix.service.template.TemplateNotFoundException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年9月13日
 */

public class DefaultJspEngine implements JspEngine
{
    private static final Logger LOG =LoggerFactory.getLogger(DefaultJspEngine.class);
    private final ServletContext servletContext;

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private ResourceManager resourceManager;

    private String contextRoot;

    private String path;

    /**
     * 创建jsp引擎。
     * <p>
     * 需要注意的是，用来创建jsp引擎的参数必须是“全局”作用域的，而不是“request”作用域的。这一点可由 <code>RequestContextChainingService</code>来保证。
     * </p>
     */
    public DefaultJspEngine(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) {
        this.servletContext = Assert.assertNotNull(servletContext, "servletContext");
        this.request = Assert.assertNotNull(request, "request");
        this.response = Assert.assertNotNull(response, "response");
    }

    public void setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public void setPath(String path) {
        this.path = StringUtils.trimToNull(path);
    }

    @PostConstruct
    protected void init() throws Exception {
        Assert.assertNotNull(resourceManager, "resourceManager");

        // 取得搜索路径（相对）。
        if (path == null) {
            path = "/templates";
        }

        // 规格化路径，以"/"结尾。
        path = FileUtils.normalizeAbsolutePath(path + "/");

        // 取得webroot根目录的URL
        URL url = servletContext.getResource("/");

        if (url != null) {
            contextRoot = url.toExternalForm();
        } else {
            // 如果取不到webroot根目录，则试着取得web.xml的URL，以此为基准，计算相对于webroot的URL。
            url = servletContext.getResource("/WEB-INF/web.xml");

            if (url != null) {
                String urlstr = url.toExternalForm();

                if (urlstr.endsWith("/WEB-INF/web.xml")) {
                    contextRoot = urlstr.substring(0, urlstr.length() - "WEB-INF/web.xml".length());
                }
            }
        }

        if (contextRoot == null) {
            throw new IllegalArgumentException("Could not find WEBROOT.  Are you sure you are in webapp?");
        }

        if (!contextRoot.endsWith("/")) {
            contextRoot += "/";
        }

        if (LOG.isDebugEnabled()) {

            LOG.debug("Initialized JSP Template Engine path:{},contextRoot:{}",path,contextRoot);
        }
    }

    /**
     * 取得默认的模板名后缀列表。
     * <p>
     * 当<code>TemplateService</code>没有指定到当前engine的mapping时，将取得本方法所返回的后缀名列表。
     * </p>
     */
    @Override
    public String[] getDefaultExtensions() {
        return new String[] { "jsp", "jspx" };
    }

    /** 判定模板是否存在。 */
    @Override
    public boolean exists(String templateName) {
        return getPathWithinServletContextInternal(templateName) != null;
    }

    /**
     * 渲染模板，并以字符串的形式取得渲染的结果。
     *
     * @param template 模板名
     * @param context template context
     * @return 模板渲然的结果字符串
     * @throws TemplateException 渲染失败
     */
    @Override
    public String evaluate(String template, TemplateContext context) throws TemplateException, IOException {
        // 取得JSP相对于webapp的路径。
        String relativeTemplateName = getPathWithinServletContext(template);

        // 取得JSP的RequestDispatcher。
        RequestDispatcher dispatcher = servletContext.getRequestDispatcher(relativeTemplateName);

        if (dispatcher == null) {
            throw new TemplateNotFoundException("Could not dispatch to JSP template " + template);
        }

        try {
            // 将template context适配到request
            HttpServletRequest requestWrapper = new TemplateContextAdapter(request, context);

            // 避免在jsp中修改content type、locale和charset，这应该在模板外部来控制
            HttpServletResponse responseWrapper = new JspResponse(response);

            dispatcher.include(requestWrapper, responseWrapper);
        } catch (ServletException e) {
            throw new TemplateException(e);
        }

        return "";
    }

    /** 渲染模板，并将渲染的结果送到字节输出流中。 */
    @Override
    public void evaluate(String templateName, TemplateContext context, OutputStream ostream) throws TemplateException, IOException {
        evaluate(templateName, context);
    }

    /** 渲染模板，并将渲染的结果送到字符输出流中。 */
    @Override
    public void evaluate(String templateName, TemplateContext context, Writer writer) throws TemplateException, IOException {
        evaluate(templateName, context);
    }

    /**
     * 取得相对于servletContext的模板路径。这个路径可被 <code>javax.servlet.RequestDispatcher</code> 使用，以便找到jsp的实例。
     */
    @Override
    public String getPathWithinServletContext(String templateName) throws TemplateNotFoundException {
        String path = getPathWithinServletContextInternal(templateName);

        if (path == null) {
            throw new TemplateNotFoundException("Template " + templateName + " not found");
        }

        return path;
    }

    private String getPathWithinServletContextInternal(String templateName) {

        String resourceName = path + (templateName.startsWith("/") ? templateName.substring(1) : templateName);
        InputStreamResource resource = resourceManager.getResourceAsStream(resourceName);
        String path = null;

        if (resource != null && resource.exists()) {
            try {
                String url = resource.getURL().toExternalForm();

                if (url.startsWith(contextRoot)) {
                    path = url.substring(contextRoot.length() - 1); // 保留slash:/
                }
            } catch (IOException e) {
                // ignore
            }
        }

        return path;
    }

    @Override
    public String toString() {
        return "JspEngine[" + path + "]";
    }


   
}
