
package org.solmix.commons;

import java.io.IOException;
import java.io.InputStream;
import java.security.CodeSource;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class Version
{

    private static final Logger LOG  = LoggerFactory.getLogger(Version.class);
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
                IOUtils.closeQuietly(is);
            }
        }

        return "undetermined (please report this as bug)";
    }
    
    public static String getVersion(Class<?> cls, String defaultVersion) {
        try {
            // 首先查找MANIFEST.MF规范中的版本号
            String version = cls.getPackage().getImplementationVersion();
            if (version == null || version.length() == 0) {
                version = cls.getPackage().getSpecificationVersion();
            }
            if (version == null || version.length() == 0) {
                // 如果规范中没有版本号，基于jar包名获取版本号
                CodeSource codeSource = cls.getProtectionDomain().getCodeSource();
                if(codeSource == null) {
                    LOG.info("No codeSource for class " + cls.getName() + " when getVersion, use default version " + defaultVersion);
                }
                else {
                    String file = codeSource.getLocation().getFile();
                    if (file != null && file.length() > 0 && file.endsWith(".jar")) {
                        file = file.substring(0, file.length() - 4);
                        int i = file.lastIndexOf('/');
                        if (i >= 0) {
                            file = file.substring(i + 1);
                        }
                        i = file.indexOf("-");
                        if (i >= 0) {
                            file = file.substring(i + 1);
                        }
                        while (file.length() > 0 && ! Character.isDigit(file.charAt(0))) {
                            i = file.indexOf("-");
                            if (i >= 0) {
                                file = file.substring(i + 1);
                            } else {
                                break;
                            }
                        }
                        version = file;
                    }
                }
            }
            // 返回版本号，如果为空返回缺省版本号
            return version == null || version.length() == 0 ? defaultVersion : version;
        } catch (Throwable e) { // 防御性容错
            // 忽略异常，返回缺省版本号
            LOG.error("return default version, ignore exception " + e.getMessage(), e);
            return defaultVersion;
        }
    }
}
