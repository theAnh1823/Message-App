package com.example.messageapplication.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messageapplication.R;
import com.example.messageapplication.adapter.MessageAdapter;
import com.example.messageapplication.databinding.ActivityMainBinding;
import com.example.messageapplication.listener.IClickItemMessageListener;
import com.example.messageapplication.model.UserMessage;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        setSupportActionBar(mainBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0091FF")));
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mainBinding.drawerLayout, mainBinding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mainBinding.drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getColor(R.color.white));
        toggle.syncState();

        mainBinding.navigationView.setNavigationItemSelectedListener(this);

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

        } else if (id == R.id.nav_log_out) {

        }
        mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}