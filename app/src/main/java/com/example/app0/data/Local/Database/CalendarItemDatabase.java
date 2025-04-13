package com.example.app0.data.Local.Database;

import android.content.Context;
import com.example.app0.data.Local.Entity.CalendarItem;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {CalendarItem.class}, version = 1)
@TypeConverters({com.example.app0.data.Converters.Converters.class})
public abstract class CalendarItemDatabase extends RoomDatabase {

    private static volatile CalendarItemDatabase INSTANCE;

    public abstract com.example.app0.data.Local.DAO.CalendarItemDao getCalendarItemDao();

    public static CalendarItemDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CalendarItemDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    CalendarItemDatabase.class,
                                    "calendar-db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}