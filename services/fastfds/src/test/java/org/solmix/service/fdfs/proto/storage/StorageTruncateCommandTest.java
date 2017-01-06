package org.solmix.service.fdfs.proto.storage;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.solmix.service.fdfs.model.StorePath;
import org.solmix.service.fdfs.proto.StorageCommandTestBase;

/**
 * 文件截取命令
 * 
 */
public class StorageTruncateCommandTest extends StorageCommandTestBase {

    @Test
    public void testStorageTruncateCommandText() throws IOException {
        String text = "Tobato is a good man. this is a test of StorageTruncateCommand";
        InputStream firstIn = getTextInputStream(text);
        long firstSize = firstIn.available();
        // 上载文字
        System.out.println(firstSize);
        StorePath path = uploadInputStream(firstIn, "txt", firstSize, true);
        // 文件截取
        StorageTruncateCommand command = new StorageTruncateCommand(path.getPath(), 0);
        executeStoreCmd(command);
        LOGGER.debug("--文件截取处理成功--");
    }

}
