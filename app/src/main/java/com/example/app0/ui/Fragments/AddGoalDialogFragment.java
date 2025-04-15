package com.example.app0.ui.Fragments;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app0.R;
import com.example.app0.data.Converters.DifficultyConverter;
import com.example.app0.data.Converters.RepeatConverter;
import com.example.app0.data.Local.Entity.Goal;
import com.example.app0.data.Local.Entity.GoalInstance;
import com.example.app0.ui.DatePickerHelper;
import com.example.app0.ui.SuggestionsAdapter;
import com.example.app0.ui.ViewModel.GoalsViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddGoalDialogFragment extends DialogFragment
        implements SuggestionsAdapter.OnSuggestionClickListener {

    private static final String ARG_SELECTED_DATE = "selected_date";
    private Date selectedDate, untilDate;
    private String frequency;
    private ImageButton closeButton;
    private Button doneButton;
    private Chip dateChip, frequencyChip, difficultyChip;
    private EditText goalInput;
    private SimpleDateFormat otherFormatter, dayFormatter, dateFormatter;

    public static AddGoalDialogFragment newInstance(Date selectedDate) {
        AddGoalDialogFragment fragment = new AddGoalDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SELECTED_DATE, selectedDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedDate = (Date) getArguments().getSerializable(ARG_SELECTED_DATE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.fragment_add_goal, null);

        // Initialize views
        goalInput = view.findViewById(R.id.goalInput);
        dateChip = view.findViewById(R.id.dateChip);
        frequencyChip = view.findViewById(R.id.frequencyChip);
        difficultyChip = view.findViewById(R.id.difficultyChip);
        closeButton = view.findViewById(R.id.closeButton);
        //tvUntilDate = view.findViewById(R.id.tvUntilDate);

        otherFormatter = new SimpleDateFormat("EEE, dd MMM YYYY", Locale.getDefault());
        dayFormatter = new SimpleDateFormat("EEE", Locale.getDefault());
        dateFormatter = new SimpleDateFormat("dd", Locale.getDefault());

        // Set up suggestions RecyclerView
        RecyclerView suggestionsRecycler = view.findViewById(R.id.suggestionsRecycler);
        suggestionsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        String[] suggestions = getResources().getStringArray(R.array.goal_suggestions);
        SuggestionsAdapter adapter = new SuggestionsAdapter(Arrays.asList(suggestions), this);
        suggestionsRecycler.setAdapter(adapter);

        // Set initial date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        dateChip.setText(sdf.format(selectedDate != null ? selectedDate : new Date()));

        // Set default values
        frequencyChip.setText("Daily");
        difficultyChip.setText("Easy");

        // Set click listeners
        dateChip.setOnClickListener(v -> showStartDatePicker());
        frequencyChip.setOnClickListener(v -> showFrequencySelector());
        difficultyChip.setOnClickListener(v -> showDifficultySelector());
        closeButton.setOnClickListener( v-> {
            dismiss();
        });

        // Done button
        Button btnDone = view.findViewById(R.id.doneButton);
        btnDone.setOnClickListener(v -> saveGoal());

        return new MaterialAlertDialogBuilder(requireContext())
                .setView(view)
                .create();
    }

    private void showStartDatePicker() {
        DatePickerHelper.showDatePickerDialog(
                requireContext(),
                dateChip.getText().toString(),
                date -> {
                    String displayDate = DatePickerHelper.DISPLAY_DATE_FORMAT.format(date);
                    dateChip.setText(displayDate);
                }
        );
    }

    private void showFrequencySelector() {
        Calendar calendar = Calendar.getInstance();
        Date curr_date = calendar.getTime();
        Calendar selectedEndDate = Calendar.getInstance();
        String[] frequencies = getResources().getStringArray(R.array.frequency_options);
        int currentSelection = Arrays.asList(frequencies).indexOf(frequencyChip.getText().toString());

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Frequency")
                .setSingleChoiceItems(frequencies, currentSelection, (dialog, which) -> {
                    if (which > 0) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),(DatePicker datePicker, int sel_Year, int sel_Month, int sel_Day) -> {
                            selectedEndDate.set(sel_Year, sel_Month, sel_Day);
                            frequencyChip.setText(frequencies[which] + ", " + otherFormatter.format(selectedEndDate.getTime())); // else follow normal format
                            frequency = frequencies[which];
                            untilDate = selectedEndDate.getTime();
                        },
                            selectedEndDate.get(Calendar.YEAR), selectedEndDate.get(Calendar.MONTH), selectedEndDate.get(Calendar.DAY_OF_MONTH));  // show last selected date
                        datePickerDialog.show();
                    }
                    else { frequencyChip.setText(frequencies[which]); }
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDifficultySelector() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_difficulty, null);

        TextView tvEasy = dialogView.findViewById(R.id.tvEasy);
        TextView tvMedium = dialogView.findViewById(R.id.tvMedium);
        TextView tvHard = dialogView.findViewById(R.id.tvHard);

        setSunIcons(tvEasy, 3);
        setSunIcons(tvMedium, 6);
        setSunIcons(tvHard, 10);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Difficulty")
                .setView(dialogView)
                .setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
                .create();

        tvEasy.setOnClickListener(v -> {
            difficultyChip.setText("Easy");
            dialog.dismiss();
        });

        tvMedium.setOnClickListener(v -> {
            difficultyChip.setText("Medium");
            dialog.dismiss();
        });

        tvHard.setOnClickListener(v -> {
            difficultyChip.setText("Challenging");
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setSunIcons(TextView textView, int count) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append("☀");
            if (i < count - 1) builder.append(" ");
        }
        textView.setText(builder);
    }

    private void saveGoal() {
        String title = goalInput.getText().toString().trim();
        if (title.isEmpty()) {
            goalInput.setError("Title is required");
            return;
        }

        try {
            String startDate = DatePickerHelper.displayToStorageFormat_2(dateChip.getText().toString());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateStart = sdf.parse(startDate);
            Calendar startDateCal = Calendar.getInstance();
            startDateCal.setTime(dateStart);

            startDateCal.set(Calendar.HOUR_OF_DAY, 0);
            startDateCal.set(Calendar.MINUTE, 0);
            startDateCal.set(Calendar.SECOND, 0);
            startDateCal.set(Calendar.MILLISECOND, 0);

            Calendar endDateCal = Calendar.getInstance();

            if (untilDate == null) {
                endDateCal.set(Calendar.HOUR_OF_DAY, 0);
                endDateCal.set(Calendar.MINUTE, 0);
                endDateCal.set(Calendar.SECOND, 0);
                endDateCal.set(Calendar.MILLISECOND, 0);
            }
            else {
                endDateCal.setTime(untilDate);

                endDateCal.set(Calendar.HOUR_OF_DAY, 0);
                endDateCal.set(Calendar.MINUTE, 0);
                endDateCal.set(Calendar.SECOND, 0);
                endDateCal.set(Calendar.MILLISECOND, 0);


            }

            frequency = frequencyChip.getText().toString().split(",")[0];
            Log.d(TAG, "frequency: " + frequency);

            // Create the goal
            Goal newGoal = new Goal(
                    title,
                    startDateCal,
                    RepeatConverter.toRepeat(frequency),
                    endDateCal,
                    DifficultyConverter.toDifficultyFromString(difficultyChip.getText().toString())
            );

            GoalsViewModel viewModel = new ViewModelProvider(requireActivity()).get(GoalsViewModel.class);
            viewModel.addGoalWithCallback(newGoal, new GoalsViewModel.GoalAddCallback() {
                @Override
                public void onGoalAdded(long goalId) {
                    Log.d(TAG, "Goal added with ID: " + goalId);

                    GoalInstance newGoalInstance = new GoalInstance(
                            goalId,  // Now this is the correct ID from the database
                            title,
                            startDateCal,
                            RepeatConverter.toRepeat(frequency),
                            endDateCal,
                            DifficultyConverter.toDifficultyFromString(difficultyChip.getText().toString()),
                            startDateCal,
                            false
                    );

                    // Add goal instance (with or without callback)
                    viewModel.addGoalInstanceWithCallback(newGoalInstance, new GoalsViewModel.GoalAddCallback() {
                        @Override
                        public void onGoalAdded(long instanceId) {
                            // This runs after the goal instance is successfully added
//                            requireActivity().runOnUiThread(() -> {
//                                Toast.makeText(requireContext(), "Goal added successfully", Toast.LENGTH_SHORT).show();
//                                dismiss();
//                            });

//                            if (getContext() != null) {
//                                Toast.makeText(getContext(), "Goal added successfully", Toast.LENGTH_SHORT).show();
//                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            requireActivity().runOnUiThread(() -> {
                                Log.e(TAG, "Error adding goal instance", e);
                                Toast.makeText(requireContext(), "Error adding goal instance: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    requireActivity().runOnUiThread(() -> {
                        Log.e(TAG, "Error adding goal", e);
                        Toast.makeText(requireContext(), "Error saving goal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });

            dismiss();

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error saving goal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuggestionClick(String suggestion) {
        goalInput.setText(suggestion);
        goalInput.setSelection(suggestion.length());
    }
}