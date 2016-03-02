package org.solmix.service.fdfs.proto.tracker;

import java.util.List;

import org.solmix.service.fdfs.model.GroupState;
import org.solmix.service.fdfs.proto.AbstractFdfsCommand;
import org.solmix.service.fdfs.proto.tracker.internal.TrackerListGroupsRequest;
import org.solmix.service.fdfs.proto.tracker.internal.TrackerListGroupsResponse;

/**
 * 列出组命令
 * 
 * @author tobato
 *
 */
public class TrackerListGroupsCommand extends AbstractFdfsCommand<List<GroupState>> {

    public TrackerListGroupsCommand() {
        super.request = new TrackerListGroupsRequest();
        super.response = new TrackerListGroupsResponse();
    }

}
