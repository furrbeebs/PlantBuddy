package com.example.app0.ui.Activity;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.DatePicker;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.app0.ProgressPage;
import com.example.app0.R;
import com.example.app0.data.Local.Entity.Goal;
import com.example.app0.data.Local.Entity.GoalInstance;
import com.example.app0.data.Local.Entity.Sort;
import com.example.app0.ui.Fragments.AddGoalDialogFragment;
import com.example.app0.ui.GoalAdapter;
import com.example.app0.ui.ViewModel.GoalsViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoalsActivity extends AppCompatActivity {

    // Declaration of Variables
    private int num_of_goals;
    private ImageButton backButton, expandButton, addGoalButton, btnAddGoal, sortButton;
    private TextView toolbarTitle, goalsTitle, goalTitle;
    private LinearLayout dateBar;
    private Calendar calendar, selectedDate;
    private Date curr_date;
    private RecyclerView recyclerView;
    private FrameLayout fragmentContainer;
    private GoalAdapter adapter;
    private List<GoalInstance> goalInstances;

    private GoalsViewModel goalsViewModel;

    private SimpleDateFormat todayFormatter, otherFormatter, dayFormatter, dateFormatter;

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1, cal2;
        cal1 = Calendar.getInstance();
        cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        if( cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
            return true; }
        else { return false; }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_goals);
        goalsViewModel = new ViewModelProvider(this).get(GoalsViewModel.class);

        // ------------- Date Formatting Section --------------
        calendar = Calendar.getInstance();
        selectedDate = Calendar.getInstance();
        curr_date = calendar.getTime();

        todayFormatter = new SimpleDateFormat("'Today, 'dd MMM YYYY", Locale.getDefault());
        otherFormatter = new SimpleDateFormat("EEE, dd MMM YYYY", Locale.getDefault());
        dayFormatter = new SimpleDateFormat("EEEEE", Locale.getDefault());
        dateFormatter = new SimpleDateFormat("dd", Locale.getDefault());

        // ------------------ Image Button (Back) -----------------------
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener( v-> {
            Intent intent = new Intent(GoalsActivity.this, ProgressPage.class);
            startActivity(intent);
        });

        // -------------------- Toolbar Title -------------------
        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(todayFormatter.format(curr_date));

        // -------------------- Image Button (Expand) ----------------------
        expandButton = findViewById(R.id.expandButton);

        expandButton.setOnClickListener( v-> {

            DatePickerDialog datePickerDialog = new DatePickerDialog(GoalsActivity.this,(DatePicker datePicker, int sel_Year, int sel_Month, int sel_Day) -> {
                selectedDate.set(sel_Year, sel_Month, sel_Day);
                if (isSameDay(selectedDate.getTime(), curr_date)) { toolbarTitle.setText(todayFormatter.format(selectedDate.getTime())); }  // if selected date is today
                else { toolbarTitle.setText(otherFormatter.format(selectedDate.getTime())); }  // else follow normal format
                ScrollableRow(selectedDate);
            },
            selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));  // show last selected date
            datePickerDialog.show();
        });

        // ---------------------- Dates Button Row ---------------------
        dateBar = findViewById(R.id.dateBar);
        ScrollableRow(calendar);

        // ---------------------- Goals Header -----------------------
        goalsTitle = findViewById(R.id.goalsTitle);
        num_of_goals = 0;
        goalsTitle.setText(num_of_goals + " Goals");

        sortButton = findViewById(R.id.sortButton);
        sortButton.setOnClickListener(v -> {
            showSortMenu(v);
        });


        addGoalButton = findViewById(R.id.addGoalButton);
        addGoalButton.setOnClickListener( v-> {
            showAddGoalDialog();
        });
        // ------------------ Fragment Container ----------------------
        fragmentContainer = findViewById(R.id.fragment_container);

        // -------------------- RecyclerView ---------------------
        recyclerView = findViewById(R.id.goalsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        goalInstances = new ArrayList<>();
        adapter = new GoalAdapter(goalInstances);

        adapter.setOnGoalChangeListener(new GoalAdapter.OnGoalChangeListener() {
            @Override
            public void onGoalUpdated(Goal goal, GoalInstance goalInstance) {
                goalsViewModel.updateGoalInstance(goalInstance, selectedDate);

                long goalId = goalInstance.getGoalId();

                goalsViewModel.getGoal(goalId, parentGoal -> {
                    if (parentGoal != null) {
                        parentGoal.setTitle(goalInstance.getTitle());
                        parentGoal.setRepeat(goalInstance.getRepeat());
                        parentGoal.setDifficulty(goalInstance.getDifficulty());
                        parentGoal.setStartDate(goalInstance.getStartDate());
                        parentGoal.setUntilDate(goalInstance.getUntilDate());

                        goalsViewModel.updateGoal(parentGoal);
                    }
                });
            }

            @Override
            public void onGoalDeleted(Goal goal, GoalInstance goalInstance, Boolean deleteAllGoalInstances, String date) {
                if (deleteAllGoalInstances) {
                    long goalId = goalInstance.getGoalId();
                    goalsViewModel.getGoal(goalId, parentGoal -> {
                        if (parentGoal != null) {
                            goalsViewModel.deleteGoal(parentGoal);
                            Log.d(TAG, "deleted goal");
                        }
                    });

                    goalsViewModel.deleteGoalInstanceByGoalId(goalId);
                }
                else {

                    // Add exclusion and delete instance
                    goalsViewModel.excludeDateAndDeleteInstance(goalInstance.getGoalId(), date, goalInstance);
                }
            }

            @Override
            public void onGoalStatusToggled(GoalInstance goalInstance) {
                goalInstance.setCompleted(!goalInstance.getCompleted());
                goalsViewModel.updateGoalInstance(goalInstance, selectedDate);
            }
        });

        recyclerView.setAdapter(adapter);

        goalsViewModel.getGoalInstances().observe(this, newGoalInstances -> {
            Log.d(TAG, "Received " + newGoalInstances.size() + " goal instances");
            for (GoalInstance instance : newGoalInstances) {
                Log.d(TAG, "Goal: " + instance.getTitle());
            }
            goalInstances = newGoalInstances;
            adapter.updateGoalInstances(newGoalInstances);
            num_of_goals = newGoalInstances.size();
            goalsTitle.setText(num_of_goals + " Goals");
        });
    }

    // Code Handling Scrollable Row of Dates
    private void ScrollableRow(Calendar startDate) {
        dateBar.removeAllViews();
        dateBar.post(() -> {
            int parentWidth = dateBar.getWidth();
            if (parentWidth > 0) {
                int buttonWidth = parentWidth / 7;
                Calendar weekStart = (Calendar) startDate.clone();
                weekStart.setFirstDayOfWeek(Calendar.MONDAY);

                int dayOfWeek = weekStart.get(Calendar.DAY_OF_WEEK);
                int diff = (dayOfWeek == Calendar.SUNDAY) ? -6 : Calendar.MONDAY - dayOfWeek;
                weekStart.add(Calendar.DAY_OF_MONTH, diff);

                Button todayButton = null;

                for (int i = 0; i < 7; i++) {
                    Calendar days = (Calendar) weekStart.clone();
                    days.add(Calendar.DAY_OF_MONTH, i);
                    String label = dayFormatter.format(days.getTime()) + "\n" + dateFormatter.format(days.getTime());

                    Button btn = new Button(this);
                    btn.setText(label);
                    btn.setTextSize(17);
                    btn.setBackgroundColor(Color.parseColor("#80CBC4"));
                    btn.setTextColor(Color.parseColor("#64a399"));
                    btn.setTypeface(null, Typeface.BOLD);

                    int heightInDp = 70;            // changing height of each button hehe long
                    int heightInPx = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(buttonWidth, heightInPx);
                    btn.setLayoutParams(params);

                    if (isSameDay(days.getTime(), new Date())) {
                        todayButton = btn;
                    }

                    final Calendar finalDays = days;
                    btn.setOnClickListener(v -> {

                        if (isSameDay(finalDays.getTime(), new Date())) {
                            toolbarTitle.setText(todayFormatter.format(finalDays.getTime()));
                        } else {
                            toolbarTitle.setText(otherFormatter.format(finalDays.getTime()));
                        }

                        // Get goals for the selected date
                        goalsViewModel.getGoalInstancesForDate(finalDays).observe(this, newGoalInstances -> {
                            goalInstances = newGoalInstances;
                            adapter.updateGoalInstances(newGoalInstances);
                            num_of_goals = newGoalInstances.size();
                            goalsTitle.setText(num_of_goals + " Goals");
                        });

                        highlightSelectedDateButton(btn);
                    });
                    dateBar.addView(btn);
                }

                if (todayButton != null) {
                    todayButton.performClick();         // force click button for today on create
                }
            }
        });
    }
    private Button currentHighlightedButton = null;
    private void highlightSelectedDateButton(Button btn) {
        if (currentHighlightedButton != null) {
            currentHighlightedButton.setBackgroundColor(Color.parseColor("#80CBC4"));
            currentHighlightedButton.setTextColor(Color.parseColor("#64a399"));
        }

        btn.setBackgroundResource(R.drawable.selected_date_button);
        currentHighlightedButton = btn;
        btn.setTextColor(Color.WHITE);
    }

    private void setupToolbarButtons() {
        ImageButton btnAddGoal = findViewById(R.id.addGoalButton);
        btnAddGoal.setOnClickListener(v -> showAddGoalDialog());
    }

    private void showSortMenu(View view) {          // showing the dropdown for sort
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.sort_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nameAscending) {
                sortByNameAscending();
                return true;
            } else if (itemId == R.id.nameDescending) {
                sortByNameDescending();
                return true;
            } else if (itemId == R.id.pointsAscending) {
                sortByPointsAscending();
                return true;
            } else if (itemId == R.id.pointsDescending) {
                sortByPointsDescending();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void sortByNameAscending() {
        ArrayList<GoalInstance> sortableList = new ArrayList<>(goalInstances);
        Sort.nameAscending(sortableList);
        adapter.updateGoalInstances(sortableList);
    }

    private void sortByNameDescending() {
        ArrayList<GoalInstance> sortableList = new ArrayList<>(goalInstances);
        Sort.nameDescending(sortableList);
        adapter.updateGoalInstances(sortableList);
    }

    private void sortByPointsDescending() {
        ArrayList<GoalInstance> sortableList = new ArrayList<>(goalInstances);
        Sort.pointsDescending(sortableList);
        adapter.updateGoalInstances(sortableList);
    }

    private void sortByPointsAscending() {
        ArrayList<GoalInstance> sortableList = new ArrayList<>(goalInstances);
        Sort.pointsAscending(sortableList);
        adapter.updateGoalInstances(sortableList);
    }

    private void showAddGoalDialog() {
        AddGoalDialogFragment dialog = AddGoalDialogFragment.newInstance(selectedDate.getTime());
        dialog.show(getSupportFragmentManager(), "AddGoalDialogFragment");
    }
}