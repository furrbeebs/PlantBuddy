package com.example.app0.data.Local.Entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DailyRepeat implements Repeat {
    @Override
    public List<Calendar> generateRepeatDates(Calendar startDate, Calendar endDate) {
        List<Calendar> repeatDates = new ArrayList<>();

        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);

        while (startDate.before(endDate) || startDate.equals(endDate)) {
            repeatDates.add((Calendar) startDate.clone());
            startDate.add(Calendar.DATE, 1);        // moving to next day
        }

        return repeatDates;
    }

    @Override
    public String getRepeatAsString() {
        return "Daily";
    }

    @Override
    public Boolean shouldCreateInstance(Calendar startDate, Calendar today) {
        return true;
    }
}
