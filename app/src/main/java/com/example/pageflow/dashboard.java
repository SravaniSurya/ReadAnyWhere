package com.example.pageflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class dashboard extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private ArrayList<book> pdfArrayList;
    private adapterpdfuser adapterPdfUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        pdfArrayList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();

        loadBooks();


        findViewById(R.id.logoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(dashboard.this, Login.class));
                finish();
            }
        });

        findViewById(R.id.profileBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(dashboard.this, ProfileActivity.class));
            }
        });
    }

    private void loadBooks() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pdfArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    book model = ds.getValue(book.class);
                    pdfArrayList.add(model);
                }
                adapterPdfUser = new adapterpdfuser(dashboard.this, pdfArrayList);
                RecyclerView rv = findViewById(R.id.booksRv);
                rv.setHasFixedSize(true);
                rv.setLayoutManager(new LinearLayoutManager(dashboard.this));
                rv.setAdapter(adapterPdfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}