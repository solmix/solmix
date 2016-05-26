
package org.solmix.commons.timer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class StopWatch {

    private long start;
    private long end;
    private Map<String, LinkedList<TimeSlice>> markerMap;
    
    public StopWatch() {
        reset();
    }

    public void markTimeBegin (String marker) {
        LinkedList<TimeSlice> list;
        if (null == (list = markerMap.get(marker))) {
            list = new LinkedList<TimeSlice>();
            markerMap.put(marker, list);
        }
        list.add(new TimeSlice(marker));
    }

    public void markTimeEnd (String marker) {
        final LinkedList<TimeSlice> list = markerMap.get(marker);
        if (list == null) {
            throw new IllegalArgumentException("Invalid marker");
        }
        final TimeSlice ts = list.getLast();
        ts.setFinished();
    }

    public StopWatch(long start) {
        this.start = start;
        markerMap = new HashMap<String, LinkedList<TimeSlice>>();
    }

    public long reset() {
        try {
            return this.getElapsed();
        } finally {
            start = now();
            markerMap = new HashMap<String, LinkedList<TimeSlice>>();
        }
    }
    
    private final long now() {
        return System.currentTimeMillis();
    }
    
    public long getElapsed() {
        end = now();
        return end - start;
    }

    public String toString() {
        final long elap = getElapsed();
        final StringBuilder buf = new StringBuilder(64);
        buf.append(elap).append(" ms");
        if (markerMap.size() > 0) {
            buf.append(" { Markers: ");
            for (Entry<String, LinkedList<TimeSlice>> entry : markerMap.entrySet()) {
                final String marker = entry.getKey();
                final LinkedList<TimeSlice> list = entry.getValue();
                writeBuf(marker, list, buf);
            }
            buf.append(" } ");
        }
        return buf.toString();
    }

    private void writeBuf(String marker, List<TimeSlice> tsList, StringBuilder buf) {
        long total = -1;
        for (final TimeSlice ts : tsList) {
            final Long elapsed = ts.getElapsed();
            if (elapsed == null) {
                continue;
            }
            if (total == -1) {
                total = 0;
            }
            total += elapsed.longValue();
        }
        buf.append(" [").append(marker).append("=")
           .append((total == -1) ? "null" : total + " ms")
           .append("]");
    }

    class TimeSlice {
        String marker;
        long begin;
        long end;
        public TimeSlice (String marker) {
            this.marker = marker;
            begin = now();
            end = 0;
        }
        public void setFinished () {
            end= now();
        }
        public Long getElapsed() {
            if (end == 0) {
                return now();
            }
            return new Long(end - begin);
        }
    }
}
