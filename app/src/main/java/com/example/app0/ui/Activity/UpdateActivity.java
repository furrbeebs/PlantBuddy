package com.example.app0.ui.Activity;

import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.app0.data.Local.Entity.PlantBuddy;
import com.example.app0.ui.CheckForms2025;
import com.example.app0.ui.ViewModel.PlantBuddyViewModel;

public class UpdateActivity extends AppCompatActivity {
    private PlantBuddyViewModel plantBuddyViewModel;
    private String username, plantname;
    private int level, image;

    private double xp;
    private double[] doubleContainer;

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("runFunction", false)) {
            updatePointBalance(100); // Call your function here
        }
    }

    public void updatePointBalance(int XP) {
        plantBuddyViewModel = new ViewModelProvider(this).get(PlantBuddyViewModel.class);
        CheckForms2025 checkform = new CheckForms2025();

        plantBuddyViewModel.getPlantBuddy().observe(this, plantBuddy -> {
            PlantBuddy buddy = plantBuddy.get(0);

            username = buddy.getUsername();
            plantname = buddy.getPlantname();
            level = buddy.getLevel();
            xp = buddy.getXp();
            image = buddy.getImage();
        });

        // Adding new Points
        xp += XP;

        // Checking if user eligible for upgrade
        doubleContainer = checkform.checkLevel(xp, level);
        xp = doubleContainer[0];
        level = (int)Math.round(doubleContainer[1]);
        Log.d("INFO", username + "/" + plantname + "/" + level + "/" + xp + "/" + image);

        plantBuddyViewModel.update(new PlantBuddy(username, plantname, level, xp, image));
    }

}