package com.example.app0.moodtracker;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;

import com.example.app0.R;
import com.example.app0.data.Local.Entity.CalendarItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MoodCalendarView extends LinearLayout {

    // selectedPosition on the calendar grid (date box)
    private int selectedPosition;
    private TextView monthYearText; // Header month/year
    private GridView calendarGrid;

    //  Object that manages how dates are displayed in calendar grid
    private CalendarAdapter adapter;

    // Object that holds date, month, year and time
    private Calendar currentCalendar = Calendar.getInstance();
    private OnDateSelectedListener dateSelectedListener;
    private OnMonthChangedListener monthChangedListener;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    // Map to store mood data: date string -> MoodEntry
    private Map<String, MoodEntry> moodEntries = new HashMap<>();

    public MoodCalendarView(Context context) {
        super(context);
        init(context);
    }

    public MoodCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    // Interface for date selection when tapped
    public interface OnDateSelectedListener {
        void onDateSelected(int year, int month, int day, @Nullable MoodEntry moodEntry);
    }
    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.dateSelectedListener = listener;
    }

    // Interface for month change
    public interface OnMonthChangedListener {
        void onMonthChanged(int year, int month);
    }
    public void setOnMonthChangedListener(OnMonthChangedListener listener) {
        this.monthChangedListener = listener;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        // if adapter exist
        if (adapter != null) {
            adapter.setSelectedPosition(position);
        }
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.calendar_layout, this, true);

        // Initialize views
        // month/year (title)
        monthYearText = findViewById(R.id.month_year_text);
        calendarGrid = findViewById(R.id.calendar_grid);
        ImageButton prevButton = findViewById(R.id.prev_month_button);
        ImageButton nextButton = findViewById(R.id.next_month_button);

        prevButton.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendarTitle();

            // If listener exist, get the current year and month
            if (monthChangedListener != null) {
                monthChangedListener.onMonthChanged(
                        currentCalendar.get(Calendar.YEAR),
                        currentCalendar.get(Calendar.MONTH)
                );
            }
        });

        nextButton.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendarTitle();

            // If listener exist, get the current year and month
            if (monthChangedListener != null) {
                monthChangedListener.onMonthChanged(
                        currentCalendar.get(Calendar.YEAR),
                        currentCalendar.get(Calendar.MONTH)
                );
            }
        });

        // Set up calendar adapter
        adapter = new CalendarAdapter(context);
        calendarGrid.setAdapter(adapter);

        // Set date click listener (when user taps on a date of the grid)
        calendarGrid.setOnItemClickListener((parent, view, position, id) -> {
            // Get the date based on the position/box that user tapped
            CalendarAdapter.CalendarDate date = adapter.getItem(position);

            // If the tapped date's month is the same as the CURRENT month that is showing
            if (date.getMonth() == currentCalendar.get(Calendar.MONTH)) {
                // get the date
                String dateKey = getDateKey(date.getYear(), date.getMonth(), date.getDay());

                // Update the selected position
                setSelectedPosition(position);

                // If dataSelectedListener exist
                if (dateSelectedListener != null) {
                    // Use its own method to get the date
                    dateSelectedListener.onDateSelected(
                            date.getYear(),
                            date.getMonth(),
                            date.getDay(),
                            moodEntries.get(dateKey)
                    );
                }
            }
        });
    }

    // Update the calendar page title (month/year > = monthYearText) & sync it with the
    // calendar < month/year > title
    public void updateCalendarTitle() {
        // Update grid
        adapter.updateCalendarGrid(currentCalendar, moodEntries);
    }

    // Update the header (< month/year >) inside calendar
    public void setCalendarLayoutHeader(int year, int month) {
        // Update the calendar + header (month/year)
        currentCalendar.set(Calendar.YEAR, year);
        currentCalendar.set(Calendar.MONTH, month);

        // Update calendar display title
        updateCalendarTitle();
    }

    public void setMoodEntry(int year, int month, int day, int moodResId, String notes) {
        String dateKey = getDateKey(year, month, day);
        MoodEntry entry = new MoodEntry(dateKey, moodResId, notes);
        moodEntries.put(dateKey, entry);
    }

    public MoodEntry getMoodEntry(int year, int month, int day) {
        return moodEntries.get(getDateKey(year, month, day));
    }

    public void removeMoodEntry(int year, int month, int day) {
        String dateKey = getDateKey(year, month, day);
        moodEntries.remove(dateKey);
    }

    // Class for mood entries
    public static class MoodEntry {
        private String date; // Format: "yyyy-MM-dd"
        private final int moodResId;
        private final String notes;

        public MoodEntry(String date, int moodResId, String notes) {
            this.date = date;
            this.moodResId = moodResId;
            this.notes = notes;
        }

        public String getDate() {
            return date;
        }

        public int getMoodResId() {
            return moodResId;
        }

        public String getNotes() {
            return notes;
        }
    }

    // Helper method to create consistent date keys
    private String getDateKey(int year, int month, int day) {
        return String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
    }

    // 2 methods below are used in Mood Fragment for easier access
    public int getCurrentYear() {
        return currentCalendar.get(Calendar.YEAR);
    }

    public int getCurrentMonth() {
        return currentCalendar.get(Calendar.MONTH);
    }

    // Converts data in a list to map
    // This code is written and edited by Ziwei
    public void updateWithCalendarItems(List<CalendarItem> items) {
        moodEntries.clear();
        // If there is data(CalendarItems) inside the list
        if (items != null) {
            for (CalendarItem item : items) {
                // Get the date
                Date date = item.getDate();
                // If date exist
                if (date != null) {
                    try {
                        String dateKey = DATE_FORMAT.format(date);
                        // Insert into the map
                        moodEntries.put(dateKey, new MoodEntry(
                                dateKey,
                                item.getMood().getIconResId(),
                                item.getEntry())
                        );
                    } catch (Exception e) {
                        Log.e("MoodCalendarView", "Error processing calendar item into moodEntries Map", e);
                    }
                } else {
                    Log.w("MoodCalendarView", "Encountered CalendarItem with null date");
                }
            }
        }
        updateCalendarTitle();
        // ^ Contains  adapter.updateCalendarGrid which updates the calendar grid
    }

    // Adapter for calendar grid
    private static class CalendarAdapter extends BaseAdapter {
        private final Context context;
        private final List<CalendarDate> cells = new ArrayList<>();
        private final LayoutInflater inflater;
        private int selectedPosition; // Store selected position here

        public CalendarAdapter(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        public void setSelectedPosition(int position) {
            this.selectedPosition = position;
            // Indicate a change in data & refresh the UI
            notifyDataSetChanged();
        }

        // The code below is written by AI and edited by Ziwei to ensure the calendar only shows dates of the current month
        public void updateCalendarGrid(Calendar calendar, Map<String, MoodEntry> moodEntries) {
            cells.clear();

            // Get current month and year
            int currentMonth = calendar.get(Calendar.MONTH);
            int currentYear = calendar.get(Calendar.YEAR);

            // Create a new Calendar set to the first day of the month
            Calendar monthCalendar = (Calendar) calendar.clone();
            monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

            // Get the WEEK DAY of the first day. Need to -1 because 0 - 6 buy dayof week return 1-7
            int firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;

            // Get the last day of the month
            int lastDay = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            // First, add empty placeholder cells for days before the 1st of the month
            for (int i = 0; i < firstDayOfMonth; i++) {
                // Add empty placeholder cells with an invalid day (-1)
                cells.add(new CalendarDate(-1, -1, -1, 0));
            }

            // Then add actual days of the current month
            for (int day = 1; day <= lastDay; day++) {
                // Format date key
                String dateKey = String.format(Locale.US, "%04d-%02d-%02d", currentYear, currentMonth + 1, day);
                MoodEntry entry = moodEntries.get(dateKey);

                cells.add(new CalendarDate(day, currentMonth, currentYear, entry != null ? entry.getMoodResId() : 0));
            }

            // Calculate remaining cells needed to complete the last week row
            int totalCells = cells.size();
            int remainingCells = 7 - (totalCells % 7);
            if (remainingCells < 7) {
                // Add placeholder cells at the end to complete the grid
                for (int i = 0; i < remainingCells; i++) {
                    cells.add(new CalendarDate(-1, -1, -1, 0));
                }
            }

            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return cells.size();
        }

        @Override
        public CalendarDate getItem(int position) {
            return cells.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View cellView = convertView;
            if (cellView == null) {
                cellView = inflater.inflate(R.layout.day_cell_layout, parent, false);
            }

            // Get day information
            CalendarDate date = getItem(position);

            // Update views
            TextView dayText = cellView.findViewById(R.id.day_text);
            ImageView moodIcon = cellView.findViewById(R.id.mood_icon);

            // This code is written by AI and edited by Ziwei
            // Check if this is a placeholder cell (day = -1)
            if (date.getDay() == -1) {
                // This is a placeholder - make it blank/invisible
                dayText.setText("");
                moodIcon.setVisibility(View.INVISIBLE);

            } else {
                // This is a real date - show it normally
                dayText.setText(String.valueOf(date.getDay()));

                // Show mood icon if available
                if (date.getMoodResId() != 0) {
                    moodIcon.setVisibility(View.VISIBLE);
                    moodIcon.setImageResource(date.getMoodResId());
                } else {
                    moodIcon.setVisibility(View.GONE);
                }
            }
            return cellView;
        }


        // Data class for calendar dates
        private static class CalendarDate {
            private final int day;
            private final int month;
            private final int year;
            private final int moodResId;

            public CalendarDate(int day, int month, int year, int moodResId) {
                this.day = day;
                this.month = month;
                this.year = year;
                this.moodResId = moodResId;
            }

            public int getDay() {
                return day;
            }

            public int getMonth() {
                return month;
            }

            public int getYear() {
                return year;
            }

            public int getMoodResId() {
                return moodResId;
            }
        }
    }
}
