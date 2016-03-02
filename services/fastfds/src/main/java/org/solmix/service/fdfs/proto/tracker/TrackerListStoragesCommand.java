package org.solmix.service.fdfs.proto.tracker;

import java.util.List;

import org.solmix.service.fdfs.model.StorageState;
import org.solmix.service.fdfs.proto.AbstractFdfsCommand;
import org.solmix.service.fdfs.proto.tracker.internal.TrackerListStoragesRequest;
import org.solmix.service.fdfs.proto.tracker.internal.TrackerListStoragesResponse;

/**
 * 列出组命令
 * 
 * @author tobato
 *
 */
public class TrackerListStoragesCommand extends AbstractFdfsCommand<List<StorageState>> {

    public TrackerListStoragesCommand(String groupName, String storageIpAddr) {
        super.request = new TrackerListStoragesRequest(groupName, storageIpAddr);
        super.response = new TrackerListStoragesResponse();
    }

    public TrackerListStoragesCommand(String groupName) {
        super.request = new TrackerListStoragesRequest(groupName);
        super.response = new TrackerListStoragesResponse();
    }

}
