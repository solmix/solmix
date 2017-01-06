package org.solmix.service.fdfs.proto.tracker;

import org.junit.Test;
import org.solmix.service.fdfs.model.StorageNodeInfo;
import org.solmix.service.fdfs.model.StorePath;
import org.solmix.service.fdfs.proto.StorageCommandTestBase;

/**
 * 获取源服务器
 * 
 */
public class TrackerGetFetchStorageCommandTest extends StorageCommandTestBase {

    /**
     * 获取源服务器
     */
    @Test
    public void testTrackerGetFetchStorageCommand() {

        // 上传文件
        StorePath path = uploadDefaultFile();

        // 获取源服务器
        TrackerGetFetchStorageCommand command = new TrackerGetFetchStorageCommand(path.getGroup(), path.getPath(),
                false);
        StorageNodeInfo client = executeTrackerCmd(command);
        LOGGER.debug("----获取源服务器-----");
        LOGGER.debug(client.toString());
    }
}
