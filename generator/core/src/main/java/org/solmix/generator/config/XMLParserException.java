package org.solmix.generator.config;

import java.util.ArrayList;
import java.util.List;


public class XMLParserException extends Exception
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5172525430401340573L;

    /** The errors. */
    private List<String> errors;

    /**
     * Instantiates a new XML parser exception.
     *
     * @param errors
     *            the errors
     */
    public XMLParserException(List<String> errors) {
        super();
        this.errors = errors;
    }

    /**
     * Instantiates a new XML parser exception.
     *
     * @param error
     *            the error
     */
    public XMLParserException(String error) {
        super(error);
        this.errors = new ArrayList<String>();
        errors.add(error);
    }

    public XMLParserException(String msg, Exception e)
    {
        super(msg,e);
    }

    /**
     * Gets the errors.
     *
     * @return the errors
     */
    public List<String> getErrors() {
        return errors;
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        if (errors != null && errors.size() > 0) {
            return errors.get(0);
        }

        return super.getMessage();
    }
}
