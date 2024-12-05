package com.example.pageflow;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminDashboard extends AppCompatActivity {

    private EditText searchEt;
    private RecyclerView categoriesRv;
    private ImageButton profileBtn, logoutBtn, addPdfBtn;

    private FirebaseAuth firebaseAuth;
    private ArrayList<book> categoryArrayList;
    private bookadaptermodel adapterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        searchEt = findViewById(R.id.searchEt);
        categoriesRv = findViewById(R.id.categoriesRv);
        profileBtn = findViewById(R.id.profileBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        addPdfBtn = findViewById(R.id.addPdfFab);

        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();
        loadCategories();

        profileBtn.setOnClickListener(view -> startActivity(new Intent(AdminDashboard.this, ProfileActivity.class)));

        logoutBtn.setOnClickListener(view -> {
            firebaseAuth.signOut();
            Toast.makeText(AdminDashboard.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AdminDashboard.this, Login.class));
            finish();
        });

        addPdfBtn.setOnClickListener(view -> startActivity(new Intent(AdminDashboard.this, addpdf.class)));

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapterCategory != null) {
                    adapterCategory.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadCategories() {
        categoryArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    book model = ds.getValue(book.class);
                    if (model != null) {
                        categoryArrayList.add(model);
                    }
                }

                adapterCategory = new bookadaptermodel(AdminDashboard.this, categoryArrayList);
                categoriesRv.setAdapter(adapterCategory);

                // Set click listener for categories
                adapterCategory.setOnItemClickListener(new bookadaptermodel.OnItemClickListener() {
                    @Override
                    public void onItemClick(book model) {
                        Intent intent = new Intent(AdminDashboard.this, pdflistadmin.class);
                        intent.putExtra("categoryId", model.getId());
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void checkUser() {
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, splashscreen.class));
            finish();
        }
    }
}
