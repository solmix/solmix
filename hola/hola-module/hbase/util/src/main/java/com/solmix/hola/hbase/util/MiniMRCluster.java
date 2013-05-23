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

package com.solmix.hola.hbase.util;

import java.io.File;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobTracker;
import org.apache.hadoop.mapred.TaskTracker;
import org.apache.hadoop.net.DNSToSwitchMapping;
import org.apache.hadoop.net.NetworkTopology;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-5-23
 */

public class MiniMRCluster
{

    private static final Logger LOG = LoggerFactory.getLogger(MiniMRCluster.class);

    private final int jobTrackerPort;

    private final int taskTrackerPort;

    private final int jobTrackerInfoPort;

    private final int numTaskTrackers;

    private final String namenode;

    private final UserGroupInformation ugi;

    private final JobConf conf;

    private final int numTrackerToExclude;

    public MiniMRCluster(int numTaskTrackers, String namenode, int numDir) throws IOException
    {
        this(0, 0, numTaskTrackers, namenode, numDir);
    }

    public MiniMRCluster(int jobTrackerPort, int taskTrackerPort, int numTaskTrackers, String namenode, int numDir) throws IOException
    {
        this(jobTrackerPort, taskTrackerPort, numTaskTrackers, namenode, numDir, null);
    }

    public MiniMRCluster(int jobTrackerPort, int taskTrackerPort, int numTaskTrackers, String namenode, int numDir, String[] racks)
        throws IOException
    {
        this(jobTrackerPort, taskTrackerPort, numTaskTrackers, namenode, numDir, racks, null);
    }

    public MiniMRCluster(int jobTrackerPort, int taskTrackerPort, int numTaskTrackers, String namenode, int numDir, String[] racks, String[] hosts)
        throws IOException
    {
        this(jobTrackerPort, taskTrackerPort, numTaskTrackers, namenode, numDir, racks, hosts, null);
    }

    public MiniMRCluster(int jobTrackerPort, int taskTrackerPort, int numTaskTrackers, String namenode, int numDir, String[] racks, String[] hosts,
        UserGroupInformation ugi) throws IOException
    {
        this(jobTrackerPort, taskTrackerPort, numTaskTrackers, namenode, numDir, racks, hosts, ugi, null);
    }

    public MiniMRCluster(int jobTrackerPort, int taskTrackerPort, int numTaskTrackers, String namenode, int numDir, String[] racks, String[] hosts,
        UserGroupInformation ugi, JobConf conf) throws IOException
    {
        this(jobTrackerPort, taskTrackerPort, numTaskTrackers, namenode, numDir, racks, hosts, ugi, conf, 0);
    }

    /**
     * @param jobTrackerPort
     * @param taskTrackerPort
     * @param numTaskTrackers
     * @param namenode
     * @param numDir
     * @param racks
     * @param hosts
     * @param ugi
     * @param conf
     * @param i
     */
    public MiniMRCluster(int jobTrackerPort, int taskTrackerPort, int numTaskTrackers, String namenode, int numDir, String[] racks, String[] hosts,
        UserGroupInformation ugi, JobConf conf, int numTrackerToExclude) throws IOException
    {
        if (racks != null && racks.length < numTaskTrackers) {
            LOG.error("Invalid number of racks specified. It should be at least  equal to the number of tasktrackers");
            shutdown();
        }
        if (hosts != null && numTaskTrackers > hosts.length) {
            throw new IllegalArgumentException(new StringBuilder().append("The length of hosts [").append(hosts.length).append(
                "] is less than the number of tasktrackers [").append(numTaskTrackers).append("].").toString());
        }
        // Generate rack names if required
        if (racks == null) {
            System.out.println("Generating rack names for tasktrackers");
            racks = new String[numTaskTrackers];
            for (int i = 0; i < racks.length; ++i) {
                racks[i] = NetworkTopology.DEFAULT_RACK;
            }
        }
        // Generate some hostnames if required
        if (hosts == null) {
            System.out.println("Generating host names for tasktrackers");
            hosts = new String[numTaskTrackers];
            for (int i = 0; i < numTaskTrackers; i++) {
                hosts[i] = "host" + i + ".hola.com";
            }
        }
        this.jobTrackerPort = jobTrackerPort;
        this.taskTrackerPort = taskTrackerPort;
        this.jobTrackerInfoPort = 0;
        this.numTaskTrackers = 0;
        this.namenode = namenode;
        this.ugi = ugi;
        this.conf = conf;
        this.numTrackerToExclude = numTrackerToExclude;

        // start the jobtracker
        startJobTracker();
    }

    /**
     * 
     */
    public void startJobTracker() {
        startJobTracker(true);

    }

    /**
     * @param b
     */
    void startJobTracker(boolean b) {
        // TODO Auto-generated method stub

    }

    /**
     * 
     */
    public void shutdown() {
        // TODO Auto-generated method stub

    }

    public JobConf createJobConf() {
        return createJobConf(new JobConf());
    }

    public JobConf createJobConf(JobConf conf) {
        if (conf == null) {
            conf = new JobConf();
        }
        return configureJobConf(conf, namenode, jobTrackerPort, jobTrackerInfoPort, ugi);
    }

    static JobConf configureJobConf(JobConf conf, String namenode, int jobTrackerPort, int jobTrackerInfoPort, UserGroupInformation ugi) {
        JobConf result = new JobConf(conf);
        FileSystem.setDefaultUri(result, namenode);
        result.set("mapred.job.tracker", "localhost:" + jobTrackerPort);
        result.set("mapred.job.tracker.http.address", "127.0.0.1:" + jobTrackerInfoPort);
        // for debugging have all task output sent to the test output
        JobClient.setTaskOutputFilter(result, JobClient.TaskStatusFilter.ALL);
        return result;
    }

    public class JobTrackerExecutor implements Runnable
    {

        private final JobTracker tracker = null;

        private volatile boolean isActive = true;

        JobConf jc = null;

        public JobTrackerExecutor(JobConf conf)
        {
            jc = conf;
        }

        public boolean isUp() {
            return (tracker != null);
        }

        public boolean isActive() {
            return isActive;
        }

        public int getJobTrackerPort() {
            return tracker.getTrackerPort();
        }

        public int getJobTrackerInfoPort() {
            return tracker.getInfoPort();
        }

        public JobTracker getJobTracker() {
            return tracker;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            try {
                jc = (jc == null) ? createJobConf() : createJobConf(jc);

                jc.setClass("topology.node.switch.mapping.impl", StaticMapping.class, DNSToSwitchMapping.class);
                final String id = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                if (ugi == null) {
                    ugi = UserGroupInformation.getCurrentUser();
                }
                tracker = ugi.doAs(new PrivilegedExceptionAction<JobTracker>() {

                    @Override
                    public JobTracker run() throws InterruptedException, IOException {
                        return JobTracker.startTracker(jc, id);
                    }
                });
                tracker.offerService();
            } catch (Throwable e) {
                LOG.error("Job tracker crashed", e);
                isActive = false;
            }

        }

        public void shutdown() {
            try {
                if (tracker != null) {
                    tracker.stopTracker();
                }
            } catch (Throwable e) {
                LOG.error("Problem shutting down job tracker", e);
            }
            isActive = false;
        }
    }

    public class TaskTrackerExecutor implements Runnable
    {

        volatile TaskTracker taskTracker;

        int trackerId;

        // the localDirs for this taskTracker
        String[] localDirs;

        volatile boolean isInitialized = false;

        volatile boolean isDead = false;

        volatile boolean exited = false;

        int numDir;

        TaskTrackerExecutor(int trackerId, int numDir, String hostname, JobConf cfg) throws IOException
        {
            this.trackerId = trackerId;
            this.numDir = numDir;
            localDirs = new String[numDir];
            final JobConf conf;
            if (cfg == null) {
                conf = createJobConf();
            } else {
                conf = createJobConf(cfg);
            }
            if (hostname != null) {
                conf.set("dfs.datanode.hostname", hostname);
            }
            conf.set("mapred.task.tracker.http.address", "0.0.0.0:0");
            conf.set("mapred.task.tracker.report.address", "127.0.0.1:" + taskTrackerPort);
            File localDirBase = new File(conf.get("mapred.local.dir")).getAbsoluteFile();
            localDirBase.mkdirs();
            StringBuilder localPath = new StringBuilder();
            for (int i = 0; i < numDir; ++i) {
                File ttDir = new File(localDirBase, Integer.toString(trackerId) + "_" + i);
                if (!ttDir.mkdirs()) {
                    if (!ttDir.isDirectory()) {
                        throw new IOException("Mkdirs failed to create " + ttDir);
                    }
                }
                localDirs[i] = ttDir.toString();
                if (i != 0) {
                    localPath.append(",");
                }
                localPath.append(localDirs[i]);
            }
            conf.set("mapred.local.dir", localPath.toString());
            LOG.info("mapred.local.dir is " + localPath);
            try {
                taskTracker = ugi.doAs(new PrivilegedExceptionAction<TaskTracker>() {

                    @Override
                    public TaskTracker run() throws InterruptedException, IOException {
                        return createTaskTracker(conf);
                    }
                });

                isInitialized = true;
            } catch (Throwable e) {
                isDead = true;
                taskTracker = null;
                LOG.error("task tracker " + trackerId + " crashed", e);
            }
        }

        /**
         * Creates a default {@link TaskTracker} using the conf passed.
         */
        TaskTracker createTaskTracker(JobConf conf) throws IOException, InterruptedException {
            return new TaskTracker(conf);
        }

        /**
         * Create and run the task tracker.
         */
        @Override
        public void run() {
            try {
                if (taskTracker != null) {
                    taskTracker.run();
                }
            } catch (Throwable e) {
                isDead = true;
                taskTracker = null;
                LOG.error("task tracker " + trackerId + " crashed", e);
            }
            exited = true;
        }

        /**
         * Get the local dir for this TaskTracker. This is there so that we do not break previous tests.
         * 
         * @return the absolute pathname
         */
        public String getLocalDir() {
            return localDirs[0];
        }

        public String[] getLocalDirs() {
            return localDirs;
        }

        public TaskTracker getTaskTracker() {
            return taskTracker;
        }

        /**
         * Shut down the server and wait for it to finish.
         */
        public void shutdown() {
            if (taskTracker != null) {
                try {
                    taskTracker.shutdown();
                } catch (Throwable e) {
                    LOG.error("task tracker " + trackerId + " could not shut down", e);
                }
            }
        }

    }
}
