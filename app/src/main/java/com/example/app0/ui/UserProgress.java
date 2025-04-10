package com.example.app0.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app0.R;

public class UserProgress extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_progress);

        // Setup click listeners for bottom navigation
        setupBottomNavigation();
    }
    private void setupBottomNavigation() {
        LinearLayout goalsTab = findViewById(R.id.goalsTab);
        LinearLayout journalTab = findViewById(R.id.journalTab);
        LinearLayout moodTab = findViewById(R.id.moodTab);

        goalsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Goals screen
                Toast.makeText(UserProgress.this, "Goals tab clicked", Toast.LENGTH_SHORT).show();
            }
        });

        journalTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Journal screen
                Toast.makeText(UserProgress.this, "Journal tab clicked", Toast.LENGTH_SHORT).show();
            }
        });

        moodTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Mood screen
                Toast.makeText(UserProgress.this, "Mood tab clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}