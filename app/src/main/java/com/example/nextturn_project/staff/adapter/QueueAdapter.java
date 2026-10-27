package com.example.nextturn_project.staff.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextturn_project.R;
import com.example.nextturn_project.staff.model.PatientModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<PatientModel> patientList;

    private final FirebaseFirestore db =
            FirebaseFirestore.getInstance();

    public QueueAdapter(Context context,
                        ArrayList<PatientModel> patientList) {

        this.context = context;
        this.patientList = patientList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_queue,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {

        PatientModel patient = patientList.get(position);

        holder.tvPatientName.setText(patient.getPatientName());

        holder.tvToken.setText(
                "Token : " + patient.getTokenNumber());

        holder.tvDepartment.setText(
                patient.getDepartment());

        holder.tvStatus.setText(
                patient.getQueueStatus());

        switch (patient.getQueueStatus()) {

            case "Waiting":

                holder.tvStatus.setTextColor(
                        Color.parseColor("#FF9800"));

                break;

            case "Called":

                holder.tvStatus.setTextColor(
                        Color.BLUE);

                break;

            case "Serving":

                holder.tvStatus.setTextColor(
                        Color.GREEN);

                break;

            case "Completed":

                holder.tvStatus.setTextColor(
                        Color.GRAY);

                break;
        }

        holder.btnCall.setOnClickListener(v ->

                updateStatus(
                        patient.getPatientId(),
                        "Called"));

        holder.btnServe.setOnClickListener(v ->

                updateStatus(
                        patient.getPatientId(),
                        "Serving"));

        holder.btnComplete.setOnClickListener(v ->

                updateStatus(
                        patient.getPatientId(),
                        "Completed"));

    }

    private void updateStatus(String patientId,
                              String status) {

        db.collection("patients")
                .document(patientId)
                .update("queueStatus", status);

    }

    @Override
    public int getItemCount() {

        return patientList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPatientName,
                tvToken,
                tvDepartment,
                tvStatus;

        Button btnCall,
                btnServe,
                btnComplete;

        ViewHolder(View itemView) {

            super(itemView);

            tvPatientName =
                    itemView.findViewById(R.id.tvPatientName);

            tvToken =
                    itemView.findViewById(R.id.tvToken);

            tvDepartment =
                    itemView.findViewById(R.id.tvDepartment);

            tvStatus =
                    itemView.findViewById(R.id.tvStatus);

            btnCall =
                    itemView.findViewById(R.id.btnCallNext);

            btnServe =
                    itemView.findViewById(R.id.btnServe);

            btnComplete =
                    itemView.findViewById(R.id.btnComplete);

        }
    }
}