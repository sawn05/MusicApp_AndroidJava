package com.example.demoapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.MyService;
import com.example.demoapp.R;
import com.example.demoapp.adapter.SongAdapter;
import com.example.demoapp.model.Song;
import com.example.demoapp.storage_data.DBHelper;

import java.util.List;
import java.util.Random;

public class AlbumSongsActivity extends AppCompatActivity {

    private TextView tvNumAlbumSong;
    private RecyclerView rvSongList;
    private ImageView btnBack;
    private Button btnShuffle;

    private List<Song> songs;
    private String nameAlbum;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_album_songs);

        // Ánh xạ view
        rvSongList = findViewById(R.id.rvSongList);
        tvNumAlbumSong = findViewById(R.id.tvNumAlbumSong);
        btnBack = findViewById(R.id.btnBack);
        btnShuffle = findViewById(R.id.btnShuffle);

        // Nút quay lại
        btnBack.setOnClickListener(view -> finish());

        // Lấy dữ liệu Intent (tên album + ảnh)
        Intent intent = getIntent();
        nameAlbum = intent.getStringExtra("albumSong");
        int imgRes = intent.getIntExtra("imgAlbum", 0);

        TextView tvAlbumTitle = findViewById(R.id.tvAlbumTitle);
        tvAlbumTitle.setText(nameAlbum);

        ImageView imgAlbum = findViewById(R.id.imgAlbum);
        if (imgRes != 0) {
            imgAlbum.setImageResource(imgRes);
        }

        // Lấy dữ liệu bài hát từ SQLite
        dbHelper = new DBHelper(this);
        songs = dbHelper.getSongsByAlbum(nameAlbum);

        // Gán queue cho MyService để service biết danh sách hiện tại
        MyService.songQueue = songs;

        // Cập nhật số lượng bài hát
        updateNumAlbumSongTextView(songs);

        // Set RecyclerView
        SongAdapter adapter = new SongAdapter(songs);
        rvSongList.setLayoutManager(new LinearLayoutManager(this));
        rvSongList.setAdapter(adapter);

        // Bottom nav: Home
        LinearLayout btnHome = findViewById(R.id.btnHomeActivity);
        btnHome.setOnClickListener(view -> {
            Intent myIntent = new Intent(AlbumSongsActivity.this, HomeActivity.class);
            startActivity(myIntent);
        });

        // Bottom nav: Favorite
        LinearLayout btnLikeActivity = findViewById(R.id.btnLikeActivity);
        btnLikeActivity.setOnClickListener(view -> {
            Intent intent1 = new Intent(AlbumSongsActivity.this, FavoriteSongs.class);
            startActivity(intent1);
        });

        // Bottom nav: Profile
        LinearLayout btnProfileActivity = findViewById(R.id.btnProfileActivity);
        btnProfileActivity.setOnClickListener(view -> {
            Intent intent2 = new Intent(AlbumSongsActivity.this, MyAccountActivity.class);
            startActivity(intent2);
        });

        // ===== NÚT PHÁT NGẪU NHIÊN =====
        btnShuffle.setOnClickListener(v -> {
            if (songs == null || songs.isEmpty()) {
                Toast.makeText(AlbumSongsActivity.this,
                        "Album này chưa có bài hát", Toast.LENGTH_SHORT).show();
                return;
            }

            Random random = new Random();
            int randomIndex = random.nextInt(songs.size()); // 0 -> size-1

            playSongAt(randomIndex);
        });
    }

    // Cập nhật text "x bài hát"
    private void updateNumAlbumSongTextView(List<Song> songs) {
        int songCount = 0;
        if (songs != null) {
            songCount = songs.size();
        }

        String formattedString = getString(R.string.number_of_songs, songCount);
        tvNumAlbumSong.setText(formattedString);
    }

    // Phát bài hát tại vị trí index trong album
    private void playSongAt(int index) {
        if (songs == null || songs.isEmpty() || index < 0 || index >= songs.size()) {
            return;
        }

        Song song = songs.get(index);

        // Đặt queue cho service (nếu bạn dùng Next/Previous)
        MyService.songQueue = songs;

        // Gửi đúng ACTION mà MyService đang xử lý
        Intent serviceIntent = new Intent(this, MyService.class);
        serviceIntent.setAction("ACTION_PLAY_NEW_SONG");

        serviceIntent.putExtra("song_res_id", song.getResId());
        serviceIntent.putExtra("songName", song.getNameSong());
        serviceIntent.putExtra("artist", song.getArtist());
        serviceIntent.putExtra("imageSong", song.getImageSong());
        serviceIntent.putExtra("isFavorite", song.isFavorite()); // hoặc isFavorite(), tùy class Song

        startService(serviceIntent);

        Toast.makeText(this,
                "Đang phát ngẫu nhiên: " + song.getNameSong(),
                Toast.LENGTH_SHORT).show();
    }
}
