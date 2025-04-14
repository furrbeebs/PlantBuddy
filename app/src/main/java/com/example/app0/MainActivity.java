package com.example.app0;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.app0.ui.Activity.LoginPage;
import com.example.app0.ui.Fragments.MoodFragment;
import com.example.app0.ui.Fragments.ProgressFragment;

public class MainActivity extends AppCompatActivity {
    private ImageView goalsButton;
    private ImageView journalButton;
    private ImageView moodButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize navigation buttons
        goalsButton = findViewById(R.id.goalsButton);
        journalButton = findViewById(R.id.journalButton);
        moodButton = findViewById(R.id.moodButton);

        // Set click listeners for navigation
        setupNavigationListeners();

        // Set default fragment when app starts
        if (savedInstanceState == null) {
            // Start with the Progress fragment as default
            loadFragment(new ProgressFragment());
        }
    }

    private void setupNavigationListeners() {
        goalsButton.setOnClickListener(view -> {
            // Placeholder for Goals feature - can use Progress for now
            loadFragment(new ProgressFragment());
//            updateButtonHighlighting(goalsButton);
        });

        journalButton.setOnClickListener(view -> {
            // Placeholder for Journal feature - can use Progress for now
            loadFragment(new ProgressFragment());
//            updateButtonHighlighting(journalButton);
        });

        moodButton.setOnClickListener(view -> {
            // Create MoodFragment instance
            MoodFragment moodFragment = new MoodFragment();

            // Pass data (if needed)
            Bundle bundle = new Bundle();
            bundle.putString("selectedDate", "2025-04-03"); // Example data
            moodFragment.setArguments(bundle);

            // Load the fragment
            loadFragment(moodFragment);
            Log.i(TAG, "pressed");

//            updateButtonHighlighting(moodButton);
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


}