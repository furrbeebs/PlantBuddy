package com.example.app0;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.example.app0.ui.Activity.GoalsActivity;
import com.example.app0.ui.Activity.MoodActivity;
import com.example.app0.ui.CheckForms2025;
import com.example.app0.ui.Fragments.MoodFragment;
import com.example.app0.ui.ViewModel.PlantBuddyViewModel;
import com.example.app0.utility.ModifiedObserver;

import java.util.List;

public class ProgressPage extends AppCompatActivity {

    // Declaration of variables
    private PlantBuddyViewModel plantBuddyViewModel;
    private TextView levelIndicator, speechBubble;
    private ProgressBar progressBar, progressCircle;
    private LottieAnimationView animation;
    private ImageView goals, journal, mood;
    private int current_level;
    private double current_XP;
    private String current_form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_progress_page);

        plantBuddyViewModel = new ViewModelProvider(this).get(PlantBuddyViewModel.class);

        // Obtain PlantBuddy Object
        ModifiedObserver.observeOnce(plantBuddyViewModel.getPlantBuddy(), this, plantBuddy -> {
            current_level = plantBuddy.get(0).getLevel();
            current_XP = plantBuddy.get(0).getXp();

            // Level Indicator
            progressCircle = findViewById(R.id.progress_circle);
            levelIndicator = findViewById(R.id.levelText);
            levelIndicator.setText("Level " + Integer.toString(current_level));

            // Progress Bar
            progressBar = findViewById(R.id.progressBar);
            // TODO: Add code for ProgressBar here

            // Progress Circle
            CheckForms2025 checkform = new CheckForms2025();
            int maxXP = checkform.maxXP(current_level);
            progressCircle.setMax(maxXP);
            progressCircle.setProgress((int) Math.round(current_XP));
        });

        // Speech Bubble
        List<String> encouragement_phrases = List.of("You should be Proud", "Hang In There",
                "Don't be so hard on yourself", "Not all days are bad", "You're almost there",
                "Love Yourself", "Be kind to Yourself", "You can do it!", "Never Give Up",
                "Keep up the good work!", "Follow Your Dreams", "The Sky is the Limit");

        speechBubble = findViewById(R.id.speechText);
        int randomiser = (int)(Math.random()*(encouragement_phrases.size()));
        speechBubble.setText(encouragement_phrases.get(randomiser));

        // Lottie Animation Section
        animation = findViewById(R.id.plant_animation);
        current_form = "Sapling";   // for testing purposes

        if (current_form.equals("Seedling")) { animation.setAnimation(R.raw.sprout); }
        else if (current_form.equals("Sapling")) { animation.setAnimation(R.raw.budding); }
        else if (current_form.equals("Tree")) { animation.setAnimation(R.raw.flower); }
        else {}

        // Bottom Navigation Section
        goals = findViewById(R.id.goalsButton);
        journal = findViewById(R.id.journalButton);
        mood = findViewById(R.id.moodButton);

        goals.setOnClickListener(v -> {
            Intent intent = new Intent(ProgressPage.this, GoalsActivity.class);
            startActivity(intent);
        });

        journal.setOnClickListener(v -> {

        });

        mood.setOnClickListener(v -> {
            Intent intent = new Intent(ProgressPage.this, MoodActivity.class);
            startActivity(intent);

        });


    }

}