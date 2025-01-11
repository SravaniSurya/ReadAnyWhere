package com.example.pageflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class  pdflistadmin extends AppCompatActivity {

    private ArrayList<book> pdfArrayList;
    private adapterpdfuser adapterPdfAdmin;

    private String categoryId, categoryTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdflistadmin);

        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        categoryTitle = intent.getStringExtra("categoryTitle");

        ((TextView) findViewById(R.id.subTitleTv)).setText(categoryTitle);

        loadPdfList();

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadPdfList() {
        pdfArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("category").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            book model = ds.getValue(book.class);
                            pdfArrayList.add(model);
                        }
                        adapterPdfAdmin = new adapterpdfuser(pdflistadmin.this, pdfArrayList);
                        ((RecyclerView) findViewById(R.id.bookRv)).setAdapter(adapterPdfAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}