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

import java.util.ArrayList;
import java.util.List;

import org.solmix.web.client.admin.presenter.AdminPresenter.MyView;
import org.solmix.web.client.admin.view.handler.AdminUiHandler;
import org.solmix.web.client.data.MenuGridRecord;
import org.solmix.web.client.widgets.AdminNavigator;
import org.solmix.web.client.widgets.NavigatorSection;
import org.solmix.web.shared.bean.MenuConfigBean;
import org.solmix.web.shared.result.MenuConfigResult;
import org.solmix.web.shared.types.EviewType;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickEvent;
import com.smartgwt.client.widgets.layout.events.SectionHeaderClickHandler;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * 
 * @author solomon
 * @version $Id$ 2011-5-31
 */

public class AdminView extends ViewWithUiHandlers<AdminUiHandler> implements MyView
{

   private static final String NAV_WIDTH = "20%";

   private static final String CONTENT_WIDTH = "80%";

   private AdminNavigator navigator;

   private final TabSet mainTabSet;

   private final HLayout mainWidget;

   private boolean initialed = false;

   @Inject
   public AdminView(AdminNavigator navigator)
   {
      this.navigator = navigator;
      this.mainTabSet = new TabSet();
      this.mainWidget = new HLayout();
      bindCustomUiHandlers();
   }

   protected void bindCustomUiHandlers()
   {
      initialTabSet();

      initNavigator();
      VLayout _westWapper = new VLayout();
      _westWapper.setWidth(NAV_WIDTH);
      _westWapper.addMember(navigator);
      _westWapper.setShowResizeBar(true);
      mainWidget.addMember(_westWapper);

      VLayout _eastWapper = new VLayout();
      _eastWapper.addMember(mainTabSet);
      _eastWapper.setWidth(CONTENT_WIDTH);

      mainWidget.addMember(_eastWapper);

   }

   /**
    * 
    */
   private void initNavigator()
   {
      navigator.addSectionHeaderClickHandler(new SectionHeaderClickHandler() {

         @Override
         public void onSectionHeaderClick(SectionHeaderClickEvent event)
         {
            SectionStackSection section = event.getSection();
            ListGridRecord record = ((NavigatorSection) section).getSelectedRecord();
            if (record == null)
               return;
            String url = record.getAttributeAsString("urlMark");
            // If there is no selected record (e.g. the data hasn't finished loading)
            // then getSelectedRecord() will return an empty string.
//            if (url == null || url.isEmpty())
//            {
//               SC.say("Menu data is null or is loading,please wait!");
//            }
//
//            if (getUiHandlers() != null && url != null)
//            {
//               getUiHandlers().onHeaderClick(url);
//            }
         }
      });

   }

   protected void initialTabSet()
   {
      mainTabSet.setWidth100();
      mainTabSet.setHeight100();
      Layout paneContainerProperties = new Layout();
      paneContainerProperties.setLayoutMargin(0);
      paneContainerProperties.setLayoutTopMargin(1);
      mainTabSet.setPaneContainerProperties(paneContainerProperties);
      mainTabSet.setBackgroundColor("#b3caec");
   }

   /**
    * {@inheritDoc}
    * 
    * @see com.gwtplatform.mvp.client.View#asWidget()
    */
   @Override
   public Widget asWidget()
   {
      return mainWidget;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.web.client.admin.presenter.AdminPresenter.MyView#initSectionItem(org.solmix.web.shared.result.MenuConfigResult)
    */
   @Override
   public void initSectionItem(MenuConfigResult result)
   {
      if (initialed)
         return;
      List<Object[]> source = process(result);
      if (source == null)
         return;
      for (int i = 0; i < source.size(); i++)
      {
         Object[] data = source.get(i);
         if (data == null)
            continue;
         MenuConfigBean parent = (MenuConfigBean) data[0];
         List<MenuConfigBean> childrens = (List<MenuConfigBean>) data[1];
         String name = parent.getTitle();
         MenuGridRecord[] records = getRecords(childrens);
         navigator.addSection(name, records);
         navigator.addRecordClickHandler(name, new RecordClickHandler() {

            @Override
            public void onRecordClick(RecordClickEvent event)
            {
               Record record = event.getRecord();
               String place = record.getAttributeAsString("urlMark");

               if (getUiHandlers() != null && place != null)
               {
                  getUiHandlers().onItemClick(place);
               }
            }
         });
      }
      initialed = true;
   }

   private MenuGridRecord[] getRecords(List<MenuConfigBean> beans)
   {
      MenuGridRecord[] records = new MenuGridRecord[beans.size()];
      for (int i = 0; i < beans.size(); i++)
      {
         MenuConfigBean bean = beans.get(i);
         records[i] = new MenuGridRecord(bean.getTitle(), bean.getId(), bean.getParentId(), bean.getIcon(), "", bean.getUrl());
      }
      return records;
   }

   @SuppressWarnings("unchecked")
   private List<Object[]> process(MenuConfigResult result)
   {
      List<MenuConfigBean> data = result.getResults();
      List<Object[]> _return = new ArrayList<Object[]>();
      if (data == null)
         return null;
      int max = 0;
      for (MenuConfigBean bean : data)
      {
         if (EviewType.SECTION_HEADER.toString().equals(bean.getViewType()))
         {
            Object[] config = new Object[2];
            config[0] = bean;
            config[1] = new ArrayList<MenuConfigBean>();
            _return.add(config);
         }
      }

      for (MenuConfigBean bean : data)
      {
         String parent_id = bean.getParentId();
         if (parent_id == null || "".equals(parent_id))
            continue;
         for (Object[] sec : _return)
         {
            MenuConfigBean parent = (MenuConfigBean) sec[0];
            String id = parent.getId();
            if (id == null || "".equals(id))
               continue;
            if (parent_id.equals(id))
            {
               ((List<MenuConfigBean>) sec[1]).add(bean);
            }

         }
      }
      return _return;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.web.client.admin.presenter.AdminPresenter.MyView#getMainTabSet()
    */
   @Override
   public TabSet getMainTabSet()
   {
      return this.mainTabSet;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.web.client.admin.presenter.AdminPresenter.MyView#getNavigator()
    */
   @Override
   public AdminNavigator getNavigator()
   {
      return this.navigator;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.solmix.web.client.admin.presenter.AdminPresenter.MyView#selectItem(java.lang.String)
    */
   @Override
   public void selectItem(String name)
   {
      // TODO Auto-generated method stub

   }

}
