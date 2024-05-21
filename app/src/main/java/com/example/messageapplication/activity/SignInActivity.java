package com.example.messageapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import com.example.messageapplication.R;
import com.example.messageapplication.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding signInBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(signInBinding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        signInBinding.tvCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });

        signInBinding.btnSignIn.setOnClickListener(v -> {
            if (!isValidEmail(signInBinding.textInputEmail.getText().toString().trim())) {
                signInBinding.textInputLayoutEmail.setError(getString(R.string.email_invalid));
            }
        });
    }

    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}