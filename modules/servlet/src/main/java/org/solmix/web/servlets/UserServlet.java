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
package org.solmix.web.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;


/**
 * 
 * @author solomon
 * @version 110035  2011-6-6
 */

public class UserServlet extends BaseServlet
{
   /**
    * 
    */
   private static final long serialVersionUID = 8332304407206809487L;

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      Subject subject =SecurityUtils.getSubject();
      if(subject!=null){
         String name = request.getParameter("username");
       String paswd  = request.getParameter("password");
       UsernamePasswordToken token = new UsernamePasswordToken(name,paswd);
//       RequestDispatcher dispatch =request.getSession().getServletContext().getRequestDispatcher("/Solmix.html");
//       dispatch.forward(request, response);  
//          try
//         {
//            subject.login(token);
//         } catch (AuthenticationException e)
//         {
//            e.printStackTrace();
//         }
      }
   
      System.out.println("why manager is null fuck.");
   }
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      System.out.println(request.getServletPath());
//      RequestDispatcher dispatch =request.getSession().getServletContext().getRequestDispatcher("/home.jsp");
//      dispatch.forward(request, response);   
   }
}
