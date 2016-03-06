package org.solmix.service.fdfs.proto.storage;

import org.junit.Test;
import org.solmix.service.fdfs.model.StorePath;
import org.solmix.service.fdfs.proto.StorageCommandTestBase;

public class StorageDeleteFileCommandTest extends StorageCommandTestBase {

    /**
     * 文件删除测试
     */
    @Test
    public void testStorageDeleteFileCommand() {

        // 上传文件
        StorePath path = uploadDefaultFile();

        // 删除文件
        StorageDeleteFileCommand command = new StorageDeleteFileCommand(path.getGroup(), path.getPath());
        executeStoreCmd(command);
        LOGGER.debug("----文件删除成功-----");
    }

}
