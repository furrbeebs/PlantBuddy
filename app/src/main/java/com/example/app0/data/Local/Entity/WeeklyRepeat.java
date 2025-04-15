package com.example.app0.data.Local.Entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeeklyRepeat implements Repeat {
    @Override
    public List<Calendar> generateRepeatDates(Calendar startDate, Calendar endDate) {
        List<Calendar> repeatDates = new ArrayList<>();

        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);

        while (startDate.before(endDate) || startDate.equals(endDate)) {
            repeatDates.add((Calendar) startDate.clone());
            startDate.add(Calendar.WEEK_OF_YEAR, 1);        // moving to next week
        }

        return repeatDates;
    }

    @Override
    public String getRepeatAsString() {
        return "Weekly";
    }

    @Override
    public Boolean shouldCreateInstance(Calendar startDate, Calendar today) {
        return today.get(Calendar.DAY_OF_WEEK) == startDate.get(Calendar.DAY_OF_WEEK);
    }
}
