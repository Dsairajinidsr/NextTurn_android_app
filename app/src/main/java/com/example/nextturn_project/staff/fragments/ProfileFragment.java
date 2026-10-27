package com.example.nextturn_project.staff.fragments;

import android.content.Intent;
import com.example.nextturn_project.staff.EditProfileActivity;

import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nextturn_project.R;
import com.example.nextturn_project.auth.staff.StaffLoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    // TextViews
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtPhone;
    private TextView txtStaffId;

    // Buttons
    private MaterialButton btnEditProfile;
    private MaterialButton btnChangePassword;
    private MaterialButton btnLogout;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile,
                container,
                false);

        // Initialize Views

        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtStaffId = view.findViewById(R.id.txtStaffId);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Firebase

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadStaffProfile();

        // Button Clicks

        btnEditProfile.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(),
                    EditProfileActivity.class);

            startActivity(intent);

        });

        btnChangePassword.setOnClickListener(v -> {

            changePassword();

        });

        btnLogout.setOnClickListener(v -> {

            logoutUser();

        });

        return view;
    }

    // ===========================================
    // Load Staff Details
    // ===========================================

    private void loadStaffProfile() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null)
            return;

        String uid = user.getUid();

        db.collection("staff")
                .document(uid)
                .get()
                .addOnSuccessListener(this::showStaffData)
                .addOnFailureListener(e ->

                        Toast.makeText(getContext(),
                                e.getMessage(),
                                Toast.LENGTH_SHORT).show());

    }

    // ===========================================
    // Display Firestore Data
    // ===========================================

    private void showStaffData(DocumentSnapshot document) {

        if (!document.exists())
            return;

        String name = document.getString("name");
        String email = document.getString("email");
        String phone = document.getString("phone");
        String uid = document.getString("uid");

        if (name != null)
            txtName.setText(name);
        else
            txtName.setText("Staff");


        if (email != null)
            txtEmail.setText(email);

        if (phone != null)
            txtPhone.setText(phone);

        if (uid != null)
            txtStaffId.setText(uid);

    }

    // ===========================================
    // Change Password
    // ===========================================

    private void changePassword() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null)
            return;

        //user.sendEmailVerification();

        mAuth.sendPasswordResetEmail(user.getEmail())
                .addOnSuccessListener(unused ->

                        Toast.makeText(getContext(),
                                "Password Reset Email Sent",
                                Toast.LENGTH_LONG).show())

                .addOnFailureListener(e ->

                        Toast.makeText(getContext(),
                                e.getMessage(),
                                Toast.LENGTH_LONG).show());

    }

    // ===========================================
    // Logout
    // ===========================================
    private void logoutUser() {

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)

                .setPositiveButton("Logout", (dialog, which) -> {

                    mAuth.signOut();

                    Intent intent = new Intent(getActivity(),
                            StaffLoginActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);

                    requireActivity().finish();

                })

                .setNegativeButton("Cancel", (dialog, which) -> {

                    dialog.dismiss();

                })

                .show();

    }
}