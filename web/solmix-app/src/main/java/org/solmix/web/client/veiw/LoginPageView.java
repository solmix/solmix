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
package org.solmix.web.client.veiw;

import org.solmix.web.client.presenter.LoginPagePresenter.LoginView;
import org.solmix.web.client.veiw.handlers.LoginPageUiHandlers;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;


/**
 * 
 * @author solomon
 * @version $Id$  2011-5-15
 */

public class LoginPageView extends ViewWithUiHandlers<LoginPageUiHandlers> implements LoginView
{

   private static final String DEFAULT_USER_NAME = "solomon";  

   private static String html = "<div>\n"
     + "<table align=\"center\">\n"
     + "  <tr>\n" + "<td>&nbsp;</td>\n" + "<td>&nbsp;</td>\n" + "</tr>\n"
     + "  <tr>\n" + "<td>&nbsp;</td>\n" + "<td>&nbsp;</td>\n" + "</tr>\n"
     + "  <tr>\n" + "<td>&nbsp;</td>\n" + "<td>&nbsp;</td>\n" + "</tr>\n"
     + "  <tr>\n"    
     + "    <td colspan=\"2\" style=\"font-weight:bold;\">登陆 <img src=\"images/signin.gif\"/></td>\n"
     + "  </tr>\n"
     + "  <tr>\n"
     + "    <td>用户名</td>\n"
     + "    <td id=\"userNameFieldContainer\"></td>\n"    
     + "  </tr>\n" 
     + "  <tr>\n"
     + "    <td>密码</td>\n"
     + "    <td id=\"passwordFieldContainer\"></td>\n"    
     + "  </tr>\n" 
     + "  <tr>\n"
     + "    <td></td>\n"
     + "    <td id=\"signInButtonContainer\"></td>\n"  
     + "  </tr>\n"     
     + "  <tr>\n" + "<td>&nbsp;</td>\n" + "<td>&nbsp;</td>\n" + "</tr>\n"
     + "  <tr>\n"
     + "    <td colspan=\"2\">密码忘记?</td>\n"
     + "  </tr>\n"
     + "  <tr>\n"
     + "    <td colspan=\"2\">请联系管理员.</td>\n"
     + "  </tr>\n"    
     + "</table>\n"
     + "</div>\n";

   HTMLPanel panel = new HTMLPanel(html);

//   VerticalPanel vPanel = new VerticalPanel();
   private final TextBox userNameField;
   private final PasswordTextBox passwordField;
   private final Button signInButton;

   @Inject
   public LoginPageView() {
     userNameField = new TextBox();
     passwordField = new PasswordTextBox();
     signInButton = new Button("Sign in");

     userNameField.setText(DEFAULT_USER_NAME);
     passwordField.setText("1234567890");

     panel.add(userNameField, "userNameFieldContainer");
     panel.add(passwordField, "passwordFieldContainer");
     panel.add(signInButton, "signInButtonContainer");
     
     bindCustomUiHandlers();
   }
   
   protected void bindCustomUiHandlers() {

     signInButton.addClickHandler(new ClickHandler() {
       public void onClick(ClickEvent event) {
         if (getUiHandlers() != null) {
           getUiHandlers().onOkButtonClicked();
         }
       }
     });
   }

   public Widget asWidget() {
     return panel;
   }

   public String getUserName() {
     return userNameField.getText();
   }
   
   public String getPassword() {
     return passwordField.getText();
   }  

   public void resetAndFocus() {
     userNameField.setFocus(true);
     userNameField.selectAll();
   }

}
