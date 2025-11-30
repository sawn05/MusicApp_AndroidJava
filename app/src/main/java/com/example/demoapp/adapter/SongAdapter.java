package com.example.demoapp.adapter;

// SongAdapter.java

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
import com.example.demoapp.model.Song;
import com.example.demoapp.ui.PlayMusic;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Song> mSongsList;
    private List<Song> mSongsListFull;

    public SongAdapter(List<Song> songsList) {
        this.mSongsList = songsList;
        // Tạo bản sao của danh sách gốc
        this.mSongsListFull = new ArrayList<>(songsList);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 1. Phồng (inflate) layout item (ví dụ: item_song.xml)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_song, parent, false);

        // 2. Trả về một thể hiện mới của ViewHolder
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        // 1. Lấy dữ liệu ở vị trí 'position'
        Song currentSong = mSongsList.get(position);

        // 2. Gán dữ liệu lên View
        holder.titleTextView.setText(currentSong.getNameSong());
        holder.artistTextView.setText(currentSong.getArtist());
        holder.imageTextView.setImageResource(currentSong.getImageSong());

        // 3. Xử lý sự kiện click item
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            // Gửi tới Service để phát nhạc
            Intent intent = new Intent(context, MyService.class);
            intent.putExtra("song_res_id", currentSong.getResId());
            intent.putExtra("songName", currentSong.getNameSong());
            intent.putExtra("artist", currentSong.getArtist());
            intent.putExtra("imageSong", currentSong.getImageSong());
            intent.setAction("ACTION_PLAY_NEW_SONG");
            context.startService(intent);

            Intent playIntent = new Intent(context, PlayMusic.class);
            playIntent.putExtra("song_res_id", currentSong.getResId());
            playIntent.putExtra("songName", currentSong.getNameSong());
            playIntent.putExtra("artist", currentSong.getArtist());
            playIntent.putExtra("imageSong", currentSong.getImageSong());
            context.startActivity(playIntent);
        });
    }

    @Override
    public int getItemCount() {
        return mSongsList.size();
    }

    public void filterList(List<Song> filteredList) {
        // Cập nhật danh sách hiển thị
        mSongsList = filteredList;
        notifyDataSetChanged();
    }

    public void restoreFullList() {
        mSongsList = mSongsListFull;
        notifyDataSetChanged();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView artistTextView;
        public ImageView imageTextView;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            imageTextView = itemView.findViewById(R.id.icon_song_art);
            titleTextView = itemView.findViewById(R.id.song_title);
            artistTextView = itemView.findViewById(R.id.song_artist);
        }
    }
}