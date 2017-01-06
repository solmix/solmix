package org.solmix.service.fdfs.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.service.fdfs.TestConstants;
import org.solmix.service.fdfs.conn.FdfsConnectionPool;
import org.solmix.service.fdfs.conn.PooledConnectionFactory;
import org.solmix.service.fdfs.conn.TrackerConnectionManager;
import org.solmix.service.fdfs.exception.FdfsServerException;
import org.solmix.service.fdfs.model.GroupState;
import org.solmix.service.fdfs.model.StorageNode;
import org.solmix.service.fdfs.model.StorageState;
import org.solmix.service.fdfs.proto.ErrorCodeConstants;

/**
 * unit test for TrackerClientService
 * 
 */
public class TrackerClientTest {

    /** 日志 */
    private static Logger LOGGER = LoggerFactory.getLogger(TrackerClientTest.class);

    private static DefaultTrackerClient trackerClient;
    
    @BeforeClass
    public static void setup(){
        trackerClient= new DefaultTrackerClient();
        TrackerConnectionManager manager = new TrackerConnectionManager(createPool());
        manager.setTrackerList(Arrays.asList("192.168.43.188:22122"));
        manager.initTracker();
        trackerClient.setTrackerConnectionManager(manager);
    }

    @Test
    public void testGetStoreStorage() {
        LOGGER.debug("testGetStoreStorage..");
        StorageNode client = trackerClient.getStoreStorage();
        assertNotNull(client.getInetSocketAddress());
        LOGGER.debug("result={}", client);

    }

    @Test
    public void testGetStoreStorageByGroup() {
        LOGGER.debug("testGetStoreStorageByGroup..");
        StorageNode client = trackerClient.getStoreStorage(TestConstants.DEFAULT_GROUP);
        assertNotNull(client.getInetSocketAddress());
        LOGGER.debug("result={}", client);
    }

    @Test
    public void testListGroups() {
        LOGGER.debug("testListGroups..");
        List<GroupState> list = trackerClient.listGroups();
        assertNotNull(list);
        LOGGER.debug("result={}", list);
    }

    @Test
    public void testListStoragesByGroup() {
        LOGGER.debug("testListStoragesByGroup..");
        List<StorageState> list = trackerClient.listStorages(TestConstants.DEFAULT_GROUP);
        assertNotNull(list);
        LOGGER.debug("result={}", list);
    }

    @Test
    public void testListStoragesByGroupAndIp() {
        LOGGER.debug("testListStoragesByGroupAndIp..");
        List<StorageState> list = trackerClient.listStorages(TestConstants.DEFAULT_GROUP,
                TestConstants.DEFAULT_STORAGE_IP);
        assertNotNull(list);
        LOGGER.debug("result={}", list);
    }

    @Test
    public void testDeleteStorage() {
        LOGGER.debug("testDeleteStorage..");
        try {
            trackerClient.deleteStorage(TestConstants.DEFAULT_GROUP,
                    TestConstants.DEFAULT_STORAGE_IP);
            fail("No exception thrown.");
        } catch (Exception e) {
            assertTrue(e instanceof FdfsServerException);
            assertTrue(((FdfsServerException) e).getErrorCode() == ErrorCodeConstants.ERR_NO_EBUSY);
        }
        LOGGER.debug("testDeleteStorage..done");
    }
    private static FdfsConnectionPool createPool() {
        PooledConnectionFactory factory = new PooledConnectionFactory();
        factory.setConnectTimeout(TestConstants.connectTimeout);
        factory.setSoTimeout(TestConstants.soTimeout);
        return new FdfsConnectionPool(factory);
    }
}
