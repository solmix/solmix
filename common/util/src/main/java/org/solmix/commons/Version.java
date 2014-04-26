
package org.solmix.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.solmix.commons.util.IOUtil;

public class Version
{

    private Version()
    {
    }

    /**
     * @param groupId
     * @param artifactId
     * @return return special version
     */
    public static String readFromMaven(String groupId, String artifactId) {
        String propPath = "/META-INF/maven/" + groupId + "/" + artifactId
            + "/pom.properties";
        InputStream is = Version.class.getResourceAsStream(propPath);
        if (is != null) {
            Properties properties = new Properties();
            try {
                properties.load(is);
                String version = properties.getProperty("version");
                if (version != null) {
                    return version;
                }
            } catch (IOException e) {
                // ignore
            } finally {
                IOUtil.closeQuitely(is);
            }
        }

        return "undetermined (please report this as bug)";
    }
}
