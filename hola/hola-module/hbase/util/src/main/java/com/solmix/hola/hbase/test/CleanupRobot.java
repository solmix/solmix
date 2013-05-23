
package com.solmix.hola.hbase.test;

import static org.apache.zookeeper.ZooKeeper.States.CONNECTED;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.replication.ReplicationAdmin;
import org.apache.hadoop.hbase.exceptions.MasterNotRunningException;
import org.apache.hadoop.hbase.exceptions.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.protobuf.generated.AdminProtos.GetRegionInfoResponse.CompactionState;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.google.common.collect.Maps;
import com.solmix.commons.util.IOUtil;
import com.solmix.hola.hbase.util.HolaHBaseSchema;

/**
 * garbage collection robot,used to cleanup some data create by hbase or zookeeper.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2013-5-23
 */
public class CleanupRobot
{

    private final Configuration config;

    private final String zkConnectUrl;

    public static final String HOLA_ZK_NODE = "/hola";

    private static final Map<String, byte[]> DEFAULT_REUSING_TABLES = new HashMap<String, byte[]>();
    static {
        DEFAULT_REUSING_TABLES.put("record", Bytes.toBytes("data"));
        DEFAULT_REUSING_TABLES.put("type", Bytes.toBytes("fieldtype-entry"));
    }

    public CleanupRobot(Configuration conf, String zkConnectUrl)
    {
        this.config = conf;
        this.zkConnectUrl = zkConnectUrl;
    }

    public void cleanZooKeeper() throws Exception {
        int sessionTimeout = 10000;

        ZooKeeper zk = new ZooKeeper(zkConnectUrl, sessionTimeout, new Watcher() {

            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Watcher.Event.KeeperState.Disconnected) {
                    System.err.println("ZooKeeper Disconnected.");
                } else if (event.getState() == Event.KeeperState.Expired) {
                    System.err.println("ZooKeeper session expired.");
                }
            }
        });

        long waitUntil = System.currentTimeMillis() + sessionTimeout;
        while (zk.getState() != CONNECTED && waitUntil > System.currentTimeMillis()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                break;
            }
        }

        if (zk.getState() != CONNECTED) {
            throw new RuntimeException("Failed to connect to ZK within " + sessionTimeout + "ms.");
        }

        if (zk.exists(HOLA_ZK_NODE, false) != null) {
            System.out.println("----------------- Clearing '/hola' node in ZooKeeper -------------------");

            List<String> paths = new ArrayList<String>();

            collectChildren(HOLA_ZK_NODE, zk, paths);
            paths.add(HOLA_ZK_NODE);

            for (String path : paths) {
                zk.delete(path, -1, null, null);
            }

            long startWait = System.currentTimeMillis();
            while (zk.exists(HOLA_ZK_NODE, null) != null) {
                Thread.sleep(5);

                if (System.currentTimeMillis() - startWait > 120000) {
                    throw new RuntimeException("State was not cleared in ZK within the expected timeout");
                }
            }

            System.out.println("Deleted " + paths.size() + " paths from ZooKeeper");
            System.out.println("------------------------------------------------------------------------");
        }

        zk.close();

    }

    private void collectChildren(String path, ZooKeeper zk, List<String> paths) throws KeeperException, InterruptedException {
        List<String> children = zk.getChildren(path, false);
        for (String child : children) {
            String childPath = path + "/" + child;
            collectChildren(childPath, zk, paths);
            paths.add(childPath);
        }

    }

    public void cleanTables() throws Exception {
        Map<String, byte[]> reusingTables = new HashMap<String, byte[]>();
        reusingTables.putAll(DEFAULT_REUSING_TABLES);
        cleanTables(reusingTables);
    }

    public void cleanTables(Map<String, byte[]> reusingTables) throws MasterNotRunningException, ZooKeeperConnectionException, IOException, InterruptedException {
        System.out.println("------------------------ Resetting HBase tables ------------------------");

        StringBuilder truncateReport = new StringBuilder();
        StringBuilder retainReport = new StringBuilder();

        HBaseAdmin admin = new HBaseAdmin(config);
        try {
            HTableDescriptor[] tables = admin.listTables();
            System.out.println("Found tables: " + (tables == null ? "null" : tables.length));
            tables = tables == null ? new HTableDescriptor[0] : tables;
            
            Set<String> exploitTimestampTables = new HashSet<String>();
            
            for (HTableDescriptor table : tables) {
                if (Bytes.equals(table.getValue(HolaHBaseSchema.TABLE_TYPE_PROPERTY), HolaHBaseSchema.TABLE_TYPE_RECORD)
                    && !table.getNameAsString().equals(HolaHBaseSchema.Table.RECORD.name)) {
                    admin.disableTable(table.getName());
                    admin.deleteTable(table.getName());
                }else{
                    HTable htable = new HTable(config, table.getName());
                    
                    if(reusingTables.containsKey(table.getName())){
                        insertTableTestRecord(table.getNameAsString(), htable,reusingTables.get(table.getNameAsString()));
                        exploitTimestampTables.add(table.getNameAsString());
                    }
                    
                    int totalCount = clearTable(htable);
                    

                    if (truncateReport.length() > 0) {
                        truncateReport.append(", ");
                    }
                    truncateReport.append(table.getNameAsString()).append(" (").append(totalCount).append(")");

                    htable.close();
                    
                    if (reusingTables.containsKey(table.getNameAsString())) {
                        admin.flush(table.getNameAsString());
                        admin.majorCompact(table.getName());
                    }
                    truncateReport.insert(0, "Truncated the following tables: ");
                    retainReport.insert(0, "Did not truncate the following tables: ");

                    System.out.println(truncateReport);
                    System.out.println(retainReport);
                    
                    
                    waitForTables(exploitTimestampTables, reusingTables);

                    System.out.println("------------------------------------------------------------------------");
                }
            }

        } finally {
            IOUtil.closeQuitely(admin);
        }
    }

    private void waitForTables(Set<String> tables, Map<String, byte[]> reusingTables) throws IOException, InterruptedException {
        for (String tableName : tables) {
            HTable htable = null;
            try {
                htable = new HTable(config, tableName);

                byte[] CF = reusingTables.get(tableName);
                byte[] tmpRowKey = waitForCompact(tableName, CF);

                // Delete our dummy row again
                htable.delete(new Delete(tmpRowKey));
            } finally {
                if (htable != null) {
                    htable.close();
                }
            }

        }
        
    }

    private byte[] waitForCompact(String tableName, byte[] cF)throws IOException, InterruptedException  {
        byte[] tmpRowKey = Bytes.toBytes("HBaseProxyDummyRow");
        byte[] COL = Bytes.toBytes("DummyColumn");
        HTable htable = null;
        try {
            htable = new HTable(config, tableName);
            System.out.println("Waiting for flush/compact of " + tableName + " table to complete");
            byte[] value = null;
            long waitStart = System.currentTimeMillis();
            while (value == null) {
                Put put = new Put(tmpRowKey);
                put.add(cF, COL, 1, new byte[] { 0 });
                htable.put(put);

                Get get = new Get(tmpRowKey);
                Result result = htable.get(get);
                value = result.getValue(cF, COL);
                if (value == null) {
                    // If the value is null, it is because the delete marker has not yet been flushed/compacted away
                    Thread.sleep(500);
                }

                long totalWait = System.currentTimeMillis() - waitStart;
                if (totalWait > 5000) {
                    HBaseAdmin admin = new HBaseAdmin(config);
                    try {
                        CompactionState compactionState = admin.getCompactionState(tableName);
                        if (compactionState != CompactionState.MAJOR && compactionState != CompactionState.MAJOR_AND_MINOR) {
                            System.out.println("Re-requesting major compaction on " + tableName);
                            admin.majorCompact(tableName);
                        }
                    } finally {
                        IOUtil.closeQuitely(admin);
                        }
                    waitStart = System.currentTimeMillis();
                }
            }
            return tmpRowKey;
        } finally {
            if (htable != null) {
                htable.close();
            }
        }
    }

    public static int clearTable(HTable htable) throws IOException {
        Scan scan = new Scan();
        scan.setCaching(1000);
        scan.setCacheBlocks(false);
        ResultScanner scanner = htable.getScanner(scan);
        Result[] results;
        int totalCount = 0;

        while ((results = scanner.next(1000)).length > 0) {
            List<Delete> deletes = new ArrayList<Delete>(results.length);
            for (Result result : results) {
                deletes.add(new Delete(result.getRow()));
            }
            totalCount += deletes.size();
            htable.delete(deletes);
        }
        scanner.close();
        return totalCount;
    }

    private void insertTableTestRecord(String nameAsString, HTable htable, byte[] family) throws IOException {
        byte[] tmpRowKey = Bytes.toBytes("HBaseProxyDummyRow");
        byte[] COL = Bytes.toBytes("DummyColumn");
        Put put = new Put(tmpRowKey);
        // put a value with a fixed timestamp
        put.add(family, COL, 1, new byte[] { 0 });

        htable.put(put);
    }

    public Map<? extends String, ? extends byte[]> getDefaultReusingTables() {
        Map<String, byte[]> defaultTables = Maps.newHashMap(DEFAULT_REUSING_TABLES);
        try {
            HBaseAdmin hbaseAdmin = new HBaseAdmin(config);
            HTableDescriptor[] descriptors = hbaseAdmin.listTables();
            hbaseAdmin.close();
            if (descriptors != null) {
                for (HTableDescriptor descriptor : descriptors) {
                    if (HolaHBaseSchema.isRecordTableDescriptor(descriptor)) {
                        defaultTables.put(descriptor.getNameAsString(), Bytes.toBytes("data"));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listing repository tables", e);
        }
        return Collections.unmodifiableMap(defaultTables);
    }

    public List<String> cleanHBaseReplicas() throws Exception {
        ReplicationAdmin repliAdmin = new ReplicationAdmin(config);
        List<String> removedPeers = new ArrayList<String>();
        try {
            for (String peerId : repliAdmin.listPeers().keySet()) {
                repliAdmin.removePeer(peerId);
                removedPeers.add(peerId);
            }
        } finally {
            IOUtil.closeQuitely(repliAdmin);
        }
        return removedPeers;
    }
}
