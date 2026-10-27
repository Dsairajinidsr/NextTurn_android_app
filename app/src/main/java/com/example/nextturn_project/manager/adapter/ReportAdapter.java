package com.example.nextturn_project.manager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextturn_project.R;
import com.example.nextturn_project.manager.model.ReportModel;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private final ArrayList<ReportModel> reportList;

    public ReportAdapter(ArrayList<ReportModel> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);

        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {

        ReportModel model = reportList.get(position);

        holder.tvPatientName.setText(model.getName());
        holder.tvToken.setText("Token : " + model.getTokenNumber());
        holder.tvDepartment.setText("Department : " + model.getDepartment());
        holder.tvStatus.setText(model.getQueueStatus());
        holder.tvDate.setText(model.getVisitDate());

    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {

        TextView tvPatientName;
        TextView tvToken;
        TextView tvDepartment;
        TextView tvStatus;
        TextView tvDate;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvToken = itemView.findViewById(R.id.tvToken);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}