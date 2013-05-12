// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2010-9-30 18:11:00
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ISCFileItemFactory.java

package com.solmix.fmk.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import com.solmix.commons.io.IByteCounter;

// Referenced classes of package com.solmix.web.core.servlet:
//            ISCFileItem

public class SlxFileItemFactory implements FileItemFactory
{

   public SlxFileItemFactory()
   {
   }

   public SlxFileItemFactory( IByteCounter byteCounter )
   {
      this.byteCounter = byteCounter;
   }

   public FileItem createItem( String fieldName, String contentType, boolean isFormField, String fileName )
   {
      return new SlxFileItem( fieldName, contentType, isFormField, fileName, byteCounter );
   }

   IByteCounter byteCounter;
}
