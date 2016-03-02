package org.solmix.service.fdfs.service;

import java.io.InputStream;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.service.fdfs.conn.ConnectionManager;
import org.solmix.service.fdfs.model.FileInfo;
import org.solmix.service.fdfs.model.MateData;
import org.solmix.service.fdfs.model.StorageNode;
import org.solmix.service.fdfs.model.StorageNodeInfo;
import org.solmix.service.fdfs.model.StorePath;
import org.solmix.service.fdfs.proto.storage.DownloadCallback;
import org.solmix.service.fdfs.proto.storage.StorageDeleteFileCommand;
import org.solmix.service.fdfs.proto.storage.StorageDownloadCommand;
import org.solmix.service.fdfs.proto.storage.StorageGetMetadataCommand;
import org.solmix.service.fdfs.proto.storage.StorageQueryFileInfoCommand;
import org.solmix.service.fdfs.proto.storage.StorageSetMetadataCommand;
import org.solmix.service.fdfs.proto.storage.StorageUploadFileCommand;
import org.solmix.service.fdfs.proto.storage.StorageUploadSlaveFileCommand;
import org.solmix.service.fdfs.proto.storage.enums.StorageMetdataSetType;

/**
 * 基本存储客户端操作实现
 * 
 * @author tobato
 *
 */
public class DefaultGenerateStorageClient implements GenerateStorageClient {

    /** trackerClient */
    @Resource
    protected TrackerClient trackerClient;

    /** connectManager */
    @Resource
    protected ConnectionManager connectionManager;

    /** 日志 */
    protected static Logger LOGGER = LoggerFactory.getLogger(DefaultGenerateStorageClient.class);

    /**
     * 上传不支持断点续传的文件
     */
    @Override
    public StorePath uploadFile(String groupName, InputStream inputStream, long fileSize, String fileExtName) {
        StorageNode client = trackerClient.getStoreStorage(groupName);
        StorageUploadFileCommand command = new StorageUploadFileCommand(client.getStoreIndex(), inputStream,
                fileExtName, fileSize, false);
        return connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    /**
     * 上传从文件
     */
    @Override
    public StorePath uploadSlaveFile(String groupName, String masterFilename, InputStream inputStream, long fileSize,
            String prefixName, String fileExtName) {
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, masterFilename);
        StorageUploadSlaveFileCommand command = new StorageUploadSlaveFileCommand(inputStream, fileSize, masterFilename,
                prefixName, fileExtName);
        return connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    /**
     * 获取metadata
     */
    @Override
    public Set<MateData> getMetadata(String groupName, String path) {
        StorageNodeInfo client = trackerClient.getFetchStorage(groupName, path);
        StorageGetMetadataCommand command = new StorageGetMetadataCommand(groupName, path);
        return connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    /**
     * 覆盖metadata
     */
    @Override
    public void overwriteMetadata(String groupName, String path, Set<MateData> metaDataSet) {
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        StorageSetMetadataCommand command = new StorageSetMetadataCommand(groupName, path, metaDataSet,
                StorageMetdataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE);
        connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    /**
     * 合并metadata
     */
    @Override
    public void mergeMetadata(String groupName, String path, Set<MateData> metaDataSet) {
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        StorageSetMetadataCommand command = new StorageSetMetadataCommand(groupName, path, metaDataSet,
                StorageMetdataSetType.STORAGE_SET_METADATA_FLAG_MERGE);
        connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    /**
     * 查询文件信息
     */
    @Override
    public FileInfo queryFileInfo(String groupName, String path) {
        StorageNodeInfo client = trackerClient.getFetchStorage(groupName, path);
        StorageQueryFileInfoCommand command = new StorageQueryFileInfoCommand(groupName, path);
        return connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    /**
     * 删除文件
     */
    @Override
    public void deleteFile(String groupName, String path) {
        StorageNodeInfo client = trackerClient.getUpdateStorage(groupName, path);
        StorageDeleteFileCommand command = new StorageDeleteFileCommand(groupName, path);
        connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    /**
     * 下载整个文件
     */
    @Override
    public <T> T downloadFile(String groupName, String path, DownloadCallback<T> callback) {
        long fileOffset = 0;
        long fileSize = 0;
        return downloadFile(groupName, path, fileOffset, fileSize, callback);
    }

    /**
     * 下载文件片段
     */
    @Override
    public <T> T downloadFile(String groupName, String path, long fileOffset, long fileSize,
            DownloadCallback<T> callback) {
        StorageNodeInfo client = trackerClient.getFetchStorage(groupName, path);
        StorageDownloadCommand<T> command = new StorageDownloadCommand<T>(groupName, path, 0, 0, callback);
        return connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);
    }

    public void setTrackerClientService(TrackerClient trackerClientService) {
        this.trackerClient = trackerClientService;
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

}
