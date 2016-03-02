package org.solmix.service.fdfs.proto.tracker;

import org.solmix.service.fdfs.proto.AbstractFdfsCommand;
import org.solmix.service.fdfs.proto.FdfsResponse;
import org.solmix.service.fdfs.proto.tracker.internal.TrackerDeleteStorageRequest;

/**
 * 移除存储服务器命令
 * 
 * @author tobato
 *
 */
public class TrackerDeleteStorageCommand extends AbstractFdfsCommand<Void> {

    public TrackerDeleteStorageCommand(String groupName, String storageIpAddr) {
        super.request = new TrackerDeleteStorageRequest(groupName, storageIpAddr);
        super.response = new FdfsResponse<Void>() {
            // default response
        };
    }

}
