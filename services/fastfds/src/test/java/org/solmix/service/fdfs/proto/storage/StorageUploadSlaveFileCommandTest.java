package org.solmix.service.fdfs.proto.storage;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.solmix.service.fdfs.TestConstants;
import org.solmix.service.fdfs.TestUtils;
import org.solmix.service.fdfs.model.StorePath;
import org.solmix.service.fdfs.proto.StorageCommandTestBase;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 文件上传命令测试
 * 
 */
public class StorageUploadSlaveFileCommandTest extends StorageCommandTestBase {

    /**
     * 文件上传测试
     */
    @Test
    public void testStorageSlaveUploadFileCommand() {
        // 上传主文件
        StorePath path = execStorageUploadFileCommand(TestConstants.CAT_IMAGE_FILE, false);

        String masterFilename = path.getPath();
        String prefixName = "_120x120";
        // 生成从文件
        execStorageUploadSlaveFileCommand(TestConstants.CAT_IMAGE_FILE, masterFilename, prefixName);
    }

    /**
     * 从文件上传操作
     * 
     * @param isAppenderFile
     */
    public StorePath execStorageUploadSlaveFileCommand(String filePath, String masterFilename, String prefixName) {

        InputStream in = null;
        File file = TestUtils.getFile(filePath);
        String fileExtName = FilenameUtils.getExtension(file.getName());

        try {
            in = getThumbImageStream(filePath);// getFileInputStream
                                               // getThumbImageStream(filePath);
            long fileSize = in.available();
            StorageUploadSlaveFileCommand command = new StorageUploadSlaveFileCommand(in, fileSize, masterFilename,
                    prefixName, fileExtName);
            StorePath path = executeStoreCmd(command);
            assertNotNull(path);
            LOGGER.debug("--从文件上传处理结果-----");
            LOGGER.debug(path.toString());
            return path;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 获取缩略图
     * 
     * @param filePath
     * @return
     * @throws IOException
     */
    private InputStream getThumbImageStream(String filePath) throws IOException {
        // 在内存当中生成缩略图
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thumbnails.of(TestUtils.getFile(filePath)).size(120, 120).toOutputStream(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

}
