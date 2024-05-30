package com.example.messageapplication.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding mainBinding;
    private LayoutHeaderNavigationBinding headerBinding;

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
        MessageAdapter messageAdapter = new MessageAdapter(getApplicationContext(), getListUserMessage(), new IClickItemMessageListener() {
            @Override
            public void onClick(UserMessage userMessage) {
                Toast.makeText(MainActivity.this, userMessage.getUserName(), Toast.LENGTH_SHORT).show();
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

    private List<UserMessage> getListUserMessage() {
        List<UserMessage> list = new ArrayList<>();
        list.add(new UserMessage(1, R.drawable.avatar_3d, "Vũ Khắc Vanh", "Đi chơi không?", "6:30"));
        list.add(new UserMessage(2, R.drawable.avatar_3d, "Bùi Việt Anh", "Haha", "19/05"));
        list.add(new UserMessage(3, R.drawable.avatar_3d, "Nguyễn Huy Hoàng", "Cảm ơn bạn!", "19/05"));
        list.add(new UserMessage(4, R.drawable.avatar_3d, "Bùi Xuân Anh", "Tuyệt vời", "18/05"));
        list.add(new UserMessage(5, R.drawable.avatar_3d, "Nguyễn Văn An", "Hello", "15/05"));
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            if (name == null) {
                headerBinding.tvUserName.setVisibility(View.GONE);
            } else {
                headerBinding.tvUserName.setVisibility(View.VISIBLE);
                headerBinding.tvUserName.setText(name);
            }
            headerBinding.tvUserEmail.setText(email);
            Glide.with(this).load(photoUrl).error(R.drawable.avatar_3d).into(headerBinding.imgUserAvatar);
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