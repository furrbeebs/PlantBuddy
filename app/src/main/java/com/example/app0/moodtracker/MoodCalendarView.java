package com.example.app0.moodtracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.app0.R;
import com.example.app0.ui.CustomCalendarView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MoodCalendarView extends CustomCalendarView {
    private Context context;
    private OnDateClickListener dateClickListener;
    private Map<Long, String> moodData = new HashMap<>();

    // Constructor
    public MoodCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public interface OnDateClickListener {
        void onDateClick(View view, Date date);
    }

    // Initialize the calendar view
    private void init() {
        // Override the default date click listener
        super.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, Date date) {
                // Call the custom date click listener if set
                if (dateClickListener != null) {
                    dateClickListener.onDateClick(view, date);
                }
            }
        });
    }

    @Override
    public void setOnDateClickListener(OnDateClickListener listener) {
        // Store the listener for use in our custom click handling
        this.dateClickListener = listener;
    }

    // Store mood data for a specific date
    public void setMoodForDate(long dateTimeMillis, String moodId) {
        moodData.put(dateTimeMillis, moodId);
        invalidate(); // Trigger redraw
    }

    // Get mood drawable resource based on mood ID
    private int getMoodDrawableResource(String moodId) {
        try {
            int moodValue = Integer.parseInt(moodId);
            switch (moodValue) {
                case 1: return R.drawable.mood_very_sad;
                case 2: return R.drawable.mood_sad;
                case 3: return R.drawable.mood_neutral;
                case 4: return R.drawable.mood_happy;
                case 5: return R.drawable.mood_very_happy;
                default: return R.drawable.mood_neutral;
            }
        } catch (NumberFormatException e) {
            return R.drawable.mood_neutral;
        }
    }

    // Override cell drawing to show mood icons
    @Override
    protected void onDrawCell(Canvas canvas, RectF rect, int day, boolean isToday,
                              boolean isSelected, boolean isOutOfMonth) {
        // Draw cell background
        Paint cellPaint = new Paint();
        cellPaint.setColor(isOutOfMonth ? Color.LTGRAY : Color.WHITE);
        canvas.drawRect(rect, cellPaint);

        // Draw today's indicator
        if (isToday) {
            Paint todayPaint = new Paint();
            todayPaint.setColor(Color.BLUE);
            todayPaint.setStyle(Paint.Style.STROKE);
            todayPaint.setStrokeWidth(4);
            canvas.drawRect(rect, todayPaint);
        }

        // Draw selected date indicator
        if (isSelected) {
            Paint selectedPaint = new Paint();
            selectedPaint.setColor(Color.GREEN);
            selectedPaint.setStyle(Paint.Style.STROKE);
            selectedPaint.setStrokeWidth(4);
            canvas.drawRect(rect, selectedPaint);
        }

        // Create calendar for this cell's date
        java.util.Calendar cellCalendar = java.util.Calendar.getInstance();
        cellCalendar.set(java.util.Calendar.DAY_OF_MONTH, day);
        // Note: In a complete implementation, you'd set the correct month and year too

        // Check if we have mood data for this date
        long dateMillis = cellCalendar.getTimeInMillis();
        String moodId = moodData.get(dateMillis);

        if (moodId != null) {
            // Draw mood icon
            try {
                Drawable moodIcon = ContextCompat.getDrawable(context, getMoodDrawableResource(moodId));
                if (moodIcon != null) {
                    // Set bounds for the drawable
                    int padding = 8;
                    moodIcon.setBounds(
                            (int) rect.left + padding,
                            (int) rect.top + padding,
                            (int) rect.right - padding,
                            (int) rect.bottom - padding);
                    moodIcon.draw(canvas);
                }
            } catch (Exception e) {
                // Fallback to drawing the day number
                drawDayNumber(canvas, rect, day, isOutOfMonth);
            }
        } else {
            // Draw the day number if no mood icon
            drawDayNumber(canvas, rect, day, isOutOfMonth);
        }
    }

    // Helper method to draw the day number
    private void drawDayNumber(Canvas canvas, RectF rect, int day, boolean isOutOfMonth) {
        Paint textPaint = new Paint();
        textPaint.setColor(isOutOfMonth ? Color.GRAY : Color.BLACK);
        textPaint.setTextSize(24);
        textPaint.setTextAlign(Paint.Align.CENTER);

        float textX = rect.centerX();
        float textY = rect.centerY() + (textPaint.getTextSize() / 3);
        canvas.drawText(String.valueOf(day), textX, textY, textPaint);
    }
}