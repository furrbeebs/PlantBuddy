package com.example.app0.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.example.app0.R;
import com.google.android.material.chip.Chip;

import java.util.Arrays;

public class AddGoalFragment extends Fragment {

    private Chip dateChip, frequencyChip, difficultyChip;
    private String selectedDate = "24 Feb 2025";
    private String selectedFrequency = "Daily";
    private String selectedDifficulty = "Easy";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_goal, container, false);

        // Initialize views
        dateChip = view.findViewById(R.id.dateChip);
        frequencyChip = view.findViewById(R.id.frequencyChip);
        difficultyChip = view.findViewById(R.id.difficultyChip);

        // Set current values
        dateChip.setText(selectedDate);
        frequencyChip.setText(selectedFrequency);
        difficultyChip.setText(selectedDifficulty);

        // Set click listeners
        setupDatePicker();
        setupFrequencyPicker();
        setupDifficultyPicker();

        // In AddGoalFragment's onCreateView:
        ImageButton closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    private void setupDatePicker() {
        dateChip.setOnClickListener(v -> {
            // Parse current date
            String[] dateParts = selectedDate.split(" ");
            int day = Integer.parseInt(dateParts[0]);
            int month = convertMonthToNumber(dateParts[1]);
            int year = Integer.parseInt(dateParts[2]);

            DatePickerDialog datePicker = new DatePickerDialog(
                    requireContext(),
                    (view, yearSelected, monthOfYear, dayOfMonth) -> {
                        selectedDate = dayOfMonth + " " + convertNumberToMonth(monthOfYear) + " " + yearSelected;
                        dateChip.setText(selectedDate);
                    },
                    year, month, day
            );
            datePicker.show();
        });
    }

    private void setupFrequencyPicker() {
        frequencyChip.setOnClickListener(v -> {
            String[] frequencies = {"Daily", "Weekly", "Monthly", "Yearly"};

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Select Frequency")
                    .setItems(frequencies, (dialog, which) -> {
                        selectedFrequency = frequencies[which];
                        frequencyChip.setText(selectedFrequency);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void setupDifficultyPicker() {
        difficultyChip.setOnClickListener(v -> {
            String[] difficulties = {"Easy", "Medium", "Hard"};

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Select Difficulty")
                    .setSingleChoiceItems(difficulties, Arrays.asList(difficulties).indexOf(selectedDifficulty),
                            (dialog, which) -> {
                                selectedDifficulty = difficulties[which];
                                difficultyChip.setText(selectedDifficulty);
                                dialog.dismiss();
                            })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    // Helper methods for date conversion
    private int convertMonthToNumber(String month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return Arrays.asList(months).indexOf(month);
    }

    private String convertNumberToMonth(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month];
    }
}