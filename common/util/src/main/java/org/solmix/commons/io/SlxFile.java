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
package org.solmix.commons.io;

import static org.solmix.commons.util.DataUtils.caseSensitiveFileExists;
import static org.solmix.commons.util.DataUtils.isURI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.IOUtils;



/**
 * simply file　operation .{@link java.io.File File} ;{@link  java.net.URL URL} ;
 * {@link org.apache.commons.vfs.FileObject FileObject};
 * @version 110035
 */
public class SlxFile
{
   private static final Logger log = LoggerFactory.getLogger(SlxFile.class.getName());

   String canonicalPath;

   String filename;

   URL url;

   File file;

   org.apache.commons.vfs.FileObject fileObject;

   /**
    * constructed function
    * 
    * @param url
    */
   public SlxFile(URL url)
   {
      this.url = url;
   }

   /**
    * constructed function
    * 
    * @param file
    */
   public SlxFile(File file)
   {
      this.file = file;
   }

   /**
    * constructed function
    * 
    * @param fileObject
    */
   public SlxFile(FileObject fileObject)
   {
      this.fileObject = fileObject;
   }

   /**
    * constructed function
    * 
    * @param fileObject
    */
   public SlxFile(String filename) throws IOException
   {
      this.filename = filename;
      if (filename.startsWith("ram://") || filename.startsWith("res://"))
         fileObject = VFS.getManager().resolveFile(filename);
      else {
         if (filename.startsWith("file:"))
            filename = filename.substring("file:".length());
            if (isURI(filename))
            url = new URL(filename);
         else
            file = new File(canonicalizePath(filename));
      }
   }

   public boolean delete() throws IOException
   {
      if (file != null)
         return file.delete();
      if (fileObject != null)
         return fileObject.delete();
      else
         throw new IOException((new StringBuilder()).append("delete() operation not supported for filename: ").append(
            filename).toString());
   }

   public boolean exists() throws IOException
   {
      if (file != null) {
            return caseSensitiveFileExists(filename, file);
      }
      if (fileObject != null) {
         return fileObject.exists();
      }
      if (url == null)
         return false;
      InputStream is = this.getInputStream();
      if (is != null) {
         return true;
      }
      return false;
   }

   public String getParent() throws FileSystemException
   {
      if (fileObject != null)
         return fileObject.getParent().getName().getURI();
      else
         return (new File(filename)).getParent();

   }

   public String getCanonicalPath() throws IOException
   {
      if(canonicalPath == null){
         if(file != null)
             canonicalPath = file.getCanonicalPath();
         else if(url != null)
         {
             if(filename != null)
                 return filename;
             canonicalPath = url.toExternalForm();
         } else {
            canonicalPath = fileObject.getURL().toExternalForm();
         }
      }
         return canonicalPath;
   }


   public boolean canRead() throws IOException
   {
      if (file != null)
         return file.canRead();
      if (fileObject != null)
         return fileObject.isReadable();
      return url != null;

   }

   public boolean canWrite() throws IOException
   {
      if (file != null)
         return file.canWrite();
      if (fileObject != null)
         return fileObject.isWriteable();
      else
         return false;
   }

   public boolean mkdir() throws IOException
   {
      if (file != null)
         return file.mkdir();
      if (fileObject != null) {
         try {
            fileObject.createFolder();
            return true;
         } catch (Exception e) {
            return false;
         }
      }
      throw new IOException((new StringBuilder()).append("mkdir() not supported for filename: ").append(filename)
         .toString());

   }

   public boolean mkdirs() throws IOException
   {
      if (file != null)
         return file.mkdirs();
      if (fileObject != null) {
         try {
            fileObject.createFolder();
            return true;
         } catch (Exception e) {
            return false;
         }
      }
      throw new IOException((new StringBuilder()).append("mkdir() not supported for filename: ").append(filename)
         .toString());
   }

   public String getPath() throws IOException
   {
      return getCanonicalPath();

   }

   public Object getNativeHandler()
   {
      if (file != null)
         return file;
      if (fileObject != null)
         return fileObject;
      else
         return url;
   }

   public Reader getReader() throws IOException
   {
      return new InputStreamReader(getInputStream());
   }

   public String getAsString() throws IOException
   {
      StringWriter sw = new StringWriter();
      IOUtils.copyCharacterStreams(getReader(), sw);
      return sw.toString();
   }

   public InputStream getInputStream() throws IOException
   {
      if (file != null)
         return new FileInputStream(file);
      if (fileObject != null)
         return fileObject.getContent().getInputStream();
      else
         return url.openConnection().getInputStream();

   }

   public OutputStream getOutputStream() throws IOException
   {
      if (file != null)
         return new FileOutputStream(file);
      if (fileObject != null)
         return fileObject.getContent().getOutputStream();
      else
         throw new IOException((new StringBuilder()).append("getOutputStream not supported for filename: ").append(
            filename).toString());
   }

   public Writer getWriter() throws IOException
   {
      return new OutputStreamWriter(getOutputStream());
   }

   public long length() throws IOException
   {
      if (!exists())
         return 0L;
      if (file != null)
         return file.length();
      if (fileObject != null)
         return fileObject.getContent().getSize();
      else
         return url.openConnection().getContentLength();
   }

   public long lastModified() throws IOException
   {
      if (file != null)
         return file.lastModified();
      if (fileObject != null) {
         if (exists()) {
            long lastModified = fileObject.getContent().getLastModifiedTime();
            lastModified = (long) Math.floor(lastModified / 1000L) * 1000L;
            return lastModified;
         } else {
            return 0L;
         }
      } else {
         return url.openConnection().getLastModified();
      }
   }


   public static String stripContainerIOPrefix(String path)
   {
      // TODO
      return path;
   }

   public static String canonicalizePath(String path)
   {
      if(path == null)
         return null;
     path = path.trim();
     StringWriter sw = new StringWriter();
     int copiedFrom = 0;
     int length = path.length();
     for(int ii = 0; ii < length; ii++)
     {
         char currChar = path.charAt(ii);
         if(currChar != '\\' && currChar != '/')
             continue;
         sw.write(path.substring(copiedFrom, ii));
         sw.write(47);
         for(; ii + 1 < length; ii++)
         {
             char nextChar = path.charAt(ii + 1);
             if(nextChar != '/' && nextChar != '\\')
                 break;
             if(ii - 1 >= 0 && path.charAt(ii - 1) == ':')
                 sw.write(47);
         }

         copiedFrom = ii + 1;
     }

     sw.write(path.substring(copiedFrom, length));
     path = sw.getBuffer().toString();
     length = path.length();
     if(length > 1)
     {
         char lastChar = path.charAt(length - 1);
         if(lastChar == '/' || lastChar == '\\')
             path = path.substring(0, length - 1);
     }
     return path;
   }

   public static boolean inContainerIOMode()
   {
      // TODO
      return false;
   }

   public static List<String> list(String path)
   {
  
      File f = new File(path);
      if (!f.exists())
         return null;
      String files[] = f.list();
      if (files == null)
         return null;
      List<String> results = new ArrayList<String>();
      for (int i = 0; i < files.length; i++) {
         String file = files[i];
         String fileName = canonicalizePath((new StringBuilder()).append(path).append("/").append(file).toString());
         File x = new File(fileName);
         if (x.isDirectory())
            results.add((new StringBuilder()).append(fileName).append("/").toString());
         else
            results.add(fileName);
      }

      return results;
   }


   public boolean isDirectory() throws IOException
   {
      if (file != null)
         return file.isDirectory();
      if (fileObject != null)
         return fileObject.getType().hasChildren();
      else
         return filename.endsWith("/");
   }

   public static boolean isDirectory(String path)
   {
      return path.endsWith("/");
   }
}
