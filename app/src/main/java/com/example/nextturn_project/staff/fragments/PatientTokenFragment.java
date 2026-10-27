package com.example.nextturn_project.staff.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nextturn_project.R;

public class PatientTokenFragment extends Fragment {

    TextView tvToken, tvName, tvDept, tvStatus;

    public PatientTokenFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_patient_token, container, false);

        tvToken = view.findViewById(R.id.tvToken);
        tvName = view.findViewById(R.id.tvName);
        tvDept= view.findViewById(R.id.tvDepartment);
        tvStatus = view.findViewById(R.id.tvStatus);

        if (getArguments() != null) {
            tvToken.setText(getArguments().getString("token"));
            tvName.setText(getArguments().getString("patientName"));
            tvDept.setText(getArguments().getString("department"));
            tvStatus.setText(getArguments().getString("status"));
        }

        return view;
    }
}