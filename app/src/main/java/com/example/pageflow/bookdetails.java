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

    private ProgressDialog progressDialog;

    private ArrayList<comment> commentArrayList;

    private adaptercomment adapterComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdetails);

        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookKey");

        findViewById(R.id.downloadBookBtn).setVisibility(View.GONE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        loadBookDetails();
        loadComments();

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

        findViewById(R.id.addCommentBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(bookdetails.this, "You're not logged in...", Toast.LENGTH_SHORT).show();
                } else {
                    addCommentDialog();
                }
            }
        });
    }

    private void loadComments() {
        commentArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        commentArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            comment model = ds.getValue(comment.class);
                            commentArrayList.add(model);
                        }
                        adapterComment = new adaptercomment(bookdetails.this, commentArrayList);
                        ((RecyclerView) findViewById(R.id.commentsRv)).setAdapter(adapterComment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void addCommentDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_comment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        dialogView.findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText commentEt = dialogView.findViewById(R.id.commentEt);
                String comment = commentEt.getText().toString().trim();
                if (TextUtils.isEmpty(comment)) {
                    Toast.makeText(bookdetails.this, "Enter your comment", Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.dismiss();
                    addComment(comment);
                }
            }
        });
    }

    private void addComment(String comment) {
        progressDialog.setMessage("Adding comment...");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        comment cmt = new comment(String.valueOf(timestamp), bookId, timestamp, comment, firebaseAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).child("Comments").child(String.valueOf(timestamp))
                .setValue(cmt)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(bookdetails.this, "Comment Added", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(bookdetails.this, "Failed to add comment due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
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