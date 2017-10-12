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
package org.solmix.generator.api.xml;

import org.solmix.commons.xml.dom.Document;
import org.solmix.generator.api.XmlFormatter;
import org.solmix.generator.config.DomainInfo;

/**
 * This class is the default formatter for generated XML.  This class will use the
 * built in formatting of the DOM classes directly.
 * 
 * @author Jeff Butler
 *
 */
public class DefaultXmlFormatter implements XmlFormatter {
    protected DomainInfo domain;

    @Override
    public String getFormattedContent(Document document) {
        return document.getFormattedContent();
    }

    @Override
    public void setDomain(DomainInfo domain) {
        this.domain=domain;
    }
}
