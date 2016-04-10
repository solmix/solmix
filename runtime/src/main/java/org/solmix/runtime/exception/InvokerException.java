
package org.solmix.runtime.exception;

public class InvokerException extends RuntimeException
{

    private static final long serialVersionUID = -543300938040420767L;

    public InvokerException(String string, Throwable e)
    {
        super(string, e);
    }

    public InvokerException(Throwable e)
    {
        super(e);
    }

    public InvokerException(String string)
    {
        super(string);
    }
}
