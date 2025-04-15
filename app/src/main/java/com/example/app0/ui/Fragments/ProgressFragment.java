package com.example.app0.ui.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.example.app0.R;
import com.example.app0.ui.CheckForms2025;
import com.example.app0.ui.ViewModel.PlantBuddyViewModel;
import com.example.app0.utility.ModifiedObserver;

import java.util.List;

public class ProgressFragment extends Fragment {

    // Declaration of variables
    private PlantBuddyViewModel plantBuddyViewModel;
    private TextView levelIndicator;
    private ProgressBar progressBar;
    private TextView speechBubble;
    private LottieAnimationView animation;
    private int current_level;
    private double current_XP;
    private String current_form;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout WITHOUT the bottom navigation elements
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        // Initialize ViewModel
        plantBuddyViewModel = new ViewModelProvider(requireActivity()).get(PlantBuddyViewModel.class);

        // Initialize views
        levelIndicator = view.findViewById(R.id.levelText);
        progressBar = view.findViewById(R.id.progressBar);
        speechBubble = view.findViewById(R.id.speechText);
        animation = view.findViewById(R.id.plant_animation);

        setupViews();

        return view;
    }

    private void setupViews() {
        // Obtain PlantBuddy Object
        ModifiedObserver.observeOnce(plantBuddyViewModel.getPlantBuddy(), getViewLifecycleOwner(), plantBuddy -> {
            current_level = plantBuddy.get(0).getLevel();
            current_XP = plantBuddy.get(0).getXp();

            // Level Indicator
            levelIndicator.setText("Level " + current_level);

            // Progress Bar
            CheckForms2025 checkform = new CheckForms2025();
            int maxXP = checkform.maxXP(current_level);
            progressBar.setMax(maxXP);
            //progressBar.setProgress((int)Math.round(current_XP));
            //progressBar.setProgress(800);  // Manual Entry for Testing

            // Set current form for animation
            //current_form = checkform.display(current_level);
            current_form = "Sapling";   // for testing purposes
            updateAnimation();
        });

        // Speech Bubble
        List<String> encouragement_phrases = List.of("You should be Proud", "Hang In There",
                "Don't be so hard on yourself", "Not all days are bad", "You're almost there",
                "Love Yourself", "Be kind to Yourself", "You can do it!", "Never Give Up",
                "Keep up the good work!", "Follow Your Dreams", "The Sky is the Limit");

        int randomiser = (int)(Math.random()*(encouragement_phrases.size()));
        speechBubble.setText(encouragement_phrases.get(randomiser));
    }

    private void updateAnimation() {
        if (current_form.equals("Seedling")) {
            animation.setAnimation(R.raw.sprout);
        }
        else if (current_form.equals("Sapling")) {
            animation.setAnimation(R.raw.budding);
        }
        else if (current_form.equals("Tree")) {
            animation.setAnimation(R.raw.flower);
        }
        animation.playAnimation();
    }
}