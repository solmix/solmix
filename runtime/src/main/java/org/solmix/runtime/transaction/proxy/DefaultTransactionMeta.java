package org.solmix.runtime.transaction.proxy;

import org.solmix.runtime.transaction.TransactionPolicy;
import org.solmix.runtime.transaction.config.TransactionMeta;
import org.solmix.runtime.transaction.support.DefaultTransactionInfo;

public class DefaultTransactionMeta  extends DefaultTransactionInfo implements TransactionMeta {

	private String qualifier;
	
	public DefaultTransactionMeta() {
		super();
	}

	
	public DefaultTransactionMeta(TransactionMeta other) {
		super(other);
	}

	
	public DefaultTransactionMeta(TransactionPolicy propagationBehavior) {
		super(propagationBehavior);
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	@Override
	public String getQualifier() {
		return this.qualifier;
	}
	
	@Override
	public boolean rollbackOn(Throwable ex) {
		return (ex instanceof RuntimeException || ex instanceof Error);
	}
	
}
