package com.Scheduling.Web.App.services;

import com.Scheduling.Web.App.models.LoginStats;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HelperServiceClass {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final ZoneId CENTRAL_TIME_ZONE = ZoneId.of("America/Chicago");
    public static String convertToUserTime(String centralDateTimeStr) {
        // Parse the central time string to a ZonedDateTime object
        ZonedDateTime centralDateTime = ZonedDateTime.parse(centralDateTimeStr, DATE_TIME_FORMATTER.withZone(CENTRAL_TIME_ZONE));

        // Get the user's time zone
        ZoneId userTimeZone = ZoneId.systemDefault();

        // Convert the central time to the user's time zone
        ZonedDateTime userDateTime = centralDateTime.withZoneSameInstant(userTimeZone);

        // Format the user date and time as a string
        return DATE_TIME_FORMATTER.format(userDateTime);
    }
    public static boolean isWithinBusinessHours(LocalDate startDate, String startTimeStr, LocalDate endDate, String endTimeStr) {
        // Define the start and end times for the business day in the business time zone
        LocalTime businessStartTime = LocalTime.of(8, 0);
        LocalTime businessEndTime = LocalTime.of(22, 0);

        // Parse the start and end time strings into LocalTime objects

        if (startTimeStr.length() == 4) {
            startTimeStr = "0" + startTimeStr;
        }
        if (endTimeStr.length() == 4) {
            endTimeStr = "0" + endTimeStr;
        }

        LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime endTime = LocalTime.parse(endTimeStr, DateTimeFormatter.ofPattern("HH:mm"));

        // Combine the start and end date and time values into LocalDateTime objects
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        // Get the user's time zone and the business time zone
        ZoneId userTimeZone = ZoneId.systemDefault();
        ZoneId businessTimeZone = ZoneId.of("America/New_York"); // Gets business time zone ID

        // Convert the start and end times of the appointment from the user's time zone to the business time zone
        ZonedDateTime userStartDateTime = startDateTime.atZone(userTimeZone);
        ZonedDateTime businessStartDateTime = userStartDateTime.withZoneSameInstant(businessTimeZone);
        LocalTime businessStartTimeOfDay = businessStartDateTime.toLocalTime();

        ZonedDateTime userEndDateTime = endDateTime.atZone(userTimeZone);
        ZonedDateTime businessEndDateTime = userEndDateTime.withZoneSameInstant(businessTimeZone);
        LocalTime businessEndTimeOfDay = businessEndDateTime.toLocalTime();

        // Check if the start and end times of the appointment fall within the business hours
        if (businessStartTimeOfDay.isBefore(businessStartTime) || businessEndTimeOfDay.isAfter(businessEndTime)) {
            return false;
        }

        // Check if the appointment falls on a weekend
        DayOfWeek startDayOfWeek = businessStartDateTime.getDayOfWeek();
        DayOfWeek endDayOfWeek = businessEndDateTime.getDayOfWeek();
        if (startDayOfWeek == DayOfWeek.SATURDAY || startDayOfWeek == DayOfWeek.SUNDAY || endDayOfWeek == DayOfWeek.SATURDAY || endDayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }

        return true;
    }
    public static String convertToCentralTime(LocalDate date, String timeStr) {
        if (timeStr.length() == 4) {
            timeStr = "0" + timeStr;
        }
        // Parse the time string into a LocalTime object using the ISO format
        LocalTime time = LocalTime.parse(timeStr);

        // Combine the date and time into a LocalDateTime object
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        // Define the time zone IDs for the user's time zone and the central time zone
        ZoneId userTimeZone = ZoneId.systemDefault(); // Replace with the user's time zone ID
        ZoneId centralTimeZone = ZoneId.of("America/Chicago"); // Central time zone ID

        // Convert the LocalDateTime object to a ZonedDateTime object in the user's time zone
        ZonedDateTime userDateTime = dateTime.atZone(userTimeZone);

        // Convert the ZonedDateTime object to a new ZonedDateTime object in the central time zone
        ZonedDateTime centralDateTime = userDateTime.withZoneSameInstant(centralTimeZone);

        // Define a format for the date and time string that is compatible with MySQL's DATETIME type
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format the centralDateTime object as a string using the specified format
        String centralDateTimeStr = centralDateTime.format(formatter);

        // Return the formatted string
        return centralDateTimeStr;
    }
    public static LocalDate getcurrentCentralTime() {
        LocalDateTime localDateTime = LocalDateTime.now();

        // Convert it to Central Time
        ZoneId centralTimeZone = ZoneId.of("America/Chicago");
        ZonedDateTime centralDateTime = localDateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(centralTimeZone);

        // Format it to the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = centralDateTime.format(formatter);

        // Convert the formatted date string to LocalDate
        LocalDate date = LocalDate.parse(formattedDate.substring(0, 10));
        return date;
    }
    /*public static ArrayList<LoginStats> getLoginStats(LocalDate startDate, LocalDate endDate) {
        ArrayList<LoginStats> loginStats = new ArrayList<>();
        String fileName = "login_activity.txt";
        HashMap<String, LoginStats> loginStatsMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" - ");
                if (parts.length == 3) {
                    String userName = parts[0];
                    String timestamp = parts[1];
                    String status = parts[2];

                    LoginStats stats = loginStatsMap.getOrDefault(userName, new LoginStats(userName));
                    if ("Success".equalsIgnoreCase(status)) {
                        stats.incrementSuccessCount();
                    } else {
                        stats.incrementFailureCount();
                    }
                    stats.setLastAttempt(timestamp);

                    loginStatsMap.put(userName, stats);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        loginStats.addAll(loginStatsMap.values());
        return loginStats;
    }*/
    public static ArrayList<LoginStats> getLoginStats(LocalDate startDate, LocalDate endDate) {
        ArrayList<LoginStats> loginStats = new ArrayList<>();
        String fileName = "login_activity.txt";
        HashMap<String, LoginStats> loginStatsMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("America/Chicago"));
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" - ");
                if (parts.length == 3) {
                    String userName = parts[0];
                    String timestamp = parts[1];
                    String status = parts[2];

                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestamp, formatter);
                    LocalDate loginDate = zonedDateTime.toLocalDate();

                    boolean withinDateRange = true;
                    if (startDate != null && endDate != null) {
                        withinDateRange = !loginDate.isBefore(startDate) && !loginDate.isAfter(endDate);
                    } else if (startDate != null) {
                        withinDateRange = !loginDate.isBefore(startDate);
                    } else if (endDate != null) {
                        withinDateRange = !loginDate.isAfter(endDate);
                    }

                    if (withinDateRange) {
                        LoginStats stats = loginStatsMap.getOrDefault(userName, new LoginStats(userName));
                        if ("Success".equalsIgnoreCase(status)) {
                            stats.incrementSuccessCount();
                        } else {
                            stats.incrementFailureCount();
                        }
                        stats.setLastAttempt(timestamp);

                        loginStatsMap.put(userName, stats);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        loginStats.addAll(loginStatsMap.values());
        return loginStats;
    }
}
