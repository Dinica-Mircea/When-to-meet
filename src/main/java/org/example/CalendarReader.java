package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CalendarReader {

    public static List<TimePeriod> readCalendar(String fileName, int lineNumber) {
        List<TimePeriod> bookedPeriods = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            //reach the line desired
            for(int k=0;k<lineNumber;k=k+1){
                reader.readLine();
            }
            String line = reader.readLine();
            if (line != null) {
                //search for the first parenthesis [, after that the time periods start
                int firstParenthesisPlace=0;
                while(line.charAt(firstParenthesisPlace)!='['){
                    firstParenthesisPlace=firstParenthesisPlace+1;
                }
                line = line.substring(firstParenthesisPlace, line.length() - 1);
                String[][] periodsArray = parsePeriodsArray(line);
                //create the list of time periods
                for (String[] period : periodsArray) {
                    LocalTime startTime = LocalTime.parse(period[0]);
                    LocalTime endTime = LocalTime.parse(period[1]);
                    bookedPeriods.add(new TimePeriod(startTime, endTime));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookedPeriods;
    }

    private static String[][] parsePeriodsArray(String periodsString) {
        String[] periods = periodsString.split("], \\[");
        String[][] periodsArray = new String[periods.length][2];
        for (int i = 0; i < periods.length; i++) {
            String[] period = periods[i].split(",");
            period[0]=period[0].replace("'","").replace("[","");
            if(period[0].length()<5){
                //we treat the case that the hour is only written with one digit
                //for example 9:00
                //we transform it into 09:00
                period[0]= "0" + period[0];
            }
            periodsArray[i][0] = period[0];
            if(period[1].length()<5){
                //we treat the case that the hour is only written with one digit
                //for example 9:00
                //we transform it into 09:00
                period[1]= "0" + period[1];
            }
            //we delete the characters ' and ] from the strings
            period[1]=period[1].replace("'","").replace("]","");
            periodsArray[i][1] = period[1];
        }
        return periodsArray;
    }
    public static int readMeetingTimeMinutes(String fileName){
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int meetingTimeMinutes;

            while ((line = br.readLine()) != null) {
                //reach the desired line
                if (line.startsWith("Meeting Time Minutes:")) {
                    //get the meeting minutes
                    String timeMinutesString = line.substring("Meeting Time Minutes: ".length());
                    meetingTimeMinutes = Integer.parseInt(timeMinutesString.trim());
                    return meetingTimeMinutes;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //if the lines is not found return 1
        return -1;
    }

}