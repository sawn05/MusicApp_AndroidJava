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

import com.example.demoapp.model.Playlist;
import com.example.demoapp.R;
import com.example.demoapp.storage_data.DBHelper;
import com.example.demoapp.ui.AlbumSongs;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<Playlist> playlistList;
    private static int layoutId;

    public PlaylistAdapter(List<Playlist> playlistList, int layoutId) {
        this.playlistList = playlistList;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlistList.get(position);
        holder.tvName.setText(playlist.getName());
        holder.img.setImageResource(playlist.getImageResId());

        // Nếu layout là item_playlist_vertical, hiển thị số bài hát
        if (layoutId == R.layout.item_playlist_vertical) {
            Context context = holder.itemView.getContext();
            DBHelper dbHelper = new DBHelper(context);
            holder.tvNumSong.setText(dbHelper.getNumSongByAlbum(playlist.getName()) + " bài hát");
        }

        // Xử lý sự kiện khi người dùng nhấn vào item
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();


            // Đồng thời mở màn hình Album
            Intent playIntent = new Intent(context, AlbumSongs.class);
            playIntent.putExtra("albumSong", playlist.getName());
            playIntent.putExtra("imgAlbum", playlist.getImageResId());

            context.startActivity(playIntent);
        });
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvNumSong;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgPlaylist);
            tvName = itemView.findViewById(R.id.tvNamePlaylist);
            if (layoutId == R.layout.item_playlist_vertical) {
                tvNumSong = itemView.findViewById(R.id.tvNumSong);
            }
        }
    }

}
