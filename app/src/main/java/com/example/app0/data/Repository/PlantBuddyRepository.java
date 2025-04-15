package com.example.app0.data.Repository;

import android.app.Application;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.app0.data.Local.Database.AppDatabase;
import com.example.app0.data.Local.Entity.PlantBuddy;

import java.util.List;

// Interfaces with DAO, can be modified to pull data from other sources as well
public class PlantBuddyRepository {
    private final com.example.app0.data.Local.DAO.PlantBuddyDao PlantBuddyDao;

    public interface OnPlantBuddyFetchedCallback {
        void onFetched(PlantBuddy plantBuddy);
    }

    public PlantBuddyRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        PlantBuddyDao = db.getPlantBuddyDao();
        //allUsers = PlantBuddyDao.getPlantBuddy();
    }

    // Special Callback Method for Insert Status
    public interface InsertStatus{
        void InsertSuccess(); // called when insertion is successful
        void InsertFailed();  // called when insertion failed
    }

    // @Insert
    public void insert(PlantBuddy plantBuddy, InsertStatus status) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                PlantBuddyDao.insert(plantBuddy);    // if insertion does not produce any errors
                status.InsertSuccess();
            } catch (SQLiteConstraintException e) {  // catches exception produced when insertion is
                status.InsertFailed();               // attempted despite having one object inside DB
            }
        });
    }

    // @Update
    public void update(PlantBuddy plantBuddy) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<PlantBuddy> allInstance = PlantBuddyDao.getAll();

            if (allInstance == null || allInstance.isEmpty()) { Log.d("INFO", "PlantBuddy Database Has No Object. Nothing to Update."); }
            else {
                int returned_int = PlantBuddyDao.update(plantBuddy);
                if (returned_int == 0) { Log.d("INFO", "Points System Update Failed!"); }
                else { Log.d("INFO", "Point System Update Success!"); }
            }

        });
    }

    // @Query
    public LiveData<List<PlantBuddy>> getPlantBuddy() {
        return PlantBuddyDao.getPlantBuddy();  // Middle man handling communication with DAO
    }

    // @Delete
    public void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            PlantBuddyDao.deleteAll();
        });
    }

}
