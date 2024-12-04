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

import java.util.Calendar;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton backBtn, profileEditBtn;
    private TextView nameTv, accountTypeTv, memberDateTv;
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
        memberDateTv = findViewById(R.id.memberDateTv);
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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = "" + snapshot.child("firstName").getValue();
                String profileImage = "" + snapshot.child("photoUrl").getValue();
                String timestamp = "" + snapshot.child("timestamp").getValue();
                String userType = "" + snapshot.child("role").getValue();

                String formattedDate = "N/A";
                if (timestamp != null && !timestamp.isEmpty() && !timestamp.equals("null")) {
                    try {
                        long timestampLong = Long.parseLong(timestamp);
                        formattedDate = formatTimestamp(timestampLong);
                    } catch (NumberFormatException e) {
                        formattedDate = "Invalid Date";
                    }
                }

                nameTv.setText(name.isEmpty() ? "N/A" : name);
                accountTypeTv.setText(userType.isEmpty() ? "N/A" : userType);
                memberDateTv.setText(formattedDate);

                Glide.with(ProfileActivity.this)
                        .load(profileImage)
                        .placeholder(R.drawable.ic_person_gray)
                        .into(profileIv);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static String formatTimestamp(long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        return android.text.format.DateFormat.format("dd/MM/yyyy", cal).toString();
    }
}
