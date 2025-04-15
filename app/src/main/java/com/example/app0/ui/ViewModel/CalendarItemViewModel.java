package com.example.app0.ui.ViewModel;

import static android.content.ContentValues.TAG;
import static android.provider.Settings.System.DATE_FORMAT;

import android.app.Application;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.app0.data.Converters.Converters;
import com.example.app0.data.Local.Entity.CalendarItem;
import com.example.app0.models.Mood;
import com.example.app0.data.Repository.CalendarItemRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalendarItemViewModel extends AndroidViewModel {
    private final com.example.app0.data.Repository.CalendarItemRepository repository;
    private final LiveData<List<CalendarItem>> allCalendarItems;
    private final ExecutorService executorService;

    @Override
    protected void onCleared() {
        super.onCleared();
        // Shut down the executor service to prevent memory leaks
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    public CalendarItemViewModel(@NonNull Application application) {
        super(application);
        repository = new CalendarItemRepository(application);
        allCalendarItems = repository.getAllCalendarItems();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<CalendarItem>> getAllCalendarItems() {
        return allCalendarItems;
    }
    public void saveMoodEntry(int year, int month, int day, int moodResId, String notes) {
        // Input validation
        // Month starts from 0 - 11
        if (year <= 0 || month < 0 || month > 11 || day < 1 || day > 31) {
            return;
        }

        // Convert source ID to mood enum
        Mood mood = Mood.fromResId(moodResId);
        repository.insertMoodEntry(year, month, day, mood, notes);
    }

    public void updateCalendarItem(CalendarItem item) {
        repository.update(item);
    }


    public void deleteCalendarItemByDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();

        if (date != null) {
            Log.d("ViewModel", "Deleting entry for date: " + date);
            repository.deleteMoodEntry(date);
        } else {
            Log.e("ViewModel", "Cannot delete with null date");
        }
    }

    public List<CalendarItem> getCalendarItemsForRangeSync(Date startDate, Date endDate) {
        return repository.getCalendarItemsForDateRangeSync(startDate, endDate);
    }

    public List<CalendarItem> getAllCalendarItemsSync() {
        final List<CalendarItem>[] result = new List[1];
        final CountDownLatch latch = new CountDownLatch(1);

        executorService.execute(() -> {
            try {
                result[0] = repository.getAllCalendarItemsSync();
            } finally {
                latch.countDown();
            }
        });

        try {
            // Wait up to 2 seconds for the operation to complete
            boolean completed = latch.await(2, TimeUnit.SECONDS);
            if (!completed) {
                Log.e("ViewModel", "Database operation timed out");
                return new ArrayList<>();
            }
        } catch (InterruptedException e) {
            Log.e("ViewModel", "Operation interrupted", e);
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }

        return result[0] != null ? result[0] : new ArrayList<>();
    }


}