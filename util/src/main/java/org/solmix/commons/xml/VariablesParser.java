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


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年6月17日
 */

public class VariablesParser
{
    public interface TokenHandler{
        String handleToken(String content);
    }
    static class GenericTokenParser{
        private final String openToken;
        private final String closeToken;
        private final TokenHandler handler;

        public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
          this.openToken = openToken;
          this.closeToken = closeToken;
          this.handler = handler;
        }

        public String parse(String text) {
          StringBuilder builder = new StringBuilder();
          if (text != null && text.length() > 0) {
            char[] src = text.toCharArray();
            int offset = 0;
            int start = text.indexOf(openToken, offset);
            while (start > -1) {
              if (start > 0 && src[start - 1] == '\\') {
                // the variable is escaped. remove the backslash.
                builder.append(src, offset, start - 1).append(openToken);
                offset = start + openToken.length();
              } else {
                int end = text.indexOf(closeToken, start);
                if (end == -1) {
                  builder.append(src, offset, src.length - offset);
                  offset = src.length;
                } else {
                  builder.append(src, offset, start - offset);
                  offset = start + openToken.length();
                  String content = new String(src, offset, end - offset);
                  builder.append(handler.handleToken(content));
                  offset = end + closeToken.length();
                }
              }
              start = text.indexOf(openToken, offset);
            }
            if (offset < src.length) {
              builder.append(src, offset, src.length - offset);
            }
          }
          return builder.toString();
        }
    }
    public static String parse(String string, Map<String,Object> variables) {
        VariableTokenHandler handler = new VariableTokenHandler(variables);
        GenericTokenParser parser = new GenericTokenParser("${", "}", handler);
        return parser.parse(string);
      }

      private static class VariableTokenHandler implements TokenHandler {
        private Map<String,Object> variables;

        public VariableTokenHandler(Map<String,Object> variables) {
          this.variables = variables;
        }

        public String handleToken(String content) {
          if (variables != null && variables.containsKey(content)) {
            return variables.get(content).toString();
          }
          return "${" + content + "}";
        }
      }
}
