package com.example.messageapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.example.messageapplication.R;

import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messageapplication.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding signUpBinding;
    private String email;
    private String initialPassword;
    private String confirmedPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(signUpBinding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mAuth = FirebaseAuth.getInstance();

        signUpBinding.btnSignUp.setOnClickListener(v -> {
            getInformationTextInput();
            if (!email.isEmpty() && !initialPassword.isEmpty() && !confirmedPassword.isEmpty()) {
                if (initialPassword.length() < 6) {
                    signUpBinding.textInputLayoutPassword.setError(getString(R.string.password_must_be_at_least_6_characters));
                } else if (initialPassword.equals(confirmedPassword) && isValidEmail(email)) {
                    signUpBinding.textInputLayoutPassword.setError(null);
                    showEmailVerificationWarning();
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        checkEmailVerification();
                    } else {
                        createAccount();
                    }
                } else {
                    showSignUpError();
                }
            } else {
                showSignUpError();
            }
        });
    }

    private void getInformationTextInput() {
        email = Objects.requireNonNull(signUpBinding.textInputEmail.getText()).toString().trim();
        initialPassword = Objects.requireNonNull(signUpBinding.textInputPassword.getText()).toString().trim();
        confirmedPassword = Objects.requireNonNull(signUpBinding.textInputReEnterPassword.getText()).toString().trim();
    }

    private void createAccount() {
        mAuth.createUserWithEmailAndPassword(email, confirmedPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                sendEmailVerification(user);
                            }
                        }
                    }
                });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                });
    }

    private void checkEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.reload()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (user.isEmailVerified()) {
                                String userId = user.getUid();
                                Intent intent = new Intent(SignUpActivity.this, ProfileActivity.class);
                                intent.putExtra(getString(R.string.key_user_id), userId);
                                startActivity(intent);
                            }
                        }
                    });
        }
    }

    private void showSignUpError(){
        signUpBinding.tvWarning.setText(R.string.please_check_your_account_or_password);
        signUpBinding.tvWarning.setTextColor(getColor(R.color.red));
        signUpBinding.tvWarning.setVisibility(View.VISIBLE);
    }

    private void showEmailVerificationWarning(){
        signUpBinding.tvWarning.setText(R.string.please_check_email_verification);
        signUpBinding.tvWarning.setTextColor(getColor(R.color.usaf_academy_blue));
        signUpBinding.tvWarning.setVisibility(View.VISIBLE);
    }

    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}