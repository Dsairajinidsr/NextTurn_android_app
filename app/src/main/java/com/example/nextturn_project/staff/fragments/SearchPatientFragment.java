package com.example.nextturn_project.staff.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nextturn_project.R;
import com.example.nextturn_project.staff.model.PatientModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.example.nextturn_project.manager.FirestoreManager;


public class SearchPatientFragment extends Fragment {

    private TextInputEditText etSearch;
    private MaterialButton btnSearch;
    private MaterialCardView cardPatient;

    private TextView tvName;
    private TextView tvToken;
    private TextView tvDepartment;
    private TextView tvAge;
    private TextView tvGender;
    private TextView tvMobile;
    private TextView tvSymptoms;
    private TextView tvPriority;
    private TextView tvStatus;

    private MaterialButton btnEdit;
    private MaterialButton btnViewQR;
    private FirestoreManager firestoreManager;

    private PatientModel currentPatient;

    public SearchPatientFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_patient,
                container,
                false);

        initViews(view);

        firestoreManager = new FirestoreManager();

        btnSearch.setOnClickListener(v -> searchPatient());

        btnEdit.setOnClickListener(v -> {

            if (currentPatient == null) {

                Toast.makeText(requireContext(),
                        "Search a patient first",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            Toast.makeText(requireContext(),
                    "Edit Patient - Coming Soon",
                    Toast.LENGTH_SHORT).show();

        });

        btnViewQR.setOnClickListener(v -> {

            if (currentPatient == null) {

                Toast.makeText(requireContext(),
                        "Search a patient first",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            Toast.makeText(requireContext(),
                    "QR Screen - Coming Soon",
                    Toast.LENGTH_SHORT).show();

        });

        return view;
    }

    private void initViews(View view) {

        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);

        cardPatient = view.findViewById(R.id.cardPatient);

        tvName = view.findViewById(R.id.tvName);
        tvToken = view.findViewById(R.id.tvToken);
        tvDepartment = view.findViewById(R.id.tvDepartment);
        tvAge = view.findViewById(R.id.tvAge);
        tvGender = view.findViewById(R.id.tvGender);
        tvMobile = view.findViewById(R.id.tvMobile);
        tvSymptoms = view.findViewById(R.id.tvSymptoms);
        tvPriority = view.findViewById(R.id.tvPriority);
        tvStatus = view.findViewById(R.id.tvStatus);

        btnEdit = view.findViewById(R.id.btnEdit);
        btnViewQR = view.findViewById(R.id.btnViewQR);
    }

    private void searchPatient() {

        String keyword = etSearch.getText().toString().trim();

        if (TextUtils.isEmpty(keyword)) {

            etSearch.setError("Enter Token, Name or Mobile");
            return;
        }

        cardPatient.setVisibility(View.GONE);

        firestoreManager.searchPatient(
                keyword,

                new FirestoreManager.SearchCallback() {

                    @Override
                    public void onSuccess(PatientModel patient) {

                        currentPatient = patient;

                        showPatient(patient);

                        Toast.makeText(requireContext(),
                                "Patient Found",
                                Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(String message) {

                        currentPatient = null;

                        Toast.makeText(requireContext(),
                                message,
                                Toast.LENGTH_SHORT).show();

                    }

                });

    }

    private void showPatient(PatientModel patient) {

        cardPatient.setVisibility(View.VISIBLE);

        tvName.setText("Name : " + patient.getPatientName());

        tvToken.setText("Token : " + patient.getTokenNumber());

        tvDepartment.setText("Department : " + patient.getDepartment());

        tvAge.setText("Age : " + patient.getAge());

        tvGender.setText("Gender : " + patient.getGender());

        tvMobile.setText("Mobile : " + patient.getMobile());

        tvSymptoms.setText("Symptoms : " + patient.getSymptoms());

        tvPriority.setText("Priority : " + patient.getPriority());

        tvStatus.setText("Queue Status : " + patient.getQueueStatus());

    }

}