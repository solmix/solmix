package org.solmix.runtime.transaction;

public class CreateTransactionException extends TransactionException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1754710119405886809L;

	public CreateTransactionException()
    {

    }
    public CreateTransactionException(Throwable e)
    {
        super(e);
    }
    public CreateTransactionException(String include)
    {
        super(include);
    }

    public CreateTransactionException(String string, Throwable e)
    {
        super(string, e);
    }
}
