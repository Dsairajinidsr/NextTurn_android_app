package com.example.nextturn_project.manager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import android.app.DatePickerDialog;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextturn_project.R;
import com.example.nextturn_project.manager.adapter.ReportAdapter;
import com.example.nextturn_project.manager.model.ReportModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ManagerReportsActivity extends AppCompatActivity {

    private RecyclerView rvReports;
    private ReportAdapter adapter;
    private ArrayList<ReportModel> reportList;
    private FirebaseFirestore db;

    private TextView tvTotalReports;
    private TextView tvWaitingReports;
    private TextView tvCompletedReports;
    private TextView tvSelectedDate;

    private Button btnToday;
    private Button btnWeek;
    private Button btnMonth;
    private Button btnSelectDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_reports);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        db = FirebaseFirestore.getInstance();

        rvReports = findViewById(R.id.rvReports);

        tvTotalReports = findViewById(R.id.tvTotalReports);
        tvWaitingReports = findViewById(R.id.tvWaitingReports);
        tvCompletedReports = findViewById(R.id.tvCompletedReports);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);

        btnToday = findViewById(R.id.btnToday);
        btnWeek = findViewById(R.id.btnWeek);
        btnMonth = findViewById(R.id.btnMonth);
        btnSelectDate = findViewById(R.id.btnSelectDate);

        reportList = new ArrayList<>();
        adapter = new ReportAdapter(reportList);

        rvReports.setLayoutManager(new LinearLayoutManager(this));
        rvReports.setAdapter(adapter);

        String today = new SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
        ).format(Calendar.getInstance().getTime());

        tvSelectedDate.setText(today);

        loadAllReports();

        btnToday.setOnClickListener(v -> loadTodayReports());

        btnWeek.setOnClickListener(v -> loadWeekReports());

        btnMonth.setOnClickListener(v -> loadMonthReports());

        // Optional: Add DatePicker functionality here
        btnSelectDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ManagerReportsActivity.this,
                    (view, year, month, dayOfMonth) -> {

                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);

                        String date = new SimpleDateFormat(
                                "dd-MM-yyyy",
                                Locale.getDefault()
                        ).format(selectedDate.getTime());

                        tvSelectedDate.setText(date);
                        loadReportsByDate(date);

                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.show();
        });
    }

    private void loadAllReports() {

        db.collection("patients")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    reportList.clear();

                    int waiting = 0;
                    int completed = 0;

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {

                        ReportModel model = new ReportModel();

                        model.setName(doc.getString("patientName"));
                        model.setDepartment(doc.getString("department"));
                        model.setQueueStatus(doc.getString("queueStatus"));
                        model.setVisitDate(doc.getString("registrationDate"));
                        model.setTokenNumber(doc.getString("tokenNumber"));

                        reportList.add(model);

                        String status = doc.getString("queueStatus");

                        if ("Waiting".equalsIgnoreCase(status)) {
                            waiting++;
                        } else if ("Completed".equalsIgnoreCase(status)) {
                            completed++;
                        }
                    }

                    adapter.notifyDataSetChanged();

                    tvTotalReports.setText(String.valueOf(reportList.size()));
                    tvWaitingReports.setText(String.valueOf(waiting));
                    tvCompletedReports.setText(String.valueOf(completed));

                })
                .addOnFailureListener(e -> {

                    reportList.clear();
                    adapter.notifyDataSetChanged();

                    tvTotalReports.setText("0");
                    tvWaitingReports.setText("0");
                    tvCompletedReports.setText("0");
                });
    }

    private void loadTodayReports() {

        String today = new SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
        ).format(Calendar.getInstance().getTime());

        tvSelectedDate.setText(today);

        loadReportsByDate(today);
    }

    private void loadWeekReports() {

        // Currently loads all reports.
        // Replace with week filter if needed.
        loadAllReports();
    }

    private void loadMonthReports() {

        // Currently loads all reports.
        // Replace with month filter if needed.
        loadAllReports();
    }

    private void loadReportsByDate(@NonNull String date) {

        db.collection("patients")
                .whereEqualTo("registrationDate", date)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    reportList.clear();

                    int waiting = 0;
                    int completed = 0;

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {

                        ReportModel model = new ReportModel();

                        model.setName(doc.getString("patientName"));
                        model.setDepartment(doc.getString("department"));
                        model.setQueueStatus(doc.getString("queueStatus"));
                        model.setVisitDate(doc.getString("registrationDate"));
                        model.setTokenNumber(doc.getString("tokenNumber"));

                        reportList.add(model);

                        String status = doc.getString("queueStatus");

                        if ("Waiting".equalsIgnoreCase(status)) {
                            waiting++;
                        } else if ("Completed".equalsIgnoreCase(status)) {
                            completed++;
                        }
                    }

                    adapter.notifyDataSetChanged();

                    tvTotalReports.setText(String.valueOf(reportList.size()));
                    tvWaitingReports.setText(String.valueOf(waiting));
                    tvCompletedReports.setText(String.valueOf(completed));

                })
                .addOnFailureListener(e -> {

                    reportList.clear();
                    adapter.notifyDataSetChanged();

                    tvTotalReports.setText("0");
                    tvWaitingReports.setText("0");
                    tvCompletedReports.setText("0");
                });
    }
}