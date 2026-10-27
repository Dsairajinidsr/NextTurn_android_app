package com.example.nextturn_project.staff.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextturn_project.R;
import com.example.nextturn_project.staff.adapter.QueueAdapter;
import com.example.nextturn_project.staff.model.PatientModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
public class QueueFragment extends Fragment {

    private AutoCompleteTextView actDepartment;

    private RecyclerView rvQueue;

    private ProgressBar progressBar;

    private TextView tvEmpty;
    private TextView tvCurrentToken;
    private TextView tvWaitingCount;

    private MaterialButton btnCallNext;
    private MaterialButton btnServe;
    private MaterialButton btnComplete;

    private FirebaseFirestore db;

    private ArrayList<PatientModel> patientList;

    private QueueAdapter adapter;

    // Currently selected department
    private String selectedDepartment = "";


    public QueueFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_queue,
                container,
                false);

        actDepartment = view.findViewById(R.id.actDepartment);

        rvQueue = view.findViewById(R.id.rvQueue);

        progressBar = view.findViewById(R.id.progressBar);

        tvEmpty = view.findViewById(R.id.tvEmpty);

        tvCurrentToken = view.findViewById(R.id.tvCurrentToken);

        tvWaitingCount = view.findViewById(R.id.tvWaitingCount);

        btnCallNext = view.findViewById(R.id.btnCallNext);
        btnComplete = view.findViewById(R.id.btnComplete);
        btnServe = view.findViewById(R.id.btnServe);

        db = FirebaseFirestore.getInstance();

        patientList = new ArrayList<>();

        adapter = new QueueAdapter(requireContext(), patientList);

        rvQueue.setLayoutManager(
                new LinearLayoutManager(requireContext()));

        rvQueue.setAdapter(adapter);

        // Department List
        String[] departments = {
                "General",
                "Cardiology",
                "Orthopedics",
                "Neurology",
                "Pediatrics"
        };

        ArrayAdapter<String> departmentAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        departments);

        actDepartment.setAdapter(departmentAdapter);

        actDepartment.setOnItemClickListener((parent, view1, position, id) -> {

            selectedDepartment =
                    parent.getItemAtPosition(position).toString();

            loadQueue(selectedDepartment);

        });

        btnCallNext.setOnClickListener(v -> {

            if (!selectedDepartment.isEmpty()) {

                callNextPatient();

            }

        });

        btnComplete.setOnClickListener(v -> {

            if (!selectedDepartment.isEmpty()) {

                completeCurrentPatient();

            }

        });
        btnServe.setOnClickListener(v -> {

            if (!selectedDepartment.isEmpty()) {

                servePatient();

            }

        });

        return view;
    }


    private void callNextPatient() {

        db.collection("patients")
                .whereEqualTo("department", selectedDepartment)
                .whereEqualTo("queueStatus", "Called")
                .get()
                .addOnSuccessListener(called -> {

                    if (!called.isEmpty()) {

                        Toast.makeText(
                                getContext(),
                                "A patient is already called.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    db.collection("patients")
                            .whereEqualTo("department", selectedDepartment)
                            .whereEqualTo("queueStatus", "Serving")
                            .get()
                            .addOnSuccessListener(serving -> {

                                if (!serving.isEmpty()) {

                                    Toast.makeText(
                                            getContext(),
                                            "A patient is currently being served.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                db.collection("patients")
                                        .whereEqualTo("department", selectedDepartment)
                                        .whereEqualTo("queueStatus", "Waiting")
                                        .orderBy("tokenNumber")
                                        .limit(1)
                                        .get()
                                        .addOnSuccessListener(waiting -> {

                                            if (waiting.isEmpty()) {

                                                Toast.makeText(
                                                        getContext(),
                                                        "No waiting patients.",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                return;
                                            }

                                            waiting.getDocuments()
                                                    .get(0)
                                                    .getReference();
                                            waiting.getDocuments()
                                                    .get(0)
                                                    .getReference()
                                                    .update("queueStatus", "Called")
                                                    .addOnSuccessListener(unused -> refreshQueue());

                                        });

                            });

                });

    }
    private void servePatient() {

        db.collection("patients")
                .whereEqualTo("department", selectedDepartment)
                .whereEqualTo("queueStatus", "Called")
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {

                    if (query.isEmpty()) {

                        Toast.makeText(
                                getContext(),
                                "No called patient found",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    query.getDocuments()
                            .get(0)
                            .getReference();
                    query.getDocuments()
                            .get(0)
                            .getReference()
                            .update("queueStatus", "Serving")
                            .addOnSuccessListener(unused -> refreshQueue());

                });

    }
    private void completeCurrentPatient() {

        db.collection("patients")
                .whereEqualTo("department", selectedDepartment)
                .whereEqualTo("queueStatus", "Serving")
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {

                    if (query.isEmpty()) {

                        Toast.makeText(
                                getContext(),
                                "No serving patient.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    query.getDocuments()
                            .get(0)
                            .getReference()
                            .update("queueStatus", "Completed")
                            .addOnSuccessListener(unused -> {

                                callNextPatient();
                                refreshQueue();

                            });

                });

    }
    private void loadQueue(String department) {

        progressBar.setVisibility(View.VISIBLE);

        db.collection("patients")
                .whereEqualTo("department", department)
                .whereNotEqualTo("queueStatus", "Completed")
                .orderBy("tokenNumber")
                .addSnapshotListener((value, error) -> {

                    progressBar.setVisibility(View.GONE);

                    if (error != null)
                        return;

                    if (value == null)
                        return;

                    patientList.clear();

                    int waiting = 0;
                    String currentToken = "--";

                    for (DocumentSnapshot document : value.getDocuments()) {

                        PatientModel patient =
                                document.toObject(PatientModel.class);

                        if (patient == null)
                            continue;

                        patientList.add(patient);

                        String status = patient.getQueueStatus();

                        if ("Waiting".equals(status)) {
                            waiting++;
                        }

                        if ("Called".equals(status)
                                || "Serving".equals(status)) {

                            currentToken =
                                    patient.getTokenNumber();
                        }

                    }

                    adapter.notifyDataSetChanged();

                    tvWaitingCount.setText(
                            String.valueOf(waiting));

                    tvCurrentToken.setText(currentToken);

                    if (patientList.isEmpty()) {

                        tvEmpty.setVisibility(View.VISIBLE);
                        rvQueue.setVisibility(View.GONE);

                    } else {

                        tvEmpty.setVisibility(View.GONE);
                        rvQueue.setVisibility(View.VISIBLE);

                    }

                });

    }
    private void refreshQueue() {

        if (!selectedDepartment.isEmpty()) {

            loadQueue(selectedDepartment);

        }

    }

}