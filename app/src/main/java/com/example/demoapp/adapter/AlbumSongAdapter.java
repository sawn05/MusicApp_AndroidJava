package com.example.demoapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.model.Album;
import com.example.demoapp.R;
import com.example.demoapp.ui.AlbumSongsActivity;
import com.example.demoapp.ui.PlaylistSongs;

import java.util.List;

public class AlbumSongAdapter extends RecyclerView.Adapter<AlbumSongAdapter.AlbumSongViewHolder>{
    private List<Album> albumList;

    public AlbumSongAdapter(List<Album> albumList) {
        this.albumList = albumList;
    }

    @NonNull
    @Override
    public AlbumSongAdapter.AlbumSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album, parent, false);
        return new AlbumSongAdapter.AlbumSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumSongAdapter.AlbumSongViewHolder holder, int position) {
        Album album = albumList.get(position);

        holder.img.setImageResource(album.getImageResId());

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent playIntent = new Intent(context, AlbumSongsActivity.class);
            playIntent.putExtra("albumSong", album.getNameAlbum());
            playIntent.putExtra("imgAlbum", album.getImageResId());
            context.startActivity(playIntent);
        });
    }
    @Override
    public int getItemCount() {
        return albumList.size();
    }
    public static class AlbumSongViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        public AlbumSongViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgPlaylist);
        }
    }
}
