package com.example.pageflow;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddBookAdmin extends AppCompatActivity {

    private EditText eTitle, eAuthor, elanguage, egenere, eimage;
    private CheckBox boxAvailable;
    private Button buttonAdd, buttonBack;
    private DatabaseReference booksDatabase;
    private String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_admin);


        eTitle = findViewById(R.id.edTitle);
        eAuthor = findViewById(R.id.edAuthor);
        elanguage = findViewById(R.id.language);
        egenere = findViewById(R.id.genere);
        eimage = findViewById(R.id.image);
        boxAvailable = findViewById(R.id.checkboxAvailable);

        buttonAdd = findViewById(R.id.buttonAddEdit);
        buttonBack = findViewById(R.id.buttonBack);


        booksDatabase = FirebaseDatabase.getInstance().getReference("books");


        bookId = getIntent().getStringExtra("bookId");
        if (bookId != null) {
            setTitle("Edit Book");
            buttonAdd.setText("Edit");
            loadBookDetails();
        } else {
            setTitle("Add Book");
        }


        buttonBack.setOnClickListener(view -> finish());

        buttonAdd.setOnClickListener(v -> {
            String title = eTitle.getText().toString().trim();
            String author = eAuthor.getText().toString().trim();
            String language = elanguage.getText().toString().trim();
            String genere = egenere.getText().toString().trim();
            String image = eimage.getText().toString().trim();
            boolean isAvailable = boxAvailable.isChecked();


            if (bookId != null) {
                updateBook(title, author, language, genere, image, isAvailable);
            } else {
                addBook(title, author, language, genere, image, isAvailable);
            }
        });
    }

    private void loadBookDetails() {
        booksDatabase.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    eTitle.setText(snapshot.child("title").getValue(String.class));
                    eAuthor.setText(snapshot.child("author").getValue(String.class));
                    elanguage.setText(snapshot.child("language").getValue(String.class));
                    egenere.setText(snapshot.child("genere").getValue(String.class));
                    eimage.setText(snapshot.child("image").getValue(String.class));
                    boxAvailable.setChecked(snapshot.child("isAvailable").getValue(Boolean.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddBookAdmin.this, "Failed to load book details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addBook(String title, String author, String language, String genere, String image, Boolean isAvailable) {
        String id = booksDatabase.push().getKey();
        book book = new book(id, title, author, language, genere, image, isAvailable);
        booksDatabase.child(id).setValue(book).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AddBookAdmin.this, "Book added successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AddBookAdmin.this, "Failed to add book", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBook(String title, String author, String language, String genere, String image, boolean isAvailable) {
        book book = new book(bookId, title, author, language, genere, image, isAvailable);
        booksDatabase.child(bookId).setValue(book).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AddBookAdmin.this, "Book updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AddBookAdmin.this, "Failed to update book", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
