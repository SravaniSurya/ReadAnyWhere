package com.example.pageflow;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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
    private ImageButton profileBtn;

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

        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();
        loadCategories();
        profileBtn.setOnClickListener(view -> startActivity(new Intent(AdminDashboard.this, ProfileActivity.class)));

        findViewById(R.id.addCategoryBtn).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboard.this, AddBookAdmin.class));
        });


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

        findViewById(R.id.addPdfFab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this, addpdf.class));
            }
        });

        findViewById(R.id.logoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(AdminDashboard.this, Login.class));
                finish();
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
                        model.setUuid(ds.getKey());
                        categoryArrayList.add(model);
                    }
                }

                adapterCategory = new bookadaptermodel(AdminDashboard.this, categoryArrayList);
                categoriesRv.setAdapter(adapterCategory);
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