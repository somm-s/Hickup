package ch.cydcampus.hickup.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Represents a microsecond precision time interval and provides methods for conversion.
 */
public class TimeInterval implements Comparable<TimeInterval> {

    public static final DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd HH:mm:ss")
        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true) // Append microseconds with 0-6 digits
        .toFormatter()
        .withZone(ZoneId.of("UTC"));
    private long start;
    private long end;

    public static long timeToMicro(Timestamp timestamp) {
        return timestamp.getTime() * 1000 + (timestamp.getNanos() / 1000) % 1000;
    }

    public TimeInterval() {
        this.start = -1; // uninitialized
        this.end = -1;
    }

    public TimeInterval(TimeInterval timeInterval) {
        this.start = timeInterval.getStart();
        this.end = timeInterval.getEnd();
    }

    public TimeInterval(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public TimeInterval(Timestamp start, Timestamp end) {
        this.start = timeToMicro(start);
        this.end = timeToMicro(end);
    }

    public TimeInterval(String start, String end) {
        this.start = timeToMicro(timeFromString(start));
        this.end = timeToMicro(timeFromString(end));
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;        
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;        
    }

    public void updateTimeInterval(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public void updateTimeInterval(Timestamp start, Timestamp end) {
        this.start = timeToMicro(start);
        this.end = timeToMicro(end);
    }

    public boolean doIntersect(TimeInterval other) {
        if(start == -1 || end == -1 || other.getStart() == -1 || other.getEnd() == -1) {
            return false;
        }
        return !(start > other.getEnd() || end < other.getStart());
    }

    public TimeInterval union(TimeInterval timeInterval) {
        if(start == -1 || end == -1) {
            return new TimeInterval(timeInterval);
        } else if(timeInterval.getStart() == -1 || timeInterval.getEnd() == -1) {
            return new TimeInterval(this);
        }
        long newStart = Math.min(start, timeInterval.getStart());
        long newEnd = Math.max(end, timeInterval.getEnd());
        return new TimeInterval(newStart, newEnd);
    }

    public void addInterval(TimeInterval other) {
        if(start == -1 || end == -1) {
            setContentTo(other);
            return;
        }
        long newStart = Math.min(start, other.getStart());
        long newEnd = Math.max(end, other.getEnd());
        this.start = newStart < 0 ? this.start : newStart;
        this.end = newEnd < 0 ? this.end : newEnd;
    }

    /**
     * Returns the time in the gap between this time interval and the other 
     * time interval in microseconds. Return 0 if the time intervals overlap.
     */
    public long getDifference(TimeInterval other) {
        // should not be called on uninitialized time intervals
        assert start != -1 && end != -1 && other.getStart() != -1 && other.getEnd() != -1;

        long difference = 0;

        if (other.getStart() > end) {
            difference = other.getStart() - end;
        } else if (other.getEnd() < start) {
            difference = start - other.getEnd();
        }

        return difference;
    }

    public void setContentTo(TimeInterval timeInterval) {
        this.start = timeInterval.getStart();
        this.end = timeInterval.getEnd();
    }

    public static Instant microToInstant(long micros) {
        long millis = micros / 1000;
        int nanos = (int) ((micros % 1000) * 1000);
        return Instant.ofEpochMilli(millis).plusNanos(nanos);
    }

    public static String microToTime(long micros) {
        long seconds = micros / 1000000;
        long nanos = (micros % 1000000) * 1000;

        Timestamp timestamp = new Timestamp(seconds * 1000);
        timestamp.setNanos((int) nanos); // Set nanoseconds from microseconds
        return timestamp.toString();
    }

    public String toString() {
        Instant startInstant = microToInstant(start);
        Instant endInstant = microToInstant(end);
        Timestamp startTimestamp = Timestamp.from(startInstant);
        Timestamp endTimestamp = Timestamp.from(endInstant);
        return "[" + startTimestamp.toString() + ", " + endTimestamp.toString() + "]";
    }


    private Timestamp timeFromString(String timeString) {
        Instant instant = Instant.from(timeFormatter.parse(timeString));
        java.sql.Timestamp timestamp = java.sql.Timestamp.from(instant);
        return timestamp;
    }

    public String timeToString(Timestamp timestamp) {
        Instant instant = timestamp.toInstant();
        String timeString = timeFormatter.format(instant);
        return timeString;
    }

    @Override
    public int compareTo(TimeInterval other) {
        if (start < other.getStart()) {
            return -1;
        } else if (start > other.getStart()) {
            return 1;
        } else {
            if (end < other.getEnd()) {
                return -1;
            } else if (end > other.getEnd()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public boolean contains(TimeInterval other) {
        return start <= other.getStart() && end >= other.getEnd();
    }

}