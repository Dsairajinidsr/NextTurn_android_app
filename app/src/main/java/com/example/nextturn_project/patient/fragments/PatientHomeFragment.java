package com.example.nextturn_project.patient.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nextturn_project.R;

import java.util.Calendar;

public class PatientHomeFragment extends Fragment {

    private TextView tvGreeting;
    private TextView tvPatientName;
    private TextView tvToken;
    private TextView tvAhead;
    private TextView tvWaitingTime;
    private TextView tvStatus;

    public PatientHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_patient_home,
                container,
                false);

        // Initialize Views
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvPatientName = view.findViewById(R.id.tvPatientName);
        tvToken = view.findViewById(R.id.tvToken);
        tvAhead = view.findViewById(R.id.tvAhead);
        tvWaitingTime = view.findViewById(R.id.tvWaitingTime);
        tvStatus = view.findViewById(R.id.tvStatus);

        loadGreeting();
        loadPatientName();

        // Temporary Values
        // Later these will come from Firebase

        tvToken.setText("A001");
        tvAhead.setText("5");
        tvWaitingTime.setText("20 Min");
        tvStatus.setText("Waiting");

        return view;
    }

    // Greeting based on time

    private void loadGreeting() {

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;

        if (hour >= 5 && hour < 12) {

            greeting = "Good Morning ☀";

        } else if (hour >= 12 && hour < 17) {

            greeting = "Good Afternoon 🌤";

        } else if (hour >= 17 && hour < 21) {

            greeting = "Good Evening 🌇";

        } else {

            greeting = "Good Night 🌙";
        }

        tvGreeting.setText(greeting);
    }

    // Load Patient Name

    private void loadPatientName() {

        SharedPreferences preferences =
                requireActivity().getSharedPreferences(
                        "PatientSession",
                        getContext().MODE_PRIVATE);

        String patientName =
                preferences.getString(
                        "patientName",
                        "Patient");

        tvPatientName.setText(patientName);
    }

}