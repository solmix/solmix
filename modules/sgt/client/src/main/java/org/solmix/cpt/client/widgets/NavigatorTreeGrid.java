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
package org.solmix.cpt.client.widgets;
import org.solmix.cpt.client.data.ExplorTreeNode;

import com.smartgwt.client.types.SortArrow;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;


/**
 * 
 * @author solomon
 * @version $Id$  2011-5-17
 */

public class NavigatorTreeGrid extends TreeGrid
{
   private boolean initialed;
   String idSuffix = "";
  public  NavigatorTreeGrid(){
     setWidth100();
     setHeight100();
     setCustomIconProperty("icon");
     setAnimateFolderTime(100);
     setAnimateFolders(true);
     setAnimateFolderSpeed(1000);
     setShowSortArrow(SortArrow.CORNER);
     setShowAllRecords(true);
     setLoadDataOnDemand(true);
     setCanSort(false);
     setLeaveScrollbarGap(false);
     setCanAutoFitFields(false);
//     this.setTitle("xxxxxxxxxxxxxxxxx");
     

     TreeGridField field = new TreeGridField();
     field.setCanFilter(true);
     field.setName("name");
     field.setTitle("<b>SandBox</b>");
     setFields(field);

     Tree tree = new Tree();
     tree.setModelType(TreeModelType.PARENT);
     tree.setNameProperty("name");
     tree.setOpenProperty("isOpen");
     tree.setIdField("nodeID");
     tree.setParentIdField("parentNodeID");
     tree.setRootValue("root" + idSuffix);
//     tree.setData(treeNodes);
//     setData(tree);
   }
  public boolean isInitialed(){
     Boolean flag=getAttributeAsBoolean("initialed");
     return flag==Boolean.TRUE?true:false;
  }
  public void setInitialed(boolean initialed){
     this.setAttribute("initialed", initialed, true);
  }
  public void selectItem(String name){
   ListGridRecord[] records=this.getRecords();
   ListGridRecord record = null;
   String recordUrl = "";
   for(int i = 0; i < records.length; i++){
      
      record = this.getRecord(i);
      recordUrl = ((ExplorTreeNode)record).getUrlMark();

      if (name.equals(recordUrl)) {
//        Log.debug("name.contentEquals(recordName)");
        this.deselectAllRecords();
        this.selectRecord(i);
        break;
      }
   }
  }
  public    ExplorTreeNode getNode(String url){
      Tree tree =this.getTree();
      TreeNode[] nodes =tree.getAllNodes();
      for(int i=0 ;i<nodes.length;i++){
         ExplorTreeNode node=(ExplorTreeNode)nodes[i];
         if(url.equals(node.getUrlMark())){
            return node;
         }
      }
      return null;
     }
 /* public void initGrid(MenuConfigResult result){
    
     Tree tree = new Tree();
     tree.setModelType(TreeModelType.PARENT);
     tree.setNameProperty("name");
     tree.setOpenProperty("isOpen");
     tree.setIdField("nodeID");
     tree.setParentIdField("parentNodeID");
     tree.setRootValue("root");
     List<MenuConfigBean> menus = result.getResults();
     ExplorTreeNode[] treeNodes = new ExplorTreeNode[menus.size()];
     int count = 0;
     for (MenuConfigBean i : menus)
     {
        if(i.getViewType()==EviewType.MAIN.value())
           continue;
        treeNodes[count] = new ExplorTreeNode(i.getTitle(), i.getId(), i.getParentId(), i.getIcon(), true, "",  i.getUrl());
        count++;
     }
     tree.setData(treeNodes);
     this.setData(tree);
  }*/
  
}
