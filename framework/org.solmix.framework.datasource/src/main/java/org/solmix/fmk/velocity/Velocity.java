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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.context.Context;
import org.solmix.api.context.WebContext;
import org.solmix.api.data.DSRequestData;
import org.solmix.api.datasource.DSRequest;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.rpc.RPCManager;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.io.SlxFile;
import org.solmix.fmk.internal.DatasourceCM;
import org.solmix.fmk.util.DataTools;

/**
 * Used Apache Velocity for template engine.dynamically constructed template.
 * <p>
 * <b>Use Velocity fundamental pattern.</b> <br>
 * 1.Initialize Velocity. <br>
 * 2.Create a context Object. <br>
 * 3.Add your data object to the context.<br>
 * 4.Choose a template.<br>
 * 5.Merge the template and your data to product output.
 * <p>
 * Velocity can used Singleton Module or Separate Instance.{@link org.apache.velocity.app.Velocity} for singleton and
 * {@link org.apache.velocity.app.VelocityEngine} for separate module
 * <table border=1 >
 * <tr width="100%" align="center" style="background-color : #BBFFFF">
 * <td>Render template into output stream</td>
 * </tr>
 * <tr>
 * <td>Once the runtime is initialized,you can do it with what you wish.Here is the method and description of what they
 * do:<br>
 * <li>{@link org.apache.velocity.app.VelocityEngine#evaluate(Context, Writer, String, String)},
 * {@link org.apache.velocity.app.VelocityEngine#evaluate(org.apache.velocity.context.Context, java.io.Writer, String, java.io.InputStream)}
 * These methods will render the input, in either the form of String or InputStream to an output Writer, using a Context
 * that you provide. This is a very convenienient method to use for token replacement of strings, or if you keep
 * 'templates' of VTL-containing content in a place like a database or other non-file storage, or simply generate such
 * dynamically.<br>
 * <li></td>
 * </tr>
 * <tr width="100%" align="center" style="background-color : #BBFFFF">
 * <td>Velocity context</td>
 * </tr>
 * <tr>
 * <td>the concept of the "Context" is central to Velocity, and is a common technique for moving a container of data
 * around between parts of a system. The idea is that the context is a 'carrier' of data between the Java layer (or you
 * the programmer) and the template layer ( or the designer ). You as the programmer will gather objects of various
 * types, whatever your application calls for, and place them in the context. To the designer, these objects, and their
 * methods and properties, will become accessable via template elements called references. Generally, you will work with
 * the designer to determine the data needs for the application. In a sense, this will become an 'API' as you produce a
 * data set for the designer to access in the template. Therefore, in this phase of the development process it is worth
 * devoting some time and careful analysis.</td>
 * </tr>
 * </table>
 * 
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 110035 2010-12-20 solmix-ds
 */
public class Velocity
{

    public static final String RESPONSE_DATA = "responseData";

    public static final String CRITERIA = "criteria";

    public static final String VALUES = "values";

    public static final String DSREQUEST = "dsRequest";

    public static final String DATASOURCES = "dataSources";

    public static final String UTIL = "util";

    public static final String USER_ID = "userId";

    public static final String HTTP_PARAMETERS = "httpParameters";

    public static final String HTTP_ATTRIBUTES = "httpAttributes";

    public static final String SESSION_ATTRIBUTES = "sessionAttributes";

    public static final String SERVLET_REQUEST = "servletRequest";

    public static final String TOOLS = "tools";

    public static final String SESSION = "session";

    private String tmplateDir;

    public Velocity()
    {
    }

    /**
     * Get Velocity Engine.
     * <p>
     * <b>**Note**</b><br>
     * Velocity Logger Configuration:<br>
     * Velocity will automatically use either the Jakarta Avalon Logkit logger, or the Jakarta Log4j logger. It will do
     * so by using whatever it finds in the current classpath, starting first with Logkit. If Logkit isn't found, it
     * tries Log4j.
     * 
     * @return
     * @throws Exception
     */
    public static VelocityEngine getEngine()  {
        if (vEngine != null) {
            return vEngine;
        } else {
            vEngine = new VelocityEngine();
            Properties properties = new Properties();
            // For velocity resource loader.
            properties.put("file.resource.loader.path", DatasourceCM.getProperties().get(DatasourceCM.P_VELOCITY_TEMPLATE_DIR));
            properties.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
            properties.put("runtime.log.logsystem.log4j.category", "org.apache.Velocity");
            vEngine.init(properties);
            return vEngine;
        }
    }

    public static synchronized Object evaluateTemplateFile(String fileName, Map parameters) throws SlxException {
        String path = DatasourceCM.getProperties().getString(DatasourceCM.P_VELOCITY_TEMPLATE_DIR);
        
        try {
            SlxFile file = new SlxFile((new StringBuilder()).append(path).append("/").append(fileName).toString());
            return evaluate(file.getAsString(), parameters);
        } catch (IOException e) {
            throw new SlxException(Tmodule.VM,Texception.NO_FOUND,"Not found vm template file.",e);
        }
        
    }

    public static synchronized String evaluateTemplateFileAsString(String fileName, Map parameters) throws SlxException {
        Object obj = evaluateTemplateFile(fileName, parameters);
        return obj != null ? obj.toString() : null;
    }

    public static synchronized String evaluateAsString(String template, Map parameters) throws SlxException {
        Object obj = evaluate(template, parameters);
        return obj != null ? obj.toString() : null;
    }

    public static synchronized Object evaluate(String template, Map parameters) throws SlxException {
        return evaluate(template, parameters, "notProvided", null, false);
    }

    public static synchronized String evaluateAsString(String template, Map parameters, String operationName, DataSource ds, boolean quoteValues)
        throws SlxException {
        Object obj = evaluate(template, parameters, operationName, ds, quoteValues);
        return obj != null ? obj.toString() : null;
    }

    public static synchronized Object evaluate(String template, Map parameters, String operationName, DataSource ds, boolean quoteValues)
        throws SlxException {
        StringWriter out = new StringWriter();
        VelocityContext context = new VelocityContext(parameters);
        DSReferenceInsertionEventHandler handler = new DSReferenceInsertionEventHandler(context, ds, quoteValues);
        try {
            if (!getEngine().evaluate(context, out, operationName, template))
                return null;
        } catch (Exception e) {
            throw new SlxException(Tmodule.DATASOURCE, Texception.VELOCITY_EVALUATE_EXCEPTION, "Velocity evalute exception:\n", e);
        }
        if (handler.foundObject != null && handler.foundObject.toString().equals(out.toString()))
            return handler.foundObject;
        else
            return out.toString();
    }

    public static synchronized Boolean evaluateBooleanExpression(String template, Map parameters) throws Exception {
        return evaluateBooleanExpression(template, parameters, "notProvided", null);
    }

    public static synchronized Boolean evaluateBooleanExpression(String template, Map parameters, String operationName, DataSource ds)
        throws Exception {
        Object obj = evaluate(template, parameters, operationName, ds, true);
        if (obj == null || !(obj instanceof Boolean) && !obj.toString().trim().toLowerCase().equals("true")
            && !obj.toString().trim().toLowerCase().equals("false") && !obj.toString().trim().toLowerCase().equals("'true'")
            && !obj.toString().trim().toLowerCase().equals("'false'")) {
            String postEval = obj.toString();
            if (template.indexOf("'true'") != -1)
                postEval = postEval.replaceAll("'true'", "true");
            if (template.indexOf("'false'") != -1)
                postEval = postEval.replaceAll("'false'", "false");
            String wrappedTemplate = (new StringBuilder()).append("#if(").append(postEval).append(") true #else false #end").toString();
            try {
                obj = evaluate(wrappedTemplate, parameters, operationName, ds, true);
            } catch (Exception e) {
                log.error("evaluate " + wrappedTemplate + "occur error" + e.getMessage());
            }
        }
        if (obj != null) {
            if (obj instanceof Boolean)
                return (Boolean) obj;
            if (obj.toString().trim().toLowerCase().equals("true"))
                return Boolean.TRUE;
            if (obj.toString().trim().toLowerCase().equals("'true'"))
                return Boolean.TRUE;
            if (obj.toString().trim().toLowerCase().equals("false"))
                return Boolean.FALSE;
            if (obj.toString().trim().toLowerCase().equals("'false'"))
                return Boolean.FALSE;
        }
        return null;
    }

    public static Map<String, Object> getStandardContextMap(DSRequest dsReq) {

        DSRequestData _reqData = dsReq.getContext();
        if (_reqData == null)
            return new HashMap<String, Object>();
        Map<String, Object> context;
        if (dsReq.getRpc() != null) {
            context = new HashMap<String, Object>(dsReq.getRpc().getContext().getTemplateContext());
            context.put(RESPONSE_DATA, new ResponseDataHandler(dsReq.getRpc(), dsReq));
        } else {
            context = new HashMap<String, Object>();
        }
        Map criteria = _reqData.getCriteria() == null ? (new HashMap<String, Object>()) : _reqData.getCriteria();
        Map values = _reqData.getValues() == null ? new HashMap<String, Object>() : _reqData.getValues();
        context.put(CRITERIA, criteria);
        context.put(VALUES, values);
        context.put(DSREQUEST, dsReq);
        context.put(DATASOURCES, new DataSourcesHandler());
        if (dsReq.getRequestContext() instanceof WebContext)
            context.putAll(getServletContextMap((WebContext) dsReq.getRequestContext()));
        context.put(UTIL, new Util());
        context.put(TOOLS, new DataTools());
        context.put(USER_ID, new UserIdHandler(dsReq));
        if (_reqData.getTemplateContext() != null) {
            for (Object key : _reqData.getTemplateContext().keySet()) {
                if (context.get(key.toString()) != null)
                    log.warn((new StringBuilder()).append("DSRequest-specified a template context variable: ").append(key).append(
                        " collides with derived key of the same name").append(" - using the DSRequest-specified value.").toString());
                context.put(key.toString(), _reqData.getTemplateContext().get(key));
            }
        }
        return context;
    }

    public static Map<String, Object> getServletContextMap(RPCManager rpc) {
        if (rpc == null)
            return new HashMap<String, Object>();
        else
            return getServletContextMap(rpc.getRequestContext());
    }

    /**
     * Convert {@link org.solmix.api.servlet.RequestContext} to stander velocity context
     * 
     * @param reqContext
     * @return
     */
    public static Map<String, Object> getServletContextMap(WebContext reqContext) {
        Map<String, Object> context = new HashMap<String, Object>();
        ServletRequestAttributeMapFacade escRequest = null;
        SessionAttributeMapFacade escSession = null;
        if (reqContext != null) {
            escRequest = new ServletRequestAttributeMapFacade(reqContext.getRequest());
            if (reqContext.getRequest() != null)
                escSession = new SessionAttributeMapFacade(reqContext.getRequest().getSession());
        }
        context.put(HTTP_PARAMETERS, new HttpParameterHandler(escRequest));
        context.put(HTTP_ATTRIBUTES, new HttpAttributeHandler(escRequest));
        context.put(SESSION_ATTRIBUTES, new HttpAttributeHandler(escSession));
        if (escRequest != null)
            context.put(SERVLET_REQUEST, escRequest);
        if (escSession != null)
            context.put(SESSION, escSession);
        return context;
    }
    
    public static void evaluateTemplateFile(String vmFileName, Map parameters,String encoding,Writer out) throws SlxException{
        try {
            VelocityContext context = new VelocityContext(parameters);
            
            VelocityEngine engin= getEngine();
            Template template;
            if(encoding!=null)
               template = engin.getTemplate(vmFileName, encoding);
            else
                template = engin.getTemplate(vmFileName);
            template.merge(context, out);
        } catch (ResourceNotFoundException e) {
           throw new SlxException(Tmodule.VM,Texception.NO_FOUND,"Not found vm template file.",e);
        } catch (ParseErrorException e) {
            throw new SlxException(Tmodule.VM,Texception.PARSER_VM_FILE_EXCEPTION,"Exception When Paraser vm template file.",e);
        } 
    }

    private static Logger log = LoggerFactory.getLogger(Velocity.class.getName());

    private static VelocityEngine vEngine;
    

}
