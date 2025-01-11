package com.example.pageflow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class bookdetails extends AppCompatActivity {

    String bookId, bookTitle, bookUrl;

    private FirebaseAuth firebaseAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdetails);

        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookKey");

        findViewById(R.id.downloadBookBtn).setVisibility(View.GONE);



        firebaseAuth = FirebaseAuth.getInstance();

        loadBookDetails();


        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.downloadBookBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myapplication.downloadBook(bookdetails.this, bookTitle, bookUrl);
            }
        });


    }

    private void loadBookDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        book model = snapshot.getValue(book.class);
                        bookTitle = model.getTitle();
                        bookUrl = model.getUrl();
                        String categoryId = model.getCategory();

                        long timestamp = model.getTimestamp();

                        findViewById(R.id.downloadBookBtn).setVisibility(View.VISIBLE);

                        String date = myapplication.formatTimestamp(timestamp);

                        myapplication.loadCategory(
                                "" + categoryId,
                                findViewById(R.id.categoryTv)
                        );

                        ((TextView) findViewById(R.id.titleTv)).setText(bookTitle);
                        ((TextView) findViewById(R.id.dateTv)).setText(date);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}