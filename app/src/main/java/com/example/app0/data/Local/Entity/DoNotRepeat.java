package com.example.app0.data.Local.Entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class DoNotRepeat implements Repeat {

    @Override
    public List<Calendar> generateRepeatDates(Calendar startDate, Calendar endDate) {
        List<Calendar> repeatDates = new ArrayList<>();
        repeatDates.add(startDate);

        return repeatDates;
    }

    @Override
    public String getRepeatAsString() {
        return "Do Not Repeat";
    }

    @Override
    public Boolean shouldCreateInstance(Calendar startDate, Calendar today) {
        return startDate == today;
    }
}
