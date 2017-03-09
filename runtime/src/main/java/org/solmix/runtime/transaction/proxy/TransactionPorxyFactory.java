package org.solmix.runtime.transaction.proxy;

import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerAware;
import org.solmix.runtime.proxy.support.AbstractSimpleProxyFactory;
import org.solmix.runtime.transaction.TransactionManager;
import org.solmix.runtime.transaction.annotation.AnnotationTransactionMetaCreator;

public class TransactionPorxyFactory extends AbstractSimpleProxyFactory implements ContainerAware {

	private static final long serialVersionUID = -4755051505774680011L;
	TxInterceptor interceptor = new TxInterceptor();
	public TransactionPorxyFactory() {
		interceptor.setTransactionMetaCreator(new AnnotationTransactionMetaCreator());
	}
	
	public TransactionPorxyFactory(TransactionManager txm) {
		interceptor.setTransactionMetaCreator(new AnnotationTransactionMetaCreator());
		interceptor.setTransactionManager(txm);
	}

	@Override
	protected Object createMainInterceptor() {
		return new DefaultTransactionMetaCreatorAspector(this.interceptor);
	}

	@Override
	public void setContainer(Container container) {
		interceptor.setContainer(container);
		
	}

}
