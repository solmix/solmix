package org.solmix.service.fdfs.proto.tracker.internal;

import org.solmix.service.fdfs.proto.CmdConstants;
import org.solmix.service.fdfs.proto.FdfsRequest;
import org.solmix.service.fdfs.proto.ProtoHead;

/**
 * 列出分组命令
 * 
 * @author tobato
 *
 */
public class TrackerListGroupsRequest extends FdfsRequest {

    public TrackerListGroupsRequest() {
        head = new ProtoHead(CmdConstants.TRACKER_PROTO_CMD_SERVER_LIST_GROUP);
    }
}
