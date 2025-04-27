package com.example.app0.ui.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.app0.ProgressPage;
import com.example.app0.R;
import com.example.app0.data.Local.Entity.CalendarItem;
import com.example.app0.models.Mood;
import com.example.app0.ui.ViewModel.CalendarItemViewModel;
import com.example.app0.moodtracker.MoodCalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MoodFragment extends Fragment {
    private CalendarItemViewModel moodViewModel;
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

    // Edit and Delete buttons
    private Button editButton;
    private Button deleteButton;

    // Back Button to Home/Progress Page
    private ImageButton backButton;

    // Currently selected date
    private int selectedYear, selectedMonth, selectedDay;
    private int selectedMoodResId;

    // Flag to track if we're editing or creating new
    private boolean isEditMode = false;

    // Calendar Dropdown
    private TextView monthTextView;
    private Calendar currentCalendar = Calendar.getInstance();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mood, container, false);


        // Get the ViewModel
        moodViewModel = new ViewModelProvider(this).get(CalendarItemViewModel.class);

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

        // Initialize dropdown menu / title
        monthTextView = rootView.findViewById(R.id.month_text);

        // Set click listener to show drop down menu of month & year
        monthTextView.setOnClickListener(v -> showMonthYearPicker());

        // Create and add the update and delete buttons
        LinearLayout buttonLayout = new LinearLayout(requireContext());
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Update title (month/year)
        updateMonthYearDisplay();

        // In onCreateView, after initializing moodCalendar
        // If month changed, set the new month and new year & update header
        moodCalendar.setOnMonthChangedListener((year, month) -> {
            currentCalendar.set(Calendar.YEAR, year);
            currentCalendar.set(Calendar.MONTH, month);
            updateMonthYearDisplay();
        });

        // Create backButton
        backButton = rootView.findViewById(R.id.back_button);

        // Set up Back Button
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ProgressPage.class);
            startActivity(intent);
        });

        // The buttons below are created by AI and edited by Ziwei because there was layout issues and buttons created on xml was not showing 
        // Create Update Button
        editButton = new Button(requireContext());
        editButton.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        editButton.setText("Edit");

        // Create Delete Button
        deleteButton = new Button(requireContext());
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        deleteButton.setText("Delete");

        // Add buttons to layout
        buttonLayout.addView(editButton);
        buttonLayout.addView(deleteButton);

        // Add button layout to moodDetailsContainer
        moodDetailsContainer.addView(buttonLayout);

        // Set up mood selection in dialog
        setupMoodSelectionListeners();

        // When closeMoodDialog button is clickedm close the view
        closeMoodDialog.setOnClickListener(v -> moodDialog.setVisibility(View.GONE));

        // Set up save button
        saveButton.setOnClickListener(v -> {
            // If isEditMode = true -> there is change in mood on that day
            if (isEditMode) {
                updateMoodEntry();
                // If there is no change
            } else {
                saveMoodEntry();
            }
        });

        // Set up edit button listener
        editButton.setOnClickListener(v -> {
            // Get data for that day
            MoodCalendarView.MoodEntry currentEntry = moodCalendar.getMoodEntry(
                    selectedYear, selectedMonth, selectedDay);
            isEditMode = true;
            showMoodDialogForEdit(selectedYear, selectedMonth, selectedDay,
                    currentEntry.getMoodResId(), currentEntry.getNotes());

        });

        // Set up delete button listener
        deleteButton.setOnClickListener(v -> {
            showDeleteConfirmationDialog();
        });

        // Set up the calendar date selection listener
        moodCalendar.setOnDateSelectedListener((year, month, day, moodEntry) -> {
            selectedYear = year;
            selectedMonth = month;
            selectedDay = day;

            // Reset edit mode flag for new selections
            isEditMode = false;

            // Show mood details if exists
            if (moodEntry != null) {
                showMoodDetails(year, month, day, moodEntry);
            } else {
                // Show dialog to create new mood entry
                showMoodDialog(year, month, day);
            }
        });

        // Observe calendar items
        moodViewModel.getAllCalendarItems().observe(getViewLifecycleOwner(), calendarItems -> {
            if (calendarItems != null) {
                updateCalendarWithItems(calendarItems);
            }
        });

        // Load initial data
        loadInitialData();

        return rootView;
    }

    private void updateCalendarWithItems(List<CalendarItem> calendarItems) {
        // Update calendar UI with database items
        moodCalendar.updateWithCalendarItems(calendarItems);
    }

    private void loadInitialData() {
        // Fetch all mood data on a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CalendarItem> items = moodViewModel.getAllCalendarItemsSync();
            if (items != null) {
                requireActivity().runOnUiThread(() -> updateCalendarWithItems(items));
            }
        });
    }

    private void setupMoodSelectionListeners() {
        // Clear previous selection when selecting a new mood
        View.OnClickListener moodClickListener = v -> {
            resetMoodSelection();
            v.setSelected(true);

            // Set the selected mood resource id
            if (v.getId() == R.id.mood_very_sad) {
                selectedMoodResId = R.drawable.mood_very_sad;
            } else if (v.getId() == R.id.mood_sad) {
                selectedMoodResId = R.drawable.mood_sad;
            } else if (v.getId() == R.id.mood_neutral) {
                selectedMoodResId = R.drawable.mood_neutral;
            } else if (v.getId() == R.id.mood_happy) {
                selectedMoodResId = R.drawable.mood_happy;
            } else if (v.getId() == R.id.mood_very_happy) {
                selectedMoodResId = R.drawable.mood_very_happy;
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
    }

    private void showMoodDialog(int year, int month, int day) {
        // Reset dialog state
        resetMoodSelection();
        notesInput.setText("");

        // Update dialog title/button text for new entry
        saveButton.setText("Save");

        // Hide details and show dialog
        moodDetailsContainer.setVisibility(View.GONE);
        moodDialog.setVisibility(View.VISIBLE);
    }

    private void showMoodDialogForEdit(int year, int month, int day, int moodResId, String existingNotes) {
        // Reset dialog state first
        resetMoodSelection();

        // Pre-select the current mood
        selectMoodInDialog(moodResId);

        // Pre-fill notes
        notesInput.setText(existingNotes);

        // Update dialog title/button text for editing
        saveButton.setText("Update");

        // Hide details and show dialog
        moodDetailsContainer.setVisibility(View.GONE);
        moodDialog.setVisibility(View.VISIBLE);
    }

    private void selectMoodInDialog(int moodResId) {
        // Select the correct mood icon based on resource ID
        if (moodResId == R.drawable.mood_very_sad) {
            moodVerySad.setSelected(true);
        } else if (moodResId == R.drawable.mood_sad) {
            moodSad.setSelected(true);
        } else if (moodResId == R.drawable.mood_neutral) {
            moodNeutral.setSelected(true);
        } else if (moodResId == R.drawable.mood_happy) {
            moodHappy.setSelected(true);
        } else if (moodResId == R.drawable.mood_very_happy) {
            moodVeryHappy.setSelected(true);
        }

        // Save the selected resource ID
        selectedMoodResId = moodResId;
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
        if (selectedMoodResId == 0) {
            // No mood selected, select default
            selectedMoodResId = R.drawable.mood_neutral;
        }

        String notes = notesInput.getText().toString().trim();

        // Save mood via ViewModel
        moodViewModel.saveMoodEntry(selectedYear, selectedMonth, selectedDay, selectedMoodResId, notes);

        // Close dialog and show details
        moodDialog.setVisibility(View.GONE);

        // Create a temporary entry to display until the LiveData updates
        MoodCalendarView.MoodEntry entry = new MoodCalendarView.MoodEntry(
                String.format(Locale.US, "%04d-%02d-%02d",
                        selectedYear, selectedMonth + 1, selectedDay),
                selectedMoodResId,
                notes
        );

        // Update UI immediately
        moodCalendar.setMoodEntry(selectedYear, selectedMonth, selectedDay, selectedMoodResId, notes);
        showMoodDetails(selectedYear, selectedMonth, selectedDay, entry);

        Toast.makeText(requireContext(), "Entry inserted", Toast.LENGTH_SHORT).show();
    }


    private void updateMoodEntry() {
        if (selectedMoodResId == 0) {
            selectedMoodResId = R.drawable.mood_neutral;
        }

        String notes = notesInput.getText().toString().trim();

        // Create a properly initialized calendar for the selected date
        Calendar calendar = Calendar.getInstance();
        calendar.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();

        // Create the mood object safely
        Mood mood = Mood.fromResId(selectedMoodResId);

        // Create a new calendar item with the date that is not null
        CalendarItem updatedItem = new CalendarItem(date, mood, notes);

        // Update in background thread
        // this code below is written by AI and edited by Ziwei
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Get existing item to obtain its ID
                List<CalendarItem> allItems = moodViewModel.getAllCalendarItemsSync();
                if (allItems != null) {
                    for (CalendarItem item : allItems) {
                        if (item != null && item.getDate() != null) {
                            Calendar itemCal = Calendar.getInstance();
                            itemCal.setTime(item.getDate());

                            if (itemCal.get(Calendar.YEAR) == selectedYear &&
                                    itemCal.get(Calendar.MONTH) == selectedMonth &&
                                    itemCal.get(Calendar.DAY_OF_MONTH) == selectedDay) {
                                // Found the item - set ID and update
                                updatedItem.setId(item.getId());
                                break;
                            }
                        }
                    }
                }

                // Update in database
                moodViewModel.updateCalendarItem(updatedItem);

                // Update UI on main thread
                requireActivity().runOnUiThread(() -> {
                    // Close dialog and show details
                    moodDialog.setVisibility(View.GONE);

                    // Create entry for display
                    MoodCalendarView.MoodEntry entry = new MoodCalendarView.MoodEntry(
                            String.format(Locale.US, "%04d-%02d-%02d",
                                    selectedYear, selectedMonth + 1, selectedDay),
                            selectedMoodResId,
                            notes
                    );

                    // Update UI
                    moodCalendar.setMoodEntry(selectedYear, selectedMonth, selectedDay, selectedMoodResId, notes);
                    showMoodDetails(selectedYear, selectedMonth, selectedDay, entry);

                    Toast.makeText(requireContext(), "Entry updated", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                Log.e("MoodFragment", "Error updating mood: " + e.getMessage(), e);
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Failed to update mood", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }


    private void showDeleteConfirmationDialog() {
        // Create AlertDialog for confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Mood Entry");
        builder.setMessage("Are you sure you want to delete this mood entry?");

        // Add buttons
        builder.setPositiveButton("Delete", (dialog, which) -> {
            deleteMoodEntry();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.create().show();
    }

    private void deleteMoodEntry() {
        try {
            // Create calendar and date objects
            Calendar calendar = Calendar.getInstance();
            calendar.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // Delete from database
            moodViewModel.deleteCalendarItemByDate(selectedYear, selectedMonth, selectedDay);

            // Remove from UI - immediate visual feedback
            moodCalendar.removeMoodEntry(selectedYear, selectedMonth, selectedDay);
            moodDetailsContainer.setVisibility(View.GONE);

            // Show confirmation
            Toast.makeText(requireContext(), "Entry deleted", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("MoodFragment", "Error deleting mood entry", e);
            if (isAdded()) {
                Toast.makeText(requireContext(), "Failed to delete entry", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Scrolling wheel after clicking dropdown menu (monthYearText)
    private void showMonthYearPicker() {
        // Create a custom dialog for month-year picker
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.month_year_picker_dialog, null);
        builder.setView(dialogView);

        // Get number pickers for month and year
        final NumberPicker monthPicker = dialogView.findViewById(R.id.month_picker);
        final NumberPicker yearPicker = dialogView.findViewById(R.id.year_picker);

        // Set up month picker
        String[] months = new String[]{"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        monthPicker.setDisplayedValues(months);

        // Get current values from MoodCalendarView's calendar
        Calendar calendarViewDate = Calendar.getInstance();
        calendarViewDate.set(
                moodCalendar.getCurrentYear(),
                moodCalendar.getCurrentMonth(),
                3
                // date can be set to any value it doesnt matter
        );

        // Set current values in pickers (scroll wheel position will be at the one
        // at the calendar layout heading month/year
        monthPicker.setValue(calendarViewDate.get(Calendar.MONTH));

        // Set up year picker
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int minYear = currentYear - 5; // 5 years back
        int maxYear = currentYear;     // Current year as maximum

        yearPicker.setMinValue(minYear);
        yearPicker.setMaxValue(maxYear);
        yearPicker.setValue(Math.min(Math.max(calendarViewDate.get(Calendar.YEAR), minYear), maxYear));
        yearPicker.setWrapSelectorWheel(false); // Turn off wrapping/cycling behavior for years

        // Set button actions
        builder.setPositiveButton("OK", (dialog, which) -> {
            int selectedMonth = monthPicker.getValue();
            int selectedYear = yearPicker.getValue();

            // Update the calendar to display the selected month
            moodCalendar.updateCalendarTitle();
            moodCalendar.setCalendarLayoutHeader(selectedYear, selectedMonth);

            // Update the month text display
            currentCalendar.set(Calendar.YEAR, selectedYear);
            currentCalendar.set(Calendar.MONTH, selectedMonth);
            updateMonthYearDisplay();
        });

        builder.setNegativeButton("Cancel", null);

        // Show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // This is different from the updateCalendarTitle() from MoodCalendarView
    // It does not update the calendar grid
    private void updateMonthYearDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        monthTextView.setText(dateFormat.format(currentCalendar.getTime()));


    }


}
