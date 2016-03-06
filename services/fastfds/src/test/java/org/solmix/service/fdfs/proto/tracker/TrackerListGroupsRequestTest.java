package org.solmix.service.fdfs.proto.tracker;

import org.junit.Test;
import org.solmix.service.fdfs.TestConstants;
import org.solmix.service.fdfs.proto.tracker.internal.TrackerListGroupsRequest;

/**
 * 列举Groups请求
 * 
 * @author tobato
 *
 */
public class TrackerListGroupsRequestTest {

    @Test
    public void testGetByteContent() {
        TrackerListGroupsRequest request = new TrackerListGroupsRequest();
        printRequest(request.getHeadByte(TestConstants.DEFAULT_CHARSET));
    }

    private void printRequest(byte[] request) {
        for (int i = 0; i < request.length; i++) {
            System.out.print(request[i]);
            System.out.print(" ");
        }
    }

}
