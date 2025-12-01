package com.example.demoapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.MyService;
import com.example.demoapp.R;
import com.example.demoapp.adapter.FavouriteAdapter;
import com.example.demoapp.model.Song;
import com.example.demoapp.storage_data.DBHelper;

import java.util.List;

public class FavoriteSongs extends AppCompatActivity {

    private List<Song> songs;           // giữ list favorite để play all
    private ImageView btnPlayAllFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite_songs);

        RecyclerView rvFavoriteSongs = findViewById(R.id.rvFavoriteSongs);

        // Lấy dữ liệu từ SQLite
        DBHelper dbHelper = new DBHelper(this);
        songs = dbHelper.getFavouriteSongs();

        // Truyền vào adapter
        FavouriteAdapter adapter = new FavouriteAdapter(songs);
        rvFavoriteSongs.setLayoutManager(new LinearLayoutManager(this));
        rvFavoriteSongs.setAdapter(adapter);

        // === NÚT PHÁT TẤT CẢ BÀI YÊU THÍCH ===
        btnPlayAllFavorite = findViewById(R.id.btnPlayAllFavorite); // id trong XML
        btnPlayAllFavorite.setOnClickListener(v -> {
            if (songs == null || songs.isEmpty()) {
                Toast.makeText(this, "Không có bài hát yêu thích nào.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gán queue cho MyService
            MyService.songQueue = songs;
            MyService.currentSongIndex = 0; // bắt đầu từ bài đầu tiên

            Song first = songs.get(0);

            // Gửi Intent phát bài đầu tiên
            Intent serviceIntent = new Intent(this, MyService.class);
            serviceIntent.setAction(MyService.ACTION_PLAY_NEW_SONG);
            serviceIntent.putExtra("song_res_id", first.getResId());
            serviceIntent.putExtra("songName", first.getNameSong());
            serviceIntent.putExtra("artist", first.getArtist());
            serviceIntent.putExtra("imageSong", first.getImageSong());
            serviceIntent.putExtra("isFavorite", first.isFavorite());
            startService(serviceIntent);

            // Mở màn hình PlayMusic
            Intent playIntent = new Intent(this, PlayMusic.class);
            playIntent.putExtra("song_res_id", first.getResId());
            playIntent.putExtra("songName", first.getNameSong());
            playIntent.putExtra("artist", first.getArtist());
            playIntent.putExtra("imageSong", first.getImageSong());
            playIntent.putExtra("isPlaying", true);
            startActivity(playIntent);
        });

        // ==== Bottom nav ====
        LinearLayout btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(view -> {
            Intent intent = new Intent(FavoriteSongs.this, HomeActivity.class);
            startActivity(intent);
        });

        LinearLayout btnProfileActivity = findViewById(R.id.btnProfileActivity);
        btnProfileActivity.setOnClickListener(view -> {
            Intent intent = new Intent(FavoriteSongs.this, MyAccountActivity.class);
            startActivity(intent);
        });
    }
}
