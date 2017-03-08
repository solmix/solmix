package org.solmix.runtime.transaction;

public class TransactionTimedOutException extends TransactionException {
	public TransactionTimedOutException()
    {

    }
    public TransactionTimedOutException(Throwable e)
    {
        super(e);
    }
    public TransactionTimedOutException(String include)
    {
        super(include);
    }

    public TransactionTimedOutException(String string, Throwable e)
    {
        super(string, e);
    }
}
