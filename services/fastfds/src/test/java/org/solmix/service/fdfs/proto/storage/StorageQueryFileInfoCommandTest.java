package org.solmix.service.fdfs.proto.storage;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.solmix.service.fdfs.model.FileInfo;
import org.solmix.service.fdfs.model.StorePath;
import org.solmix.service.fdfs.proto.StorageCommandTestBase;

/**
 * 文件查询处理
 * 
 */
public class StorageQueryFileInfoCommandTest extends StorageCommandTestBase {

    @Test
    public void testStorageQueryFileInfoCommand() {
        // 上传文件
        StorePath path = uploadDefaultFile();

        // 查询文件
        StorageQueryFileInfoCommand command = new StorageQueryFileInfoCommand(path.getGroup(), path.getPath());
        FileInfo fileInfo = executeStoreCmd(command);
        assertNotNull(fileInfo);
        LOGGER.debug("----文件查询处理结果-----");
        LOGGER.debug(fileInfo.toString());
    }

}
