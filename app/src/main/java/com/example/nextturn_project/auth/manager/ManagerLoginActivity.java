package com.example.nextturn_project.auth.manager;

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
import com.example.nextturn_project.manager.ManagerDashBoardActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ManagerLoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView txtRegister, txtForgot;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_login);

        // Initialize Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
        txtForgot = findViewById(R.id.txtForgot);

        // Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Login Button
        btnLogin.setOnClickListener(v -> loginManager());

        // Register
        txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ManagerLoginActivity.this,
                    ManagerRegisterActivity.class);
            startActivity(intent);
        });

        // Forgot Password
        txtForgot.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this,
                        "Enter your email first",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(
                                    ManagerLoginActivity.this,
                                    "Password reset email sent",
                                    Toast.LENGTH_LONG).show())

                    .addOnFailureListener(e ->
                            Toast.makeText(
                                    ManagerLoginActivity.this,
                                    e.getMessage(),
                                    Toast.LENGTH_LONG).show());

        });

    }

    private void loginManager() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation

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

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter Password");
            etPassword.requestFocus();
            return;
        }

        // Firebase Login

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                ManagerLoginActivity.this,
                                "Login Successful",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(
                                ManagerLoginActivity.this,
                                ManagerDashBoardActivity.class);

                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(
                                ManagerLoginActivity.this,
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();

                    }

                });

    }
}