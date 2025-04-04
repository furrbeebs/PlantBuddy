package com.example.app0;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

// calendar view
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.app0.fragments.MoodFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // the xml file

        if (savedInstanceState == null) {
            // Create MoodFragment instance
            MoodFragment moodFragment = new MoodFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mood_fragment_container, moodFragment)
                    .commit();

            // Create a Bundle to pass data
            Bundle bundle = new Bundle();
            bundle.putString("selectedDate", "2025-04-03"); // Example of passing selected date
            moodFragment.setArguments(bundle);

            // Add the fragment to the container
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mood_fragment_container, moodFragment);
            transaction.commit();


        }
    }
}