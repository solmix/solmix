package org.solmix.commons.util;


public class TransformException extends IllegalArgumentException
{

    private static final long serialVersionUID = 2533681346408947708L;
    public TransformException()            { super(); }
    public TransformException(String s)    { super(s); }
    public TransformException(Throwable t) { super(t); }
    public TransformException(String s,  Throwable t) { super(s, t); }
}
