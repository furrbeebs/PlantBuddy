package com.example.app0.ui.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app0.R;
import com.example.app0.ui.Fragments.MoodFragment;

public class MoodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);
        
        // Add the MoodFragment to the container
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mood_fragment_container, new MoodFragment())
                .commit();
        }
    }
}