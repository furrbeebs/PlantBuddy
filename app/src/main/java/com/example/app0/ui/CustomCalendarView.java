package com.example.app0.ui;
// Customise calendar to show mood icon + user selection interaction

import android.content.Context;
import android.util.AttributeSet;

// this that built in function
import android.widget.CalendarView;

import java.util.HashMap;

// Extends CalendarView
public class CustomCalendarView extends CalendarView {
    private Map<Long, String> moodMap = new HashMap<>(); // Map to store mood icons for specific dates

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMoodForDate(long dateInMillis, String mood) {
        moodMap.put(dateInMillis, mood);  // Store the mood for the selected date
        invalidate(); // Redraw the view
    }

    @Override
    protected void onDateChanged(CalendarView view, int year, int month, int dayOfMonth) {
        super.onDateChanged(view, year, month, dayOfMonth);

        long selectedDateInMillis = getDate();  // Get the selected date in milliseconds
        String mood = moodMap.get(selectedDateInMillis);  // Check if there's a mood for the selected date

        if (mood != null) {
            // Display mood icon for the selected date
        }
    }

    public void setDateSelectedListener(OnDateSelectedListener listener) {
        // Allow other classes listen to selection events
        // e.g. the user taps on a date then this listener can trigger actions like saving the mood, updating calendar
    }
}

