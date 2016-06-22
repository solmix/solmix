
package org.solmix.commons.schedule;

public class EmptyScheduleException extends Exception {

	private static final long serialVersionUID = 1L;

	EmptyScheduleException(){
	super("No items in schedule");
    }
}
