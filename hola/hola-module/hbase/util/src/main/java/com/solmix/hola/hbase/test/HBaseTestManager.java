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
package com.solmix.hola.hbase.test;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.zookeeper.MiniZooKeeperCluster;
import org.apache.hadoop.hdfs.MiniDFSCluster;

import com.solmix.hola.hbase.util.MiniMRCluster;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2013-5-23
 */

public class HBaseTestManager
{
    private static final Log LOG = LogFactory.getLog(HBaseTestManager.class);
    private Configuration conf;
    private final MiniZooKeeperCluster zkCluster = null;
    
    /**
     * The default number of regions per regionserver when creating a pre-split
     * table.
     */
    private static int DEFAULT_REGIONS_PER_SERVER = 5;

    /**
     * Set if we were passed a zkCluster.  If so, we won't shutdown zk as
     * part of general shutdown.
     */
    private final boolean passedZkCluster = false;
    private final MiniDFSCluster dfsCluster = null;

//    private final MiniHBaseCluster hbaseCluster = null;
    private final MiniMRCluster mrCluster = null;

    // Directory where we put the data for this instance of HBaseTestingUtility
    private final File dataTestDir = null;

    // Directory (usually a subdirectory of dataTestDir) used by the dfs cluster
    //  if any
    private final File clusterTestDir = null;
    
    // Lily change: add the clearData boolean
    private boolean clearData;
}
