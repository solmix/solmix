package org.solmix.service.fdfs.proto.storage;

import java.io.InputStream;

import org.solmix.service.fdfs.proto.AbstractFdfsCommand;
import org.solmix.service.fdfs.proto.FdfsResponse;
import org.solmix.service.fdfs.proto.storage.internal.StorageAppendFileRequest;

/**
 * 添加文件命令
 * 
 * 
 *
 */
public class StorageAppendFileCommand extends AbstractFdfsCommand<Void> {

    public StorageAppendFileCommand(InputStream inputStream, long fileSize, String path) {
        this.request = new StorageAppendFileRequest(inputStream, fileSize, path);
        // 输出响应
        this.response = new FdfsResponse<Void>() {
            // default response
        };
    }

}
