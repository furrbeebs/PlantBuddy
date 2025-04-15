package com.example.app0.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Helper class for date picking functionality across the app
 */
public class DatePickerHelper {

    // Date format for displaying to users
    public static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    // Date format used for storing dates in the Goal class
   public static final SimpleDateFormat STORAGE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

    public static final SimpleDateFormat STORAGE_DATE_FORMAT_2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


    /**
     * Shows a date picker dialog and updates the provided EditText with the selected date
     *
     * @param context Context for creating the dialog
     * @param editText The EditText to update with the selected date
     */
    public static void showDatePickerDialog(Context context, EditText editText) {
        showDatePickerDialog(context, editText.getText().toString(), date -> {
            editText.setText(DISPLAY_DATE_FORMAT.format(date));
        });
    }

    /**
     * Shows a date picker dialog and updates the provided TextView with the selected date
     *
     * @param context Context for creating the dialog
     * @param textView The TextView to update with the selected date
     */
    public static void showDatePickerDialog(Context context, TextView textView) {
        showDatePickerDialog(context, textView.getText().toString(), date -> {
            textView.setText(DISPLAY_DATE_FORMAT.format(date));
        });
    }

    /**
     * Shows a date picker dialog with a custom callback for handling the selected date
     *
     * @param context Context for creating the dialog
     * @param dateText The current date text to initialize the picker
     * @param callback Callback to handle the selected date
     */
    public static void showDatePickerDialog(Context context, String dateText, OnDateSelectedListener callback) {
        Calendar calendar = Calendar.getInstance();

        try {
            // Try to parse the existing date text
            Date date = parseDisplayDate(dateText);
            if (date != null) {
                calendar.setTime(date);
            }
        } catch (Exception e) {
            // Use current date if parsing fails
        }

        new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    callback.onDateSelected(calendar.getTime());
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    /**
     * Parses a date string in display format to a Date object
     */
    public static Date parseDisplayDate(String dateText) {
        try {
            return DISPLAY_DATE_FORMAT.parse(dateText);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Converts a date from display format to storage format
     */
    public static String displayToStorageFormat(String displayDate) {
        try {
            Date date = DISPLAY_DATE_FORMAT.parse(displayDate);
            if (date != null) {
                return STORAGE_DATE_FORMAT.format(date);
            }
        } catch (ParseException e) {
            // Return the original string if parsing fails
        }
        return displayDate;
    }

    public static String displayToStorageFormat_2(String displayDate) {
        try {
            Date date = DISPLAY_DATE_FORMAT.parse(displayDate);
            if (date != null) {
                return STORAGE_DATE_FORMAT_2.format(date);
            }
        } catch (ParseException e) {
            // Return the original string if parsing fails
        }
        return displayDate;
    }

    /**
     * Converts a date from storage format to display format
     */
    public static String storageToDisplayFormat(String storageDate) {
        try {
            Date date = STORAGE_DATE_FORMAT.parse(storageDate);
            if (date != null) {
                return DISPLAY_DATE_FORMAT.format(date);
            }
        } catch (ParseException e) {
            // Return the original string if parsing fails
        }
        return storageDate;
    }

    /**
     * Interface for handling date selection
     */
    public interface OnDateSelectedListener {
        void onDateSelected(Date date);
    }
}