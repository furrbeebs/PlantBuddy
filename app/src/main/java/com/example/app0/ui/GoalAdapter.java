package com.example.app0.ui;

import static com.example.app0.data.Converters.DifficultyConverter.fromDifficulty;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.app0.data.Local.Entity.PlantBuddy;
import com.example.app0.data.Local.Entity.Repeat;
import com.example.app0.ui.ViewModel.PlantBuddyViewModel;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private List<GoalInstance> goalInstances;
    private OnGoalChangeListener listener;
    private SimpleDateFormat otherFormatter;
    private boolean isPastDate = false;
    private PlantBuddyViewModel plantBuddyViewModel;
    private String username, plantname;
    private int level, image;
    private double xp;
    private double[] doubleContainer;
    private PlantBuddy plantBuddy;

    public void setIsPastDate(boolean isPastDate) {
        this.isPastDate = isPastDate;
        notifyDataSetChanged(); // refresh view to apply changes
    }

    public interface OnGoalChangeListener {
        void onGoalUpdated(Goal goal, GoalInstance goalInstance);
        void onGoalDeleted(Goal goal, GoalInstance goalInstance, Boolean deleteAllGoalInstances, String date);
        void onGoalStatusToggled(GoalInstance goalInstance);
    }

    public GoalAdapter(List<GoalInstance> goalInstances, PlantBuddyViewModel plantBuddyViewModel, PlantBuddy plantBuddy) {
        this.goalInstances = goalInstances;
        this.plantBuddyViewModel = plantBuddyViewModel;
        this.plantBuddy = plantBuddy;
    }

    // Update PlantBuddy after Observing
    public void setPlantBuddy(PlantBuddy plantBuddy) {
        this.plantBuddy = plantBuddy;
    }

    // set listeners
    public void setOnGoalChangeListener(OnGoalChangeListener listener) {
        this.listener = listener;
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
        // hide edit container and show goal container
        holder.goalItemContainer.setVisibility(View.VISIBLE);
        holder.editGoalContainer.setVisibility(View.GONE);

        GoalInstance goal = goalInstances.get(position);

        if (goal.getCompleted()) {
            holder.checkmark.setVisibility(View.VISIBLE);
        }
        else {
            holder.checkmark.setVisibility(View.GONE);
        }

        // disabling edit button and greying out depending on whether its in the past
        holder.editButton.setEnabled(!isPastDate);

        if (isPastDate) {
            holder.editButton.setAlpha(0.5f);
        }
        else {
            holder.editButton.setAlpha(1.0f);
        }

        // disable checkbox visually depending on whether its in the past
        holder.checkboxBackground.setEnabled(!isPastDate);
        holder.checkmark.setEnabled(!isPastDate);


        // only setting click listeners if it's not a past date
        // making function since its the same code for the two setOnClickListeners
        if (!isPastDate) {
            View.OnClickListener toggleCheckListeners = v -> {
                boolean newStatus = !goal.getCompleted();
                goal.setCompleted(newStatus);

                if (newStatus) {
                    holder.checkmark.setVisibility(View.VISIBLE);
                    updatePointBalance(plantBuddyViewModel, plantBuddy, fromDifficulty(goal.getDifficulty()));
                }
                else {
                    holder.checkmark.setVisibility(View.GONE);
                    updatePointBalance(plantBuddyViewModel, plantBuddy, -fromDifficulty(goal.getDifficulty()));
                }

                if (listener != null) {
                    listener.onGoalStatusToggled(goal);
                }
            };

            holder.checkboxBackground.setOnClickListener(toggleCheckListeners);
            holder.checkmark.setOnClickListener(toggleCheckListeners);

        }

        // if past date, set both click listeners to null so that nothing happens on click
        else {
            holder.checkmark.setOnClickListener(null);
            holder.checkboxBackground.setOnClickListener(null);
        }

        holder.title.setText(goal.getTitle());
        holder.frequencyText.setText(RepeatConverter.fromRepeat(goal.getRepeat()));
        holder.sun_points.setText(String.valueOf(DifficultyConverter.fromDifficulty(goal.getDifficulty())));

        otherFormatter = new SimpleDateFormat("dd MMM YYYY", Locale.getDefault());

        Calendar selectedDate = Calendar.getInstance();


        // ---------- GoalItemContainer Portion ----------
        holder.editButton.setOnClickListener( v-> {
            // hide goal container and show edit container
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


        // ------------------ Repeat Spinner -------------------
        ArrayAdapter<CharSequence> repeatAdapter = ArrayAdapter.createFromResource(
                holder.itemView.getContext(), R.array.repeat_options, android.R.layout.simple_spinner_item);
        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.editFrequencyBtn.setAdapter(repeatAdapter);

        // sets starting selection from DB
        String[] repeat_options = holder.itemView.getContext().getResources().getStringArray(R.array.repeat_options);
        int startingSelection_repeat = Arrays.asList(repeat_options).indexOf(RepeatConverter.fromRepeat(goal.getRepeat()));
        holder.editFrequencyBtn.setSelection(startingSelection_repeat);


        // -------------------- Difficulty Spinner -----------------------
        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(
                holder.itemView.getContext(), R.array.difficulty_options, android.R.layout.simple_spinner_item);
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.editSun.setAdapter(difficultyAdapter);

        // sets starting selection from DB
        String[] difficulty_options = holder.itemView.getContext().getResources().getStringArray(R.array.difficulty_options);
        int startingSelection_difficulty = Arrays.asList(difficulty_options).indexOf(DifficultyConverter.fromDifficultyToString(goal.getDifficulty()));
        holder.editSun.setSelection(startingSelection_difficulty);

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

            // handling delete for only the specific goal instance
            builder.setPositiveButton("This Instance", (dialog, which) -> {
                if (listener != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {

                        GoalInstance instance = goalInstances.get(adapterPosition);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String dateId = dateFormat.format(instance.getInstanceDate().getTime());

                        listener.onGoalDeleted(null, instance, false, dateId);

                        goalInstances.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                    }
                }
            });

            // handling delete for all goal instances of that goal + main goal object
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

            holder.goalItemContainer.setVisibility(View.VISIBLE);
            holder.editGoalContainer.setVisibility(View.GONE);

            notifyItemChanged(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() { return goalInstances.size(); }

    public static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView title, frequency, sun_points, frequencyText , editEndDate, editStartDate;
        ImageView checkboxBackground, checkmark;
        ImageButton editButton, closeButton;
        ConstraintLayout goalItemContainer, editGoalContainer;
        Button saveBtn, deleteBtn;
        EditText editGoal;
        Spinner editFrequencyBtn, editSun;
        LinearLayout startDate, endDate;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.goalTitle);
            frequency = itemView.findViewById(R.id.editgoalFrequency);
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
            closeButton = itemView.findViewById(R.id.closeButton);
            editGoal = itemView.findViewById(R.id.goalTitleEdit);
            startDate = itemView.findViewById(R.id.startDateSection);
            endDate = itemView.findViewById(R.id.endDateSection);
            editFrequencyBtn = itemView.findViewById(R.id.editFrequencyBtn);
            editSun = itemView.findViewById(R.id.editSunDifficulty);
            editEndDate = itemView.findViewById(R.id.editEndDate);
            editStartDate = itemView.findViewById(R.id.editStartDate);
        }
    }

    // to update the points for the plant buddy upon completion of goal
    public void updatePointBalance(PlantBuddyViewModel plantBuddyViewModel, PlantBuddy buddy, int XP) {
        CheckForms2025 checkform = new CheckForms2025();

        username = buddy.getUsername();
        plantname = buddy.getPlantname();
        level = buddy.getLevel();
        xp = buddy.getXp();
        image = buddy.getImage();

        // Adding new Points
        xp += XP;

        // Checking if user eligible for upgrade
        doubleContainer = checkform.checkLevel(xp, level);
        xp = doubleContainer[0];
        level = (int)Math.round(doubleContainer[1]);
        Log.d("INFO", username + "/" + plantname + "/" + level + "/" + xp + "/" + image);

        plantBuddyViewModel.update(new PlantBuddy(username, plantname, level, xp, image));
    }
}
