package org.example;
import java.time.LocalTime;

public class TimePeriod {
    private final LocalTime startTime;
    private final LocalTime endTime;

    public TimePeriod(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean overlap(TimePeriod t){
        //method for finding if two time periods overlap
        //the if represents the case in which they are one after the other or vice-versa
        if(t.getEndTime().isBefore(this.startTime) || t.getStartTime().isAfter(this.endTime) ||
                t.getEndTime().equals(this.startTime) || t.getStartTime().equals(this.endTime) )
            return false;

        return true;
    }

    @Override
    public String toString() {
        return "['" + startTime +
                "','" + endTime +
                "']";
    }
}
