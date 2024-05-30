package com.example.messageapplication.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messageapplication.R;
import com.example.messageapplication.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    public static final int MY_REQUEST_CODE = 10;
    private Uri uri;
    private ActivityProfileBinding profileBinding;
    private String userId, email, password;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK){
                        Intent intent = result.getData();
                        if (intent == null){
                            return;
                        }
                        uri = intent.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            profileBinding.profilePicture.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());

        Intent intent = getIntent();
        userId = intent.getStringExtra(getString(R.string.key_user_id));
        email = intent.getStringExtra(getString(R.string.key_email));
        password = intent.getStringExtra(getString(R.string.key_password));

        updateUserProfileView();

        profileBinding.layoutProfilePicture.setOnClickListener(v -> {
            onClickRequestPermission();
        });

        profileBinding.btnConfirm.setOnClickListener(v -> {
            if (isNameFieldFilled())
                saveUserInformation();
        });

    }

    private void onClickRequestPermission() {
        if(this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            openGallery();
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select picture"));
    }

    private void updateUserProfileView() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // Lấy thông tin người dùng từ Firestore
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Lấy thông tin từ documentSnapshot
                            String address = documentSnapshot.getString("address");
                            String birthday = documentSnapshot.getString("birthday");
                            String gender = documentSnapshot.getString("sex");
                            // Xử lý các thông tin lấy được
                        } else {
                            Log.d("Firestore", "No such document");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error getting document", e);
                    });
        }

    }

    private boolean isNameFieldFilled() {
        if (profileBinding.editTextFirstName.getText().toString().trim().isEmpty()) {
            profileBinding.editTextFirstName.setError(getString(R.string.please_fill_in_full_name));
            return false;
        } else if (profileBinding.editTextLastName.getText().toString().trim().isEmpty()) {
            profileBinding.editTextLastName.setError(getString(R.string.please_fill_in_full_name));
            return false;
        } else {
            profileBinding.editTextFirstName.setError(null);
            profileBinding.editTextLastName.setError(null);
        }
        return true;
    }

    private void saveUserInformation() {
        String firstName = profileBinding.editTextFirstName.getText().toString().trim();
        String lastName = profileBinding.editTextLastName.getText().toString().trim();
        String fullName = firstName + " " + lastName;
        String birthday = profileBinding.datePickerBirthday.getDayOfMonth() + "/" + (profileBinding.datePickerBirthday.getMonth() + 1)
                + "/" + profileBinding.datePickerBirthday.getYear();
        String gender = getGenderSelected();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tạo một bản ghi người dùng với thông tin bổ sung
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put(getString(R.string.key_name), fullName);
        userProfile.put("photoUrl", uri);
        userProfile.put(getString(R.string.key_birthday), birthday);
        userProfile.put(getString(R.string.key_gender), gender);

        // Lưu thông tin vào Firestore
        db.collection("users").document(userId).set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    // Lưu trữ thành công
                    Log.d("Firestore", "User profile successfully updated!");
                })
                .addOnFailureListener(e -> {
                    // Xảy ra lỗi khi lưu trữ
                    Log.w("Firestore", "Error updating user profile", e);
                });

//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        // Đăng ký thành công, lấy người dùng hiện tại
//                        FirebaseUser user = mAuth.getCurrentUser();
//                        if (user != null) {
//                            // Lấy UID của người dùng
//                            String uid = user.getUid();
//
//                            // Tạo đối tượng UserProfileChangeRequest để cập nhật thông tin cơ bản
//                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                                    .setDisplayName(fullName)
//                                    .setPhotoUri(uri)
//                                    .build();
//                            user.updateProfile(profileUpdates);
//
//
//                        }
//                    } else {
//                        // Xử lý lỗi đăng ký
//                        Log.w("SignUp", "createUserWithEmail:failure", task.getException());
//                    }
//                });

    }

    @SuppressLint("NonConstantResourceId")
    private String getGenderSelected() {
        if (profileBinding.radioButtonMale.isChecked()){
            return getString(R.string.male);
        } else if (profileBinding.radioButtonFemale.isChecked()){
            return getString(R.string.female);
        } else if (profileBinding.radioButtonOther.isChecked()){
            return getString(R.string.other);
        }
        return getString(R.string.other);
    }
}
