package com.example.demoapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.MyService;
import com.example.demoapp.R;
import com.example.demoapp.model.SessionManager;
import com.example.demoapp.model.Song;
import com.example.demoapp.storage_data.DBHelper;
import com.example.demoapp.ui.PlayMusic;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.SongViewHolder> {

    private List<Song> songs;
    public FavouriteAdapter(List<Song> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_songs, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.imgSongCover.setImageResource(song.getImageSong());
        holder.tvSongName.setText(song.getNameSong());
        holder.tvSongArtist.setText(song.getArtist());
        holder.tvSongDuration.setText(song.getDuration());

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();

            // Gửi tới Service để phát nhạc
            Intent intent = new Intent(context, MyService.class);
            intent.putExtra("song_res_id", song.getResId());
            intent.putExtra("songName", song.getNameSong());
            intent.putExtra("artist", song.getArtist());
            intent.putExtra("imageSong", song.getImageSong());
            intent.setAction("ACTION_PLAY_NEW_SONG");
            context.startService(intent);

            // Mở màn hình PlayMusic — PHẢI truyền cùng dữ liệu
            Intent playIntent = new Intent(context, PlayMusic.class);
            playIntent.putExtra("song_res_id", song.getResId());
            playIntent.putExtra("songName", song.getNameSong());
            playIntent.putExtra("artist", song.getArtist());
            playIntent.putExtra("imageSong", song.getImageSong());
            context.startActivity(playIntent);
        });




        if (song.isFavorite()) {
            holder.isFavourite.setImageResource(R.drawable.heart_fill);
        } else {
            holder.isFavourite.setImageResource(R.drawable.heart);
        }

        holder.isFavourite.setOnClickListener(v -> {
            boolean newState = !song.isFavorite();
            song.setFavorite(newState);

            // Đổi icon theo trạng thái mới
            if (newState) {
                holder.isFavourite.setImageResource(R.drawable.heart_fill);
                Toast.makeText(v.getContext(), "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                holder.isFavourite.setImageResource(R.drawable.heart);
                Toast.makeText(v.getContext(), "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }

            // Cập nhật xuống SQLite
            DBHelper dbHelper = new DBHelper(v.getContext());
            int favouriteValue = newState ? 1 : 0;
            dbHelper.updateFavourite(song.getId(), favouriteValue);

            // Update usage history
            String username = SessionManager.getInstance().getUsername();
            dbHelper.recordEvent(username, "FAVORITES", "Đã thêm bài hát " + song.getNameSong() + " vào danh sách yêu thích");
        });
    }


    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSongCover, isFavourite;
        TextView tvSongName, tvSongArtist, tvSongDuration;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSongCover = itemView.findViewById(R.id.imgSongCover);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            tvSongArtist = itemView.findViewById(R.id.tvSongArtist);
            tvSongDuration = itemView.findViewById(R.id.tvSongDuration);
            isFavourite = itemView.findViewById(R.id.isLiked);
        }
    }
}

