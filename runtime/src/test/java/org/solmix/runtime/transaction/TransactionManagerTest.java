package org.solmix.runtime.transaction;

import junit.framework.TestCase;

import org.solmix.runtime.transaction.support.DefaultTransactionInfo;
import org.solmix.runtime.transaction.support.DefaultTransactionState;
import org.solmix.runtime.transaction.support.IllegalTransactionStateException;
import org.solmix.tests.transaction.FooTransactionManager;

public class TransactionManagerTest extends TestCase {

	/**
	 * 测试在不存在事物的情况，下各种事物策略的处理方式。
	 */
	public void testNotExsitingTransaction() {
		TransactionManager tm = new FooTransactionManager(false, true);
		DefaultTransactionState status1 = (DefaultTransactionState)
				tm.getTransaction(new DefaultTransactionInfo(TransactionPolicy.SUPPORTS));
		assertTrue("Must not have transaction", status1.getTransaction() == null);

		DefaultTransactionState status2 = (DefaultTransactionState)
				tm.getTransaction(new DefaultTransactionInfo(TransactionPolicy.REQUIRED));
		assertTrue("Must have transaction", status2.getTransaction() != null);
		assertTrue("Must be new transaction", status2.isNewTransaction());

		try {
			tm.getTransaction(new DefaultTransactionInfo(TransactionPolicy.MANDATORY));
			fail("Should not have thrown NoTransactionException");
		}
		catch (IllegalTransactionStateException ex) {
			// expected
		}
	}
	/**
	 * 测试在存在事物的情况下，各种事物策略的处理情况
	 */
	public void testExistingTransaction() {
		TransactionManager tm = new FooTransactionManager(true, true);
		DefaultTransactionState status1 = (DefaultTransactionState)
				tm.getTransaction(new DefaultTransactionInfo(TransactionPolicy.SUPPORTS));
		assertTrue("Must have transaction", status1.getTransaction() != null);
		assertTrue("Must not be new transaction", !status1.isNewTransaction());

		DefaultTransactionState status2 = (DefaultTransactionState)
				tm.getTransaction(new DefaultTransactionInfo(TransactionPolicy.REQUIRED));
		assertTrue("Must have transaction", status2.getTransaction() != null);
		assertTrue("Must not be new transaction", !status2.isNewTransaction());

		try {
			DefaultTransactionState status3 = (DefaultTransactionState)
					tm.getTransaction(new DefaultTransactionInfo(TransactionPolicy.MANDATORY));
			assertTrue("Must have transaction", status3.getTransaction() != null);
			assertTrue("Must not be new transaction", !status3.isNewTransaction());
		}catch (NoExistTransactionException ex) {
			fail("Should not have thrown NoExistTransactionException");
		}
		DefaultTransactionState status4 = (DefaultTransactionState)
				tm.getTransaction(new DefaultTransactionInfo(TransactionPolicy.NOT_SUPPORTED));
		assertTrue("Must have transaction", status4.getTransaction() == null);
	}
	
	public void testCommitWithoutExistingTransaction() {
		FooTransactionManager tm = new FooTransactionManager(false, true);
		TransactionState status = tm.getTransaction(null);
		tm.commit(status);
		assertTrue("triggered begin", tm.isBegin());
		assertTrue("triggered commit", tm.isCommit());
		assertTrue("no rollback", !tm.isRollback());
		assertTrue("no rollbackOnly", !tm.isRollbackOnly());
	}
	
	public void testRollbackWithoutExistingTransaction() {
		FooTransactionManager tm = new FooTransactionManager(false, true);
		TransactionState status = tm.getTransaction(null);
		tm.rollback(status);
		assertTrue("triggered begin", tm.isBegin());
		assertTrue("triggered commit", !tm.isCommit());
		assertTrue("no rollback", tm.isRollback());
		assertTrue("no rollbackOnly", !tm.isRollbackOnly());
	}
	
	public void testRollbackOnlyWithoutExistingTransaction() {
		FooTransactionManager tm = new FooTransactionManager(false, true);
		TransactionState status = tm.getTransaction(null);
		status.setRollbackOnly();
		tm.commit(status);
		assertTrue("triggered begin", tm.isBegin());
		assertTrue("triggered commit", !tm.isCommit());
		assertTrue("no rollback", tm.isRollback());
		assertTrue("no rollbackOnly", !tm.isRollbackOnly());
	}
	
	public void testCommitWithExistingTransaction() {
		FooTransactionManager tm = new FooTransactionManager(true, true);
		TransactionState status = tm.getTransaction(null);
		tm.commit(status);
		assertTrue("no begin", !tm.isBegin());
		assertTrue("no commit", !tm.isCommit());
		assertTrue("no rollback", !tm.isRollback());
		assertTrue("no rollbackOnly", !tm.isRollbackOnly());
	}
	
	/**对于已存在的事物，回滚只是标记为回滚*/
	public void testRollbackWithExistingTransaction() {
		FooTransactionManager tm = new FooTransactionManager(true, true);
		TransactionState status = tm.getTransaction(null);
		tm.rollback(status);
		assertTrue("no begin", !tm.isBegin());
		assertTrue("no commit", !tm.isCommit());
		assertTrue("no rollback", !tm.isRollback());
		assertTrue("triggered rollbackOnly", tm.isRollbackOnly());
	}
	
	public void testRollbackOnlyWithExistingTransaction() {
		FooTransactionManager tm = new FooTransactionManager(true, true);
		TransactionState status = tm.getTransaction(null);
		status.setRollbackOnly();
		tm.commit(status);
		assertTrue("no begin", !tm.isBegin());
		assertTrue("no commit", !tm.isCommit());
		assertTrue("no rollback", !tm.isRollback());
		assertTrue("triggered rollbackOnly", tm.isRollbackOnly());
	}
}
