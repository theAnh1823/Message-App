package com.example.messageapplication.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.messageapplication.databinding.ActivityChatBinding;
import com.example.messageapplication.model.UserMessage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding activityChatBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(activityChatBinding.getRoot());

        Toolbar toolbar = activityChatBinding.toolbarChat;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();
        UserMessage userMessage = (UserMessage) intent.getSerializableExtra("userMessage");
        if (userMessage != null){
            activityChatBinding.tvUsernameSender.setText(userMessage.getUserName());
            Uri profileImageUri = Uri.parse(userMessage.getImageResource());
            try {
                // Lấy InputStream của URI và chuyển thành Bitmap
                InputStream inputStream = getContentResolver().openInputStream(profileImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                activityChatBinding.avatarSender.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("ProfileImageError", "File not found: " + e.getMessage());
            }
        }
    }
}
