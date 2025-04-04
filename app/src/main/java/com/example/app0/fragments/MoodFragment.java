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
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.example.app0.R;
import com.example.app0.moodtracker.MoodCalendarView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MoodFragment extends Fragment {
    private MoodCalendarView calendarView;
    private LinearLayout moodDetailsContainer;
    private TextView selectedDateText;
    private ImageView selectedMoodImage;
    private TextView moodNotesText;

    // for dialog (mood - daily notes)
    private View moodInputDialog;
    private EditText notesInput;
    private ImageView closeDialogButton;
    private Button doneButton;
    private ImageView[] moodIcons = new ImageView[5];

    private int selectedMoodValue = 3; // Default to neutral
    private Date currentSelectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_mood, container, false);

        // Initialize UI components
        calendarView = view.findViewById(R.id.mood_calendar);
        moodDetailsContainer = view.findViewById(R.id.mood_details_container);
        selectedDateText = view.findViewById(R.id.selected_date);
        selectedMoodImage = view.findViewById(R.id.selected_mood);
        moodNotesText = view.findViewById(R.id.mood_notes);

        // Inflate the dialog layout and add it to the main layout
        moodInputDialog = inflater.inflate(R.layout.dialog_mood_input, null);

        // Add the dialog to the root layout with proper layout params
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        // Position it below the calendar
        params.addRule(RelativeLayout.BELOW, R.id.mood_calendar);

        // Create a RelativeLayout as the container for the fragment content
        RelativeLayout rootLayout = new RelativeLayout(getContext());
        rootLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // Add the existing view content to the RelativeLayout
        ((ViewGroup) view).removeView(view.findViewById(R.id.mood_fragment_root_layout));
        rootLayout.addView(view.findViewById(R.id.mood_fragment_root_layout));

        // Add the dialog to the RelativeLayout
        rootLayout.addView(moodInputDialog, params);

        // Initially hide the dialog
        moodInputDialog.setVisibility(View.GONE);

        // Initialize dialog components
        notesInput = moodInputDialog.findViewById(R.id.notes_input);
        closeDialogButton = moodInputDialog.findViewById(R.id.close_dialog);
        doneButton = moodInputDialog.findViewById(R.id.done_button);

        // Initialize mood icons
        moodIcons[0] = moodInputDialog.findViewById(R.id.mood_very_sad);
        moodIcons[1] = moodInputDialog.findViewById(R.id.mood_sad);
        moodIcons[2] = moodInputDialog.findViewById(R.id.mood_neutral);
        moodIcons[3] = moodInputDialog.findViewById(R.id.mood_happy);
        moodIcons[4] = moodInputDialog.findViewById(R.id.mood_very_happy);

        // Set up click listeners for mood icons
        for (int i = 0; i < moodIcons.length; i++) {
            final int moodValue = i + 1;
            moodIcons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectMood(moodValue);
                }
            });
        }

        // Set up close button click listener
        closeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMoodInputDialog();
            }
        });

        // Set up done button click listener
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMoodAndNotes();
            }
        });

        // Set up date click listener to show mood input dialog
        calendarView.setOnDateClickListener(new MoodCalendarView.OnDateClickListener() {
            @Override
            public void onDateClick(View view, Date date) {
                // Save the selected date
                currentSelectedDate = date;

                // Format date for display
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                String dateString = sdf.format(date);
                selectedDateText.setText(dateString);

                // Show mood input dialog
                showMoodInputDialog(date);
            }
        });

        return rootLayout; // Return the new root layout
    }

    // Method to show the mood input dialog
    private void showMoodInputDialog(Date date) {
        // Reset the dialog state
        notesInput.setText("");
        selectMood(3); // Default to neutral mood

        // Make sure the dialog is visible
        moodInputDialog.setVisibility(View.VISIBLE);
        moodInputDialog.bringToFront();  // Bring it to the front

        // Ensure the layout is redrawn properly
        moodInputDialog.requestLayout();
        moodInputDialog.invalidate();
    }

    // Method to hide the mood input dialog
    private void hideMoodInputDialog() {
        moodInputDialog.setVisibility(View.GONE);
    }

    // Method to select a mood in the dialog
    private void selectMood(int moodValue) {
        // Update selected mood
        selectedMoodValue = moodValue;

        // Update UI to highlight selected mood
        for (int i = 0; i < moodIcons.length; i++) {
            if (i == moodValue - 1) {
                // Selected mood - add a selection indicator
                moodIcons[i].setBackgroundResource(R.drawable.mood_selected_background);
            } else {
                // Unselected moods
                moodIcons[i].setBackground(null);
            }
        }
    }

    // Method to save mood and notes
    private void saveMoodAndNotes() {
        // Get notes from input field
        String notes = notesInput.getText().toString();

        // Update mood display
        selectedMoodImage.setImageResource(getMoodDrawableResource(selectedMoodValue));

        // Update notes display
        moodNotesText.setText(notes);

        // Show the details container
        moodDetailsContainer.setVisibility(View.VISIBLE);

        // Hide the dialog
        hideMoodInputDialog();

        // Update the calendar view with the new mood
        try {
            // Convert Date to long timestamp (milliseconds since epoch)
            long dateTimeMillis = currentSelectedDate.getTime();

            // Convert the integer mood value to a String
            String moodId = String.valueOf(selectedMoodValue);

            // Pass the string ID to the calendar view
            calendarView.setMoodForDate(dateTimeMillis, moodId);
        } catch (Exception e) {
            // If the method doesn't exist yet, this will catch the error
        }
    }

    // Utility method to get the drawable resource for a mood value
    private int getMoodDrawableResource(int moodValue) {
        switch (moodValue) {
            case 1: return R.drawable.mood_very_sad;
            case 2: return R.drawable.mood_sad;
            case 3: return R.drawable.mood_neutral;
            case 4: return R.drawable.mood_happy;
            case 5: return R.drawable.mood_very_happy;
            default: return R.drawable.mood_neutral;
        }
    }
}