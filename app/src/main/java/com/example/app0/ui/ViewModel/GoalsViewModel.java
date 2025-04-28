package com.example.app0.ui.ViewModel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.app0.data.Local.Entity.Goal;
import com.example.app0.data.Local.Entity.GoalInstance;
import com.example.app0.data.Repository.GoalsRepository;

import java.util.Calendar;
import java.util.List;
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

    public void createGoalInstancesForDate(Calendar today) {
        repository.createGoalInstancesForDate(today);
    }

    public LiveData<List<GoalInstance>> getGoalInstances() {
        return goalInstances;
    }

    public interface GoalAddCallback {
        void onGoalAdded(long goalId);
        void onError(Exception e);
    }

    // callback so that goal instance can be created right after goal creation
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

    // getting goal instances for a specific date
    public LiveData<List<GoalInstance>> getGoalInstancesForDate(Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        repository.createGoalInstancesForDate(date);

        return repository.getGoalInstancesForDateLive(date);
    }

    public void updateGoal(Goal goal) {
        repository.updateGoal(goal);
    }

    public void updateGoalInstance(GoalInstance instance) {
        repository.updateGoalInstance(instance);
    }

    public void deleteGoal(Goal goal) {
        repository.deleteGoal(goal);
    }

    public void deleteGoalInstanceByGoalId(long goalId) {
        repository.deleteGoalInstanceByGoalId(goalId);
    }

    // to delete future goal instances (for manipulating goal instances after end dates are changed)
    public void deleteFutureGoalInstancesByGoalId(long goalId, Calendar today, long instanceId) {
        repository.deleteFutureGoalInstancesByGoalId(goalId, today, instanceId);
    }

    // to exclude dates of deleted goal instances so they won't be recreated again
    public void excludeDateAndDeleteInstance(long goalId, String dateId, GoalInstance goalInstance) {
        repository.excludeDateAndDeleteInstance(goalId, dateId, goalInstance);
    }

    // to calculate and return the goal completion status to be displayed on the progress bar
    public LiveData<GoalCompletionStatus> getTodayGoalCompletionStatus() {
        MutableLiveData<GoalCompletionStatus> completionStatus = new MutableLiveData<>();
        Calendar today = Calendar.getInstance();

        getGoalInstancesForDate(today).observeForever(goalInstances -> {
            int total = goalInstances.size();
            int completed = 0;

            for (GoalInstance instance : goalInstances) {
                if (instance.getCompleted()) {
                    completed += 1;
                }
            }

            completionStatus.setValue(new GoalCompletionStatus(completed, total));
        });

        return completionStatus;
    }

    // clas to hold goal completion
    public static class GoalCompletionStatus {
        private int completed;
        private int total;

        public GoalCompletionStatus(int completed, int total) {
            this.completed = completed;
            this.total = total;
        }

        public int getCompleted() {
            return completed;
        }

        public int getTotal() {
            return total;
        }
    }
}