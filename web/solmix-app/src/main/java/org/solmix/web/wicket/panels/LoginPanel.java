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

package org.solmix.web.wicket.panels;



import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;


/**
 * 
 * @author solomon
 * @version $Id$ 2011-6-12
 */
public class LoginPanel extends Panel
{

   /**
    * Sign in form.
    */
   public final class SignInForm extends StatelessForm<Void>
   {

      private static final long serialVersionUID = 1L;

      /** El-cheapo model for form. */
      private final ValueMap properties = new ValueMap();

      /**
       * Constructor.
       * 
       * @param id id of the form component
       */
      public SignInForm(final String id)
      {
         super(id);

         // Attach textfield components that edit properties map
         // in lieu of a formal beans model
         username = new TextField<String>("username", new PropertyModel<String>(properties, "username"));
         username.setType(String.class);
         add(username);
         password = new PasswordTextField("password", new PropertyModel<String>(properties, "password"));
         password.setType(String.class);
         add(password);

         // MarkupContainer row for remember me checkbox
         final WebMarkupContainer rememberMeRow = new WebMarkupContainer("rememberMeRow");
         add(rememberMeRow);

         // Add rememberMe checkbox
         CheckBox cb=new CheckBox("rememberMe", new PropertyModel<Boolean>(LoginPanel.this, "rememberMe"));
         cb.setModelValue("false");
         rememberMeRow.add(cb);
         // Show remember me checkbox?
         rememberMeRow.setVisible(includeRememberMe);
      }

      /**
       * @see org.apache.wicket.markup.html.form.Form#onSubmit()
       */
      @Override
      public final void onSubmit()
      {
         if (login(getUsername(), getPassword(), getRememberMe()))
            onSignInSucceeded();
      }
   }

   private static final long serialVersionUID = 1L;

   /** True if the panel should display a remember-me checkbox */
   private boolean includeRememberMe = false;

   /** Field for password. */
   private PasswordTextField password;

   /** True if the user should be remembered via form persistence (cookies) */
   private boolean rememberMe = true;

   /** Field for user name. */
   private TextField<String> username;

   /**
    * @see org.apache.wicket.Component#Component(String)
    */
   public LoginPanel(final String id)
   {
      this(id, true);
   }

   /**
    * @param id See Component constructor
    * @param includeRememberMe True if form should include a remember-me checkbox
    * @see org.apache.wicket.Component#Component(String)
    */
   public LoginPanel(final String id, final boolean includeRememberMe)
   {
      super(id);

      this.includeRememberMe = includeRememberMe;
      if (!includeRememberMe)
         rememberMe = false;

      // Create feedback panel and add to page
      add(new FeedbackPanel("feedback"));

      // Add sign-in form to page, passing feedback panel as
      // validation error handler
      add(new SignInForm("signInForm"));
   }

   /**
    * Convenience method to access the password.
    * 
    * @return The password
    */
   public String getPassword()
   {
      return password.getInput();
   }

   /**
    * Get model object of the rememberMe checkbox
    * 
    * @return True if user should be remembered in the future
    */
   public boolean getRememberMe()
   {
      return rememberMe;
   }

   /**
    * Convenience method to access the username.
    * 
    * @return The user name
    */
   public String getUsername()
   {
      return username.getDefaultModelObjectAsString();
   }

   /**
    * Sign in user if possible.
    * 
    * @param username The username
    * @param password The password
    * @return True if signin was successful
    */
   public boolean login(final String username, final String password, final boolean rememberMe)
   {
       final Subject currentUser = SecurityUtils.getSubject();
       final UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
       try
       {
       currentUser.login(token);
       return true;
      
       // the following exceptions are just a few you can catch and handle accordingly. See the
       // AuthenticationException JavaDoc and its subclasses for more.
       }
       catch (final IncorrectCredentialsException ice)
       {
       error("Password is incorrect.");
       }
       catch (final UnknownAccountException uae)
       {
       error("There is no account with that username.");
       }
       catch (final AuthenticationException ae)
       {
       error("Invalid username and/or password.");
       }
       catch (final Exception ex)
       {
       error("Login failed");
       }
       return false;
   }

   protected void onSignInSucceeded()
   {
      // If login has been called because the user was not yet
      // logged in, than continue to the original destination,
      // otherwise to the Home page
      String root = this.getRequest().getRelativePathPrefixToContextRoot();
      if (!continueToOriginalDestination())
         setResponsePage(new RedirectPage(root + "solmix.html"));
   }

   /**
    * Set model object for rememberMe checkbox
    * 
    * @param rememberMe
    */
   public void setRememberMe(final boolean rememberMe)
   {
      this.rememberMe = rememberMe;
   }
}
