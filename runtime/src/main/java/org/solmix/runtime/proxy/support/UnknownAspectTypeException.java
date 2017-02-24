package org.solmix.runtime.proxy.support;

public class UnknownAspectTypeException extends IllegalArgumentException {

	private static final long serialVersionUID = -1435365557865933454L;

	public UnknownAspectTypeException(Object advice) {
		super("Aspect object [" + advice + "] is neither a supported subinterface of org.solmix.runtime.proxy.Aspect");
	}

	/**
	 * Create a new UnknownAdviceTypeException with the given message.
	 * @param message the message text
	 */
	public UnknownAspectTypeException(String message) {
		super(message);
	}
}
