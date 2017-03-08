package org.solmix.runtime.transaction.proxy;

import java.io.Serializable;

import org.solmix.runtime.proxy.interceptor.MethodInterceptor;
import org.solmix.runtime.proxy.support.MethodInvocation;
import org.solmix.runtime.proxy.support.ProxyUtils;
import org.solmix.runtime.transaction.TransactionManager;


public class TxInterceptor extends TxProxySupport implements MethodInterceptor,Serializable{

	private static final long serialVersionUID = -671065917775551507L;

	public TxInterceptor() {
	}


	public TxInterceptor(TransactionManager ptm, TransactionMetaCreater tas) {
		setTransactionManager(ptm);
		setTransactionMetaCreator(tas);
	}

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		Class<?> targetClass = (invocation.getThis() != null ? ProxyUtils.getTargetClass(invocation.getThis()) : null);

		return invokeWithinTransaction(invocation.getMethod(), targetClass, new InvocationCallback() {
			@Override
			public Object proceedWithInvocation() throws Throwable {
				return invocation.proceed();
			}
		});
	}

}
