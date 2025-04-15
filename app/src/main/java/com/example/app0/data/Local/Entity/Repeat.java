package com.example.app0.data.Local.Entity;

import java.util.Calendar;
import java.util.List;

public interface Repeat {
    List<Calendar> generateRepeatDates(Calendar startDate, Calendar endDate);

    String getRepeatAsString();

    Boolean shouldCreateInstance(Calendar startDate, Calendar today);
}
