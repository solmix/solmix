package org.solmix.runtime.transaction;

public interface ResourceBinder {
	void reset();
	void unbind();
	boolean isVoid();
}
