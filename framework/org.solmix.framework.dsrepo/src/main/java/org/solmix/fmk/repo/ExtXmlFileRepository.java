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

package org.solmix.fmk.repo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.cm.ConfigureUnitManager;
import org.solmix.api.context.SystemContext;
import org.solmix.api.exception.SlxException;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.collections.DataTypeMap;
import org.solmix.commons.io.SlxFile;
import org.solmix.commons.util.DataUtil;

/**
 * 
 * @version 110035
 */
public class ExtXmlFileRepository extends AbstractDSRepository
{

    public static final String DEFAULT_LOCAL = "datasource";

    private String location;

    public void init() {
        name = "Default File System ds repository";
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    private final static Logger log = LoggerFactory.getLogger(ExtXmlFileRepository.class.getName());

    public ExtXmlFileRepository()
    {
        this(null);
    }

    /**
     * @param sc
     */
    public ExtXmlFileRepository(SystemContext sc)
    {
        super(EXT_FILE, ObjectType.SLX_FILE,ObjectFormat.XML);
        if (sc != null) {
            ConfigureUnitManager cm = sc.getBean(ConfigureUnitManager.class);
            try {
                DataTypeMap config = cm.getConfigureUnit(DSRepositoryManagerImpl.PID).getProperties();
                location = config.getString("repo.ext.location");
            } catch (IOException e) {
            }
        }
    }

    /**
     * 检查指定路径的文件是否存在，如果存在返回文件路径。
     * 
     * @param baseName 文件路径
     * @return
     * @throws IOException
     */
    public SlxFile XMLOrJSFile(String baseDir, String dsName) throws SlxException {
        if (DataUtil.isNullOrEmpty(baseDir))
            return null;
        String absolutePath = baseDir.endsWith("/") ? baseDir : baseDir + "/";
        absolutePath = DataUtil.isNullOrEmpty(dsName) ? absolutePath : absolutePath + "/" + dsName;

        String xmlName = (new StringBuilder()).append(absolutePath).append(".xml").toString();
        try {
            SlxFile slxFile = new SlxFile(xmlName);
            if (slxFile.exists()) {
                return slxFile;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new SlxException(Tmodule.REPO, Texception.DS_DSFILE_NOT_FOUND, "can not load " + xmlName + "file.");
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.repo.IDSRepo#load(java.lang.String)
     */
    @Override
    public Object load(String dsName) throws SlxException {
        SlxFile dsFile = null;
        List<String> dsLocations = new ArrayList<String>();
        if (DataUtil.isNullOrEmpty(location)) {
            String base = System.getProperty("solmix.base");
            if (base == null) {
                base = System.getProperty("karaf.base");
            }
            String defaultLocation = (base.endsWith("/") ? base : base + "/") + DEFAULT_LOCAL;

            if (log.isTraceEnabled())
                log.trace("None setting extension configuration file location .used default location:" + defaultLocation);
            log.warn("Not set datasource file location ,used the system-datasource file location as default");
            Collections.addAll(dsLocations, defaultLocation);
        } else {
            dsLocations = DataUtil.commaSeparatedStringToList(location);
        }
        for (String dsLocation : dsLocations) {
            dsFile = XMLOrJSFile(dsLocation,dsName);
            if (dsFile != null) {
                if (log.isTraceEnabled())
                    log.trace((new StringBuilder()).append("load dsConfigFile sucessed File: [").append(dsName).append("] File Location: ").append(
                        dsLocation).toString());
                return dsFile;
            }
        }
        if (log.isTraceEnabled())
            log.trace((new StringBuilder()).append("File ").append(dsName).append(" not found at explicitly specified location ").append(location).toString());
        return null;
    }
}
