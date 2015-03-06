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
package org.solmix.cpt.client.data;

import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * @author solomon
 * @since 0.0.1
 * @version $ID$  2011-2-23 gwt 
 */
public class ExplorTreeNode extends TreeNode
{
   public static final String iconSuffix=".png";
   public static  final String iconPrefix="menu/16/";
   public ExplorTreeNode(String name, String nodeID, String parentID, String icon,
 boolean enabled, String idsuffix, String urlMark)
   {
      if (enabled)
         setName(name);
      else
         setName("<span style='color:808080'>" + name + "</span>");
      if(nodeID!=null)
      setNodeID(nodeID.replace("-", "_") + idsuffix);
      if(parentID!=null)
      setParentNodeID(parentID.replace("-", "_") + idsuffix);
      String icon_url = getIconPrefix()+icon+getIconSuffix(); 
      setIcon(icon_url);
      setIsOpen(enabled);
      setUrlMark(urlMark);
   }

   public void setIconPrefix(String iconPrefix){
      setAttribute("iconPrefix", iconPrefix);
   }
   public String getIconPrefix(){
      String __return =getAttributeAsString("iconPrefix");
      if(__return==null||__return.equals(""))
         return iconPrefix;
      else
         return __return;
   }
   public void setIconSuffix(String iconSuffix){
      setAttribute("iconSuffix", iconSuffix);
   }
   public String getIconSuffix(){
      String __return =getAttributeAsString("iconSuffix");
      if(__return==null||__return.equals(""))
         return iconSuffix;
      else
         return __return;
   }
   public void setNodeID(String nodeID)
   {
      setAttribute("nodeID", nodeID);
   }

   public void setUrlMark(String urlMark)
   {
      setAttribute("urlMark", urlMark);
   }

   public String getUrlMark()
   {
      return getAttributeAsString("urlMark");
   }
   public String getNodeID()
   {
      return getAttributeAsString("nodeID");
   }

   public void setParentNodeID(String parentID)
   {
      setAttribute("parentNodeID", parentID);
   }

   public String getParentNodeID()
   {
      return getAttributeAsString("parentNodeID");
   }

   @Override
public void setName(String name)
   {
      setAttribute("name", name);
   }

   @Override
public String getName()
   {
      return getAttributeAsString("name");
   }

   @Override
public void setIcon(String icon)
   {
      setAttribute("icon", icon);
   }

   @Override
public String getIcon()
   {
      return getAttributeAsString("icon");
   }

   public void setIsOpen(boolean isOpen)
   {
      setAttribute("isOpen", isOpen);
   }

   public void setIconSrc(String iconSrc)
   {
      setAttribute("iconSrc", iconSrc);
   }

   public String getIconSrc()
   {
      return getAttributeAsString("iconSrc");
   }

}
