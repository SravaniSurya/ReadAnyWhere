package com.example.pageflow;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddBookAdmin extends AppCompatActivity {

    private ImageButton backBtn;
    private EditText categoryEt;
    private Button submitBtn;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_admin);


        backBtn = findViewById(R.id.backBtn);
                categoryEt = findViewById(R.id.categoryEt);
                submitBtn = findViewById(R.id.submitBtn);


                firebaseAuth = FirebaseAuth.getInstance();


                backBtn.setOnClickListener(v -> onBackPressed());

                submitBtn.setOnClickListener(v -> validateAndAddCategory());
            }

            private void validateAndAddCategory() {

                String category = categoryEt.getText().toString().trim();


                if (TextUtils.isEmpty(category)) {
                    Toast.makeText(this, "Please enter a category!", Toast.LENGTH_SHORT).show();
                } else {
                    addCategoryToFirebase(category);
                }
            }

            private void addCategoryToFirebase(String category) {

                long timestamp = System.currentTimeMillis();


                HashMap<String, Object> categoryData = new HashMap<>();
                categoryData.put("id", String.valueOf(timestamp));
                categoryData.put("category", category);
                categoryData.put("timestamp", timestamp);
                categoryData.put("uid", firebaseAuth.getUid());


                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
                ref.child(String.valueOf(timestamp))
                        .setValue(categoryData)
                        .addOnSuccessListener(unused -> {

                            Toast.makeText(AddBookAdmin.this, "Category added successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {

                            Toast.makeText(AddBookAdmin.this, "Failed to add category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        }
