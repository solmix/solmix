/*
 * Copyright 2012 The Solmix Project
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

package org.solmix.api.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;

/**
 * Wrapped solmix project Exception.
 * 
 * @author solmix.f@gmail.com
 * @since 0.0.1
 * @version 0.0.4
 */
public class SlxException extends Exception
{

    /**
    * 
    */
    private static final long serialVersionUID = 1252155663355037115L;

    public static final String ERROR_CODE_PREFIX = "ERROR CODE: [";

    public static final String ERROR_CODE_SUFFIX = "]";

    private Tmodule module;


    private Texception code;

    private String message;

    private Object[] args;

    /**
     * SlxException construction.
     * 
     * @param module Defined the framework module.
     * @param errorNumber Exception number.
     * @param message Error message.
     * @param e The cause exception.
     * @param args exception arguments.
     */
    public SlxException(Tmodule module, Texception errorNumber,
        String errorMsg, Throwable e, Object[] args)
    {
        super(e);
        setModule(module);
        setCode(errorNumber);
        setArgs(args);
        setMessage(errorMsg);
    }

    /**
     * SlxException construction.
     * 
     * @param module Defined the framework module.
     * @param errorNumber Exception number.
     * @param message Error message.
     * @param e The cause exception.
     */
    public SlxException(Tmodule module, Texception errorNumber,
        String errorMsg, Throwable e)
    {
        this(module, errorNumber, errorMsg, e, null);
    }

    /**
     * SlxException construction.
     * 
     * @param module Defined the framework module.
     * @param errorNumber Exception number.
     * @param message Error message.
     */
    public SlxException(Tmodule module, Texception errorNumber, String errorMsg)
    {
        this(module, errorNumber, errorMsg, null, null);
    }

    /**
     * SlxException construction.
     * 
     * @param module Defined the framework module.
     * @param errorNumber Exception number.
     * @param e The cause exception.
     */
    public SlxException(Tmodule module, Texception errorNumber, Throwable e)
    {
        this(module, errorNumber, "", e, null);
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


    /**
     * Return predefined module name.
     * 
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
     * Get Exception error code.
     * 
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
     * @return the message
     */
    @Override
    public String getMessage() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(ERROR_CODE_PREFIX);
        buffer.append(getCode().value());
        buffer.append(ERROR_CODE_SUFFIX);
        buffer.append(" in ");
        if (getModule() != null)
            buffer.append(getModule().value());
        else if (module != null)
            buffer.append(module.value());
        if (message != null) {
            buffer.append(": ");
            if (args == null)
                buffer.append(message);
            else {
                MessageFormat msgFormat = new MessageFormat(message);
                try {
                    buffer.append(msgFormat.format(args));
                } catch (Exception e) {
                    buffer.append("Cannot format message " + message
                        + " with args ");
                    for (int i = 0; i < args.length; i++) {
                        if (i != 0)
                            buffer.append(",");
                        buffer.append(args[i]);
                    }
                }
            }
        }
        Throwable cause=getCause();
        if (cause != null) {
            buffer.append("\n Wrapped Exception: ");
            if(cause instanceof SlxException)
                cause = getRootThrowable((SlxException)cause);
            buffer.append(cause.getMessage());
        }
        return buffer.toString();
    }

    /**
     * Return Root exception.
     * 
     * @param exception
     * @return
     */
    private Throwable getRootThrowable(SlxException exception) {
        Throwable root = exception.getCause();
        if(root==null)
            return exception;
        else if (root instanceof SlxException)
            return getRootThrowable((SlxException) root);
        else
            return root;

    }

    public String getFullMessage() {
        StringBuffer buffer = new StringBuffer(getMessage());
        buffer.append("\n");
        buffer.append(getStackTraceAsString());
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
}
