package com.example.messageapplication.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.messageapplication.R;
import com.example.messageapplication.databinding.ActivityResetPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {
    private ActivityResetPasswordBinding resetPasswordBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetPasswordBinding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(resetPasswordBinding.getRoot());
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(R.string.account_authentication);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        resetPasswordBinding.btnEnterAuthentication.setOnClickListener(v -> {
            String email = Objects.requireNonNull(resetPasswordBinding.textInputEmail.getText()).toString().trim();
            if (!isValidEmail(email)) {
                resetPasswordBinding.textInputLayoutEmail.setError(getString(R.string.email_invalid));
            } else {
                resetPasswordBinding.textInputLayoutEmail.setError(null);
                FirebaseAuth auth = FirebaseAuth.getInstance();

                Task<Void> voidTask = auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    resetPasswordBinding.tvSuccessfulPasswordReset.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        });
    }
    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}