package com.example.app0.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.app0.MainActivity;
import com.example.app0.R;
import com.example.app0.data.Local.Entity.PlantBuddy;
import com.example.app0.data.Repository.PlantBuddyRepository;
import com.example.app0.ui.ViewModel.PlantBuddyViewModel;
import com.example.app0.utility.ModifiedObserver;

public class LoginPage extends AppCompatActivity {

    private EditText usernameInput;
    private EditText plantnameInput;
    private Button loginButton;

    private PlantBuddyViewModel plantBuddyViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        usernameInput = findViewById(R.id.usernameInput);
        plantnameInput = findViewById(R.id.plantnameInput);
        loginButton = findViewById(R.id.loginButton);

        plantBuddyViewModel = new ViewModelProvider(this).get(PlantBuddyViewModel.class);

        ModifiedObserver.observeOnce(plantBuddyViewModel.getPlantBuddy(), this, plantBuddy -> {

            if (plantBuddy == null || plantBuddy.isEmpty()) {
                //PlantBuddy buddy = plantBuddy.get(0);
                Log.d("LoginPage Test", "Entered the if condition, DB is empty");
                usernameInput.setVisibility(View.VISIBLE);
                plantnameInput.setVisibility(View.VISIBLE);

                int level = 0;
                double xp = 0.0;
                int image = 0;

                loginButton.setOnClickListener(v -> {
                    String username = usernameInput.getText().toString().trim();
                    String plantname = plantnameInput.getText().toString().trim();

                    if (username.isEmpty() || plantname.isEmpty()) {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    plantBuddyViewModel.insert(new PlantBuddy(username, plantname, level, xp, image), new PlantBuddyRepository.InsertStatus() {
                        @Override
                        public void InsertSuccess() {
                            runOnUiThread(() -> {
                                Toast.makeText(LoginPage.this, "New Profile Created!", Toast.LENGTH_SHORT).show();
                            });
                        }

                        public void InsertFailed() {
                            runOnUiThread(() -> {
                                Toast.makeText(LoginPage.this, "A PlantBuddy Profile Exists!", Toast.LENGTH_SHORT).show();
                            });
                        }

                    });

                    Intent intent = new Intent(LoginPage.this, MainActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
            } else {
                Log.d("LoginPage Test", "Entered the else condition, DB is not empty");

                usernameInput.setVisibility(View.GONE);
                plantnameInput.setVisibility(View.GONE);
                loginButton.setText("Enter");
                loginButton.setOnClickListener(v -> {

                    Intent intent = new Intent(LoginPage.this, MainActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
            }


        });

    }
}