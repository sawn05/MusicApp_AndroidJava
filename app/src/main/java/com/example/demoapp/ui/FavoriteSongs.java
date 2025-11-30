package com.example.demoapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.R;
import com.example.demoapp.adapter.AlbumSongAdapter;
import com.example.demoapp.model.Song;
import com.example.demoapp.storage_data.DBHelper;

import java.util.List;

public class FavoriteSongs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite_songs);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        RecyclerView rvFavoriteSongs = findViewById(R.id.rvFavoriteSongs);

        // Lấy dữ liệu từ SQLite
        DBHelper dbHelper = new DBHelper(this);
        List<Song> songs = dbHelper.getFavouriteSongs();

        // Truyền vào adapter
        AlbumSongAdapter adapter = new AlbumSongAdapter(songs);
        rvFavoriteSongs.setLayoutManager(new LinearLayoutManager(this));
        rvFavoriteSongs.setAdapter(adapter);

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