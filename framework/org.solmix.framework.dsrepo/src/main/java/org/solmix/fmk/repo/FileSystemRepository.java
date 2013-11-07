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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.exception.SlxException;
import org.solmix.api.types.Texception;
import org.solmix.api.types.Tmodule;
import org.solmix.commons.io.SlxFile;
import org.solmix.commons.util.DataUtil;

/**
 * 
 * @version 110035
 */
public class FileSystemRepository extends AbstractDSRepository
{

    private String location;

    private String sysLocation;

    public void init() {
        name = "Default File System ds repository";
    }

    public void close() {

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

    /**
     * @return the sysLocation
     */
    public String getSysLocation() {
        return sysLocation;
    }

    /**
     * @param sysLocation the sysLocation to set
     */
    public void setSysLocation(String sysLocation) {
        this.sysLocation = sysLocation;
    }

    private final static Logger log=LoggerFactory.getLogger(FileSystemRepository.class.getName());

    public FileSystemRepository()
    {
    }

    /**
     * 检查指定路径的文件是否存在，如果存在返回文件路径。
     * 
     * @param baseName 文件路径
     * @return
     * @throws IOException
     */
    public SlxFile XMLOrJSFile(String baseDir,/* String group, */String dsName) throws SlxException {
        if (DataUtil.isNullOrEmpty(baseDir))
            return null;
        String absolutePath = baseDir.endsWith("/") ? baseDir : baseDir + "/";
        absolutePath = DataUtil.isNullOrEmpty(dsName) ? absolutePath : absolutePath + "/" + dsName;

        String xmlName = (new StringBuilder()).append(absolutePath).append(".xml").toString();
        String jsName = (new StringBuilder()).append(absolutePath).append(".js").toString();
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
     * @see org.solmix.api.repo.IDSRepo#getDSLocation()
     */
    @Override
    public String[] getLocations() {
        List<String> _tmp = new ArrayList<String>();
        List<String> locations = DataUtil.commaSeparatedStringToList(location);
        locations.addAll(DataUtil.commaSeparatedStringToList(sysLocation));
        for (String str : locations) {
            _tmp.add("file:" + str);
        }
        return DataUtil.listToStringArray(_tmp);

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.repo.IDSRepo#loadSystem(java.lang.String)
     */
    @Override
    public Object loadSystem(String name) throws SlxException {
        if (DataUtil.isNullOrEmpty(sysLocation)) {
            log.warn("Not set system-datasource file location");
            return null;
        }
        SlxFile dsFile;
        List<String> sysdsLocations = DataUtil.commaSeparatedStringToList(sysLocation);
        for (String sysdsLocation : sysdsLocations) {
            dsFile = XMLOrJSFile(sysdsLocation, name);
            if (dsFile != null) {
                log.debug((new StringBuilder()).append("load dsConfigFile sucessed File: ").append(name).append(" File Location").append(
                    sysdsLocation).toString());
                return dsFile;
            }
        }
        log.warn((new StringBuilder()).append("File ").append(name).append(" not found at explicitly specified location ").append(sysLocation).append(
            ", checking system ds repo area").toString());
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.repo.IDSRepo#loadSystemDS(java.lang.String)
     */
    @Override
    public DataSource loadSystemDS(String ds) throws SlxException {
        Object obj = this.loadSystem(ds);

        return obj instanceof DataSource ? (DataSource) obj : null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.repo.IDSRepo#load(java.lang.String, java.lang.String)
     */
    @Override
    public Object load(/* String group, */String dsName) throws SlxException {
        SlxFile dsFile = null;
        if (DataUtil.isNullOrEmpty(location)) {
            log.warn("Not set datasource file location ,used the system-datasource file location as default");
            return /* DataUtil.isNullOrEmpty(group) ? */this.loadSystem(dsName);
        } else {
            List<String> dsLocations = DataUtil.commaSeparatedStringToList(location);
            // List<String> sysdsLocations = DataUtil.commaSeparatedStringToList(sysLocation);
            for (String dsLocation : dsLocations) {
                dsFile = XMLOrJSFile(dsLocation, /* group, */dsName);
                if (dsFile != null) {
                    if (log.isDebugEnabled())
                        log.debug((new StringBuilder()).append("load dsConfigFile sucessed File: [").append(dsName).append("] File Location: ").append(
                            dsLocation).toString());
                    return dsFile;
                }
            }
            if (dsFile == null)
                log.warn((new StringBuilder()).append("File ").append(dsName).append(" not found at explicitly specified location ").append(
                    location).toString());
            // find in system repo
            log.warn("try to found in system ds repo");
            return this.loadSystem(dsName);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.api.repo.IDSRepo#loadDS(java.lang.String)
     */
    @Override
    public DataSource loadDS(String ds) throws SlxException {
       
            throw new SlxException(Tmodule.REPO, Texception.NO_SUPPORT, ("The file system ds repository not support the method ,please call load(name); '"));
    }

}
