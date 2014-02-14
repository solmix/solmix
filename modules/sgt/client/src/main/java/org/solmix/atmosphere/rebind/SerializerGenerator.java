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
package org.solmix.atmosphere.rebind;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.solmix.atmosphere.client.SerialTypes;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.IncrementalGenerator;
import com.google.gwt.core.ext.RebindMode;
import com.google.gwt.core.ext.RebindResult;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.impl.Serializer;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.rpc.SerializableTypeOracle;
import com.google.gwt.user.rebind.rpc.SerializableTypeOracleBuilder;
import com.google.gwt.user.rebind.rpc.TypeSerializerCreator;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-9-27
 */

public class SerializerGenerator extends IncrementalGenerator
{

    @Override
    public long getVersionId() {
      return 1L;
    }
    
    @Override
    public RebindResult generateIncrementally(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {

          TypeOracle typeOracle = context.getTypeOracle();

          // Create the CometSerializer impl
          String packageName = "comet";
          String className = typeName.replace('.', '_') + "Impl";
          PrintWriter printWriter = context.tryCreate(logger, packageName, className);

          if (printWriter != null) {

              try {
                  JClassType type = typeOracle.getType(typeName);
                  SerialTypes annotation = type.getAnnotation(SerialTypes.class);
                  if (annotation == null) {
                      logger.log(TreeLogger.ERROR, "No SerialTypes annotation on CometSerializer type: " + typeName);
                      throw new UnableToCompleteException();
                  }

                  SerializableTypeOracleBuilder typesSentToBrowserBuilder = new SerializableTypeOracleBuilder(
                          logger, context.getPropertyOracle(), context);
                  SerializableTypeOracleBuilder typesSentFromBrowserBuilder = new SerializableTypeOracleBuilder(
                          logger, context.getPropertyOracle(), context);

                  List<Class<?>> serializableTypes = new ArrayList();
                  Collections.addAll(serializableTypes, annotation.value());
                  for (Class<?> serializable : serializableTypes) {
                      int rank = 0;
                      if (serializable.isArray()) {
                          while (serializable.isArray()) {
                              serializable = serializable.getComponentType();
                              rank++;
                          }
                      }

                      JType resolvedType = typeOracle.getType(serializable.getCanonicalName());
                      while (rank > 0) {
                          resolvedType = typeOracle.getArrayType(resolvedType);
                          rank--;
                      }

                      typesSentToBrowserBuilder.addRootType(logger, resolvedType);
                      typesSentFromBrowserBuilder.addRootType(logger, resolvedType);
                  }

                  // Create a resource file to receive all of the serialization information
                  // computed by STOB and mark it as private so it does not end up in the
                  // output.
                  OutputStream pathInfo = context.tryCreateResource(logger, typeName + ".rpc.log");
                  PrintWriter writer = new PrintWriter(new OutputStreamWriter(pathInfo));
                  writer.write("====================================\n");
                  writer.write("Types potentially sent from server:\n");
                  writer.write("====================================\n\n");
                  writer.flush();

                  typesSentToBrowserBuilder.setLogOutputWriter(writer);
                  SerializableTypeOracle typesSentToBrowser = typesSentToBrowserBuilder.build(logger);

                  writer.write("===================================\n");
                  writer.write("Types potentially sent from browser:\n");
                  writer.write("===================================\n\n");
                  writer.flush();
                  typesSentFromBrowserBuilder.setLogOutputWriter(writer);
                  SerializableTypeOracle typesSentFromBrowser = typesSentFromBrowserBuilder.build(logger);

                  writer.close();

                  if (pathInfo != null) {
                      context.commitResource(logger, pathInfo).setPrivate(true);
                  }

                  // Create the serializer
                  final String modifiedTypeName = typeName.replace('.', '_');
                  TypeSerializerCreator tsc = new TypeSerializerCreator(logger, typesSentFromBrowser, typesSentToBrowser, context, "comet." + modifiedTypeName, modifiedTypeName);
                  String realize = tsc.realize(logger);

                  // Create the CometSerializer impl
                  ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, className);

                  composerFactory.addImport(Serializer.class.getName());
                  composerFactory.addImport(SerializationException.class.getName());
                  composerFactory.addImport(Serializable.class.getName());
                  
                  composerFactory.setSuperclass(typeName);
                  SourceWriter sourceWriter = composerFactory.createSourceWriter(context, printWriter);
                  sourceWriter.print("private Serializer SERIALIZER = new " + realize + "();");
                  sourceWriter.print("protected Serializer getRPCSerializer() {return SERIALIZER;}");
                  sourceWriter.commit(logger);

              } catch (NotFoundException e) {
                  logger.log(TreeLogger.ERROR, "", e);
                  throw new UnableToCompleteException();
              }
          }

          return new RebindResult(RebindMode.USE_ALL_NEW_WITH_NO_CACHING, packageName + '.' + className);
      }

}
