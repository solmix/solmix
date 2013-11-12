// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2010-9-30 18:11:00
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ISCFileItemFactory.java

package org.solmix.fmk.upload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.solmix.commons.io.IByteCounter;


public class UploadItemFactory implements FileItemFactory
{

   public UploadItemFactory()
   {
   }

   public UploadItemFactory( IByteCounter byteCounter )
   {
      this.byteCounter = byteCounter;
   }

   @Override
public FileItem createItem( String fieldName, String contentType, boolean isFormField, String fileName )
   {
      return new UploadItem( fieldName, contentType, isFormField, fileName, byteCounter );
   }

   IByteCounter byteCounter;
}
