/*
 * SOLMIX PROJECT
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

package org.solmix.fmk.internal;

import java.io.IOException;

import org.solmix.api.cm.ConfigureUnit;
import org.solmix.api.cm.ConfigureUnitManager;
import org.solmix.api.context.SystemContext;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.fmk.SlxContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-11-6
 */

public class DatasourceCM
{

    public static final String PID = "org.solmix.framework.datasource";

    public static final String P_DEFAULT_PARSER = "defaultParser";

    public static final String P_MAX_UPLOAD_FILESIZE = "maxUploadFilesize";

    public static final String P_AUTO_JOIN_TRANSACTIONS = "autoJoinTransactions";

    public static final String P_VELOCITY_TEMPLATE_DIR = "velocityTemplateDir";

    public static final String P_SLX_VERSION_NUMBER = "slxVersionNumber";

    public static final String P_CLIENT_VERSION_NUMBER = "clientVersionNumber";

    public static final String P_SHOW_CLIENT_OUTPUT = "showClientOutput";

    public static final String SLX_VERSION_NUMBER = "0.4-SNAPHOST";

    public static String DEFAULT_PARSER = "default";

    public static String CLIENT_VERSION_NUMBER = "0.4-SNAPHOST";

    public static String FRAMEWORK_VERSION = "0.4";

    public static ConfigureUnit getConfigureUnit() {
        SystemContext sc = SlxContext.getThreadSystemContext();
        ConfigureUnitManager cm = sc.getBean(ConfigureUnitManager.class);
        try {
            return cm.getConfigureUnit(PID);
        } catch (IOException e) {
        }
        return null;
    }

    public static DataTypeMap getProperties() {

        return getConfigureUnit() == null ? new DataTypeMap() : getConfigureUnit().getProperties();
    }
}
