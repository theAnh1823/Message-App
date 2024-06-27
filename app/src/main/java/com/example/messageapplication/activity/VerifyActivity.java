package com.example.messageapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messageapplication.R;
import com.example.messageapplication.databinding.ActivityVerifyBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity {
    private ActivityVerifyBinding verifyBinding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyBinding = ActivityVerifyBinding.inflate(getLayoutInflater());
        setContentView(verifyBinding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mAuth = FirebaseAuth.getInstance();

        verifyBinding.btnNextVerify.setOnClickListener(v -> {
            String phoneNumber = Objects.requireNonNull(verifyBinding.editPhoneNumber.getText()).toString().trim();
            if (phoneNumber.isEmpty() || !isValidPhoneNumber(phoneNumber)) {
                Toast.makeText(this, getString(R.string.check_and_enter_the_correct_phone_number_and_country_code), Toast.LENGTH_SHORT).show();
            } else{
                verifyBinding.verifyProgressBar.setVisibility(View.VISIBLE);
                onClickVerifyPhoneNumber(phoneNumber);
            }
        });
    }

    private void onClickVerifyPhoneNumber(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Số điện thoại cần xác thực
                        .setTimeout(60L, TimeUnit.SECONDS) // Thời gian chờ xác thực và đơn vị thời gian
                        .setActivity(this)                 // Activity hiện tại
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                verifyBinding.verifyProgressBar.setVisibility(View.GONE);
                                Toast.makeText(VerifyActivity.this, R.string.verification_failed, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);
                                goToEnterOtpActivity(phoneNumber, verificationId);
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void goToEnterOtpActivity(String phoneNumber, String verificationId) {
        Intent intent = new Intent(this, EnterOtpActivity.class);
        intent.putExtra("phone_number", phoneNumber);
        intent.putExtra("verification_id", verificationId);
        startActivity(intent);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.startsWith("+") && phoneNumber.length() > 1;
    }
}