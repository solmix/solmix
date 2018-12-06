package org.solmix.karaf.command;

import org.junit.Test;

public class MvnProtocolTest {

	@Test
	public void test() throws Exception {
		UrlDownloadCommand cmd = new UrlDownloadCommand();
		String repository = System.setProperty("karaf.home", MvnProtocolTest.class.getResource("/").getFile());
		String base = System.setProperty("karaf.default.repository", "system");
		// cmd.setUrl("mvn:org.osgi/org.osgi.core/6.0.0");
		// cmd.execute();
	}
}
