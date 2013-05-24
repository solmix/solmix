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

package org.solmix.web.client.data;

import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-5-31
 */

public class MenuGridRecord extends ListGridRecord
{

   public MenuGridRecord(String name, String nodeID, String parentID, String icon, String idsuffix, String urlMark)
   {
      setName(name);
      setNodeID(nodeID.replace("-", "_") + idsuffix);
      setParentNodeID(parentID.replace("-", "_") + idsuffix);
      setIcon(icon);
      setUrlMark(urlMark);
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

   public void setName(String name)
   {
      setAttribute("name", name);
   }

   public String getName()
   {
      return getAttributeAsString("name");
   }

   public void setIcon(String icon)
   {
      setAttribute("icon", icon);
   }

   public String getIcon()
   {
      return getAttributeAsString("icon");
   }
}
