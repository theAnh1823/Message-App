package com.example.messageapplication.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messageapplication.R;
import com.example.messageapplication.adapter.MessageAdapter;
import com.example.messageapplication.databinding.ActivityMainBinding;
import com.example.messageapplication.databinding.LayoutHeaderNavigationBinding;
import com.example.messageapplication.listener.IClickItemMessageListener;
import com.example.messageapplication.model.UserMessage;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding mainBinding;
    private LayoutHeaderNavigationBinding headerBinding;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        setSupportActionBar(mainBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.main_blue)));
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mainBinding.drawerLayout, mainBinding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mainBinding.drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getColor(R.color.white));
        toggle.syncState();

        mainBinding.navigationView.setNavigationItemSelectedListener(this);
        NavigationView navigationView = mainBinding.navigationView;
        View headerView = navigationView.getHeaderView(0);
        headerBinding = LayoutHeaderNavigationBinding.bind(headerView);
        showUserInformation();

        RecyclerView recyclerView = mainBinding.rcvMessage;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(getApplicationContext(), getListUserMessage(), new IClickItemMessageListener() {
            @Override
            public void onClick(UserMessage userMessage) {
            }
        });
        recyclerView.setAdapter(messageAdapter);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    private List<UserMessage> getListUserMessage() {
        List<UserMessage> list = new ArrayList<>();

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Xử lý dữ liệu trả về từ Firestore
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userId = document.getId();

                            // Kiểm tra nếu userId không phải là của người dùng hiện tại
                            if (!userId.equals(currentUserId)) {
                                String fullName = document.getString(getString(R.string.key_name));
                                String profileImageUrl = document.getString(getString(R.string.key_profile_image_url));
                                String birthday = document.getString(getString(R.string.key_birthday));
                                String gender = document.getString(getString(R.string.key_gender));

                                UserMessage userMessage = new UserMessage(userId, profileImageUrl, fullName, gender, birthday);
                                list.add(userMessage);
                            }
                        }
                        messageAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });


        return list;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_contacts) {

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_invite_friends) {

        } else if (id == R.id.nav_sign_out) {
            signOut();
        }
        mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("CheckResult")
    private void showUserInformation() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            headerBinding.tvUserEmail.setText(email);

            String uid = user.getUid();
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String profileImageUrl = documentSnapshot.getString("profile image url");

                            headerBinding.tvUserName.setText(name);

                            if (profileImageUrl != null) {
                                Uri profileImageUri = Uri.parse(profileImageUrl);
                                try {
                                    // Lấy InputStream của URI và chuyển thành Bitmap
                                    InputStream inputStream = getContentResolver().openInputStream(profileImageUri);
                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    headerBinding.imgUserAvatar.setImageBitmap(bitmap);
                                } catch (FileNotFoundException e) {
                                    Log.e("ProfileImageError", "File not found: " + e.getMessage());
                                }
                            }

                        } else {
                            Log.d("Firestore", "No such document");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error getting document", e);
                    });
        }
    }

    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_sign_out)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

