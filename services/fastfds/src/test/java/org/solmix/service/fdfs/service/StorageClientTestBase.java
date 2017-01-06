
package org.solmix.service.fdfs.service;

import java.util.Arrays;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.service.fdfs.TestConstants;
import org.solmix.service.fdfs.conn.ConnectionManager;
import org.solmix.service.fdfs.conn.FdfsConnectionPool;
import org.solmix.service.fdfs.conn.PooledConnectionFactory;
import org.solmix.service.fdfs.conn.TrackerConnectionManager;

/**
 * StorageClient测试基类
 * 
 */
public class StorageClientTestBase
{

    protected DefaultAppendFileStorageClient storageClient;

    /** 日志 */
    protected static Logger LOGGER = LoggerFactory.getLogger(StorageClientTestBase.class);

    @Before
    public void setup() {
        storageClient = new DefaultAppendFileStorageClient();
        storageClient.setConnectionManager(createConnectionManager());
        
        DefaultTrackerClient trackerClient= new DefaultTrackerClient();
        TrackerConnectionManager manager = new TrackerConnectionManager(createPool());
        manager.setTrackerList(Arrays.asList("192.168.43.188:22122"));
        manager.initTracker();
        trackerClient.setTrackerConnectionManager(manager);
        
        storageClient.setTrackerClientService(trackerClient);
    }

    private ConnectionManager createConnectionManager() {
        return new ConnectionManager(createPool());
    }

    private FdfsConnectionPool createPool() {
        PooledConnectionFactory factory = new PooledConnectionFactory();
        factory.setConnectTimeout(TestConstants.connectTimeout);
        factory.setSoTimeout(TestConstants.soTimeout);
        return new FdfsConnectionPool(new PooledConnectionFactory());
    }
}
