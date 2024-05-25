package com.example.messageapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messageapplication.R;
import com.example.messageapplication.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

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
            } else {
                signInBinding.textInputLayoutEmail.setError(null);
                userLogin();
            }
        });

        signInBinding.tvForgotPassword.setOnClickListener(v -> {
            goToResetPasswordActivity();
        });
    }

    private void goToResetPasswordActivity() {
        Intent intent = new Intent(SignInActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    private void userLogin() {
        String email = Objects.requireNonNull(signInBinding.textInputEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(signInBinding.textInputPassword.getText()).toString().trim();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            signInBinding.tvWarning.setVisibility(View.GONE);
                            finishAffinity();
                        } else {
                            signInBinding.tvWarning.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}