package org.solmix.commons.schedule;


public class ScheduleException extends Exception {

	private static final long serialVersionUID = 4843644638590788923L;

	public ScheduleException() {
        super();
    }

    public ScheduleException(String s) {
        super(s);
    }

    public ScheduleException(Throwable t) {
        super(t);
    }

    public ScheduleException(String s, Throwable t) {
        super(s, t);
    }
}
