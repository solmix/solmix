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

package org.solmix.web.client.data;

import java.util.HashMap;
import java.util.Map;

import org.solmix.web.client.NameTokens;

import com.smartgwt.client.data.DataSource;

/**
 * Just Simple module datasource loading.
 * 
 * @author Administrator
 * @version $Id$ 2011-8-21
 */

public class DSRepo
{

    private static Map<String, String[]> config = new HashMap<String, String[]>();
    static {
        String[] system = {"SYSTEM:user"};
        
        String[] sandboxs = {"menuConfig","csm_stat"};
        
        String[] basics = {"csm:csm_stat"};
        String[] admin = {"user_manager"};
        config.put(NameTokens.Basic, basics);
        config.put(NameTokens.SYSTEM, system);
        config.put(NameTokens.SandBox, sandboxs);
        config.put(NameTokens.Admin, admin);
       
        
    }
    public static void loadByModuleName(String moduleName) {
        String[] datasources=null;
        for (String module : config.keySet()) {
            if (module.equals(moduleName.trim())) {
                datasources=config.get(module);
            }
        }
        if (datasources==null) {
            return;
        } else {
          
            DataSource.load(datasources, null, true);
        }
    }

    public static void loadSystem(){
        loadByModuleName(NameTokens.SYSTEM);
    }
    // public static void loadByModuleName(String moduleName,Function callback,boolean forceReload){
    // List<String> l = new ArrayList<String>();
    // for(String module:config.keySet()){
    // if(module.equals(moduleName.trim())){
    // l.add(config.get(module));
    // }
    // }
    // DataSource.load((String[])l.toArray(), callback, forceReload);
    // }
    public static void loadByName(String name) {
        DataSource.load(name, null, true);
    }

    public static void loadByNames(String... names) {
        DataSource.load(names, null, true);
    }
}
