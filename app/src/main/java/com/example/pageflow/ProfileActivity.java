package com.example.pageflow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton backBtn, profileEditBtn;
    private TextView nameTv, accountTypeTv;
    private com.google.android.material.imageview.ShapeableImageView profileIv;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        backBtn = findViewById(R.id.backBtn);
        profileEditBtn = findViewById(R.id.profileEditBtn);
        nameTv = findViewById(R.id.nameTv);
        accountTypeTv = findViewById(R.id.accountTypeTv);
        profileIv = findViewById(R.id.profileIv);


        firebaseAuth = FirebaseAuth.getInstance();


        loadUserInfo();

        backBtn.setOnClickListener(v -> onBackPressed());
        profileEditBtn.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, ProfileEditActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo();
    }

    private void loadUserInfo() {

        String uid = firebaseAuth.getUid();

        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid);


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String name = snapshot.child("firstName").getValue(String.class);
                String profileImage = snapshot.child("photoUrl").getValue(String.class);
                String userType = snapshot.child("role").getValue(String.class);


                nameTv.setText(name != null && !name.isEmpty() ? name : "N/A");
                accountTypeTv.setText(userType != null && !userType.isEmpty() ? userType : "N/A");


                Glide.with(ProfileActivity.this)
                        .load(profileImage != null && !profileImage.isEmpty() ? profileImage : R.drawable.ic_person_gray)
                        .placeholder(R.drawable.ic_person_gray)
                        .into(profileIv);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
