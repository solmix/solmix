/**
 * Copyright 2015 The Solmix Project
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
package org.solmix.karaf.command;

import java.util.List;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Command;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.api.console.SessionFactory;
import org.apache.karaf.shell.support.table.ShellTable;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.ContainerFactory;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2016年4月27日
 */
@org.apache.karaf.shell.api.action.Command(scope = "ha", name = "cmd", description = "Displays a list of all created Solmix Containers.")
@Service
public class CommandProxyAction implements Action
{

    private static final Logger LOG  = LoggerFactory.getLogger(ContainerListCommand.class);
    @Reference
    private BundleContext context;
    @Reference
    private SessionFactory sessionFactory;
    @Reference
    private Session session;
    
    @Override
    public Object execute() throws Exception {
        List<Command> cds  =sessionFactory.getRegistry().getCommands();
        ShellTable table = new ShellTable();
        table.column("Name");
        table.column("Status");
        table.column("Production");
        table.emptyTableText("No created  container right now!");
        for(Command c :cds){
            if(c!=null){
                table.addRow().addContent(c.getScope(),c.getName(),c.getDescription());
            }
        }
        ContainerFactory.getContainers();
        table.print(System.out, true);
        session.execute("list -t 1");
        return null;
    }

}
