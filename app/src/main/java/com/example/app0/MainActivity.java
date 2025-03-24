package com.example.app0;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

// calendar view
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // the xml file

        // initialise button
        Button btn = findViewById(R.id.modebutt);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use intent to go to activity page
                Intent intent = new Intent(MainActivity.this);
                startActivity(intent);
            }
        });


        // calendar
        CalendarView calendar = findViewById(R.id.calendar);
        TextView dateView = findViewById(R.id.date_view);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "-" + (month + 1) + "-" + year;
                dateView.setText(date);
            }
        });
    }
}