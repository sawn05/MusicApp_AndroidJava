package com.example.demoapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.R;
import com.example.demoapp.model.HistoryLog;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final Context context;
    private List<HistoryLog> historyList;

    public HistoryAdapter(Context context, List<HistoryLog> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_history_log, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryLog log = historyList.get(position);

        holder.tvEventType.setText(log.getEventType());
        holder.tvTimestamp.setText(log.getTimestamp());

        if (log.getDetails() != null && !log.getDetails().isEmpty()) {
            holder.tvEventDetails.setText(log.getDetails());
            holder.tvEventDetails.setVisibility(View.VISIBLE);
        } else {
            holder.tvEventDetails.setVisibility(View.GONE);
        }

        holder.imgEventIcon.setImageResource(R.drawable.ic_history);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateList(List<HistoryLog> newList) {
        this.historyList = newList;
        notifyDataSetChanged();
    }


    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventType;
        TextView tvTimestamp;
        TextView tvEventDetails;
        ImageView imgEventIcon;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventType = itemView.findViewById(R.id.tv_event_type);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            tvEventDetails = itemView.findViewById(R.id.tv_event_details);
            imgEventIcon = itemView.findViewById(R.id.img_event_icon);
        }
    }
}