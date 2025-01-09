package com.example.pageflow;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class addpdf extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ArrayList<String> categoryTitleArrayList, categoryIdArrayList;
    private Uri pdfUri = null;

    private static final int PDF_PICK_CODE = 1000;

    private String title = "";
    private String selectedCategoryId, selectedCategoryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpdf);

        firebaseAuth = FirebaseAuth.getInstance();
        loadPdfCategories();

        findViewById(R.id.backBtn).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.attachBtn).setOnClickListener(v -> pdfPickIntent());
        findViewById(R.id.categoryTv).setOnClickListener(v -> categoryPickDialog());
        findViewById(R.id.submitBtn).setOnClickListener(v -> checkData());
    }

    private void checkData() {
        title = ((EditText) findViewById(R.id.titleEt)).getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Enter Title...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedCategoryTitle)) {
            Toast.makeText(this, "Pick Category...", Toast.LENGTH_SHORT).show();
        } else if (pdfUri == null) {
            Toast.makeText(this, "Pick Pdf...", Toast.LENGTH_SHORT).show();
        } else {
            uploadPdfToStorage();
        }
    }

    private void uploadPdfToStorage() {
        Toast.makeText(this, "Uploading PDF...", Toast.LENGTH_SHORT).show();

        long timestamp = System.currentTimeMillis();
        String filePathAndName = "Books/" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);

        storageReference.putFile(pdfUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    String uploadedPdfUrl = "" + uriTask.getResult();
                    uploadPdfInfoToDb(title, uploadedPdfUrl, timestamp);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(addpdf.this, "PDF upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadPdfInfoToDb(String title, String uploadedPdfUrl, long timestamp) {
        Toast.makeText(this, "Uploading PDF info...", Toast.LENGTH_SHORT).show();

        String uid = firebaseAuth.getUid();
        book bk = new book(title, selectedCategoryId, uid, timestamp, uploadedPdfUrl);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child("" + timestamp)
                .setValue(bk)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(addpdf.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(addpdf.this, "Failed to upload to DB: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadPdfCategories() {
        categoryTitleArrayList = new ArrayList<>();
        categoryIdArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();
                categoryIdArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String categoryId = "" + ds.child("id").getValue();
                    String categoryTitle = "" + ds.child("category").getValue();

                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(addpdf.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void categoryPickDialog() {
        String[] categoriesArray = categoryTitleArrayList.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Category")
                .setItems(categoriesArray, (dialog, which) -> {
                    selectedCategoryTitle = categoryTitleArrayList.get(which);
                    selectedCategoryId = categoryIdArrayList.get(which);
                    ((TextView) findViewById(R.id.categoryTv)).setText(selectedCategoryTitle);
                })
                .show();
    }

    private void pdfPickIntent() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PDF_PICK_CODE && data != null) {
            pdfUri = data.getData();
            Toast.makeText(this, "PDF selected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cancelled picking PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
