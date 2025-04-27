package com.example.app0.data.Local.DAO;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.app0.data.Local.Entity.GoalInstance;

import java.util.Calendar;
import java.util.List;

@Dao
public interface GoalInstanceDao {
    @Insert
    public long addGoalInstance(GoalInstance goalInstance);

    @Update
    public void updateGoalInstance(GoalInstance goalInstance);

    @Delete
    public void deleteGoalInstance(GoalInstance goalInstance);

    // selecting all goal instances for today, livedata
    @Query("select * from goalInstances where instanceDate = :date")
    public LiveData<List<GoalInstance>> getGoalInstancesDateLive(Calendar date);

    @Query("select * from goalInstances where goalId = :goalId and instanceDate = :date")
    public int countGoalInstancesForDate(long goalId, Calendar date);

    @Query("delete from goalInstances where goalId = :goalId")
    public void deleteGoalInstanceByGoalId(long goalId);

    @Query("delete from goalInstances where goalId = :goalId and instanceDate > :today and instanceId != :instanceId")
    public void deleteFutureGoalInstancesByGoalId(long goalId, Calendar today, long instanceId);
}
