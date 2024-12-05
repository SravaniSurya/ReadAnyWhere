package com.example.pageflow;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class pdflistadmin extends AppCompatActivity {


    private TextView subTitleTv;
    private EditText searchEt;
    private ImageButton backBtn;
    private RecyclerView bookRv;


    private ArrayList<modelpdf> pdfArrayList;
    private BookAdapterAdmin adapterPdfAdmin;

    private String categoryId, categoryTitle;

    private static final String TAG = "PDF_LIST_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdflistadmin);


        subTitleTv = findViewById(R.id.subTitleTv);
                searchEt = findViewById(R.id.searchEt);
                backBtn = findViewById(R.id.backBtn);
                bookRv = findViewById(R.id.bookRv);


                Intent intent = getIntent();
                categoryId = intent.getStringExtra("categoryId");
                categoryTitle = intent.getStringExtra("categoryTitle");


                if (categoryTitle != null) {
                    subTitleTv.setText(categoryTitle);
                }


                loadPdfList();

                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }

            private void loadPdfList() {

                pdfArrayList = new ArrayList<>();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
                ref.orderByChild("categoryId").equalTo(categoryId)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                pdfArrayList.clear();
                                for (DataSnapshot ds : snapshot.getChildren()) {

                                    modelpdf model = ds.getValue(modelpdf.class);
                                    if (model != null) {
                                        pdfArrayList.add(model);
                                        Log.d(TAG, "onDataChange: " + model.getId() + " " + model.getTitle());
                                    }
                                }


                                if (pdfArrayList.isEmpty()) {
                                    Toast.makeText(pdflistadmin.this, "No books found in this category", Toast.LENGTH_SHORT).show();
                                }


                                adapterPdfAdmin = new BookAdapterAdmin(pdflistadmin.this, pdfArrayList);
                                bookRv.setLayoutManager(new LinearLayoutManager(pdflistadmin.this));
                                bookRv.setAdapter(adapterPdfAdmin);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(pdflistadmin.this, "Failed to load books", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
