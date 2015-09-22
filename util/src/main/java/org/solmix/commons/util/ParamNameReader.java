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
package org.solmix.commons.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年9月21日
 */

public class ParamNameReader extends ClassByteReader
{
    private String methodName;
    private Map<String, MethodInfo> methods = new HashMap<String, MethodInfo>();
    private Class<?>[] paramTypes;

    /**
     * process a class file, given it's class. We'll use the defining
     * classloader to locate the bytecode.
     * 
     * @param c
     * @throws IOException
     */
    public ParamNameReader(Class<?> c) throws IOException {
        this(getBytes(c));
    }

    /**
     * process the given class bytes directly.
     * 
     * @param b
     * @throws IOException
     */
    public ParamNameReader(byte[] b) throws IOException {
        super(b, findAttributeReaders(ParamNameReader.class));

        // check the magic number
        if (readInt() != 0xCAFEBABE) {
            // not a class file!
            throw new IOException();
        }

        readShort(); // minor version
        readShort(); // major version

        readCpool(); // slurp in the constant pool

        readShort(); // access flags
        readShort(); // this class name
        readShort(); // super class name

        int count = readShort(); // ifaces count
        for (int i = 0; i < count; i++) {
            readShort(); // interface index
        }

        count = readShort(); // fields count
        for (int i = 0; i < count; i++) {
            readShort(); // access flags
            readShort(); // name index
            readShort(); // descriptor index
            skipAttributes(); // field attributes
        }

        count = readShort(); // methods count
        for (int i = 0; i < count; i++) {
            readShort(); // access flags
            int m = readShort(); // name index
            String name = resolveUtf8(m);
            int d = readShort(); // descriptor index
            this.methodName = name + resolveUtf8(d);
            readAttributes(); // method attributes
        }

    }

    /**
     * Retrieve a list of function parameter names from a method Returns null if
     * unable to read parameter names (i.e. bytecode not built with debug).
     */
    public static String[] getParameterNamesFromDebugInfo(Method method) {
        // Don't worry about it if there are no params.
        int numParams = method.getParameterTypes().length;
        if (numParams == 0) {
            return null;
        }
        
        // get declaring class
        Class<?> c = method.getDeclaringClass();

        // Don't worry about it if the class is a Java dynamic proxy
        if (Proxy.isProxyClass(c)) {
            return null;
        }

        try {
            ParamNameReader pr = new ParamNameReader(c);
            try{
                return pr.getParameterNames(method);
            }finally{
                pr.close();
            }
               
        } catch (IOException e) {
            // log it and leave
            // log.info(Messages.getMessage("error00") + ":" + e);
            return null;
        }
    }

    @Override
    public void readCode() throws IOException {
        readShort(); // max stack
        int maxLocals = readShort(); // max locals

        MethodInfo info = new MethodInfo(maxLocals);
        if (methods != null && methodName != null) {
            methods.put(methodName, info);
        }

        skipFully(readInt()); // code
        skipFully(8 * readShort()); // exception table
        // read the code attributes (recursive). This is where
        // we will find the LocalVariableTable attribute.
        readAttributes();
    }

    /**
     * return the names of the declared parameters for the given constructor. If
     * we cannot determine the names, return null. The returned array will have
     * one name per parameter. The length of the array will be the same as the
     * length of the Class[] array returned by Constructor.getParameterTypes().
     * 
     * @param ctor
     * @return String[] array of names, one per parameter, or null
     */
    public String[] getParameterNames(Constructor<?> ctor) {
        paramTypes = ctor.getParameterTypes();
        return getParameterNames(ctor, paramTypes);
    }

    /**
     * return the names of the declared parameters for the given method. If we
     * cannot determine the names, return null. The returned array will have one
     * name per parameter. The length of the array will be the same as the
     * length of the Class[] array returned by Method.getParameterTypes().
     * 
     * @param method
     * @return String[] array of names, one per parameter, or null
     */
    public String[] getParameterNames(Method method) {
        paramTypes = method.getParameterTypes();
        return getParameterNames(method, paramTypes);
    }

    protected String[] getParameterNames(Member member, Class<?>[] pTypes) {
        // look up the names for this method
        MethodInfo info = methods.get(getSignature(member, pTypes));

        // we know all the local variable names, but we only need to return
        // the names of the parameters.

        if (info != null) {
            String[] paramNames = new String[pTypes.length];
            int j = Modifier.isStatic(member.getModifiers()) ? 0 : 1;

            boolean found = false; // did we find any non-null names
            for (int i = 0; i < paramNames.length; i++) {
                if (info.names[j] != null) {
                    found = true;
                    paramNames[i] = info.names[j];
                }
                j++;
                if (pTypes[i] == double.class || pTypes[i] == long.class) {
                    // skip a slot for 64bit params
                    j++;
                }
            }

            if (found) {
                return paramNames;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private static class MethodInfo {
        String[] names;

        public MethodInfo(int maxLocals) {
            names = new String[maxLocals];
        }
    }

    private MethodInfo getMethodInfo() {
        MethodInfo info = null;
        if (methods != null && methodName != null) {
            info = methods.get(methodName);
        }
        return info;
    }

    /**
     * this is invoked when a LocalVariableTable attribute is encountered.
     * 
     * @throws IOException
     */
    public void readLocalVariableTable() throws IOException {
        int len = readShort(); // table length
        MethodInfo info = getMethodInfo();
        for (int j = 0; j < len; j++) {
            readShort(); // start pc
            readShort(); // length
            int nameIndex = readShort(); // name_index
            readShort(); // descriptor_index
            int index = readShort(); // local index
            if (info != null) {
                info.names[index] = resolveUtf8(nameIndex);
            }
        }
    }
}
