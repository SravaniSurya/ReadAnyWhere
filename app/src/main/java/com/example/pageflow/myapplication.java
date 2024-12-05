package com.example.pageflow;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.ValueEventListener;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Locale;

public class myapplication extends Application {

        @Override
        public void onCreate() {
            super.onCreate();
        }

        // Format timestamp into a readable date
        public static final String formatTimestamp(long timestamp) {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(timestamp);
            return DateFormat.format("dd/MM/yyyy", cal).toString();
        }

        // Delete a book from Firebase
        public static void deleteBook(Context context, String bookId, String bookUrl, String bookTitle) {
            // Notify user about the deletion start
            Toast.makeText(context, "Deleting " + bookTitle + "...", Toast.LENGTH_SHORT).show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
            storageReference.delete()
                    .addOnSuccessListener(unused -> {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Books");
                        reference.child(bookId)
                                .removeValue()
                                .addOnSuccessListener(unused1 -> Toast.makeText(context, "Book deleted successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete book: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete file: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        // Load category name into a TextView
        public static void loadCategory(String categoryId, TextView categoryTv) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
            ref.child(categoryId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String category = "" + snapshot.child("category").getValue();
                            categoryTv.setText(category);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(categoryTv.getContext(), "Failed to load category", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Download a book and save to local storage
        public static void downloadBook(Context context, String bookTitle, String bookUrl) {
            String nameWithExtension = bookTitle + ".pdf";
            Toast.makeText(context, "Downloading " + nameWithExtension, Toast.LENGTH_SHORT).show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
            storageReference.getBytes(50000000) // Adjust size as needed
                    .addOnSuccessListener(bytes -> saveDownloadedBook(context, bytes, nameWithExtension))
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to download: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        // Save downloaded book to local storage
        private static void saveDownloadedBook(Context context, byte[] bytes, String nameWithExtension) {
            try {
                File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                downloadsFolder.mkdirs();

                String filePath = downloadsFolder.getPath() + "/" + nameWithExtension;
                FileOutputStream out = new FileOutputStream(filePath);
                out.write(bytes);
                out.close();

                Toast.makeText(context, "Saved to Downloads Folder", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "Failed to save file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


