package org.solmix.service.fdfs.proto.storage;

import java.util.Set;

import org.solmix.service.fdfs.model.MateData;
import org.solmix.service.fdfs.proto.AbstractFdfsCommand;
import org.solmix.service.fdfs.proto.storage.internal.StorageGetMetadataRequest;
import org.solmix.service.fdfs.proto.storage.internal.StorageGetMetadataResponse;

/**
 * 设置文件标签
 * 
 * 
 *
 */
public class StorageGetMetadataCommand extends AbstractFdfsCommand<Set<MateData>> {

    /**
     * 设置文件标签(元数据)
     * 
     * @param groupName
     * @param path
     * @param metaDataSet
     * @param type
     */
    public StorageGetMetadataCommand(String groupName, String path) {
        this.request = new StorageGetMetadataRequest(groupName, path);
        // 输出响应
        this.response = new StorageGetMetadataResponse();
    }

}
