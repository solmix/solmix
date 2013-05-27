
package com.solmix.fmk.servlet;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solmix.api.exception.SlxException;
import com.solmix.api.types.Texception;
import com.solmix.api.types.Tmodule;
import com.solmix.commons.io.IByteCounter;
import com.solmix.commons.util.DataUtil;

public class WrappedHttpServletRequest extends HttpServletRequestWrapper
{

   private static Logger log = LoggerFactory.getLogger( WrappedHttpServletRequest.class.getName() );

   HttpServletRequest request;

   boolean requestParsed;

   Map stringParams;

   Map allParams;

   Map fileParams;

   Map< String ,String > queryParams;

   boolean requestIsMultipart;

   String contentType;

   List fileItemParts;

   public WrappedHttpServletRequest( HttpServletRequest request )
   {
      super( request );
      requestParsed = false;
      fileItemParts = new ArrayList();
      this.request = request;
      contentType = request.getContentType();
      requestIsMultipart = FileUpload.isMultipartContent( request );
   }

   public Map< String ,String > getQueryParams()
   {
      if ( queryParams == null )
         try
         {
            queryParams = ServletTools.parseQueryString( request.getQueryString() );
         } catch ( Exception e )
         {
            log.error( "caught exception parsing queryParams", e );
            queryParams = new HashMap< String ,String >();
         }
      return queryParams;
   }

   public String getQueryParameter( String paramName )
   {
      return getQueryParams().get( paramName );
   }

   public Map getParams() throws SlxException
   {
      if ( allParams == null )
         allParams = DataUtil.mapUnion( getFileParams(), getStringParams() );
      return allParams;
   }

   public Map getStringParams() throws SlxException
   {
      if ( stringParams == null )
      {
         parseRequest();
         stringParams = ServletTools.paramsToMap( request );
         Iterator i = fileItemParts.iterator();
         do
         {
            if ( !i.hasNext() )
               break;
            FileItem item = (FileItem) i.next();
            if ( item.isFormField() )
            {
               String fieldName = item.getFieldName();
               String value = null;
               try
               {
                  value = item.getString( "UTF-8" );
               } catch ( UnsupportedEncodingException uee )
               {
                  log.warn( ( new StringBuilder() ).append( "Can't parse field '" ).append( fieldName ).append(
                     " using UTF-8 encoding, trying default" ).toString() );
               }
               value = item.getString();
               stringParams.put( fieldName, value );
            }
         } while ( true );
      }
      return stringParams;
   }

   public Map getFileParams() throws SlxException
   {
      return getFileParams( null );
   }

   public Map getFileParams( List errors ) throws SlxException
   {
      if ( fileParams == null )
      {
         parseRequest( errors );
         fileParams = new HashMap();
         Iterator i = fileItemParts.iterator();
         do
         {
            if ( !i.hasNext() )
               break;
            FileItem item = (FileItem) i.next();
            if ( !item.isFormField() )
               fileParams.put( item.getFieldName(), item );
         } while ( true );
      }
      return fileParams;
   }

   public HttpServletRequest getHttpServletRequest()
   {
      return request;
   }

   public boolean isMultipart()
   {
      return requestIsMultipart;
   }

   public void parseRequest() throws SlxException
   {
      parseRequest( null );
   }

   public void parseRequest( List errors ) throws SlxException
   {
      if ( !isMultipart() )
         return;
      if ( requestParsed )
      {
         if ( errors != null && errors.size() > 0 )
         {
            SlxFileItem fileItem;
            List allErrors;
            for ( Iterator i = fileItemParts.iterator(); i.hasNext(); fileItem.setErrors( allErrors ) )
            {
               fileItem = (SlxFileItem) i.next();
               allErrors = fileItem.getErrors();
               if ( allErrors == null )
                  allErrors = errors;
               else
                  allErrors.addAll( errors );
            }

         }
         return;
      } else
      {
         long contentLength = request.getContentLength();
         IByteCounter byteCounter = new SessionByteCounter( request, contentLength, request.getParameter( "formID" ), errors );
         FileUpload upload = new FileUpload( new SlxFileItemFactory( byteCounter ) );
         try
         {
            fileItemParts = upload.parseRequest( request );
         } catch ( FileUploadException e )
         {
            throw new SlxException( Tmodule.SERVLET, Texception.SERVLET_UPLOAD_FILE, "parse upload fileitem failed", e );
         }
         requestParsed = true;
         return;
      }
   }

   public SlxFileItem getUploadedFile( String fieldName ) throws Exception
   {
      return getUploadedFile( fieldName, null );
   }

   public SlxFileItem getUploadedFile( String fieldName, List errors ) throws SlxException
   {
      if ( !isMultipart() )
         return null;
      else
         return (SlxFileItem) getFileParams( errors ).get( fieldName );
   }

   @Override
   public String getParameter( String name )
   {
      Object param = null;
      try
      {
         param = getStringParams().get( name );
      } catch ( Exception e )
      {
         log.error( "getParameter()", e );
         return null;
      }
      if ( param == null )
         return request.getParameter( name );
      if ( param instanceof String )
      {
         return (String) param;
      } else
      {
         log.warn( ( new StringBuilder() ).append( "Parameter " ).append( name ).append( " is multivalued, but is being fetched as " ).append(
            "single-valued" ).toString() );
         return (String) ( (List) param ).get( 0 );
      }
   }

   @Override
   public Enumeration getParameterNames()
   {
      try
      {
         return ( new Hashtable( getStringParams() ) ).keys();
      } catch ( Exception e )
      {
         log.error( "getParameterNames()", e );
      }
      return null;
   }

   @Override
   public String[] getParameterValues( String name )
   {
      Object params = null;
      try
      {
         params = getStringParams().get( name );
      } catch ( Exception e1 )
      {
         // TODO Auto-generated catch block
         e1.printStackTrace();
      }
      if ( params == null )
         return null;
      if ( params instanceof List )
         return DataUtil.listToStringArray( (List) params );
      try
      {
         String result[] = { params.toString() };
         return result;
      } catch ( Exception e )
      {
         log.error( "getParameterValues()", e );
      }
      return null;
   }

}
