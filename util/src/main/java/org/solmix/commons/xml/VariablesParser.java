/*
 * Copyright 2014 The Solmix Project
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
package org.solmix.commons.xml;

import java.util.Map;

import org.solmix.commons.util.SystemPropertyAction;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年6月17日
 */

public class VariablesParser
{
    public static final String DEFAULT_START_TOKEN="${";
    public static final String DEFAULT_END_TOKEN="}";
    /**变量映射*/
    public static String parse(String string, Map<String,Object> variables) {
        VariableTokenHandler handler = new VariableTokenHandler(variables);
        return parse(string,handler);
      }
    public static String parse(String string, TokenHandler handler) {
        GenericTokenParser parser = new GenericTokenParser(DEFAULT_START_TOKEN, DEFAULT_END_TOKEN, handler);
        return parser.parse(string);
      }
    public static String parse(String string, TokenHandler handler,String startToken,String endToken) {
        GenericTokenParser parser = new GenericTokenParser(startToken, endToken, handler);
        return parser.parse(string);
      }
    /**首先在配置中查找,找不到通过System.getProperty*/
    public static String parseWithSystem(String string, Map<String,Object> variables) {
        VariableSystemHandler handler = new VariableSystemHandler(variables);
        GenericTokenParser parser = new GenericTokenParser(DEFAULT_START_TOKEN, DEFAULT_END_TOKEN, handler);
        return parser.parse(string);
      }

      private static class VariableTokenHandler implements TokenHandler {
        private Map<String,Object> variables;

        public VariableTokenHandler(Map<String,Object> variables) {
          this.variables = variables;
        }

        @Override
        public String handleToken(String content) {
          if (variables != null && variables.containsKey(content)) {
            return variables.get(content).toString();
          }
          return "${" + content + "}";
        }
      }
      
      private static class VariableSystemHandler implements TokenHandler {
          private Map<String,Object> variables;

          public VariableSystemHandler(Map<String,Object> variables) {
            this.variables = variables;
          }

          @Override
        public String handleToken(String content) {
            if (variables != null && variables.containsKey(content)) {
              return variables.get(content).toString();
            }else{
                String prop=SystemPropertyAction.getProperty(content);
                if(prop!=null){
                    return prop;
                }else{
                    return "${" + content + "}";
                }
            }
          }
        }
}
