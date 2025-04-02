package com.example.app0.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.app0.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class EditGoalFragment extends Fragment {

    private TextInputEditText goalNameEditText;
    private Button startDateButton, repeatButton, untilButton, difficultyButton, saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_goal, container, false);

        // Initialize views
        goalNameEditText = view.findViewById(R.id.goalNameEditText);
        startDateButton = view.findViewById(R.id.startDateButton);
        repeatButton = view.findViewById(R.id.repeatButton);
        untilButton = view.findViewById(R.id.untilButton);
        difficultyButton = view.findViewById(R.id.difficultyButton);
        saveButton = view.findViewById(R.id.saveButton);

        // Set click listeners
        setupButtonListeners();

        // Load goal data from arguments
        loadGoalData();

        return view;
    }

    private void setupButtonListeners() {
        // Date picker for start date
        startDateButton.setOnClickListener(v -> showDatePicker());

        // Repeat options dialog
        repeatButton.setOnClickListener(v -> showRepeatOptionsDialog());

        // Until options dialog
        untilButton.setOnClickListener(v -> showUntilOptionsDialog());

        // Difficulty options dialog
        difficultyButton.setOnClickListener(v -> showDifficultyOptionsDialog());

        // Save button
        saveButton.setOnClickListener(v -> saveGoal());
    }

    private void loadGoalData() {
        Bundle args = getArguments();
        if (args != null) {
            String goalTitle = args.getString("goal_title", "");
            goalNameEditText.setText(goalTitle);

            // Set other fields if available in arguments
            if (args.containsKey("goal_points")) {
                // Set points if needed
            }
            if (args.containsKey("goal_frequency")) {
                repeatButton.setText(args.getString("goal_frequency"));
            }
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%02d/%02d/%02d", dayOfMonth, month + 1, year % 100);
                    startDateButton.setText(date);
                },
                2025, 1, 24 // Default date (24/02/25)
        );
        datePicker.show();
    }

    private void showRepeatOptionsDialog() {
        String[] options = {"Daily", "Weekly", "Monthly"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Repeat")
                .setItems(options, (dialog, which) -> repeatButton.setText(options[which]))
                .show();
    }

    private void showUntilOptionsDialog() {
        String[] options = {"Forever", "Custom date"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Until")
                .setItems(options, (dialog, which) -> {
                    untilButton.setText(options[which]);
                    if (which == 1) { // If "Custom date" selected
                        showDatePickerForUntil();
                    }
                })
                .show();
    }

    private void showDatePickerForUntil() {
        DatePickerDialog datePicker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%02d/%02d/%02d", dayOfMonth, month + 1, year % 100);
                    untilButton.setText(date);
                },
                2025, 1, 24
        );
        datePicker.show();
    }

    private void showDifficultyOptionsDialog() {
        String[] options = {"Easy", "Medium", "Hard"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Difficulty")
                .setItems(options, (dialog, which) -> difficultyButton.setText(options[which]))
                .show();
    }

    private void saveGoal() {
        String goalName = goalNameEditText.getText().toString().trim();
        if (goalName.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a goal name", Toast.LENGTH_SHORT).show();
            return;
        }

        String startDate = startDateButton.getText().toString();
        String repeat = repeatButton.getText().toString();
        String until = untilButton.getText().toString();
        String difficulty = difficultyButton.getText().toString();

        // TODO: Implement actual save logic (database, viewmodel, etc.)
        saveGoalToDatabase(goalName, startDate, repeat, until, difficulty);

        Toast.makeText(requireContext(), "Goal saved!", Toast.LENGTH_SHORT).show();
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void saveGoalToDatabase(String name, String startDate, String repeat, String until, String difficulty) {
        // Implement your database/API saving logic here
        // This could use Room, Firebase, Retrofit, etc.
    }
}