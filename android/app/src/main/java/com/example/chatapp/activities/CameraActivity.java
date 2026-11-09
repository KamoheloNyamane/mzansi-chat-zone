package com.example.chatapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.chatapp.R;
import com.example.chatapp.database.ChatDatabaseHelper;
import com.example.chatapp.database.Message;
import com.example.chatapp.database.MessageDao;
import com.example.chatapp.utils.Constants;

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int CAMERA_PERMISSION_CODE = 1001;

    private ImageView photoPreview;
    private Button captureButton, shareButton, backButton;
    private Bitmap capturedBitmap;
    private MessageDao messageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        photoPreview = findViewById(R.id.photo_preview);
        captureButton = findViewById(R.id.btn_capture);
        shareButton = findViewById(R.id.btn_share);
        backButton = findViewById(R.id.btn_back);

        messageDao = ChatDatabaseHelper.getInstance(this).messageDao();

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndOpenCamera();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (capturedBitmap != null) {
                    SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                    String currentUser = prefs.getString(Constants.KEY_LOGGED_IN_USER, Constants.CURRENT_USER);

                    // Insert photo record message into Room Database
                    Message photoMsg = new Message();
                    photoMsg.setSender(currentUser);
                    photoMsg.setReceiver(Constants.OTHER_USER);
                    photoMsg.setContent("📷 [Photo Captured & Shared]");
                    photoMsg.setTimestamp(System.currentTimeMillis());

                    messageDao.insertMessage(photoMsg);

                    Toast.makeText(CameraActivity.this, "Photo shared to chat!", Toast.LENGTH_SHORT).show();

                    // Open conversation to view message
                    Intent intent = new Intent(CameraActivity.this, ConversationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            launchCameraIntent();
        }
    }

    private void launchCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No camera application found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCameraIntent();
            } else {
                Toast.makeText(this, "Camera permission is required to capture photos", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                capturedBitmap = (Bitmap) extras.get("data");
                photoPreview.setImageBitmap(capturedBitmap);
                shareButton.setEnabled(true);
            }
        }
    }
}
