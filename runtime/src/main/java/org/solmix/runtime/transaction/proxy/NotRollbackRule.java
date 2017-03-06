package org.solmix.runtime.transaction.proxy;

public class NotRollbackRule extends RollbackRule{

	private static final long serialVersionUID = -3547319706333014197L;
	public NotRollbackRule(Class<?> clazz) {
		super(clazz);
	}
	public NotRollbackRule(String exceptionName) {
		super(exceptionName);
	}
}
