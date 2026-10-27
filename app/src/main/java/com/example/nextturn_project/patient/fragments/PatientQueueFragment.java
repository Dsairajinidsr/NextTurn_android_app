package com.example.nextturn_project.patient.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import android.widget.Toast;

import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.DocumentReference;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nextturn_project.R;
import com.example.nextturn_project.patient.utils.NotificationHelper;

public class PatientQueueFragment extends Fragment {

    private TextView tvMyToken;
    private TextView tvDepartment;
    private TextView tvCurrentToken;
    private TextView tvPatientsAhead;
    private TextView tvEstimatedTime;
    private TextView tvQueueStatus;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration queueListener;
    private String previousStatus = "";

    public PatientQueueFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_patient_queue,
                container,
                false);
        NotificationHelper.createNotificationChannel(requireContext());

        // Initialize Views

        tvMyToken = view.findViewById(R.id.tvMyToken);
        tvDepartment = view.findViewById(R.id.tvDepartment);
        tvCurrentToken = view.findViewById(R.id.tvCurrentToken);
        tvPatientsAhead = view.findViewById(R.id.tvPatientsAhead);
        tvEstimatedTime = view.findViewById(R.id.tvEstimatedTime);
        tvQueueStatus = view.findViewById(R.id.tvQueueStatus);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        startQueueListener();

        return view;
    }
    private void startQueueListener() {

        if (mAuth.getCurrentUser() == null)
            return;

        String uid = mAuth.getCurrentUser().getUid();

        DocumentReference patientRef =
                db.collection("patients")
                        .document(uid);

        queueListener =
                patientRef.addSnapshotListener((snapshot, error) -> {

                    if (error != null) {

                        Toast.makeText(
                                getContext(),
                                error.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    if (snapshot == null || !snapshot.exists())
                        return;

                    showQueueDetails(snapshot);

                    calculateQueue(
                            snapshot.getString("department"),
                            snapshot.getString("tokenNumber")
                    );

                });

    }

    //loadQueueFromFirebase
    private void loadQueueFromFirebase() {

        if (mAuth.getCurrentUser() == null)
            return;

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("patients")
                .document(uid)
                .addSnapshotListener((document, error) -> {

                    if (error != null) {

                        Toast.makeText(
                                getContext(),
                                error.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    if (document != null && document.exists()) {

                        showQueueDetails(document);

                    }

                });

    }
    //showQueueDetails
    private void showQueueDetails(DocumentSnapshot document) {

        if (!document.exists())
            return;

        String token = document.getString("tokenNumber");
        String department = document.getString("department");
        String status = document.getString("queueStatus");

        if (token != null)
            tvMyToken.setText(token);

        if (department != null)
            tvDepartment.setText(department);

        if (status != null) {

            // Show notification only when THIS patient's status changes
            if (previousStatus.isEmpty()) {

                previousStatus = status;

            } else if (!status.equals(previousStatus)) {

                previousStatus = status;

                switch (status) {

                    case "Called":

                        NotificationHelper.showNotification(
                                requireContext(),
                                "NextTurn",
                                "It's your turn! Please proceed to the consultation room."
                        );
                        break;

                    case "Serving":

                        NotificationHelper.showNotification(
                                requireContext(),
                                "NextTurn",
                                "Your consultation has started."
                        );
                        break;

                    case "Completed":

                        NotificationHelper.showNotification(
                                requireContext(),
                                "NextTurn",
                                "Your consultation has been completed."
                        );
                        break;
                }
            }

            tvQueueStatus.setText(status);

            switch (status) {

                case "Waiting":
                    tvQueueStatus.setTextColor(
                            getResources().getColor(android.R.color.holo_orange_dark));
                    break;

                case "Called":
                    tvQueueStatus.setTextColor(
                            getResources().getColor(android.R.color.holo_blue_dark));
                    break;

                case "Serving":
                    tvQueueStatus.setTextColor(
                            getResources().getColor(android.R.color.holo_green_dark));
                    break;

                case "Completed":
                    tvQueueStatus.setTextColor(
                            getResources().getColor(android.R.color.darker_gray));
                    break;
            }
        }
    }
    private void calculateQueue(String department,
                                String myToken) {

        db.collection("patients")
                .whereEqualTo("department", department)
                .get()
                .addOnSuccessListener(query -> {

                    int ahead = 0;

                    String currentToken = "--";

                    for (DocumentSnapshot document : query.getDocuments()) {

                        String token =
                                document.getString("tokenNumber");

                        String status =
                                document.getString("queueStatus");
                        if (token == null)
                            continue;

                        if ("Called".equals(status)) {

                            currentToken = token;

                        }

                        if ("Waiting".equals(status)
                                && token.compareTo(myToken) < 0) {

                            ahead++;

                        }

                    }

                    tvCurrentToken.setText(currentToken);

                    tvPatientsAhead.setText(
                            String.valueOf(ahead));

                    int estimatedMinutes =
                            ahead * 5;

                    tvEstimatedTime.setText(
                            estimatedMinutes + " Minutes");

                });

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (queueListener != null) {
            queueListener.remove();
        }
    }
}