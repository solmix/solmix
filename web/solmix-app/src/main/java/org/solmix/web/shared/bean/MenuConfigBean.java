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
package org.solmix.web.shared.bean;

import java.io.Serializable;


/**
 * 
 * @author solomon
 * @version $Id$  2011-5-18
 */

public class MenuConfigBean implements Serializable
{
   /**
    * 
    */
   private static final long serialVersionUID = 7639196694352516240L;

   public MenuConfigBean(){
      
   }
 public MenuConfigBean(String moduleName,String parentID,String ID,String url,String urlType,String icon,int order,String title,String viewType,String locale){
    this.moduleName=moduleName; 
    this.parentId=parentID;
      this.id=ID;
      this.url=url;
      this.urlType=urlType;
      this.icon= icon;
      this.index=order;
      this.title=title;
      this.viewType=viewType;
      this.local=locale;
   }
   private String parentId;

   private String id;
   private String moduleName;

   private String url;

   private String urlType;

   private String icon;

   private int index;


   private String title;

   private String viewType;
   
   private String local;

   
   /**
    * @return the parentId
    */
   public String getParentId()
   {
      return parentId;
   }
   
   /**
    * @param parentId the parentId to set
    */
   public void setParentId(String parentId)
   {
      this.parentId = parentId;
   }
   
   /**
    * @return the id
    */
   public String getId()
   {
      return id;
   }
   
   /**
    * @param id the id to set
    */
   public void setId(String id)
   {
      this.id = id;
   }
   
   /**
    * @return the moduleName
    */
   public String getModuleName()
   {
      return moduleName;
   }
   
   /**
    * @param moduleName the moduleName to set
    */
   public void setModuleName(String moduleName)
   {
      this.moduleName = moduleName;
   }
   
   /**
    * @return the url
    */
   public String getUrl()
   {
      return url;
   }
   
   /**
    * @param url the url to set
    */
   public void setUrl(String url)
   {
      this.url = url;
   }
   
   /**
    * @return the urlType
    */
   public String getUrlType()
   {
      return urlType;
   }
   
   /**
    * @param urlType the urlType to set
    */
   public void setUrlType(String urlType)
   {
      this.urlType = urlType;
   }
   
   /**
    * @return the icon
    */
   public String getIcon()
   {
      return icon;
   }
   
   /**
    * @param icon the icon to set
    */
   public void setIcon(String icon)
   {
      this.icon = icon;
   }
   
   /**
    * @return the index
    */
   public int getIndex()
   {
      return index;
   }
   
   /**
    * @param index the index to set
    */
   public void setIndex(int index)
   {
      this.index = index;
   }
   
   /**
    * @return the title
    */
   public String getTitle()
   {
      return title;
   }
   
   /**
    * @param title the title to set
    */
   public void setTitle(String title)
   {
      this.title = title;
   }
   
   /**
    * @return the viewType
    */
   public String getViewType()
   {
      return viewType;
   }
   
   /**
    * @param viewType the viewType to set
    */
   public void setViewType(String viewType)
   {
      this.viewType = viewType;
   }
   
   /**
    * @return the local
    */
   public String getLocal()
   {
      return local;
   }
   
   /**
    * @param local the local to set
    */
   public void setLocal(String local)
   {
      this.local = local;
   }
}
