package com.example.app0.data.Repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
import com.example.app0.data.Local.DAO.GoalDao;
import com.example.app0.data.Local.DAO.GoalInstanceDao;
import com.example.app0.data.Local.Database.AppDatabase;
import com.example.app0.data.Local.Entity.Goal;
import com.example.app0.data.Local.Entity.GoalInstance;
import com.example.app0.data.Local.Entity.Repeat;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class GoalsRepository {

    private GoalDao goalDao;
    private GoalInstanceDao goalInstanceDao;
    private ExecutorService executorService;
    private final Handler handler;

    public GoalsRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getDatabase(application);
        goalDao = appDatabase.getGoalDao();
        goalInstanceDao = appDatabase.getGoalInstanceDao();
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }

    public interface DatabaseCallback<T> {
        void onComplete(T result);
        void onError(Exception e);
    }

    public void addGoal(Goal goal, DatabaseCallback<Long> callback) {
        executorService.execute(() -> {
            try {
                long id = goalDao.addGoal(goal);
                callback.onComplete(id);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void addGoalInstance(GoalInstance goalInstance, DatabaseCallback<Long> callback) {
        executorService.execute(() -> {
            try {
                long id = goalInstanceDao.addGoalInstance(goalInstance);
                callback.onComplete(id);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void updateGoal(Goal goal) {
        executorService.execute(() -> goalDao.updateGoal(goal));
    }

    public void updateGoalInstance(GoalInstance goalInstance) {
        executorService.execute(() -> goalInstanceDao.updateGoalInstance(goalInstance));
    }

    public void deleteGoal(Goal goal) {
        executorService.execute(() -> goalDao.deleteGoal(goal));
    }

    public void deleteGoalInstance(GoalInstance goalInstance) {
        executorService.execute(() -> goalInstanceDao.deleteGoalInstance(goalInstance));
    }

    public void deleteGoalInstanceByGoalId(long goalId) {
        executorService.execute(() -> goalInstanceDao.deleteGoalInstanceByGoalId(goalId));
    }

    public void deleteFutureGoalInstancesByGoalId(long goalId, Calendar today, long instanceId) {
        executorService.execute(() -> goalInstanceDao.deleteFutureGoalInstancesByGoalId(goalId, today, instanceId));
    }

    public void createGoalInstancesForDate(Calendar date) {
        executorService.execute(() -> {
            List<Goal> allGoals = goalDao.getAllGoals();
            date.set(Calendar.HOUR_OF_DAY, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);

            for (Goal goal : allGoals) {
                Calendar startDate = goal.getStartDate();
                Calendar endDate = goal.getUntilDate();

                if ((startDate.before(date) || isSameDay(startDate, date)) && (endDate == null || endDate.after(date) || isSameDay(endDate, date))) {
                    Repeat repeatBehaviour = goal.getRepeat();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String dateId = dateFormat.format(date.getTime());

                    List<String> excludedDates = goal.getExcludedDatesList();

                    if (repeatBehaviour.shouldCreateInstance(startDate, date) && !excludedDates.contains(dateId)) {
                        int count = goalInstanceDao.countGoalInstancesForDate(goal.getId(), date);
                        if (count == 0) {
                            GoalInstance goalInstance =
                                    new GoalInstance(goal.getId(), goal.getTitle(), goal.getStartDate(), goal.getRepeat(),
                                            goal.getUntilDate(), goal.getDifficulty(), date, false);
                            goalInstanceDao.addGoalInstance(goalInstance);
                        }
                    }
                }
            }
        });
    }

    public boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    public void getGoal(long goalId, Consumer<Goal> callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final Goal goal = goalDao.getGoal(goalId);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.accept(goal);
                    }
                });
            }
        });
    }

    public LiveData<List<GoalInstance>> getGoalInstancesForTodayLive() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);         // setting time for start of day (00:00:00)
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        return goalInstanceDao.getGoalInstancesDateLive(today);
    }

    public LiveData<List<GoalInstance>> getGoalInstancesForDateLive(Calendar date) {
        return goalInstanceDao.getGoalInstancesDateLive(date);
    }

    public void excludeDateAndDeleteInstance(long goalId, String dateId, GoalInstance instance) {
        // Run in background
        executorService.execute(() -> {
            // Get the parent goal
            Goal parentGoal = goalDao.getGoal(goalId);
            if (parentGoal != null) {
                // Add exclusion date
                parentGoal.addExcludedDate(dateId);
                goalDao.updateGoal(parentGoal);

                // Delete the instance
                goalInstanceDao.deleteGoalInstance(instance);
            }
        });
    }
}