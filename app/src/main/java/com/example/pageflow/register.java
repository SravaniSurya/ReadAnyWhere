package com.example.pageflow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {


    EditText emailField, firstNameField, passwordField, confirmPasswordField;
    Spinner roleSpinner;
    Button submitButton;
    TextView backToLogin;
    FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        emailField = findViewById(R.id.createemail);
        firstNameField = findViewById(R.id.FirstName);

        passwordField = findViewById(R.id.createpassword);
        confirmPasswordField = findViewById(R.id.Confirmpassword);
        roleSpinner = findViewById(R.id.roleSpinner);
        submitButton = findViewById(R.id.submitbutton);
        backToLogin = findViewById(R.id.backLogin);

        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference("users");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserRegistration();
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void UserRegistration() {
        String email = emailField.getText().toString();
        String firstName = firstNameField.getText().toString();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();
        String role = roleSpinner.getSelectedItem().toString();

        if (email.isEmpty() || firstName.isEmpty()  || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(register.this, "Enter all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordField.setError("Password does not match");
            confirmPasswordField.requestFocus();
            return;
        }

        if (password.length() < 8) {
            passwordField.setError("Password should contain more than 8 characters");
            passwordField.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Email should be in correct format");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userId = mAuth.getCurrentUser().getUid();
                    user user = new user(firstName, email, role);
                    mUsersDatabase.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(register.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(register.this, Login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(register.this, "Failed to store user data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(register.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(register.this, "Sign Up failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}