
package org.solmix.commons.test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class TestHomeUtil {
	  private TestHomeUtil() {
	    }

	    public static File createTestHome() throws IOException {
	        return createTestHome("solmix-test-");
	    }

	    public static File createTestHome(String prefix) throws IOException {
	        String randomStr = UUID.randomUUID().toString();
	        String tmpdir = System.getProperty("java.io.tmpdir");
	        File testHomeDir = new File(tmpdir, prefix + randomStr);
	        if (!testHomeDir.mkdirs()) {
	            throw new IOException("Failed to create directory path " + testHomeDir.getPath());
	        }
	        return testHomeDir;
	    }

	    public static void cleanupTestHome(File testHome) throws IOException {
	        deleteDirectory(testHome);
	    }

	    private static void deleteDirectory(File file) throws IOException {
	        if (!file.exists()) {
	            return;
	        }

	        File[] children  = file.listFiles();
	        if (children != null) {
	            for (File child : children) {
	                if (child.isDirectory()) {
	                    deleteDirectory(child);
	                } else {
	                    if (!child.delete()) {
	                        throw new IOException("Failed to delete " + child.getAbsolutePath());
	                    }
	                }
	            }
	        }

	        if (!file.delete()) {
	            throw new IOException("Failed to delete " + file.getAbsolutePath());
	        }
	    }
}
