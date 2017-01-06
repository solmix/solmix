package org.solmix.service.fdfs;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.solmix.service.fdfs.socket.FdfsMockSocketServer;

/**
 * 测试常量定义
 * 
 */
@SuppressWarnings("unused")
public class TestConstants {
    private static String ip_home = "192.168.43.188";
    private static String ip_work = "192.168.174.47";
    private static String ip_work_store = "192.168.174.49";
    public static InetSocketAddress address = new InetSocketAddress(ip_home, FdfsMockSocketServer.PORT);
    public static InetSocketAddress store_address = new InetSocketAddress(ip_home, FdfsMockSocketServer.STORE_PORT);
    public static final int soTimeout = 550;
    public static final int connectTimeout = 500;
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static final String DEFAULT_GROUP = "group1";
    public static final String DEFAULT_STORAGE_IP = ip_home;

    public static final String PERFORM_FILE_PATH = "/images/1x.png";
    public static final String CAT_IMAGE_FILE = "/images/2x.png";

}
