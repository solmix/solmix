package org.solmix.service.fdfs.proto.storage;

import org.junit.Test;
import org.solmix.service.fdfs.model.StorePath;
import org.solmix.service.fdfs.proto.StorageCommandTestBase;

/**
 * 文件下载
 * 
 * @author tobato
 *
 */
public class StorageDownloadCommandTest extends StorageCommandTestBase {

    @Test
    public void testStorageDownloadCommand() {
        // 上传文件
        StorePath path = uploadDefaultFile();
        DownloadFileWriter callback = new DownloadFileWriter("Test.jpg");
        // 删除文件
        StorageDownloadCommand<String> command = new StorageDownloadCommand<String>(path.getGroup(), path.getPath(),
                callback);
        String fileName = executeStoreCmd(command);
        LOGGER.debug("----文件下载成功-----{}", fileName);
    }

}
