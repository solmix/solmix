package org.solmix.service.fdfs;

import java.io.ByteArrayInputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.solmix.runtime.threadpool.DefaultThreadPool;
import org.solmix.service.fdfs.model.StorePath;
import org.solmix.service.fdfs.service.AppendFileStorageClient;

/**
 * 性能测试
 * 
 */
public class PerformanceTest {

    protected AppendFileStorageClient storageClient;

     @Test
    public void testPerformance() {
        final AtomicInteger failCount = new AtomicInteger(0);
        final AtomicInteger count = new AtomicInteger(0);
        int totalCount = 1000;
        DefaultThreadPool executor = new DefaultThreadPool();
        executor.setMaxThreads(100);
        for (int i = 0; i < totalCount; i++) {
            executor.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        byte[] bytes = FileUtils.readFileToByteArray( TestUtils.getFile(TestConstants.PERFORM_FILE_PATH));
                        StorePath storePath = storageClient.uploadFile(null, new ByteArrayInputStream(bytes),
                                bytes.length, "jpg");
                    } catch (Exception e) {
                        e.printStackTrace();
                        failCount.incrementAndGet();
                    } finally {
                        count.incrementAndGet();
                    }

                }
            });

        }
        while (count.get() < totalCount) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
        }
        executor.shutdown(true);
        System.out.println("success count: " + count.get());
        System.out.println("fail count: " + failCount.get());
    }

}
