package com.example.nextturn_project.staff.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nextturn_project.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    // Welcome Card,
    private TextView tvGreeting;
    private TextView tvStaffName;

    // Dashboard Cards
    private TextView tvTotal;
    private TextView tvWaiting;
    private TextView tvServing;
    private TextView tvCompleted;

    // Recent Activity
    private TextView tvActivity1;
    private TextView tvActivity2;
    private TextView tvActivity3;

    private FirebaseFirestore db;

    public HomeFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_home,
                container,
                false);

        initViews(view);

        db = FirebaseFirestore.getInstance();

        loadStaffName();

        setGreeting();

        loadDashboardCounts();

        loadRecentActivities();

        return view;
    }

    // ===========================================
    // Initialize Views
    // ===========================================

    private void initViews(View view) {

        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvStaffName = view.findViewById(R.id.tvStaffName);

        tvTotal = view.findViewById(R.id.tvTotal);
        tvWaiting = view.findViewById(R.id.tvWaiting);
        tvServing = view.findViewById(R.id.tvServing);
        tvCompleted = view.findViewById(R.id.tvCompleted);

        tvActivity1 = view.findViewById(R.id.tvActivity1);
        tvActivity2 = view.findViewById(R.id.tvActivity2);
        tvActivity3 = view.findViewById(R.id.tvActivity3);
    }

    // ===========================================
    // Load Staff Name
    // ===========================================

    private void loadStaffName() {

        SharedPreferences preferences =
                requireActivity().getSharedPreferences(
                        "NextTurn",
                        Context.MODE_PRIVATE);

        String name = preferences.getString(
                "staffName",
                "Staff");

        tvStaffName.setText(name);
    }

    // ===========================================
    // Greeting
    // ===========================================

    private void setGreeting() {

        int hour = Calendar.getInstance()
                .get(Calendar.HOUR_OF_DAY);

        if (hour < 12) {

            tvGreeting.setText("Good Morning ☀️");

        } else if (hour < 17) {

            tvGreeting.setText("Good Afternoon 🌤️");

        } else {

            tvGreeting.setText("Good Evening 🌙");
        }
    }

    // ===========================================
    // Dashboard Counts
    // ===========================================

    private void loadDashboardCounts() {

        db.collection("patients")
                .addSnapshotListener((value, error) -> {

                    if (error != null) {

                        Toast.makeText(
                                getContext(),
                                error.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    if (value == null)
                        return;

                    int total = 0;
                    int waiting = 0;
                    int serving = 0;
                    int completed = 0;

                    for (DocumentSnapshot document : value.getDocuments()) {

                        total++;

                        String status =
                                document.getString("queueStatus");

                        if (status == null)
                            continue;

                        switch (status) {

                            case "Waiting":
                                waiting++;
                                break;

                            case "Serving":
                                serving++;
                                break;

                            case "Completed":
                                completed++;
                                break;
                        }
                    }

                    tvTotal.setText(String.valueOf(total));
                    tvWaiting.setText(String.valueOf(waiting));
                    tvServing.setText(String.valueOf(serving));
                    tvCompleted.setText(String.valueOf(completed));

                });
    }

    // ===========================================
    // Recent Activities
    // ===========================================

    private void loadRecentActivities() {

        db.collection("patients")
                .limit(3)
                .addSnapshotListener((value, error) -> {

                    if (error != null)
                        return;

                    if (value == null)
                        return;

                    String[] activity = {
                            "• No recent activity",
                            "",
                            ""
                    };

                    int index = 0;

                    for (DocumentSnapshot document : value.getDocuments()) {

                        if (index >= 3)
                            break;

                        String name =
                                document.getString("patientName");

                        if (name == null)
                            name = document.getString("name");

                        String token =
                                document.getString("tokenNumber");

                        String status =
                                document.getString("queueStatus");

                        activity[index] =
                                "• "
                                        + (token == null ? "--" : token)
                                        + " - "
                                        + (name == null ? "Unknown" : name)
                                        + " ("
                                        + (status == null ? "Waiting" : status)
                                        + ")";

                        index++;
                    }

                    tvActivity1.setText(activity[0]);
                    tvActivity2.setText(activity[1]);
                    tvActivity3.setText(activity[2]);

                });
    }
}