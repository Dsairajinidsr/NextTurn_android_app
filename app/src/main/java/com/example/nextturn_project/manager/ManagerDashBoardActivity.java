package com.example.nextturn_project.manager;

import android.os.Bundle;
import android.widget.TextView;

import android.content.Intent;

import androidx.cardview.widget.CardView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextturn_project.R;
import com.example.nextturn_project.manager.adapter.RecentActivityAdapter;
import com.example.nextturn_project.manager.model.ActivityModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;

public class ManagerDashBoardActivity extends AppCompatActivity {

    private TextView tvGreeting, tvManagerName;
    private TextView tvTotalPatients, tvWaiting, tvServing, tvCompleted;
    private TextView tvGeneralCount, tvCardiologyCount, tvOrthopedicsCount,
            tvNeurologyCount, tvPediatricsCount;

    private RecyclerView rvRecentActivity;

    private FirebaseAuth mAuth;
    private CardView cardReports;
    private FirebaseFirestore db;

    private ArrayList<ActivityModel> activityList;
    private RecentActivityAdapter activityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);
        cardReports = findViewById(R.id.cardReports);

        cardReports.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ManagerDashBoardActivity.this,
                    ManagerReportsActivity.class);

            startActivity(intent);

        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Greeting Card
        tvGreeting = findViewById(R.id.tvGreeting);
        tvManagerName = findViewById(R.id.tvManagerName);

        // Dashboard Cards
        tvTotalPatients = findViewById(R.id.tvTotalPatients);
        tvWaiting = findViewById(R.id.tvWaiting);
        tvServing = findViewById(R.id.tvServing);
        tvCompleted = findViewById(R.id.tvCompleted);

        // Department Statistics
        tvGeneralCount = findViewById(R.id.tvGeneralCount);
        tvCardiologyCount = findViewById(R.id.tvCardiologyCount);
        tvOrthopedicsCount = findViewById(R.id.tvOrthopedicsCount);
        tvNeurologyCount = findViewById(R.id.tvNeurologyCount);
        tvPediatricsCount = findViewById(R.id.tvPediatricsCount);

        cardReports = findViewById(R.id.cardReports);

        // Recent Activity
        rvRecentActivity = findViewById(R.id.rvRecentActivity);

        activityList = new ArrayList<>();
        activityAdapter = new RecentActivityAdapter(activityList);

        rvRecentActivity.setLayoutManager(new LinearLayoutManager(this));
        rvRecentActivity.setAdapter(activityAdapter);

        // Load Data
        setGreeting();
        loadManagerName();
        loadDashboardStatistics();
        loadDepartmentStatistics();
        loadRecentActivity();

    }

    // Greeting
    private void setGreeting() {

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (hour < 12) {
            tvGreeting.setText("Good Morning ☀️");
        } else if (hour < 17) {
            tvGreeting.setText("Good Afternoon 🌤️");
        } else {
            tvGreeting.setText("Good Evening 🌙");
        }
    }

    // Load Manager Name
    private void loadManagerName() {

        if (mAuth.getCurrentUser() == null)
            return;

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("manager")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if (document.exists()) {

                        String fullName = document.getString("name");

                        if (fullName != null && !fullName.isEmpty()) {

                            String firstName = fullName.split(" ")[0];
                            tvManagerName.setText(firstName);

                        } else {

                            tvManagerName.setText("Manager");
                        }
                    }

                })
                .addOnFailureListener(e ->
                        tvManagerName.setText("Manager"));
    }

    // Dashboard Statistics
    private void loadDashboardStatistics() {

        db.collection("patients")
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null)
                        return;

                    int total = 0;
                    int waiting = 0;
                    int serving = 0;
                    int completed = 0;

                    for (DocumentSnapshot document : value.getDocuments()) {

                        total++;

                        String status = document.getString("queueStatus");

                        if (status == null)
                            continue;

                        switch (status) {

                            case "Waiting":
                                waiting++;
                                break;

                            case "Called":
                            case "Serving":
                                serving++;
                                break;

                            case "Completed":
                                completed++;
                                break;
                        }
                    }

                    tvTotalPatients.setText(String.valueOf(total));
                    tvWaiting.setText(String.valueOf(waiting));
                    tvServing.setText(String.valueOf(serving));
                    tvCompleted.setText(String.valueOf(completed));

                });
    }

    // Department Statistics
    private void loadDepartmentStatistics() {

        db.collection("patients")
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null)
                        return;

                    int general = 0;
                    int cardiology = 0;
                    int orthopedics = 0;
                    int neurology = 0;
                    int pediatrics = 0;

                    for (DocumentSnapshot document : value.getDocuments()) {

                        String department = document.getString("department");

                        if (department == null)
                            continue;

                        switch (department) {

                            case "General":
                                general++;
                                break;

                            case "Cardiology":
                                cardiology++;
                                break;

                            case "Orthopedics":
                                orthopedics++;
                                break;

                            case "Neurology":
                                neurology++;
                                break;

                            case "Pediatrics":
                                pediatrics++;
                                break;
                        }
                    }

                    tvGeneralCount.setText(String.valueOf(general));
                    tvCardiologyCount.setText(String.valueOf(cardiology));
                    tvOrthopedicsCount.setText(String.valueOf(orthopedics));
                    tvNeurologyCount.setText(String.valueOf(neurology));
                    tvPediatricsCount.setText(String.valueOf(pediatrics));

                });
    }

    // Recent Activity
    private void loadRecentActivity() {

        db.collection("patients")
                .orderBy("tokenNumber", Query.Direction.DESCENDING)
                .limit(5)
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null)
                        return;

                    activityList.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {

                        ActivityModel model = new ActivityModel();

                        model.setTokenNumber(doc.getString("tokenNumber"));
                        model.setName(doc.getString("name"));
                        model.setQueueStatus(doc.getString("queueStatus"));

                        activityList.add(model);
                    }

                    activityAdapter.notifyDataSetChanged();

                });
    }
}