package com.example.app0.moodtracker;
// To show mood icons for each date

import android.content.Context;

import com.example.app0.ui.CustomCalendarView;

import java.util.HashMap;

// For the mood icons that will replace the calendar dates
public class MoodCalendarView extends CustomCalendarView {
    private HashMap<String, MoodType> moodMap = new HashMap<>();

    public MoodCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMood(String date, MoodType mood) {
        moodMap.put(date, mood);
        invalidate(); // Refresh UI
    }

    public MoodType getMood(String date) {
        return moodMap.getOrDefault(date, MoodType.NONE);
    }
}

