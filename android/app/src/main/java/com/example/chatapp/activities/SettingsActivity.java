package com.example.chatapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.R;
import com.example.chatapp.database.ChatDatabaseHelper;
import com.example.chatapp.database.User;
import com.example.chatapp.database.UserDao;
import com.example.chatapp.utils.Constants;

public class SettingsActivity extends AppCompatActivity {

    private TextView phoneDisplayTextView;
    private EditText usernameEditText, passwordEditText;
    private Button saveButton, cancelButton;

    private UserDao userDao;
    private User currentUserObject;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        phoneDisplayTextView = findViewById(R.id.settings_phone_display);
        usernameEditText = findViewById(R.id.settings_username);
        passwordEditText = findViewById(R.id.settings_password);
        saveButton = findViewById(R.id.btn_save_settings);
        cancelButton = findViewById(R.id.btn_cancel_settings);

        userDao = ChatDatabaseHelper.getInstance(this).userDao();

        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        currentUsername = prefs.getString(Constants.KEY_LOGGED_IN_USER, "");

        loadUserData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadUserData() {
        if (currentUsername != null && !currentUsername.isEmpty()) {
            currentUserObject = userDao.getUserByUsername(currentUsername);
        }

        if (currentUserObject != null) {
            String phone = currentUserObject.getPhoneNumber();
            phoneDisplayTextView.setText(phone != null && !phone.isEmpty() ? phone : "No phone number linked");
            usernameEditText.setText(currentUserObject.getUsername());
            passwordEditText.setText(currentUserObject.getPassword());
        } else {
            phoneDisplayTextView.setText("Offline Session");
            usernameEditText.setText(currentUsername);
        }
    }

    private void saveChanges() {
        String newUsername = usernameEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString().trim();

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // If username was changed, verify no conflict exists
        if (!newUsername.equalsIgnoreCase(currentUsername)) {
            User existing = userDao.getUserByUsername(newUsername);
            if (existing != null) {
                Toast.makeText(this, "Username '" + newUsername + "' is already taken. Please choose another.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (currentUserObject == null) {
            currentUserObject = new User();
        }

        currentUserObject.setUsername(newUsername);
        currentUserObject.setPassword(newPassword);
        userDao.insertUser(currentUserObject);

        // Update active session in SharedPreferences
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(Constants.KEY_LOGGED_IN_USER, newUsername).apply();

        Toast.makeText(this, "Username & Password updated successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
