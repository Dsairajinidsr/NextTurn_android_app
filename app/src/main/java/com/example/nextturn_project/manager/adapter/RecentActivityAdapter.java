package com.example.nextturn_project.manager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextturn_project.R;
import com.example.nextturn_project.manager.model.ActivityModel;

import java.util.List;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ViewHolder> {

    private final List<ActivityModel> list;

    public RecentActivityAdapter(List<ActivityModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_activity, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ActivityModel model = list.get(position);

        holder.tvToken.setText(model.getTokenNumber());
        holder.tvPatientName.setText(model.getName());
        holder.tvStatus.setText(model.getQueueStatus());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvToken, tvPatientName, tvStatus;

        ViewHolder(View itemView) {
            super(itemView);

            tvToken = itemView.findViewById(R.id.tvToken);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}