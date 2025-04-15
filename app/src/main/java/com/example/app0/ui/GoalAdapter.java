package com.example.app0.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app0.R;
import com.example.app0.data.Converters.DifficultyConverter;
import com.example.app0.data.Converters.RepeatConverter;
import com.example.app0.data.Local.Entity.Difficulty;
import com.example.app0.data.Local.Entity.Goal;
import com.example.app0.data.Local.Entity.GoalInstance;
import com.example.app0.data.Local.Entity.Repeat;
import com.example.app0.ui.Activity.UpdateActivity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private List<GoalInstance> goalInstances;
    private final String[] frequencies = {"Daily", "Weekly", "Monthly"};
    private int curr_index, next_index;
    private boolean checkedState, newState;
    private OnGoalChangeListener listener;
    private SimpleDateFormat otherFormatter, dayFormatter, dateFormatter;
    private Date selectDate;

    public interface OnGoalChangeListener {
        void onGoalUpdated(Goal goal, GoalInstance goalInstance);
        void onGoalDeleted(Goal goal, GoalInstance goalInstance, Boolean deleteAllGoalInstances, String date);
        void onGoalStatusToggled(GoalInstance goalInstance);
    }

    public GoalAdapter(List<GoalInstance> goalInstances) {
        this.goalInstances = goalInstances;
    }

    // Add a method to set the listener
    public void setOnGoalChangeListener(OnGoalChangeListener listener) {
        this.listener = listener;
    }

    public List<GoalInstance> getGoalInstances() {
        return goalInstances;
    }

    public void updateGoalInstances(List<GoalInstance> newGoalInstances) {
        this.goalInstances = newGoalInstances;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.goal_item, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        holder.goalItemContainer.setVisibility(View.VISIBLE);
        holder.editGoalContainer.setVisibility(View.GONE);

        GoalInstance goal = goalInstances.get(position);

        holder.title.setText(goal.getTitle());
        holder.frequencyText.setText(RepeatConverter.fromRepeat(goal.getRepeat()));
        holder.sun_points.setText(String.valueOf(DifficultyConverter.fromDifficulty(goal.getDifficulty())));
        checkedState = false;

        otherFormatter = new SimpleDateFormat("dd MMM YYYY", Locale.getDefault());
        dayFormatter = new SimpleDateFormat("EEE", Locale.getDefault());
        dateFormatter = new SimpleDateFormat("dd", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        Calendar selectedDate = Calendar.getInstance();
        Date curr_date = calendar.getTime();


        // ---------- GoalItemContainer Portion ----------
        holder.editButton.setOnClickListener( v-> {
            holder.goalItemContainer.setVisibility(View.GONE);
            holder.editGoalContainer.setVisibility(View.VISIBLE);

            holder.editGoal.setText(goal.getTitle());
        });

        if (goal.getStartDate() != null) {
            holder.editStartDate.setText(otherFormatter.format(goal.getStartDate().getTime()));
        }

        if (goal.getUntilDate() != null) {
            holder.editEndDate.setText(otherFormatter.format(goal.getUntilDate().getTime()));
        }

        holder.checkboxBackground.setOnClickListener(v -> {
            // Toggle completion status
            boolean newStatus = !goal.getCompleted();
            goal.setCompleted(newStatus);

            // Update the UI
            holder.checkmark.setVisibility(newStatus ? View.VISIBLE : View.GONE);

            // Notify listener about status change
            if (listener != null) {
//                listener.onGoalStatusToggled(goal);
                listener.onGoalUpdated(null, goal);
            }

            // UpdateActivity.updatePointBalance(DifficultyConverter.fromDifficulty(goal.getDifficulty()));
        });

        // ------------------ Repeat Spinner -------------------
        ArrayAdapter<CharSequence> repeatAdapter = ArrayAdapter.createFromResource(
                holder.itemView.getContext(), R.array.repeat_options, android.R.layout.simple_spinner_item);
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.editFrequencyBtn.setAdapter(repeatAdapter);

        // Sets default option for Repeat based on DB data
        String[] repeat_options = holder.itemView.getContext().getResources().getStringArray(R.array.repeat_options);
        int startingSelection_repeat = Arrays.asList(repeat_options).indexOf(RepeatConverter.fromRepeat(goal.getRepeat()));
        holder.editFrequencyBtn.setSelection(startingSelection_repeat); // Sets starting selection from DB

        holder.editFrequencyBtn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean FirstRun = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = parent.getItemAtPosition(pos).toString();
                if (FirstRun == true) {
                    FirstRun = false;
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // -------------------- Difficulty Spinner -----------------------
        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(
                holder.itemView.getContext(), R.array.difficulty_options, android.R.layout.simple_spinner_item);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.editSun.setAdapter(difficultyAdapter);

        // Sets default option for Difficulty Spinner based on DB
        String[] difficulty_options = holder.itemView.getContext().getResources().getStringArray(R.array.difficulty_options);
        int startingSelection_difficulty = Arrays.asList(difficulty_options).indexOf(DifficultyConverter.fromDifficultyToString(goal.getDifficulty()));
        holder.editSun.setSelection(startingSelection_difficulty); // Sets starting selection from DB

        // --------------------- Start Date Button ---------------------
        holder.startDate.setOnClickListener( v-> {
            if (goal.getStartDate() != null) {
                selectedDate.setTime(goal.getStartDate().getTime());
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(holder.itemView.getContext(),(DatePicker datePicker, int sel_Year, int sel_Month, int sel_Day) -> {
                selectedDate.set(sel_Year, sel_Month, sel_Day);
                holder.editStartDate.setText(otherFormatter.format(selectedDate.getTime()));  // else follow normal format
                goal.setStartDate(selectedDate);
            },
                    selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));  // show last selected date
            datePickerDialog.show();
        });

        // --------------------- End Date Button ---------------------
        holder.endDate.setOnClickListener( v-> {
            if (goal.getUntilDate() != null) {
                selectedDate.setTime(goal.getUntilDate().getTime());
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(holder.itemView.getContext(),(DatePicker datePicker, int sel_Year, int sel_Month, int sel_Day) -> {
                selectedDate.set(sel_Year, sel_Month, sel_Day);
                holder.editEndDate.setText(otherFormatter.format(selectedDate.getTime()));  // else follow normal format
                goal.setUntilDate(selectedDate);
            },
                    selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));  // show last selected date
            datePickerDialog.show();
        });


        // ---------------------- Delete Button ----------------------
        holder.deleteBtn.setOnClickListener( v-> {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
            builder.setTitle("Delete Goal");
            builder.setMessage("Do you want to delete this goal instance or all recurring instances?");

            builder.setPositiveButton("This Instance", (dialog, which) -> {
                if (listener != null) {
                    // Get the current position before removing the item
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {

                        GoalInstance instance = goalInstances.get(adapterPosition);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String dateId = dateFormat.format(instance.getInstanceDate().getTime());

                        listener.onGoalDeleted(null, instance, false, dateId);

                        // Remove from adapter
                        goalInstances.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                    }
                }
            });

            builder.setNegativeButton("All Instances", (dialog, which) -> {
                if (listener != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        GoalInstance instance = goalInstances.get(adapterPosition);
                        listener.onGoalDeleted(null, instance, true, null);

                        long goalIdToRemove = instance.getGoalId();
                        for (int i = goalInstances.size() - 1; i >= 0; i--) {
                            if (goalInstances.get(i).getGoalId() == goalIdToRemove) {
                                goalInstances.remove(i);
                                notifyItemRemoved(i);
                            }
                        }
                    }
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        // ---------------------- Save Button -------------------------
        holder.saveBtn.setOnClickListener( v-> {
            holder.goalItemContainer.setVisibility(View.VISIBLE);
            holder.editGoalContainer.setVisibility(View.GONE);

            goal.setTitle(holder.editGoal.getText().toString());

            String selectedRepeat = holder.editFrequencyBtn.getSelectedItem().toString();
            Repeat repeat = RepeatConverter.toRepeat(selectedRepeat);
            goal.setRepeat(repeat);

            String selectedDifficulty = holder.editSun.getSelectedItem().toString();
            Difficulty difficulty = DifficultyConverter.toDifficultyFromString(selectedDifficulty);
            goal.setDifficulty(difficulty);

            if (listener != null) {
                listener.onGoalUpdated(null, goal);
            }

            // Update UI
            notifyItemChanged(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() { return goalInstances.size(); }

    public static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView title, frequency, sun_points, frequencyText , editEndDate, editStartDate;
        ImageView checkboxBackground, checkmark;
        ImageButton editButton, startDate, endDate;
        ConstraintLayout goalItemContainer, editGoalContainer;
        Button saveBtn, deleteBtn;
        EditText editGoal;
        Spinner editFrequencyBtn, editSun;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.goalTitle);
            frequency= itemView.findViewById(R.id.editgoalFrequency);
            frequencyText = itemView.findViewById(R.id.goalFrequency);
            sun_points = itemView.findViewById(R.id.sun_points);
            checkboxBackground = itemView.findViewById(R.id.checkboxBackground);
            checkmark = itemView.findViewById(R.id.checkmark);
            editButton = itemView.findViewById(R.id.editButton);
            goalItemContainer = itemView.findViewById(R.id.goalItemContainer);

            // Edit Goal Section
            editGoalContainer = itemView.findViewById(R.id.editGoalContainer);
            saveBtn = itemView.findViewById(R.id.saveEditGoalBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            editGoal = itemView.findViewById(R.id.goalTitleEdit);
            startDate = itemView.findViewById(R.id.startDateBtn);
            endDate = itemView.findViewById(R.id.endDateBtn);
            editFrequencyBtn = itemView.findViewById(R.id.editFrequencyBtn);
            editSun = itemView.findViewById(R.id.editSunDifficulty);
            editEndDate = itemView.findViewById(R.id.editEndDate);
            editStartDate = itemView.findViewById(R.id.editStartDate);
        }
    }
}
