package com.example.demoapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.MyService;
import com.example.demoapp.R;
import com.example.demoapp.model.RecentSong;
import com.example.demoapp.ui.PlayMusic;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;


public class RecentSongAdapter extends RecyclerView.Adapter<RecentSongAdapter.SongViewHolder> {

    private List<RecentSong> recentSongList;

    public RecentSongAdapter(List<RecentSong> recentSongList) {
        this.recentSongList = recentSongList;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        RecentSong recentSong = recentSongList.get(position);
        holder.imgSong.setImageResource(recentSong.getImageSong());
        holder.txtNameSong.setText(recentSong.getNameSong());
        holder.txtNameAuthor.setText(recentSong.getArtist());
        holder.tvDuration.setText(recentSong.getDuration());




        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            // Gửi tới Service để phát nhạc
            Intent intent = new Intent(context, MyService.class);
            intent.putExtra("song_res_id", recentSong.getSongResId());
            intent.putExtra("songName", recentSong.getNameSong());
            intent.putExtra("artist", recentSong.getArtist());
            intent.putExtra("imageSong", recentSong.getImageSong());
            intent.setAction("ACTION_PLAY_NEW_SONG");
            context.startService(intent);

            Intent playIntent = new Intent(context, PlayMusic.class);
            playIntent.putExtra("song_res_id", recentSong.getSongResId());
            playIntent.putExtra("songName", recentSong.getNameSong());
            playIntent.putExtra("artist", recentSong.getArtist());
            playIntent.putExtra("imageSong", recentSong.getImageSong());
            context.startActivity(playIntent);
        });

    }

    @Override
    public int getItemCount() {
        return recentSongList.size();
    }
    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgSong;
        TextView txtNameSong;
        TextView txtNameAuthor;
        TextView tvDuration;
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.imgCover);
            txtNameSong = itemView.findViewById(R.id.txtNameSong);
            txtNameAuthor = itemView.findViewById(R.id.txtArtist);
            tvDuration = itemView.findViewById(R.id.txtDuration);
        }
    }
}
