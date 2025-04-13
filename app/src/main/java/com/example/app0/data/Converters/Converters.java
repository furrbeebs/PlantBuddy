package com.example.app0.data.Converters;

import android.text.TextUtils;
import android.util.Log;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Converters {
    private static final String TAG = "Converters";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @TypeConverter
    public static Date StringToDate(String value) {
        if (value == null || value.isEmpty()) {
            Log.w(TAG, "Attempted to convert null or empty date string");
            return null;
        }
        try {
            // Ensure we're using the proper format
            return DATE_FORMAT.parse(value);
        } catch (ParseException e) {
            Log.e(TAG, "Failed to parse date string: " + value, e);
            return null;
        }
    }

    @TypeConverter
    public static String DateToString(Date date) {
        if (date == null) {
            Log.w(TAG, "Attempted to convert null date");
            return null;
        }
        // Ensure consistent date formatting
        return DATE_FORMAT.format(date);
    }
}