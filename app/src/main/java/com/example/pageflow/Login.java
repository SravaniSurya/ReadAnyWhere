package com.example.pageflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    EditText Uname, pwd;
    TextView tvforgotpwd, tvnewtolibrary;
    Button button;

    FirebaseAuth mAuth;
    DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Uname = findViewById(R.id.Email);
        pwd = findViewById(R.id.password);
        button = findViewById(R.id.submitbutton);
        tvforgotpwd = findViewById(R.id.forgotpwd);
        tvnewtolibrary = findViewById(R.id.newtolibrary);
        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference("users");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userlogin();
            }
        });

        tvforgotpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotpassword = new Intent(Login.this, ForgotPassword.class);
                startActivity(forgotpassword);
                finish();
            }
        });

        tvnewtolibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Accountcreationpage = new Intent(Login.this, register.class);
                startActivity(Accountcreationpage);
                finish();
            }
        });
    }

    private void userlogin() {
        String emailadd = Uname.getText().toString();
        String passcode = pwd.getText().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("users");

        if (emailadd.isEmpty() || passcode.isEmpty()) {
            Toast.makeText(Login.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailadd, passcode).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userId = user.getUid();

                    mUsersDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String role = dataSnapshot.child("role").getValue(String.class);
                                if (role != null && role.equals("Admin")) {
                                    Intent k = new Intent(Login.this, AdminDashboard.class);
                                    startActivity(k);
                                    finish();
                                } else {
                                    Intent k = new Intent(Login.this, dashboard.class);
                                    startActivity(k);
                                    finish();
                                }
                            } else {
                                Toast.makeText(Login.this, "Failed to retrieve user role", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(Login.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Login.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "Sign In failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
