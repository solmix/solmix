/*
 * SOLMIX PROJECT
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

package org.solmix.fmk.context;

import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.context.Context;
import org.solmix.api.context.Context.Scope;
import org.solmix.api.context.SystemContext;
import org.solmix.api.context.SystemContextFactory;
import org.solmix.api.context.WebContext;
import org.solmix.api.exception.SlxException;
import org.solmix.api.rpc.RPCManager;
import org.solmix.fmk.rpc.Transaction;

/**
 * This class allows obtaining of the current Request without passing the request around the world.
 * <p>
 * A ThreadLocal variable is used to manage the request and to make sure it doesn't escape to another processing.
 * <p>
 * This class is not depend on servlets. the framework should used the context to get and set attributes instead of
 * using the request or session object directly. Solmix can run with POJO,OSGI,Spring-servlets ,the must has a neutral
 * and configurable context.
 * 
 * @author solmix.f@gmail.com
 * @version 110081 2013-3-26
 * @since 0.0.2
 */
public final class SlxContext
{

    private static final Logger log = LoggerFactory.getLogger(SlxContext.class);

    /**
     * The thread local variable holding the current context.
     */
    private static InheritableThreadLocal<Context> localContext = new InheritableThreadLocal<Context>();


    /**
     * Do not instantiate this class. The constructor must be public to use discovery
     */
    public SlxContext()
    {

    }

 

    /**
     * @return the systemContext
     */
    public static SystemContext getSystemContext() {
      
        return   SystemContextFactory.getDefaultSystemContext();
    }
    
    public static SystemContext getThreadSystemContext() {
        return   SystemContextFactory.getThreadDefaultSystemContext();
    }

    /**
     * At most condition must reset the SystemContext after you used completely.
     * <p>
     * <code>
     * Context original =SlxContext.getContext();
     * .....
     *  SlxContext.setContext(otherContext);
     * .....
     *     reset context.
     * SlxContext.setContext(originalCtx);
     * 
     * </code>
     * 
     * @param systemContext the systemContext to set
     */
    public static void setSystemContext(SystemContext systemContext) {
        SystemContextFactory.setDefaultSystemContext(systemContext);
    }
    
    /**
     * set the thread dependency system context.
     * @param systemContext
     */
    public static void setThreadSystemContext(SystemContext systemContext){
        SystemContextFactory.setThreadDefaultSystemContext(systemContext);
    }

    /**
     * Get the current context of this thread.
     */
    public static Context getContext() {
        Context context = localContext.get();
        if (context == null) {
            final IllegalStateException ise = new IllegalStateException("Context is not set for this thread");
            log.error("Context is not initialized. This could happen if the request does not go through the  default context filters.", ise);
            throw ise;
        }
        return context;
    }

    public static void removeContext() {
        localContext.remove();
    }

 

    /**
     * Get EventManager of this framework .
     * 
     * @return
     */
   /* public static EventManager getEventManager() {
        AbstractSystemContext abc = getAbstractSystemContext();
        if (abc != null) {
            EventManager ctx = abc.getEventManager();
            if (ctx == null) {
                throw new IllegalArgumentException("EventManager is not set.");
            }
            return ctx;
        } else {
            return null;
        }
    }*/

    /**
     * Set the context for locale thread.
     * 
     * @param context
     */
    public static void setContext(Context context) {
        localContext.set(context);
    }

    public static boolean hasContext() {
        return localContext.get() != null;
    }

    /**
     * Set the locale for the current context.
     */
    public static void setLocale(Locale locale) {
        getContext().setLocale(locale);
    }

    /**
     * Get Locale in this thread.
     * 
     * @return
     */
    public static Locale getLocale() {
        return getContext().getLocale();
    }

    /**
     * Get parameter in web context.
     * 
     * @return
     */
    public static Map<String, String> getParameters() {
        WebContext ctx = getWebContext();
        if (ctx != null) {
            return ctx.getParameters();
        }
        return null;

    }

    /**
     * Get access manager for a resource.this is part of JAAS framework.
     * 
     * @throws SlxException
     */
    /*
     * public static AccessManager getAccessManager(String name) throws SlxException { return
     * getContext().getAccessManager(name); }
     */

    /**
     * Get parameter value as string.
     */
    public static String getParameter(String name) {
        WebContext ctx = getWebContext();
        if (ctx != null) {
            return ctx.getParameter(name);
        }
        return null;

    }

    /**
     * @return
     */
    public static WebContext getWebContext() {
        return getWebContext(null);
    }

    /**
     * Throws an IllegalStateException if the current context is not set, or if it is not an instance of WebContext.
     * Yes, you can specify the exception message if you want. This is useful if you're calling this from a component
     * which only supports WebContext and still care enough to actually throw an exception with a meaningful message.
     * 
     * @see #getWebContext()
     */
    public static WebContext getWebContext(String exceptionMessage) {
        final WebContext wc = getWebContextIfExisting(getContext());
        if (wc == null) {
            throw new IllegalStateException(exceptionMessage == null ? "The current context is not an instance of WebContext (" + localContext.get()
                + ")" : exceptionMessage);
        }
        return wc;
    }

    private static WebContext getWebContextIfExisting(Context ctx) {
        if (ctx instanceof WebContext) {
            return (WebContext) ctx;
        } else if (ctx instanceof ContextDecorator) {
            return getWebContextIfExisting(((ContextDecorator) ctx).getWrappedContext());
        }
        return null;
    }

    public static boolean isSystemContext() {
        return hasContext() ? getContext() instanceof SystemContext : false;
    }

    public static boolean isWebContext() {
        return hasContext() ? getContext() instanceof WebContext : false;
    }

   
   

    /**
     * Releases the current thread (if not a system context) and calls the releaseThread() method of the system context.
     */
    public static void release() {
        if (hasContext() && !(getContext() instanceof SystemContext)) {
            getContext().release();
        }

    }

    /**
     * @param attributeLoginerror
     * @param loginResult
     */
    public static void setAttribute(String name, Object value) {
        getContext().setAttribute(name, value, Scope.LOCAL);

    }

    /**
     * @param attributeLoginerror
     * @return
     */
    public static Object getAttribute(String name) {
        return getContext().getAttribute(name);
    }

    /**
     * Executes the given operation in the system context and sets it back to the original once done (also if an
     * exception is thrown). Also works if there was no context upon calling. (sets it back to null in this case)
     */
    public static <T, E extends Throwable> T doInSystemContext(final Op<T, E> op) throws E {
        return doInSystemContext(op, false);
    }

    /**
     * Executes the given operation in the system context and sets it back to the original once done (also if an
     * exception is thrown). Also works if there was no context upon calling (sets it back to null in this case)
     * 
     * @param releaseAfterExecution set to true if the context should be released once the execution is done (e.g. in
     *        workflow operations or scheduled jobs).
     */
    public static <T, E extends Throwable> T doInSystemContext(final Op<T, E> op, boolean releaseAfterExecution) throws E {
        final Context originalCtx = SlxContext.hasContext() ? SlxContext.getContext() : null;
        T result;
        try {
            SlxContext.setContext(SlxContext.getSystemContext());
            result = op.exe();
            if (releaseAfterExecution) {
                SlxContext.release();
            }
        } finally {
            SlxContext.setContext(originalCtx);
        }
        return result;
    }

    /**
     * Executes the given operation in the web context ,if this thread is not has a
     * {@link org.solmix.api.context.WebContext} in it, return null.
     * 
     * @param op
     * @param releaseAfterExecution
     * @return
     * @throws E
     */
    public static <T, E extends Throwable> T doInWebContext(final Op<T, E> op) throws E {
        if (SlxContext.hasContext() && SlxContext.isWebContext()) {
            return op.exe();
        } else {
            log.warn("This is not a WebContext");
            return null;
        }

    }

    public static Transaction getTransaction() throws SlxException {

        return new Transaction(getThreadSystemContext());
    }

    /**
     * @param rpc used exited rpc to complete transaction.
     * @return
     */
    public static Transaction getTransaction(RPCManager rpc) {

        return new Transaction(rpc,getThreadSystemContext());
    }

    /**
     * A simple execution interface to be used with the doInSystemContext and doInWebContext method. If no return value
     * is necessary, return null (for semantic's sake, declare T as <Void>) If no checked exception need to be thrown,
     * declare E as <RuntimeException>)
     * 
     * @see SlxContext#doInSystemContext(Op)
     * @see SlxContext#doInSystemContext(Op, boolean)
     * @see SlxContext#doInWebContext(Op)
     * 
     * @author solmix
     * 
     * @param <T>
     * @param <E>
     */
    public static interface Op<T, E extends Throwable>
    {

        T exe() throws E;
    }

    /**
     * An Op that does not return values and can only throw RuntimeExceptions.
     */
    public abstract static class VoidOp implements Op<Void, RuntimeException>
    {

        @Override
        public Void exe() {
            doExe();
            return null;
        }

        abstract public void doExe();
    }
}
