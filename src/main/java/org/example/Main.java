package org.example;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        //read the data from the input file
        List<TimePeriod> bookedCalendar1 = CalendarReader.readCalendar("src/main/resources/data.in", 0);
        List<TimePeriod> rangeLimitsCalendar1 = CalendarReader.readCalendar("src/main/resources/data.in", 1);
        List<TimePeriod> bookedCalendar2 = CalendarReader.readCalendar("src/main/resources/data.in", 2);
        List<TimePeriod> rangeLimitsCalendar2 = CalendarReader.readCalendar("src/main/resources/data.in", 3);
        int meetingTimeMinutes = CalendarReader.readMeetingTimeMinutes("src/main/resources/data.in");

        //we try to create a list that has the time periods in which any one of them is booked
        List<TimePeriod> bookedCalendarForBoth = new ArrayList<>();
        for (TimePeriod timePeriod1 : bookedCalendar1) {
            //for each time period from the first person's calendar we search for a time period in the second
            //person's calendar where they overlap, making the time period that we add into the list bigger
            //if there is no overlap, we just add the time period as it is from the first person's calendar
            LocalTime startTime = timePeriod1.getStartTime();
            LocalTime endTime = timePeriod1.getEndTime();
            for (TimePeriod timePeriod2 : bookedCalendar2) {
                if (timePeriod1.overlap(timePeriod2)) {
                    //we see if the start of the overlapping time period is before the current saved start time
                    //if it is we update it
                    if (startTime.isAfter(timePeriod2.getStartTime()))
                        startTime = timePeriod2.getStartTime();
                    //we do the same for the end time, but if it is after
                    if (endTime.isBefore(timePeriod2.getEndTime()))
                        endTime = timePeriod2.getEndTime();
                }
            }
            //we add the new time period after iterating through the second person's booked calendar
            bookedCalendarForBoth.add(new TimePeriod(startTime, endTime));
        }
        //if there is a time period left from the second person's calendar that wasn't added to the list, we have to add it
        //that can happen if there is no time period from the first person's calendar that overlaps with it
        for (TimePeriod timePeriod2 : bookedCalendar2) {
            boolean notAdded = true;
            for (TimePeriod timePeriod : bookedCalendarForBoth) {
                if (timePeriod2.overlap(timePeriod)) {
                    notAdded = false;
                }
            }
            if (notAdded)
                bookedCalendarForBoth.add(timePeriod2);
        }
        //we sort the booked calendar for both by the starting time of the time period
        bookedCalendarForBoth.sort(Comparator.comparing(TimePeriod::getStartTime));

        //we get the minimum range in which they are both available
        LocalTime rangeStartTimeBoth;
        LocalTime rangeEndTimeBoth;
        if (rangeLimitsCalendar1.get(0).getStartTime().isBefore(rangeLimitsCalendar2.get(0).getStartTime()))
            rangeStartTimeBoth = rangeLimitsCalendar1.get(0).getStartTime();
        else
            rangeStartTimeBoth = rangeLimitsCalendar2.get(0).getStartTime();
        if (rangeLimitsCalendar1.get(0).getEndTime().isBefore(rangeLimitsCalendar2.get(0).getEndTime()))
            rangeEndTimeBoth = rangeLimitsCalendar1.get(0).getEndTime();
        else
            rangeEndTimeBoth = rangeLimitsCalendar2.get(0).getEndTime();

        //now we find all the available time when they can meet
        List<TimePeriod> availableTimes = new ArrayList<>();

        //we first see if they can meet before the booked times start
        //that is between the start time of the range and the start time of the first booked period
        TimePeriod first = bookedCalendarForBoth.get(0);
        if (first.getStartTime().isAfter(rangeStartTimeBoth)) {
            long minutes = Duration.between(rangeStartTimeBoth, first.getStartTime()).toMinutes();
            //we check if in the available time they can arrange a meeting
            if (minutes != 0 && minutes >= meetingTimeMinutes)
                availableTimes.add(new TimePeriod(rangeStartTimeBoth, first.getEndTime()));
        }

        //then we see if they can meet between the booked periods
        //that is between one booked time period's end time and the next one's start time
        for (int i = 0; i < bookedCalendarForBoth.size() - 1; i = i + 1) {
            TimePeriod t1 = bookedCalendarForBoth.get(i);
            TimePeriod t2 = bookedCalendarForBoth.get(i + 1);
            if (t1.getEndTime().isAfter(rangeStartTimeBoth) && t2.getStartTime().isBefore(rangeEndTimeBoth)) {
                long minutes = Duration.between(t1.getEndTime(), t2.getStartTime()).toMinutes();
                //we check if in the available time they can arrange a meeting
                if (minutes != 0 && minutes >= meetingTimeMinutes) {
                    availableTimes.add(new TimePeriod(t1.getEndTime(), t2.getStartTime()));
                }
            }
        }
        //at last, we check if they can meet after the booked time and until the end time of the range
        //that is between the end time of the last booked period and the range end time
        TimePeriod last = bookedCalendarForBoth.get(bookedCalendarForBoth.size() - 1);
        if (last.getEndTime().isBefore(rangeEndTimeBoth)) {
            long minutes = Duration.between(last.getEndTime(), rangeEndTimeBoth).toMinutes();
            //we check if in the available time they can arrange a meeting
            if (minutes != 0 && minutes >= meetingTimeMinutes)
                availableTimes.add(new TimePeriod(last.getEndTime(), rangeEndTimeBoth));
        }
        //finally we print the available times when they can meet
        System.out.println(availableTimes);
    }
}