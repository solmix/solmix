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
package org.solmix.generator.plugin;

import java.util.List;
import java.util.StringTokenizer;

import org.solmix.generator.api.IntrospectedTable;
import org.solmix.generator.api.PluginAdapter;

/**
 * This plugin can be used to specify columns that act as a primary key, even if
 * they are not strictly defined as primary keys in the database.
 * 
 * <p>To use the plugin, add a property to the table configuration specifying a
 * comma delimited list of column names to use as a primary key:
 * 
 * <p>&lt;property name="virtualKeyColumns" value="ID1,ID2"&gt;
 * 
 * @author Jeff Butler
 * 
 */
public class VirtualPrimaryKeyPlugin extends PluginAdapter {

    /* (non-Javadoc)
     * @see org.solmix.generator.api.Plugin#validate(java.util.List)
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /* (non-Javadoc)
     * @see org.solmix.generator.api.PluginAdapter#initialized(org.solmix.generator.api.IntrospectedTable)
     */
    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String virtualKey = introspectedTable.getTableInfo().getProperty("virtualKeyColumns"); 

        if (virtualKey != null) {
            StringTokenizer st = new StringTokenizer(virtualKey, ", ", false); 
            while (st.hasMoreTokens()) {
                String column = st.nextToken();
                introspectedTable.addPrimaryKeyColumn(column);
            }
        }
    }
}
