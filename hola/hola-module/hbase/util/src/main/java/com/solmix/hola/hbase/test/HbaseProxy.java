
package com.solmix.hola.hbase.test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

import com.solmix.commons.test.TestHomeUtil;

/**
 * 
 * @author solmix.f@gmail.com
 * @version 0.1.1 2013-5-23
 */
public class HbaseProxy
{

    public enum Mode
    {
        EMBED , CONNECT
    }

    public static final String HBASE_MODE_PROP_NAME = "hola.hbase.mode";

    private final boolean clearData;
    
    private CleanupRobot cleanupRobot;

    private boolean cleanStateOnConnect = true;

    private Mode mode;

    private File testHome;

    private Configuration hbaseConf;

    private boolean enableMapReduce;

    public HbaseProxy() throws IOException
    {
        this(null);
    }

    public HbaseProxy(Mode mode) throws IOException
    {
        this(mode, true);
    }

    public HbaseProxy(Mode mode, boolean clearData) throws IOException
    {
        this.clearData = clearData;

        if (mode == null) {
            String hbaseModeProp = System.getProperty(HBASE_MODE_PROP_NAME);
            if (hbaseModeProp == null || hbaseModeProp.equals("") || hbaseModeProp.equals("embed")) {
                this.mode = Mode.EMBED;
            } else if (hbaseModeProp.equals("connect")) {
                this.mode = Mode.CONNECT;
            } else {
                throw new RuntimeException("Unexpected value for " + HBASE_MODE_PROP_NAME + ": " + hbaseModeProp);
            }
        } else {
            this.mode = mode;
        }
    }

    public void setTestHome(File testHome) throws IOException {
        if (mode != Mode.EMBED) {
            throw new RuntimeException("testHome should only be set when mode is EMBED");
        }
        this.testHome = testHome;
    }

    private void initTestHome() throws IOException {
        if (testHome == null) {
            testHome = TestHomeUtil.createTestHome("hola-hbase");
        }

        FileUtils.forceMkdir(testHome);
    }

    public void start() throws Exception {
        start(Collections.<String, byte[]> emptyMap());
    }

    public boolean getCleanStateOnConnect() {
        return cleanStateOnConnect;
    }

    public void setCleanStateOnConnect(boolean cleanStateOnConnect) {
        this.cleanStateOnConnect = cleanStateOnConnect;
    }

    public boolean getEnableMapReduce() {
        return enableMapReduce;
    }

    public void setEnableMapReduce(boolean enableMapReduce) {
        this.enableMapReduce = enableMapReduce;
    }

    public void start(Map<String, byte[]>  resusingTables) throws Exception {
        System.out.println("HBaseProxy mode: " + mode);

        hbaseConf = HBaseConfiguration.create();
        switch (mode) {
            case CONNECT:
                hbaseConf.set("hbase.zookeeper.quorum", "localhost");
                hbaseConf.set("hbase.zookeeper.property.clientPort", "2181");
                hbaseConf.set("hbase.replication", "true");
                
                cleanupRobot = new CleanupRobot(hbaseConf,getzkConnectUrl());
                if(this.cleanStateOnConnect){
                    cleanupRobot.cleanZooKeeper();

                    Map<String, byte[]> allReusingTables = new HashMap<String, byte[]>();
                    allReusingTables.putAll(cleanupRobot.getDefaultReusingTables());
                    allReusingTables.putAll(resusingTables);
                    cleanupRobot.cleanTables(allReusingTables);
                    
                    List<String> removedPeers = cleanupRobot.cleanHBaseReplicas();
                    for (String removedPeer : removedPeers) {
                        waitOnReplicationPeerStopped(removedPeer);
                    }
                }
                break;
            case EMBED:
                addHBaseTestProps(hbaseConf);
                initTestHome();

                System.out.println("HBaseProxy embedded mode temp dir: " + testHome.getAbsolutePath());
                break;
            default:
                throw new RuntimeException("Unexpected mode: " + mode);

        }

    }
    protected static void addHBaseTestProps(Configuration conf) {
        // The following properties are from HBase's src/test/resources/hbase-site.xml
        conf.set("hbase.regionserver.msginterval", "1000");
        conf.set("hbase.client.pause", "5000");
        conf.set("hbase.client.retries.number", "4");
        conf.set("hbase.master.meta.thread.rescanfrequency", "10000");
        conf.set("hbase.server.thread.wakefrequency", "1000");
        conf.set("hbase.regionserver.handler.count", "5");
        conf.set("hbase.master.info.port", "-1");
        conf.set("hbase.regionserver.info.port", "-1");
        conf.set("hbase.regionserver.info.port.auto", "true");
        conf.set("hbase.master.lease.thread.wakefrequency", "3000");
        conf.set("hbase.regionserver.optionalcacheflushinterval", "1000");
        conf.set("hbase.regionserver.safemode", "false");
    }
    private void waitOnReplicationPeerStopped(String peerId) {
//        if (mode == Mode.EMBED) {
//            mbean.waitOnReplicationPeerStopped(peerId);
//        } else {
//            JmxLiaison jmxLiaison = new JmxLiaison();
//            jmxLiaison.connect(false);
//            jmxLiaison.invoke(new ObjectName("HbaseProxy:name=ReplicationPeer"), "waitOnReplicationPeerStopped",
//                    peerId);
//            jmxLiaison.disconnect();
//        }
        
    }

    private String getzkConnectUrl() {
        return hbaseConf.get("hbase.zookeeper.quorum") + ":" + hbaseConf.get("hbase.zookeeper.property.clientPort");
    }

}
