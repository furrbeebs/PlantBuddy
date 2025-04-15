package com.example.app0.data.Converters;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class CalendarConverter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @TypeConverter
    public static String fromCalendar(Calendar date) {
        if (date == null) {
            return null;
        }
        Date time = date.getTime();
        LocalDate localDate = time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.format(formatter); // Convert Calendar to String
    }

    @TypeConverter
    public static Calendar toCalendar(String dateString) {
        if (dateString == null) {
            return null;
        }
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
        return calendar; // Convert String to LocalDate
    }
}
