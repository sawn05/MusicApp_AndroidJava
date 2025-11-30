package com.example.demoapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.model.Outstanding;
import com.example.demoapp.R;

import java.util.List;

public class OutstandingAdapter extends RecyclerView.Adapter<OutstandingAdapter.OutstandingViewHolder>{
    private List<Outstanding> outstandingList;

    public OutstandingAdapter(List<Outstanding> outstandingList) {
        this.outstandingList = outstandingList;
    }

    @NonNull
    @Override
    public OutstandingAdapter.OutstandingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_outstanding, parent, false);
        return new OutstandingAdapter.OutstandingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutstandingAdapter.OutstandingViewHolder holder, int position) {
        Outstanding outstanding = outstandingList.get(position);

        holder.img.setImageResource(outstanding.getImgPlaylist());
    }
    @Override
    public int getItemCount() {
        return outstandingList.size();
    }
    public static class OutstandingViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        public OutstandingViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgPlaylist);
        }
    }
}
