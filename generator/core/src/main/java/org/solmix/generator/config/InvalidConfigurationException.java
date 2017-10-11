package org.solmix.generator.config;

import java.util.List;


public class InvalidConfigurationException extends Exception
{
    /** The errors. */
    private List<String> errors;

    /**
     * Instantiates a new invalid configuration exception.
     *
     * @param errors
     *            the errors
     */
    public InvalidConfigurationException(List<String> errors) {
        super();
        this.errors = errors;
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
