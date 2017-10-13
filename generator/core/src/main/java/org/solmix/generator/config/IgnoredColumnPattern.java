/**
 *    Copyright 2006-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.solmix.generator.config;

import java.util.List;
import java.util.regex.Pattern;

import org.solmix.commons.util.StringUtils;
import org.solmix.commons.xml.dom.Attribute;
import org.solmix.commons.xml.dom.XmlElement;
import org.solmix.generator.util.Messages;

public class IgnoredColumnPattern {

    private String patternRegex;
    private Pattern pattern;
//    private List<IgnoredColumnException> exceptions = new ArrayList<IgnoredColumnException>();

    public IgnoredColumnPattern(String patternRegex) {
        this.patternRegex = patternRegex;
        pattern = Pattern.compile(patternRegex);
    }
/*
    public void addException(IgnoredColumnException exception) {
        exceptions.add(exception);
    }*/

    public boolean matches(String columnName) {
        boolean matches = pattern.matcher(columnName).matches();

        /*if (matches) {
            for (IgnoredColumnException exception : exceptions) {
                if (exception.matches(columnName)) {
                    matches = false;
                    break;
                }
            }
        }*/

        return matches;
    }

    public XmlElement toXmlElement() {
        XmlElement xmlElement = new XmlElement("ignoreColumnsByRegex"); 
        xmlElement.addAttribute(new Attribute("pattern", patternRegex)); 

       /* for (IgnoredColumnException exception : exceptions) {
            xmlElement.addElement(exception.toXmlElement());
        }*/

        return xmlElement;
    }

    public void validate(List<String> errors, String tableName) {
        if (!StringUtils.stringHasValue(patternRegex)) {
            errors.add(Messages.getString("ValidationError.27", 
                    tableName));
        }
    }
}
