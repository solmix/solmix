package org.solmix.service.fdfs.proto.tracker;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.solmix.service.fdfs.model.StorageNode;
import org.solmix.service.fdfs.proto.CommandTestBase;

/**
 * 获取存储节点交易
 * 
 */
public class TrackerGetStoreStorageCommandTest extends CommandTestBase {

    @Test
    public void testTrackerGetStoreStorageCommand() {
        StorageNode client = executeTrackerCmd(new TrackerGetStoreStorageCommand());
        assertNotNull(client.getInetSocketAddress());
        LOGGER.debug("-----获取存储节点交易处理结果-----{}");
        LOGGER.debug(client.toString());

        // Connection conn = new
        // DefaultConnection(client.getInetSocketAddress(), 500, 300, null);
        // LOGGER.debug("连接状态{}", conn.isValid());
        // conn.close();
    }

    @Test
    public void testTrackerGetStoreStorageWithGroupCommand() {
        StorageNode client = executeTrackerCmd(new TrackerGetStoreStorageCommand("group1"));
        assertNotNull(client.getInetSocketAddress());
        LOGGER.debug("-----按组获取存储节点交易处理结果-----");
        LOGGER.debug(client.toString());

        // Connection conn = new
        // DefaultConnection(client.getInetSocketAddress(), 500, 300, null);
        // LOGGER.debug("连接状态{}", conn.isValid());
        // conn.close();
    }

}
