package com.example.app0.data.Converters;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.room.TypeConverter;

import com.example.app0.data.Local.Entity.DailyRepeat;
import com.example.app0.data.Local.Entity.DoNotRepeat;
import com.example.app0.data.Local.Entity.MonthlyRepeat;
import com.example.app0.data.Local.Entity.Repeat;
import com.example.app0.data.Local.Entity.WeeklyRepeat;

public class RepeatConverter {
    @TypeConverter
    public static String fromRepeat(Repeat repeat) {
        if (repeat == null) {
            return null;
        }
        return repeat.getRepeatAsString();
    }

    @TypeConverter
    public static Repeat toRepeat(String repeatType) {
        switch (repeatType) {
            case "Daily":
                return new DailyRepeat();
            case "Weekly":
                return new WeeklyRepeat();
            case "Monthly":
                return new MonthlyRepeat();
            case "Do Not Repeat":
                return new DoNotRepeat();
            default:
                Log.d(TAG, "Unknown repeat type: " + repeatType);
                return new DailyRepeat();
        }
    }
}
