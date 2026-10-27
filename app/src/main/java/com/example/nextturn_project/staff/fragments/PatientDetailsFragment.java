package com.example.nextturn_project.staff.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nextturn_project.R;
import com.example.nextturn_project.manager.FirestoreManager;
import com.example.nextturn_project.staff.model.PatientModel;
import com.google.android.material.button.MaterialButton;

public class PatientDetailsFragment extends Fragment {

    private TextView tvToken;
    private TextView tvName;
    private TextView tvAge;
    private TextView tvGender;
    private TextView tvMobile;
    private TextView tvDepartment;
    private TextView tvSymptoms;
    private TextView tvPriority;
    private TextView tvStatus;
    private TextView tvDate;
    private TextView tvTime;

    private MaterialButton btnCallPatient;
    private MaterialButton btnComplete;
    private MaterialButton btnBack;

    private FirestoreManager firestoreManager;
    private PatientModel patient;

    public PatientDetailsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_patient_details,
                container,
                false);

        firestoreManager = new FirestoreManager();

        initViews(view);

        Bundle bundle = getArguments();

        if (bundle != null) {

            patient = (PatientModel) bundle.getSerializable("patient");

            if (patient != null) {

                tvToken.setText(patient.getTokenNumber());
                tvName.setText(patient.getPatientName());
                tvAge.setText(patient.getAge());
                tvGender.setText(patient.getGender());
                tvMobile.setText(patient.getMobile());
                tvDepartment.setText(patient.getDepartment());
                tvSymptoms.setText(patient.getSymptoms());
                tvPriority.setText(patient.getPriority());
                tvStatus.setText(patient.getQueueStatus());
                tvDate.setText(patient.getRegistrationDate());
                tvTime.setText(patient.getRegistrationTime());

            } else {

                Toast.makeText(
                        requireContext(),
                        "Patient data not found",
                        Toast.LENGTH_SHORT
                ).show();

            }
        }

        btnCallPatient.setOnClickListener(v -> {

            if (patient == null) return;

            firestoreManager.updatePatientStatus(
                    patient.getPatientId(),
                    "In Progress",
                    new FirestoreManager.StatusCallback() {

                        @Override
                        public void onSuccess() {

                            patient.setQueueStatus("In Progress");
                            tvStatus.setText("In Progress");

                            Toast.makeText(
                                    requireContext(),
                                    "Patient Called Successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                        @Override
                        public void onFailure(String message) {

                            Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_SHORT
                            ).show();

                        }
                    });

        });

        btnComplete.setOnClickListener(v -> {

            if (patient == null) return;

            firestoreManager.updatePatientStatus(
                    patient.getPatientId(),
                    "Completed",
                    new FirestoreManager.StatusCallback() {

                        @Override
                        public void onSuccess() {

                            patient.setQueueStatus("Completed");
                            patient.setCompleted(true);

                            tvStatus.setText("Completed");

                            Toast.makeText(
                                    requireContext(),
                                    "Consultation Completed",
                                    Toast.LENGTH_SHORT
                            ).show();

                        }

                        @Override
                        public void onFailure(String message) {

                            Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_SHORT
                            ).show();

                        }
                    });

        });

        btnBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack());

        return view;
    }

    private void initViews(View view) {

        tvToken = view.findViewById(R.id.tvToken);
        tvName = view.findViewById(R.id.tvName);
        tvAge = view.findViewById(R.id.tvAge);
        tvGender = view.findViewById(R.id.tvGender);
        tvMobile = view.findViewById(R.id.tvMobile);
        tvDepartment = view.findViewById(R.id.tvDepartment);
        tvSymptoms = view.findViewById(R.id.tvSymptoms);
        tvPriority = view.findViewById(R.id.tvPriority);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvDate = view.findViewById(R.id.tvDate);
        tvTime = view.findViewById(R.id.tvTime);

        btnCallPatient = view.findViewById(R.id.btnCallPatient);
        btnComplete = view.findViewById(R.id.btnComplete);
        btnBack = view.findViewById(R.id.btnBack);
    }
}