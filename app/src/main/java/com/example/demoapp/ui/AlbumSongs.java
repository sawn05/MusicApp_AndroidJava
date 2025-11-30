package com.example.demoapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.MyService;
import com.example.demoapp.R;
import com.example.demoapp.adapter.AlbumSongAdapter;
import com.example.demoapp.model.Song;
import com.example.demoapp.storage_data.DBHelper;

import java.util.List;

public class AlbumSongs extends AppCompatActivity {

    private TextView tvNumAlbumSong;
    private RecyclerView rvSongList;
    private ImageView btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_album_songs);

        rvSongList = findViewById(R.id.rvSongList);
        tvNumAlbumSong = findViewById(R.id.tvNumAlbumSong);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(view -> finish());


        // Cập nhật nameAlbum
        Intent intent = getIntent();
        String nameAlbum = intent.getStringExtra("albumSong");

        TextView tvAlbumTitle = findViewById(R.id.tvAlbumTitle);
        tvAlbumTitle.setText(nameAlbum);

        ImageView imgAlbum = findViewById(R.id.imgAlbum);
        imgAlbum.setImageResource(intent.getIntExtra("imgAlbum", 0));



        // Lấy dữ liệu từ SQLite
        DBHelper dbHelper = new DBHelper(this);
        List<Song> songs = dbHelper.getSongsByAlbum(nameAlbum);
        MyService.songQueue = songs;
        updateNumAlbumSongTextView(songs);



        // Truyền vào adapter
        AlbumSongAdapter adapter = new AlbumSongAdapter(songs);
        rvSongList.setLayoutManager(new LinearLayoutManager(this));
        rvSongList.setAdapter(adapter);

        LinearLayout btnHome = findViewById(R.id.btnHomeActivity);
        btnHome.setOnClickListener(view -> {
            Intent myIntent = new Intent(AlbumSongs.this, HomeActivity.class);
            startActivity(myIntent);
        });

        // --- Chuyển đến các màn khác ---
        LinearLayout btnLikeActivity = findViewById(R.id.btnLikeActivity);
        btnLikeActivity.setOnClickListener(view -> {
            Intent intent1 = new Intent(AlbumSongs.this, FavoriteSongs.class);
            startActivity(intent1);
        });

        LinearLayout btnProfileActivity = findViewById(R.id.btnProfileActivity);
        btnProfileActivity.setOnClickListener(view -> {
            Intent intent2 = new Intent(AlbumSongs.this, MyAccountActivity.class);
            startActivity(intent2);
        });
    }

    private void updateNumAlbumSongTextView(List<Song> songs) {
        int songCount = 0;
        if (songs != null) {
            songCount = songs.size();
        }

        String formattedString = getString(R.string.number_of_songs, songCount);

        tvNumAlbumSong.setText(formattedString);
    }

}