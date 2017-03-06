package org.solmix.runtime.transaction.proxy;

import org.solmix.runtime.proxy.support.AbstractSimpleProxyFactory;
import org.solmix.runtime.transaction.annotation.AnnotationTransactionMetaCreator;

public class TransactionPorxyFactory extends AbstractSimpleProxyFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4755051505774680011L;
	TxInterceptor interceptor = new TxInterceptor();
	
	public TransactionPorxyFactory(){
		interceptor.setTransactionMetaCreator(new AnnotationTransactionMetaCreator());
	}
	@Override
	protected Object createMainInterceptor() {
		return new DefaultTransactionMetaCreatorAspector(this.interceptor);
	}

}
