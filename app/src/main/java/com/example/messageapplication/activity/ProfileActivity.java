package com.example.messageapplication.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
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
import androidx.core.app.ActivityCompat;

import com.example.messageapplication.R;
import com.example.messageapplication.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    public static final int MY_REQUEST_CODE = 10;
    private Uri uri;
    private ActivityProfileBinding profileBinding;
    private String userId;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("WrongConstant")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            uri = intent.getData();
                            // Duy trì quyền truy cập lâu dài
                            final int takeFlags = intent.getFlags()
                                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            getContentResolver().takePersistableUriPermission(uri, takeFlags);
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                profileBinding.profilePicture.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());

        Intent intentReceived = getIntent();
        userId = intentReceived.getStringExtra(getString(R.string.key_user_id));

        profileBinding.layoutProfilePicture.setOnClickListener(v -> {
            onClickRequestPermission();
        });

        profileBinding.btnConfirm.setOnClickListener(v -> {
            if (isNameFieldFilled()) {
                saveUserInformation();

                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

    }

    private void onClickRequestPermission() {
        if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, MY_REQUEST_CODE);
        } else {
            openGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    private Uri getUriFromDrawable(int drawableId) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getResources().getResourcePackageName(drawableId)
                + '/' + getResources().getResourceTypeName(drawableId)
                + '/' + getResources().getResourceEntryName(drawableId));
    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
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
        if (uri == null) {
            uri = getUriFromDrawable(R.drawable.avatar_3d);
        }
        String profileImageUrl = uri.toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put(getString(R.string.key_name), fullName);
        userProfile.put(getString(R.string.key_profile_image_url), profileImageUrl);
        userProfile.put(getString(R.string.key_birthday), birthday);
        userProfile.put(getString(R.string.key_gender), gender);

        db.collection("users").document(userId).set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "User profile successfully updated!");
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error updating user profile", e);
                });
    }

    @SuppressLint("NonConstantResourceId")
    private String getGenderSelected() {
        if (profileBinding.radioButtonMale.isChecked()) {
            return getString(R.string.male);
        } else if (profileBinding.radioButtonFemale.isChecked()) {
            return getString(R.string.female);
        } else if (profileBinding.radioButtonOther.isChecked()) {
            return getString(R.string.other);
        }
        return getString(R.string.other);
    }
}
