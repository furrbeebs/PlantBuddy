package com.example.app0.ui.ViewModel;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.app0.data.Local.Entity.Goal;
import com.example.app0.data.Local.Entity.GoalInstance;
import com.example.app0.data.Repository.GoalsRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class GoalsViewModel extends AndroidViewModel {
    private final GoalsRepository repository;
    private LiveData<List<GoalInstance>> goalInstances;

    public GoalsViewModel(@NonNull Application application) {
        super(application);
        repository = new GoalsRepository(application);
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        repository.createGoalInstancesForDate(today);  // ensure instances are generated at startup
        goalInstances = repository.getGoalInstancesForTodayLive();
    }

    public LiveData<List<GoalInstance>> getGoalInstances() {
        return goalInstances;
    }

    public interface GoalAddCallback {
        void onGoalAdded(long goalId);
        void onError(Exception e);
    }

    public void addGoalWithCallback(Goal goal, GoalAddCallback callback) {
        repository.addGoal(goal, new GoalsRepository.DatabaseCallback<Long>() {
            @Override
            public void onComplete(Long id) {
                callback.onGoalAdded(id);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public void addGoalInstanceWithCallback(GoalInstance goalInstance, GoalAddCallback callback) {
        repository.addGoalInstance(goalInstance, new GoalsRepository.DatabaseCallback<Long>() {
            @Override
            public void onComplete(Long id) {
                callback.onGoalAdded(id);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public void getGoal(long goalId, Consumer<Goal> callback) {
        repository.getGoal(goalId, callback);
    }

    public LiveData<List<GoalInstance>> getGoalInstancesForDate(Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

//        LiveData<List<GoalInstance>> goalsForThatDate = repository.getGoalInstancesForDateLive(date);
        repository.createGoalInstancesForDate(date);

        return repository.getGoalInstancesForDateLive(date);
    }

    public void updateGoal(Goal goal) {
        repository.updateGoal(goal);
    }

    public void updateGoalInstance(GoalInstance instance, Calendar currentDate) {
        repository.updateGoalInstance(instance);
    }

    public void deleteGoal(Goal goal) {
        repository.deleteGoal(goal);
    }

    public void deleteGoalInstance(GoalInstance goalInstance, Calendar currentDate) {
        repository.deleteGoalInstance(goalInstance);
    }

    public void deleteGoalInstanceByGoalId(long goalId) {
        repository.deleteGoalInstanceByGoalId(goalId);
    }

    public void excludeDateAndDeleteInstance(long goalId, String dateId, GoalInstance goalInstance) {
        repository.excludeDateAndDeleteInstance(goalId, dateId, goalInstance);
    }
}