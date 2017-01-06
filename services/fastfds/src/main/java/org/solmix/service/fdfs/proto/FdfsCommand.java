package org.solmix.service.fdfs.proto;

import org.solmix.service.fdfs.conn.Connection;

/**
 * Fdfs交易命令抽象
 * 
 * 
 *
 */
public interface FdfsCommand<T> {

    /** 执行交易 */
    public T execute(Connection conn);

}
