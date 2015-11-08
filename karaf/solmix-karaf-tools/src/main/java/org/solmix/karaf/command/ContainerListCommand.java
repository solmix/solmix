/*
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

import java.util.ArrayList;
import java.util.List;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.support.table.ShellTable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年11月4日
 */
@Command(scope = "rt", name = "list-container", description = "Displays a list of all created Solmix Containers.")
@Service
public class ContainerListCommand implements Action
{

    private static final Logger LOG  = LoggerFactory.getLogger(ContainerListCommand.class);
    @Reference
    private BundleContext context;
    
    @Override
    public Object execute() throws Exception {

        ShellTable table = new ShellTable();
        table.column("Name");
        table.column("Status");
        table.column("Production");
        table.emptyTableText("No created  container right now!");
        for(Container c :getContainers()){
            if(c!=null){
                String status = c.getStatus().toString();
                table.addRow().addContent(c.getId(),status,c.isProduction());
            }
        }
        ContainerFactory.getContainers();
        table.print(System.out, true);
        return null;
    }
    
    private List<Container> getContainers(){
        List<Container> cs = new ArrayList<Container>();
        try {
            ServiceReference<?>[] references = context.getServiceReferences(Container.class.getName(), null);
            if (references != null) {
                for (ServiceReference<?> reference : references) {
                    if (reference != null) {
                        Container bus = (Container) context.getService(reference);
                        if (bus != null) {
                            cs.add(bus);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.info("Cannot retrieve the list of CXF Busses.", e);
        }
        return cs;
    }

}
