package com.example.app0.data.Local.Database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.app0.data.Converters.CalendarConverter;
import com.example.app0.data.Converters.Converters;
import com.example.app0.data.Converters.DifficultyConverter;
import com.example.app0.data.Converters.RepeatConverter;
import com.example.app0.data.Local.DAO.GoalDao;
import com.example.app0.data.Local.DAO.GoalInstanceDao;
import com.example.app0.data.Local.Entity.CalendarItem;
import com.example.app0.data.Local.DAO.CalendarItemDao;
import com.example.app0.data.Local.Entity.Goal;
import com.example.app0.data.Local.Entity.GoalInstance;
import com.example.app0.data.Local.Entity.PlantBuddy;
import com.example.app0.data.Local.DAO.PlantBuddyDao;

@Database(entities = {CalendarItem.class, PlantBuddy.class, Goal.class, GoalInstance.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class, CalendarConverter.class, DifficultyConverter.class, RepeatConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    // Create an ExecutorService with a fixed thread pool for database operations
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    // Define abstract methods for all DAOs
    public abstract CalendarItemDao getCalendarItemDao();
    public abstract PlantBuddyDao getPlantBuddyDao();

    public abstract GoalDao getGoalDao();

    public abstract GoalInstanceDao getGoalInstanceDao();

    // Singleton pattern to get the database instance
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "app_database")  // New combined database name
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}