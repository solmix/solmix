package org.solmix.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class TestEnvSetup {
    private Params params = createParams();
    private File basedir;
    private File srcdir;
    private File destdir;

    public TestEnvSetup setBasedir(String basedir) {
        getParams().basedirParam = basedir;
        return this;
    }

    public TestEnvSetup setSrcdir(String srcdir) {
        getParams().srcdirParam = srcdir;
        return this;
    }

    public TestEnvSetup setDestdir(String destdir) {
        getParams().destdirParam = destdir;
        return this;
    }

    public TestEnvSetup setLogbackConfig(String logbackConfig) {
        getParams().logbackConfigParam = logbackConfig;
        return this;
    }

    public TestEnvSetup setInitFailure(Exception initFailure) {
        getParams().initFailure = initFailure;
        return this;
    }

    public TestEnvSetup init() {
        Params params = getParams();

        if (params.inited) {
            return this;
        }

        params.inited = true;

        try {
            setupDirectories();

            URL logConfigFile = null;

            if (getParams().logbackConfigParam != null) {
                logConfigFile = findLogbackXml();
            }
                LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(lc);
                lc.reset();
                try {
                    configurator.doConfigure(logConfigFile);
                }
                catch (JoranException e) {
                    e.printStackTrace(System.err);
                    System.exit(-1);
                }
            System.out.println("+-----------------------------------------------------------------------------");

        } catch (Exception e) {
            params.initFailure = e;
        }

        return this;
    }

    /** 初始化basedir, srcdir和destdir等。 */
    private void setupDirectories() throws IOException {
        Params params = getParams();

        basedir = new File(params.basedirParam).getCanonicalFile();
        srcdir = new File(basedir, params.srcdirParam);
        destdir = new File(basedir, params.destdirParam);

        if (!destdir.exists()) {
            destdir.mkdirs();
        }

        if (!destdir.isDirectory() || !destdir.exists()) {
            throw new IllegalArgumentException("Destination directory does not exist: " + destdir);
        }

        System.out.println("+-----------------------------------------------------------------------------");
        System.out.println("| Set base dir to:          " + basedir);
        System.out.println("| Set source dir to:        " + srcdir);
        System.out.println("| Set destination dir to:   " + destdir);
    }


    private URL findLogbackXml() throws IOException {
        File test = new File(srcdir, getParams().logbackConfigParam);
        URL logbackXml;

        if (test.exists()) {
            logbackXml = test.toURI().toURL();
        } else {
            URL testURL = getClass().getResource("logback-test-default.xml");

            if (testURL == null) {
                throw new IllegalArgumentException("missing logback-test-default.xml");
            }

            logbackXml = testURL;
        }

        System.out.println("| Initializing log system:  " + logbackXml.toExternalForm());

        return logbackXml;
    }

    public File getBasedir() {
        assertInited();
        return basedir;
    }

    public File getSrcdir() {
        assertInited();
        return srcdir;
    }

    public File getDestdir() {
        assertInited();
        return destdir;
    }

    protected final void assertInited() {
        if (params == null) {
            return;
        }

        if (!params.inited) {
            throw new IllegalStateException("Not inited yet!");
        }

        Exception initFailure = params.initFailure;

        params = null;

        if (initFailure != null) {
            if (initFailure instanceof RuntimeException) {
                throw (RuntimeException) initFailure;
            } else {
                throw new RuntimeException(initFailure);
            }
        }
    }

    /**
     * 取得初始化参数。
     * <p>
     * 在初始化完成以后，将返回<code>null</code>。
     * </p>
     */
    protected final Params getParams() {
        if (params == null) {
            throw new IllegalStateException();
        }

        return params;
    }

    /**
     * 创建初始化参数。
     * <p>
     * 子类可扩展该参数。
     * </p>
     */
    protected Params createParams() {
        return new Params();
    }

    /** 初始化参数。 */
    protected class Params {
        public String    basedirParam       = ".";
        public String    srcdirParam        = "src/test/config/";
        public String    destdirParam       = "target/test/";
        public String    logbackConfigParam = "logback-test.xml"; // 相对于srcdir
        public Exception initFailure        = null;
        public boolean   inited             = false;
    }
}
