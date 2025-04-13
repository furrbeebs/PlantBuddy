package com.example.app0.data.Local.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.app0.data.Local.Entity.CalendarItem;

import java.util.Date;
import java.util.List;

@Dao
public interface CalendarItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
        // replace same key if there is one
    void insertCalendarItem(CalendarItem item);

    @Update
    void updateCalendarItem(CalendarItem item);

    @Delete
    void deleteCalendarItem(CalendarItem item);

    @Query("DELETE FROM calendarItems WHERE date = :date")
    void deleteCalendarItemByDate(Date date);

    @Query("SELECT * FROM calendarItems")
    List<CalendarItem> getAllCalendarItems();


    @Query("SELECT * FROM calendarItems")
    LiveData<List<CalendarItem>> getAllCalendarItemsLive();

    // date is like 2004-10-28

    @Query("SELECT * FROM calendarItems WHERE date = :date")
    LiveData<CalendarItem> getCalendarItemByDateLive(String date);

}