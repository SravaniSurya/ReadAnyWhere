package com.example.pageflow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileEditActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private DatabaseReference firebaseDatabase;

    private EditText nameEt, passwordEt;
    private ImageView profileIv;
    private Uri imageUri;
    private Button updateBtn;
    private TextView changeImageTv;

    private user user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);


        ImageButton backBtn = findViewById(R.id.backBtn);
        nameEt = findViewById(R.id.nameEt);
        passwordEt = findViewById(R.id.passwordEt);
        profileIv = findViewById(R.id.profileIv);
        changeImageTv = findViewById(R.id.changeImageTv);
        updateBtn = findViewById(R.id.updateBtn);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("user_profile_pictures");
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("users");


        fetchUserData();


        backBtn.setOnClickListener(v -> onBackPressed());


        changeImageTv.setOnClickListener(v -> openFileChooser());


        updateBtn.setOnClickListener(v -> validateAndUpdateProfile());
    }

    private void fetchUserData() {
        String userId = firebaseUser.getUid();

        firebaseDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user = dataSnapshot.getValue(user.class);
                    if (user != null) {
                        nameEt.setText(user.getFirstName());
                        Glide.with(ProfileEditActivity.this)
                                .load(user.getPhotoUrl())
                                .placeholder(R.drawable.ic_person_gray)
                                .into(profileIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileEditActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileIv.setImageURI(imageUri);
        }
    }

    private void validateAndUpdateProfile() {
        if (user == null) {
            user = new user();
        }


        user.setFirstName(nameEt.getText().toString());


        if (!passwordEt.getText().toString().isEmpty()) {
            firebaseUser.updatePassword(passwordEt.getText().toString())
                    .addOnSuccessListener(aVoid -> Toast.makeText(ProfileEditActivity.this, "Password updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(ProfileEditActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show());
        }


        if (imageUri != null) {
            uploadImageToFirebaseStorage();
        } else {
            updateInFirebase();
        }
    }

    private void uploadImageToFirebaseStorage() {
        StorageReference fileReference = storageReference.child(firebaseUser.getUid() + ".jpg");

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            user.setPhotoUrl(uri.toString());
                            updateInFirebase();
                        }))
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show());
    }

    private void updateInFirebase() {
        firebaseDatabase.child(firebaseUser.getUid()).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show());
    }
}
