package org.solmix.commons.schedule;


public class ScheduledItem {
    private Object  obj;      // Scheduled object
    private long    interval; // Execution interval
    private long    nextTime; // Absolute time the object should be invoked
    private boolean repeat;   // Should this event be repeated?
    private long    id;       // Global schedule ID
    
    ScheduledItem(Object obj, long interval, boolean prev,
                  boolean repeat, long id) 
    {
        this.obj      = obj;
        this.interval = interval;
        this.repeat   = repeat;
        this.id       = id;
        if (prev)
            this.nextTime = ScheduledItem.getScheduledTimePrev(interval);
        else
            this.nextTime = ScheduledItem.getScheduledTime(interval);
    }

    /**
     * The the previous fire time for an item that scheduled
     *
     * @param interval The millisecond interval
     *
     * @return The previous fire time this item should be executed
     */
    public static long getScheduledTimePrev(long interval){
        long currentTime = System.currentTimeMillis();
        return currentTime - (currentTime % interval);
    }
    
    /** 
     * Get the time when something scheduled right now would be
     * executed.
     *
     * @param interval The millisecond interval that would ordinarily be
     *                 passed to a ScheduledItem constructor.
     *
     * @return the time when a scheduled item with the passed interval would
     *         be executed.
     */
    public static long getScheduledTime(long interval){
        long currentTime = System.currentTimeMillis();
        return currentTime + interval - (currentTime % interval);
    }

    public Object getObj() {
        return obj;
    }
    
    public long getInterval() {
        return interval;
    }
    
    public long getNextTime() {
        return nextTime;
    }
        
    public boolean isRepeat() {
        return repeat;
    }
    
    public long getId() {
        return id;
    }
    
    /**
     * Step the nextTime attribute to the current nextTime plus the 
     * interval for which the object should repeat.
     */
    public void stepNextTime(){
        long curTime = System.currentTimeMillis();
        
        this.nextTime += this.interval;
        // Somehow the clock jumped (laptop was suspended?), or we got really 
        // far behind.  Jump up to the next slot  
        if (this.nextTime < curTime) {
            this.nextTime = getScheduledTime(this.interval);
        }
    }
}
