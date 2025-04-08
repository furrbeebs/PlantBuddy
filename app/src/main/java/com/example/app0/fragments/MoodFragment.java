package com.example.app0.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app0.R;
import com.example.app0.moodtracker.MoodCalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MoodFragment extends Fragment {

    private MoodCalendarView moodCalendar;
    private LinearLayout moodDetailsContainer;
    private TextView selectedDateText;
    private ImageView selectedMoodImage;
    private TextView moodNotesText;

    // Dialog views
    private View moodDialog;
    private ImageView closeMoodDialog;
    private ImageView moodVerySad, moodSad, moodNeutral, moodHappy, moodVeryHappy;
    private EditText notesInput;
    private Button saveButton;

    // Currently selected date
    private int selectedYear, selectedMonth, selectedDay;
    private int selectedMoodResId;
    private int selectedMoodValue; // 1-7 corresponding to the mood values

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mood, container, false);

        // Initialize views
        moodCalendar = rootView.findViewById(R.id.mood_calendar);
        moodDetailsContainer = rootView.findViewById(R.id.mood_details_container);
        selectedDateText = rootView.findViewById(R.id.selected_date);
        selectedMoodImage = rootView.findViewById(R.id.selected_mood);
        moodNotesText = rootView.findViewById(R.id.mood_notes);

        // Create and add mood dialog
        moodDialog = inflater.inflate(R.layout.mood_dialog, container, false);
        ((ViewGroup) rootView).addView(moodDialog);
        moodDialog.setVisibility(View.GONE);

        // Initialize dialog views
        closeMoodDialog = moodDialog.findViewById(R.id.close_dialog);
        moodVerySad = moodDialog.findViewById(R.id.mood_very_sad);
        moodSad = moodDialog.findViewById(R.id.mood_sad);
        moodNeutral = moodDialog.findViewById(R.id.mood_neutral);
        moodHappy = moodDialog.findViewById(R.id.mood_happy);
        moodVeryHappy = moodDialog.findViewById(R.id.mood_very_happy);
        notesInput = moodDialog.findViewById(R.id.notes_input);
        saveButton = moodDialog.findViewById(R.id.done_button);

        // Set up the calendar date selection listener
        moodCalendar.setOnDateSelectedListener((year, month, day, moodEntry) -> {
            selectedYear = year;
            selectedMonth = month;
            selectedDay = day;

            // Show mood details if exists
            if (moodEntry != null) {
                showMoodDetails(year, month, day, moodEntry);
            } else {
                // Show dialog to create new mood entry
                showMoodDialog(year, month, day);
            }
        });

        // Set up mood selection in dialog
        setupMoodSelectionListeners();

        // Set up close dialog button
        closeMoodDialog.setOnClickListener(v -> {
            moodDialog.setVisibility(View.GONE);
        });

        // Set up save button
        saveButton.setOnClickListener(v -> {
            saveMoodEntry();
        });

        return rootView;
    }

    private void setupMoodSelectionListeners() {
        // Clear previous selection when selecting a new mood
        View.OnClickListener moodClickListener = v -> {
            resetMoodSelection();
            v.setSelected(true);

            // Set the selected mood resource id and value
            if (v.getId() == R.id.mood_very_sad) {
                selectedMoodResId = R.drawable.mood_very_sad;
                selectedMoodValue = 1;
            } else if (v.getId() == R.id.mood_sad) {
                selectedMoodResId = R.drawable.mood_sad;
                selectedMoodValue = 2;
            } else if (v.getId() == R.id.mood_neutral) {
                selectedMoodResId = R.drawable.mood_neutral;
                selectedMoodValue = 3;
            } else if (v.getId() == R.id.mood_happy) {
                selectedMoodResId = R.drawable.mood_happy;
                selectedMoodValue = 4;
            } else if (v.getId() == R.id.mood_very_happy) {
                selectedMoodResId = R.drawable.mood_very_happy;
                selectedMoodValue = 6;
            }
        };

        // Apply the listener to all mood icons
        moodVerySad.setOnClickListener(moodClickListener);
        moodSad.setOnClickListener(moodClickListener);
        moodNeutral.setOnClickListener(moodClickListener);
        moodHappy.setOnClickListener(moodClickListener);
        moodVeryHappy.setOnClickListener(moodClickListener);
    }

    private void resetMoodSelection() {
        moodVerySad.setSelected(false);
        moodSad.setSelected(false);
        moodNeutral.setSelected(false);
        moodHappy.setSelected(false);
        moodVeryHappy.setSelected(false);
        selectedMoodResId = 0;
        selectedMoodValue = 0;
    }

    private void showMoodDialog(int year, int month, int day) {
        // Reset dialog state
        resetMoodSelection();
        notesInput.setText("");

        // Format and display the date
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());

        // Hide details and show dialog
        moodDetailsContainer.setVisibility(View.GONE);
        moodDialog.setVisibility(View.VISIBLE);
    }

    private void showMoodDetails(int year, int month, int day, MoodCalendarView.MoodEntry moodEntry) {
        // Format and display the date
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());
        selectedDateText.setText(formattedDate);

        // Display mood and notes
        selectedMoodImage.setImageResource(moodEntry.getMoodResId());
        moodNotesText.setText(moodEntry.getNotes());

        // Hide dialog and show details
        moodDialog.setVisibility(View.GONE);
        moodDetailsContainer.setVisibility(View.VISIBLE);
    }

    private void saveMoodEntry() {
        if (selectedMoodValue == 0) {
            // No mood selected, show error or select default
            selectedMoodValue = 3; // Default to neutral if none selected
            selectedMoodResId = R.drawable.mood_neutral;
        }

        String notes = notesInput.getText().toString().trim();

        // Save mood to calendar
        moodCalendar.setMoodEntry(selectedYear, selectedMonth, selectedDay, selectedMoodResId, notes);

        // Also save the mood value for the adapter's coloring
        saveMoodValueToStorage(selectedYear, selectedMonth, selectedDay, selectedMoodValue);

        // Close dialog and show details
        moodDialog.setVisibility(View.GONE);

        // Get the saved entry and show details
        MoodCalendarView.MoodEntry entry = moodCalendar.getMoodEntry(selectedYear, selectedMonth, selectedDay);
        showMoodDetails(selectedYear, selectedMonth, selectedDay, entry);
    }

    // This method would save the mood value to data storage (Database)
    private void saveMoodValueToStorage(int year, int month, int day, int moodValue) {
        // Implementation depends on your storage mechanism
        // e.g., using SharedPreferences:
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long dateMillis = cal.getTimeInMillis();

        // a placeholder - implement according to storage method
        // example:
        // SharedPreferences prefs = requireContext().getSharedPreferences("MoodPrefs", Context.MODE_PRIVATE);
        // prefs.edit().putInt("mood_" + dateMillis, moodValue).apply();
    }
}