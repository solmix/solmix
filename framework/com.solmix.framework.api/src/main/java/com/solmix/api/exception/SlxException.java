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

package com.solmix.api.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

import javax.servlet.ServletException;

import org.apache.velocity.exception.MethodInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.types.Texception;
import com.solmix.api.types.Tmodule;

/**
 * Wrapped solmix project Exception
 * 
 * @author solomon
 * @since 0.0.1
 * @version 0.0.4
 */
public class SlxException extends Exception
{

    /**
    * 
    */
    private static final long serialVersionUID = 1252155663355037115L;

    private static Logger log = LoggerFactory.getLogger(SlxException.class);

    private Tmodule module;

    private String moduleName;

    /**
     * @return the moduleName
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * @param moduleName the moduleName to set
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private Texception code;

    private Throwable exception;

    private String message;

    private Object[] args;

    public SlxException(Tmodule module, Texception code, String message, Throwable e, Object[] args)
    {
        setModule(module);
        setCode(code);
        setException(e);
        setArgs(args);
        setMessage(message);
        log.trace(getMessage(), e);
    }

    public SlxException(Tmodule module, Texception code, String message, Throwable e)
    {
        this(module, code, message, e, null);
    }

    public SlxException(Tmodule module, Texception code, String message)
    {
        this(module, code, message, null, null);
    }

    public SlxException(Tmodule module, Texception code, Throwable e)
    {
        this(module, code, "", e, null);
    }

    public SlxException()
    {
        this(Tmodule.BASIC, Texception.DEFAULT, null, null);
    }

    public SlxException(String msg)
    {
        this(Tmodule.BASIC, Texception.DEFAULT, msg, null);
    }

    public SlxException(Throwable e)
    {
        this(Tmodule.BASIC, Texception.DEFAULT, e.getMessage(), e);

    }

    public SlxException(Texception code, Throwable e)
    {
        this(Tmodule.BASIC, code, e.getMessage(), e);

    }

    public SlxException(String moduleName, Texception code, String message, Throwable e, Object[] args)
    {
        setModuleName(moduleName);
        setCode(code);
        setException(e);
        setArgs(args);
        setMessage(message);
        log.trace(getMessage(), e);
    }

    public SlxException(String moduleName, Texception code, String message, Throwable e)
    {
        this(moduleName, code, message, e, null);
    }

    public SlxException(String moduleName, Texception code, String message)
    {
        this(moduleName, code, message, null, null);
    }

    public SlxException(String moduleName, Texception code, Throwable e)
    {
        this(moduleName, code, "", e, null);
    }

    /**
     * @return the module
     */
    public Tmodule getModule() {
        return module;
    }

    /**
     * @param module the module to set
     */
    public void setModule(Tmodule module) {
        this.module = module;
    }

    /**
     * @return the code
     */
    public Texception getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(Texception code) {
        this.code = code;
    }

    /**
     * @return the exception
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * @param exception the exception to set
     */
    public void setException(Throwable exception) {
        this.exception = exception;
    }

    /**
     * @return the message
     */
    @Override
    public String getMessage() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Error number ");
        buffer.append(getCode().value());
        buffer.append(" in ");
        if (getModule() != null)
            buffer.append(getModule().value());
        else if (moduleName != null)
            buffer.append(moduleName);
        if (message != null) {
            buffer.append(": ");
            if (args == null)
                buffer.append(message);
            else {
                MessageFormat msgFormat = new MessageFormat(message);
                try {
                    buffer.append(msgFormat.format(args));
                } catch (Exception e) {
                    buffer.append("Cannot format message " + message + " with args ");
                    for (int i = 0; i < args.length; i++) {
                        if (i != 0)
                            buffer.append(",");
                        buffer.append(args[i]);
                    }
                }
            }
        }

        if (exception != null) {
            buffer.append("\nWrapped Exception: ");
            buffer.append(exception.getMessage());
        }
        return buffer.toString();
    }

    public String getFullMessage() {
        StringBuffer buffer = new StringBuffer(getMessage());
        buffer.append("\n");
        buffer.append(getStackTraceAsString());
        buffer.append("\n");
        return buffer.toString();
    }

    public String getStackTraceAsString() {
        StringWriter swriter = new StringWriter();
        PrintWriter pwriter = new PrintWriter(swriter);
        printStackTrace(pwriter);
        pwriter.flush();
        return swriter.getBuffer().toString();
    }

    public String getStackTraceAsString(Throwable e) {
        StringWriter swriter = new StringWriter();
        PrintWriter pwriter = new PrintWriter(swriter);
        e.printStackTrace(pwriter);
        pwriter.flush();
        return swriter.getBuffer().toString();
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the args
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * @param args the args to set
     */
    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        Throwable tmp = exception;
        do {
            if (!(tmp instanceof SlxException))
                break;
            if (((SlxException) tmp).getException() == null)
                break;
            tmp = ((SlxException) tmp).getException();

        } while (true);

        if (tmp != null) {

            if (tmp instanceof MethodInvocationException) {
                (((MethodInvocationException) tmp).getWrappedThrowable()).printStackTrace(s);
            } else if (tmp instanceof ServletException) {
                (((ServletException) tmp).getRootCause()).printStackTrace(s);
            } else {
                if (tmp.getCause() != null) {
                    s.print("Root cause:  ");
                    tmp.getCause().printStackTrace(s);
                } else {
                    tmp.printStackTrace(s);
                }
            }
        }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        Throwable tmp = exception;
        do {
            if (!(tmp instanceof SlxException))
                break;
            if (((SlxException) tmp).getException() == null)
                break;
            tmp = ((SlxException) tmp).getException();

        } while (true);

        if (tmp != null) {

            if (tmp instanceof MethodInvocationException) {
                (((MethodInvocationException) tmp).getWrappedThrowable()).printStackTrace(s);
            } else if (tmp instanceof ServletException) {
                (((ServletException) tmp).getRootCause()).printStackTrace(s);
            } else {
                if (tmp.getCause() != null) {
                    s.print("Root cause:  ");
                    tmp.getCause().printStackTrace(s);
                } else {
                    tmp.printStackTrace(s);
                }
            }

        }
    }
}
