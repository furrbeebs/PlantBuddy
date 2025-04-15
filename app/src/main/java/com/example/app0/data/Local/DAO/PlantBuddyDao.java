package com.example.app0.data.Local.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.app0.data.Local.Entity.PlantBuddy;

import java.util.List;

@Dao
public interface PlantBuddyDao {
    // Inserting Plant object into the database
    @Insert (onConflict = OnConflictStrategy.ABORT)
    void insert(PlantBuddy plantBuddy);

    // Queries the database for the Plant Buddy Object. Stores it in getPlantBuddy
    @Query("SELECT * FROM PlantBuddy")
    LiveData<List<PlantBuddy>> getPlantBuddy();

    // Updates the database for one user
    @Update
    int update(PlantBuddy plantBuddy);

    @Query("DELETE FROM PlantBuddy")
    void deleteAll();

    @Query("SELECT * FROM PlantBuddy")
    List<PlantBuddy> getAll();
}
