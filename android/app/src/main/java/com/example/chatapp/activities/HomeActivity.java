package com.example.chatapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.utils.Constants;

public class HomeActivity extends AppCompatActivity {

    private Button conversationsButton;
    private Button cameraButton;
    private Button logoutButton;
    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        conversationsButton = findViewById(R.id.btn_conversations);
        cameraButton = findViewById(R.id.btn_camera);
        logoutButton = findViewById(R.id.btn_logout);
        welcomeTextView = findViewById(R.id.welcome_text);

        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        String currentUser = prefs.getString(Constants.KEY_LOGGED_IN_USER, "User");

        if (welcomeTextView != null) {
            welcomeTextView.setText("Welcome to ChatApp, " + currentUser + "!");
        }

        conversationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ConversationActivity.class);
                startActivity(intent);
            }
        });

        if (cameraButton != null) {
            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomeActivity.this, CameraActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (logoutButton != null) {
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefs.edit().remove(Constants.KEY_LOGGED_IN_USER).apply();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
