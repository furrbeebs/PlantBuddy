package com.example.app0.data.Repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.app0.data.Local.Entity.CalendarItem;
import com.example.app0.data.Local.DAO.CalendarItemDao;
import com.example.app0.data.Local.Database.AppDatabase;
import com.example.app0.models.Mood;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CalendarItemRepository {
    private final CalendarItemDao calendarItemDao;
    private final LiveData<List<CalendarItem>> allCalendarItems;
    private final ExecutorService executorService;
    // private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public CalendarItemRepository(Application application) {
        // Initialize the Room database instance (singleton pattern)
        AppDatabase database = AppDatabase.getDatabase(application);
        // Call database method by using dao
        calendarItemDao = database.getCalendarItemDao();
        // Use DAO to get all data, Live automatically updates the app when there are changes
        allCalendarItems = calendarItemDao.getAllCalendarItemsLive();
        // Manage background activities
        executorService = Executors.newSingleThreadExecutor();
    }

    // Methods from Dao
    public LiveData<List<CalendarItem>> getAllCalendarItems() {
        return allCalendarItems;
    }

    // Background Threads
    public void insert(CalendarItem calendarItem) {
        executorService.execute(() -> calendarItemDao.insertCalendarItem(calendarItem));
    }

    public void update(CalendarItem calendarItem) {
        executorService.execute(() -> calendarItemDao.updateCalendarItem(calendarItem));
    }

    public void delete(CalendarItem calendarItem) {
        executorService.execute(() -> calendarItemDao.deleteCalendarItem(calendarItem));
    }

    public void cleanUp() {
        executorService.shutdown();
    }

    public void insertMoodEntry(int year, int month, int day, Mood mood, String notes) {
        // Create date (month is 0-based in Calendar)
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        Date date = calendar.getTime();

        // Create calendar item
        CalendarItem item = new CalendarItem(date, mood, notes);

        // Insert into database
        insert(item);
    }


    public void deleteMoodEntry(Date date) {
        if (date == null) {
            Log.e("Repository", "Cannot delete with null date");
            return;
        }

        executorService.execute(() -> {
            try {
                // Delete by date
                calendarItemDao.deleteCalendarItemByDate(date);
            } catch (Exception e) {
                Log.e("Repository", "Error deleting by date: " + e.getMessage());
            }
        });
    }

    public List<CalendarItem> getAllCalendarItemsSync() {
        try {
            // Synchronization aid that allows one thread to wait until other threads complete their tasks
            final CountDownLatch latch = new CountDownLatch(1);
            final List<CalendarItem>[] result = new List[1];

            executorService.execute(() -> {
                try {
                    result[0] = calendarItemDao.getAllCalendarItems();
                } finally {
                    latch.countDown();
                }
            });

            // Wait for the operation to complete with a timeout
            boolean completed = latch.await(3, TimeUnit.SECONDS);
            if (!completed) {
                Log.e("Repository", "Database operation timed out");
                return new ArrayList<>();
            }

            return result[0] != null ? result[0] : new ArrayList<>();
        } catch (InterruptedException e) {
            Log.e("Repository", "Database operation interrupted", e);
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
    }
}