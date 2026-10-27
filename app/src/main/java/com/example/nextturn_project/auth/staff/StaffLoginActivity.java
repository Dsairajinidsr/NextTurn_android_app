package com.example.nextturn_project.auth.staff;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nextturn_project.R;
import com.example.nextturn_project.staff.StaffDashBoardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class StaffLoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;

    private Button btnLogin;

    private TextView txtRegister;
    private TextView txtForgot;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);

        // Initialize Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);

        txtRegister = findViewById(R.id.txtRegister);
        txtForgot = findViewById(R.id.txtForgot);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Login Button
        btnLogin.setOnClickListener(v -> loginStaff());

        // Register
        txtRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    StaffLoginActivity.this,
                    StaffRegisterActivity.class);

            startActivity(intent);

        });

        // Forgot Password
        txtForgot.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {

                Toast.makeText(
                        StaffLoginActivity.this,
                        "Enter your email first",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(unused ->

                            Toast.makeText(
                                    StaffLoginActivity.this,
                                    "Password reset email sent",
                                    Toast.LENGTH_LONG
                            ).show()

                    )

                    .addOnFailureListener(e ->

                            Toast.makeText(
                                    StaffLoginActivity.this,
                                    e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show()

                    );

        });

    }

    private void loginStaff() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Email Validation
        if (TextUtils.isEmpty(email)) {

            etEmail.setError("Enter Email");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            etEmail.setError("Enter Valid Email");
            etEmail.requestFocus();
            return;
        }

        // Password Validation
        if (TextUtils.isEmpty(password)) {

            etPassword.setError("Enter Password");
            etPassword.requestFocus();
            return;
        }

        // Firebase Login
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String uid = mAuth.getCurrentUser().getUid();

                        db.collection("staff")
                                .document(uid)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {

                                    String fullName = "";

                                    if (documentSnapshot.exists()) {
                                        fullName = documentSnapshot.getString("name");
                                    }

                                    if (fullName == null || fullName.isEmpty()) {
                                        fullName = "Staff";
                                    }

                                    // Get First Name
                                    String firstName = fullName.split(" ")[0];

                                    // Save First Name
                                    getSharedPreferences(
                                            "NextTurn",
                                            MODE_PRIVATE
                                    )
                                            .edit()
                                            .putString("staffName", firstName)
                                            .apply();

                                    Toast.makeText(
                                            StaffLoginActivity.this,
                                            "Login Successful",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    Intent intent = new Intent(
                                            StaffLoginActivity.this,
                                            StaffDashBoardActivity.class
                                    );

                                    startActivity(intent);
                                    finish();

                                })

                                .addOnFailureListener(e ->

                                        Toast.makeText(
                                                StaffLoginActivity.this,
                                                "Unable to load staff details",
                                                Toast.LENGTH_LONG
                                        ).show()

                                );

                    } else {

                        Toast.makeText(
                                StaffLoginActivity.this,
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

}