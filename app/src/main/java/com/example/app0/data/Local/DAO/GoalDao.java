package com.example.app0.data.Local.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.example.app0.data.Local.Entity.Goal;

import java.util.List;

@Dao
public interface GoalDao {
    @Insert
    public long addGoal(Goal goal);

    @Update
    public void updateGoal(Goal goal);

    @Delete
    public void deleteGoal(Goal goal);

    @Query("select * from goalTable")
    public List<Goal> getAllGoals();

    @Query("select * from goalTable where goalId==:goalId")
    public Goal getGoal(long goalId);
}
