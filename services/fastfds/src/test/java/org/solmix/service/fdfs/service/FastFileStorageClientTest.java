package org.solmix.service.fdfs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.service.fdfs.TestConstants;
import org.solmix.service.fdfs.TestUtils;
import org.solmix.service.fdfs.conn.ConnectionManager;
import org.solmix.service.fdfs.conn.FdfsConnectionPool;
import org.solmix.service.fdfs.conn.PooledConnectionFactory;
import org.solmix.service.fdfs.conn.TrackerConnectionManager;
import org.solmix.service.fdfs.model.DefaultThumbImageConfig;
import org.solmix.service.fdfs.model.FileInfo;
import org.solmix.service.fdfs.model.MateData;
import org.solmix.service.fdfs.model.RandomTextFile;
import org.solmix.service.fdfs.model.StorePath;
import org.solmix.service.fdfs.model.ThumbImageConfig;

/**
 * FastFileStorageClient客户端
 * 
 * @author tobato
 *
 */
public class FastFileStorageClientTest {

    protected DefaultFastFileStorageClient storageClient;

    private ThumbImageConfig thumbImageConfig=new DefaultThumbImageConfig(150,150);

    @Before
    public void setup() {
        storageClient = new DefaultFastFileStorageClient();
        storageClient.setConnectionManager(createConnectionManager());
        
        DefaultTrackerClient trackerClient= new DefaultTrackerClient();
        TrackerConnectionManager manager = new TrackerConnectionManager(createPool());
        manager.setTrackerList(Arrays.asList("192.168.43.188:22122"));
        manager.initTracker();
        trackerClient.setTrackerConnectionManager(manager);
        
        storageClient.setTrackerClientService(trackerClient);
        storageClient.setThumbImageConfig(thumbImageConfig);
    }

    /** 日志 */
    protected static Logger LOGGER = LoggerFactory.getLogger(FastFileStorageClientTest.class);

    /**
     * 上传文件，并且设置MateData
     */
    @Test
    public void testUploadFileAndMateData() {

        LOGGER.debug("##上传文件..##");
        RandomTextFile file = new RandomTextFile();
        // Metadata
        Set<MateData> metaDataSet = createMateData();
        // 上传文件和Metadata
        StorePath path = storageClient.uploadFile(file.getInputStream(), file.getFileSize(), file.getFileExtName(),
                metaDataSet);
        assertNotNull(path);

        // 验证获取MataData
        LOGGER.debug("##获取Metadata##");
        Set<MateData> fetchMateData = storageClient.getMetadata(path.getGroup(), path.getPath());
        assertEquals(fetchMateData, metaDataSet);

        LOGGER.debug("##删除文件..##");
        storageClient.deleteFile(path.getGroup(), path.getPath());
    }

    /**
     * 不带MateData也应该能上传成功
     */
    @Test
    public void testUploadFileWithoutMateData() {

        LOGGER.debug("##上传文件..##");
        RandomTextFile file = new RandomTextFile();
        // 上传文件和Metadata
        StorePath path = storageClient.uploadFile(file.getInputStream(), file.getFileSize(), file.getFileExtName(),
                null);
        assertNotNull(path);

        LOGGER.debug("##删除文件..##");
        storageClient.deleteFile(path.getFullPath());
    }

    /**
     * 上传图片，并且生成缩略图
     */
    @Test
    public void testUploadImageAndCrtThumbImage() {
        LOGGER.debug("##上传文件..##");
        Set<MateData> metaDataSet = createMateData();
        StorePath path = uploadImageAndCrtThumbImage(TestConstants.PERFORM_FILE_PATH, metaDataSet);

        // 验证获取MataData
        LOGGER.debug("##获取Metadata##");
        Set<MateData> fetchMateData = storageClient.getMetadata(path.getGroup(), path.getPath());
        assertEquals(fetchMateData, metaDataSet);

        // 验证获取从文件
        LOGGER.debug("##获取Metadata##");
        // 这里需要一个获取从文件名的能力，所以从文件名配置以后就最好不要改了
        String slavePath = thumbImageConfig.getThumbImagePath(path.getPath());
        // 或者由客户端再记录一下从文件的前缀
        FileInfo slaveFile = storageClient.queryFileInfo(path.getGroup(), slavePath);
        assertNotNull(slaveFile);
        LOGGER.debug("##获取到从文件##{}", slaveFile);

    }

    /**
     * 上传文件
     * 
     * @param filePath
     * @return
     */
    private StorePath uploadImageAndCrtThumbImage(String filePath, Set<MateData> metaDataSet) {
        InputStream in = null;
        File file = TestUtils.getFile(filePath);
        String fileExtName = FilenameUtils.getExtension(file.getName());
        long fileSize = file.length();
        try {
            in = TestUtils.getFileInputStream(filePath);
            return storageClient.uploadImageAndCrtThumbImage(in, fileSize, fileExtName, metaDataSet);
        } catch (FileNotFoundException e) {
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

    private Set<MateData> createMateData() {
        Set<MateData> metaDataSet = new HashSet<MateData>();
        metaDataSet.add(new MateData("Author", "wyf"));
        metaDataSet.add(new MateData("CreateDate", "2016-01-05"));
        return metaDataSet;
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
