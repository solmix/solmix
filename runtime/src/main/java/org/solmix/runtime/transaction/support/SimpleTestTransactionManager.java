package org.solmix.runtime.transaction.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.transaction.TransactionException;
import org.solmix.runtime.transaction.TransactionInfo;

public class SimpleTestTransactionManager extends AbstractTransactionManager {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleTestTransactionManager.class);
	@Override
	protected void doCommit(DefaultTransactionState state)
			throws TransactionException {
		LOG.info("--->doCommit:"+state.toString());

	}

	@Override
	protected void doRollback(DefaultTransactionState state)
			throws TransactionException {
		LOG.info("--->doRollback:"+state.toString());
	}

	@Override
	protected void doBegin(Object transaction, TransactionInfo definition)
			throws TransactionException {
		LOG.info("--->doBegin:"+definition.toString());
	}

	@Override
	protected Object doGetTransaction() throws TransactionException {
		LOG.info("--->doGetTransaction:");
		return "TestTransaction";
	}

}
