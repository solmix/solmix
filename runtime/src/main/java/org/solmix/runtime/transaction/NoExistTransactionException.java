package org.solmix.runtime.transaction;

public class NoExistTransactionException extends TransactionException {

	private static final long serialVersionUID = -8776780278498728382L;

	public NoExistTransactionException()
    {

    }
    public NoExistTransactionException(Throwable e)
    {
        super(e);
    }
    public NoExistTransactionException(String include)
    {
        super(include);
    }

    public NoExistTransactionException(String string, Throwable e)
    {
        super(string, e);
    }
}
