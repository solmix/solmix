package org.solmix.service.fdfs.conn;

import java.net.InetSocketAddress;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * 定义Fdfs连接池对象
 * 
 * <pre>
 * 定义了对象池要实现的功能,对一个地址进行池化Map Pool
 * </pre>
 * 
 * 
 *
 */
public class FdfsConnectionPool extends GenericKeyedObjectPool<InetSocketAddress, Connection> {

    /**
     * 默认构造函数
     * 
     * @param factory
     * @param config
     */
    public FdfsConnectionPool(KeyedPooledObjectFactory<InetSocketAddress, Connection> factory,
            GenericKeyedObjectPoolConfig config) {
        super(factory, config);
    }

    /**
     * 默认构造函数
     * 
     * @param factory
     */
    public FdfsConnectionPool(KeyedPooledObjectFactory<InetSocketAddress, Connection> factory) {
        super(factory);
    }

}
