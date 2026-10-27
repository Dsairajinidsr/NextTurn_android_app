package com.example.nextturn_project.patient.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nextturn_project.R;
import com.example.nextturn_project.auth.patient.PatientLoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PatientProfileFragment extends Fragment {

    private TextView tvPatientName;
    private TextView tvMobile;
    private TextView tvAge;
    private TextView tvGender;
    private TextView tvDepartment;

    private MaterialButton btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public PatientProfileFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_patient_profile,
                container,
                false);

        // Initialize Views
        tvPatientName = view.findViewById(R.id.tvPatientName);
        tvMobile = view.findViewById(R.id.tvMobile);
        tvAge = view.findViewById(R.id.tvAge);
        tvGender = view.findViewById(R.id.tvGender);
        tvDepartment = view.findViewById(R.id.tvDepartment);

        btnLogout = view.findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadPatientProfile();

        btnLogout.setOnClickListener(v -> showLogoutDialog());

        return view;
    }

    // -----------------------------
    // Load Patient Details
    // -----------------------------
    private void loadPatientProfile() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null)
            return;

        String uid = user.getUid();

        db.collection("patients")
                .document(uid)
                .get()
                .addOnSuccessListener(this::showPatientData);
    }

    // -----------------------------
    // Display Data
    // -----------------------------
    private void showPatientData(DocumentSnapshot document) {

        if (!document.exists())
            return;

        String name = document.getString("name");
        String phone = document.getString("phone");
        String age = document.getString("age");
        String gender = document.getString("gender");
        String department = document.getString("department");

        if (name != null)
            tvPatientName.setText(name);

        if (phone != null)
            tvMobile.setText(phone);

        if (age != null)
            tvAge.setText(age);

        if (gender != null)
            tvGender.setText(gender);

        if (department != null)
            tvDepartment.setText(department);
    }

    // -----------------------------
    // Logout Dialog
    // -----------------------------
    private void showLogoutDialog() {

        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> logout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    // -----------------------------
    // Logout
    // -----------------------------
    private void logout() {

        requireActivity()
                .getSharedPreferences(
                        "PatientSession",
                        requireContext().MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(
                getActivity(),
                PatientLoginActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        requireActivity().finish();
    }
}