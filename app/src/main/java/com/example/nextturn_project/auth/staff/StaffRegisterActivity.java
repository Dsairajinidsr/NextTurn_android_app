package com.example.nextturn_project.auth.staff;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nextturn_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StaffRegisterActivity extends AppCompatActivity {

    EditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword;
    Button btnRegister;
    CheckBox cbTerms;
    TextView txtLogin;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_register);

        // Initialize Views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        cbTerms = findViewById(R.id.cbTerms);
        txtLogin = findViewById(R.id.txtLogin);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Register Button
        btnRegister.setOnClickListener(v -> registerStaff());

        // Login Text
        txtLogin.setOnClickListener(v -> {
            Intent intent = new Intent(
                    StaffRegisterActivity.this,
                    StaffLoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerStaff() {

        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validation

        if (TextUtils.isEmpty(name)) {
            etFullName.setError("Enter Full Name");
            etFullName.requestFocus();
            return;
        }

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

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Enter Phone Number");
            etPhone.requestFocus();
            return;
        }

        if (phone.length() != 10) {
            etPhone.setError("Phone Number must be 10 digits");
            etPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter Password");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this,
                    "Please accept Terms & Conditions",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Firebase Account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String uid = mAuth.getCurrentUser().getUid();

                        Map<String, Object> staff = new HashMap<>();

                        staff.put("uid", uid);
                        staff.put("name", name);
                        staff.put("email", email);
                        staff.put("phone", phone);
                        staff.put("userType", "Staff");

                        db.collection("staff")
                                .document(uid)
                                .set(staff)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(
                                            StaffRegisterActivity.this,
                                            "Staff Registration Successful",
                                            Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(
                                            StaffRegisterActivity.this,
                                            StaffLoginActivity.class);

                                    startActivity(intent);
                                    finish();

                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(
                                                StaffRegisterActivity.this,
                                                e.getMessage(),
                                                Toast.LENGTH_LONG).show());

                    } else {

                        Toast.makeText(
                                StaffRegisterActivity.this,
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();

                    }

                });

    }
}