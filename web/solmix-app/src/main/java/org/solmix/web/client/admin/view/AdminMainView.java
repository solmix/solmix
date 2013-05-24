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
package org.solmix.web.client.admin.view;

import org.solmix.web.client.widgets.AbstractFactory;

import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;


/**
 * 
 * @author Administrator
 * @version $Id$  2011-8-5
 */

public class AdminMainView extends VLayout
{
   public static class Factory extends AbstractFactory
   {

      private String id;

      public Canvas create()
      {
         AdminMainView pane = new AdminMainView();
         id = pane.getID();
         return pane;
      }

      public String getDescription()
      {
         return "main";
      }

      public String getID()
      {
         return id;
      }

   }
   private static final String EXAMPLE_TEXT1 = "基于OSGI框架,采用SOA设计.实现模块化、动态加载、异构环境运行、多语言开发。 ";

   private static final String EXAMPLE_TEXT2 = "用企业总线<ESB>和服务注册机制,集成DAO、事务处理、IOC、单点登录、安全控制、系统监控、事件追踪等企业和互联网典型应用环境 ";

   private static final String EXAMPLE_TEXT3 = "遵循UI 和后台分离,数据和绑定分离 原则。通过消息驱动,绑定机制解耦,服务通过绑定对RIA 提供一致的支持（AJAX-GWT、flex"
      + "、JAVAFX);UI提供公共接口或者经过NMR路由支持后台多语言,异构。";

   private Layout main;

   public AdminMainView()
   {

      this.setMargin(3);

      final Label label1 = createLabel(EXAMPLE_TEXT1);
      final Label label2 = createLabel(EXAMPLE_TEXT2);
      final Label label3 = createLabel(EXAMPLE_TEXT3);

      label1.setShowEdges(true);
      label1.setEdgeImage("edges/custom/sharpframe_10.png");
      label1.setEdgeSize(10);

      label2.setLeft(100);
      label2.setTop(80);
      label2.setEdgeImage("edges/custom/frame_10.png");
      label2.setEdgeSize(10);

      label3.setLeft(200);
      label3.setTop(160);
      label3.setEdgeImage("edges/custom/glow_15.png");
      label3.setEdgeSize(15);

      this.addChild(label1);
      this.addChild(label2);
      this.addChild(label3);

   }

   private Label createLabel(String EXAMPLE_TEXT)
   {
      Label label = new Label(EXAMPLE_TEXT);
      label.setWidth(250);
      label.setPadding(8);
      label.setBackgroundColor("#b3caec");
      label.setCanDragReposition(true);
      label.setCanDragResize(true);
      label.setDragAppearance(DragAppearance.TARGET);
      label.setShowEdges(true);
      label.setKeepInParentRect(false);
      return label;
   }
}
