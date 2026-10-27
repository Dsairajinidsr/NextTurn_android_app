package com.example.nextturn_project.staff;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nextturn_project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etPhone;
    private MaterialButton btnUpdate;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Views
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getCurrentUser().getUid();
        }

        // Load Existing Data
        loadProfile();

        // Update Button
        btnUpdate.setOnClickListener(v -> updateProfile());
    }

    // ---------------- Load Profile ----------------

    private void loadProfile() {

        db.collection("staff")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phone");

                        etName.setText(name);
                        etPhone.setText(phone);

                    }

                });

    }

    // ---------------- Update Profile ----------------

    private void updateProfile() {

        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Enter Name");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Enter Phone");
            return;
        }

        if (phone.length() != 10) {
            etPhone.setError("Enter Valid Phone Number");
            return;
        }

        Map<String, Object> update = new HashMap<>();
        update.put("name", name);
        update.put("phone", phone);

        btnUpdate.setEnabled(false);

        db.collection("staff")
                .document(uid)
                .update(update)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            EditProfileActivity.this,
                            "Profile Updated Successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    btnUpdate.setEnabled(true);

                    finish();

                })
                .addOnFailureListener(e -> {

                    btnUpdate.setEnabled(true);

                    Toast.makeText(
                            EditProfileActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });

    }
}