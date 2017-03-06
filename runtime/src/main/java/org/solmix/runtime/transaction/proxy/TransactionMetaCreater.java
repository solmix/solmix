package org.solmix.runtime.transaction.proxy;

import java.lang.reflect.Method;

import org.solmix.runtime.transaction.config.TransactionMeta;

public interface TransactionMetaCreater {

	TransactionMeta getTransactionMeta(Method method, Class<?> targetClass);

}
