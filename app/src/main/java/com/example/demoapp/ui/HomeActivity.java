package com.example.demoapp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.widget.TextView;
import android.view.View;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.MyService;
import com.example.demoapp.R;
import com.example.demoapp.adapter.OutstandingAdapter;
import com.example.demoapp.adapter.PlaylistAdapter;
import com.example.demoapp.adapter.RecentSongAdapter;
import com.example.demoapp.adapter.SongAdapter;
import com.example.demoapp.adapter.TrendingAdapter;
import com.example.demoapp.model.Outstanding;
import com.example.demoapp.model.Playlist;
import com.example.demoapp.model.RecentSong;
import com.example.demoapp.model.Song;
import com.example.demoapp.model.Trending;
import com.example.demoapp.storage_data.DBHelper;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    public static final String UPDATE_MINI_PLAYER = "ACTION_UPDATE_MINI_PLAYER";
    public static final String NOW_PLAYING = "ACTION_NOW_PLAYING";

    private ShapeableImageView imgMiniSong;
    private TextView tvMiniSongName, tvMiniArtist;
    private ImageView imgPlayPause, btnNextSong;
    private LinearLayout miniPlayer;

    // Dl xử lý search
    private androidx.appcompat.widget.SearchView searchView;
    private RecyclerView rvSearchResults;
    private SongAdapter songAdapter;
    private List<Song> allSongsList;



    // Receiver duy nhất cho HomeActivity
    private final BroadcastReceiver miniPlayerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UPDATE_MINI_PLAYER.equals(intent.getAction())) {
                String title = intent.getStringExtra("song_name");
                String artist = intent.getStringExtra("artist_name");
                int imageResId = intent.getIntExtra("image_res", 0);
                boolean isPlaying = intent.getBooleanExtra("is_playing", false);

                // Cập nhật MiniPlayer UI
                tvMiniSongName.setText(title != null ? title : "Chưa có bài hát");
                tvMiniArtist.setText(artist != null ? artist : "");
                imgMiniSong.setImageResource(imageResId != 0 ? imageResId : R.drawable.avatar);
                imgPlayPause.setImageResource(isPlaying ? R.drawable.pause : R.drawable.play);
            }
        }
    };

    private final BroadcastReceiver nowPlayingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NOW_PLAYING.equals(intent.getAction())) {
                // Nhận dữ liệu bài hát hiện tại
                String songName = intent.getStringExtra("songName");
                String artist = intent.getStringExtra("artist");
                int imageSong = intent.getIntExtra("imageSong", 0);
                int songResId = intent.getIntExtra("song_res_id", -1);
                boolean isPlaying = intent.getBooleanExtra("isPlaying", false);

                // Mở lại màn PlayMusic
                Intent playIntent = new Intent(HomeActivity.this, PlayMusic.class);
                playIntent.putExtra("songName", songName);
                playIntent.putExtra("artist", artist);
                playIntent.putExtra("imageSong", imageSong);
                playIntent.putExtra("song_res_id", songResId);
                playIntent.putExtra("isPlaying", isPlaying);
                startActivity(playIntent);
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ánh xạ MiniPlayer
        miniPlayer = findViewById(R.id.miniPlayer);
        imgMiniSong = findViewById(R.id.imgMiniSong);
        tvMiniSongName = findViewById(R.id.tvMiniSongName);
        tvMiniArtist = findViewById(R.id.tvMiniArtist);
        imgPlayPause = findViewById(R.id.btnMiniPlayPause);
        btnNextSong = findViewById(R.id.btnNext);

        // Đăng ký Receiver (Android 13+ yêu cầu flag rõ ràng)
        registerReceiver(miniPlayerReceiver, new IntentFilter(UPDATE_MINI_PLAYER), Context.RECEIVER_EXPORTED);
        registerReceiver(miniPlayerReceiver, new IntentFilter(NOW_PLAYING), Context.RECEIVER_EXPORTED);


        // Xử lý search
        searchView = findViewById(R.id.searchView);
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.setQueryHint("Tìm kiếm...");

        rvSearchResults = findViewById(R.id.rvSearchResults);

        allSongsList = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(this);
        allSongsList.addAll(dbHelper.getAllSongs());

        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        songAdapter = new SongAdapter(new ArrayList<>());
        rvSearchResults.setAdapter(songAdapter);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintScrollView();
                rvSearchResults.setVisibility(View.VISIBLE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                rvSearchResults.setVisibility(View.GONE);
                showScrollView();

                songAdapter.filterList(new ArrayList<>());
                return false;
            }
        });
        // B. Xử lý khi văn bản thay đổi
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSongs(newText);
                hintScrollView();
                rvSearchResults.setVisibility(View.VISIBLE);
                return true;
            }
        });










        // --- Dữ liệu Playlist ---
        RecyclerView rvPlaylist = findViewById(R.id.rvPlaylist);
        DBHelper dbHelperPlaylist = new DBHelper(this);
        List<Playlist> playlists = dbHelperPlaylist.getAllAlbums();

        PlaylistAdapter playlistAdapter = new PlaylistAdapter(playlists, R.layout.item_playlist_horizontal);
        rvPlaylist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvPlaylist.setAdapter(playlistAdapter);

        // --- Dữ liệu Trending ---
        RecyclerView rvTrending = findViewById(R.id.rvTrending);
        List<Trending> trendings = new ArrayList<>();
        trendings.add(new Trending("Âm thầm bên em", "ERIK", R.drawable.amthambenem, R.raw.amthambenem));
        trendings.add(new Trending("Người ấy", "Nguyenn", R.drawable.nguoiay, R.raw.nguoiay));
        trendings.add(new Trending("Làm vợ anh nhé", "ERIK", R.drawable.lamvoaanhnhe, R.raw.lamvoanhnhe));
        trendings.add(new Trending("Sai người sai thời điểm", "ERIK", R.drawable.sainguoisaithoidiem, R.raw.sainguoisaithoidiem));
        trendings.add(new Trending("Bình yên nơi đâu", "ERIK", R.drawable.binhyennoidau, R.raw.binhyennoidau));
        trendings.add(new Trending("Tệ thật anh nhớ em", "ERIK", R.drawable.tethatanhnhoem, R.raw.tethatanhnhoem));

        TrendingAdapter trendingAdapter = new TrendingAdapter(trendings);
        rvTrending.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvTrending.setAdapter(trendingAdapter);

        // --- Outstanding ---
        RecyclerView rvOutstanding = findViewById(R.id.rvOutstanding);
        List<Outstanding> outstandings = new ArrayList<>();
        outstandings.add(new Outstanding("Dù cho tận thế", R.drawable.duchotanthe));
        outstandings.add(new Outstanding("Nơi này có anh", R.drawable.noinaycoanh));
        outstandings.add(new Outstanding("Anh sai rồi", R.drawable.anhsairoi));
        OutstandingAdapter outstandingAdapter = new OutstandingAdapter(outstandings);
        rvOutstanding.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvOutstanding.setAdapter(outstandingAdapter);

        // --- Recent songs ---
        RecyclerView rvRecentSong = findViewById(R.id.rvRecentSong);
        List<RecentSong> recentSongs = new ArrayList<>();
        recentSongs.add(new RecentSong(R.drawable.lamvoaanhnhe, "Làm vợ anh nhé", "Chi Dân", "3:20", R.raw.lamvoanhnhe));
        recentSongs.add(new RecentSong(R.drawable.noinaycoanh, "Nơi này có anh", "Sơn Tùng MTP", "3:45", R.raw.noinaycoanh));
        recentSongs.add(new RecentSong(R.drawable.nguoiay, "Người ấy", "Trịnh Thanh Bình", "3:05", R.raw.nguoiay));
        recentSongs.add(new RecentSong(R.drawable.nhungngaymua, "Những ngày mưa", "Gia Bảo", "3:45", R.raw.nhungngaymua));

        RecentSongAdapter recentSongAdapter = new RecentSongAdapter(recentSongs);
        rvRecentSong.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvRecentSong.setAdapter(recentSongAdapter);

        // Kiểm tra xem có bài hát nào đang phát không, nếu có thì hiển thị MiniPlayer
        setminiPlayerVisibility();

        // --- Chuyển đến các màn khác ---
        LinearLayout btnLikeActivity = findViewById(R.id.btnLikeActivity);
        btnLikeActivity.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, FavoriteSongs.class);
            startActivity(intent);
        });

        LinearLayout btnProfileActivity = findViewById(R.id.btnProfileActivity);
        btnProfileActivity.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, MyAccountActivity.class);
            startActivity(intent);
        });

        // --- Mở PlayMusic khi bấm MiniPlayer ---
        LinearLayout miniPlayer = findViewById(R.id.miniPlayer);
        miniPlayer.setOnClickListener(v -> {
            Intent requestIntent = new Intent(HomeActivity.this, MyService.class);
            requestIntent.setAction("ACTION_REQUEST_NOW_PLAYING");
            startService(requestIntent);
        });


        imgPlayPause.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MyService.class);
                intent.setAction("ACTION_TOGGLE_PLAY_PAUSE");
            startService(intent);
        });

        btnNextSong.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MyService.class);
            intent.setAction("ACTION_NEXT_SONG");
            startService(intent);
        });




    }

    private void setminiPlayerVisibility() {
        Intent intentPlaying = getIntent();
        boolean isPlaying = intentPlaying.getBooleanExtra("isPlaying", true);
        if (MyService.getMediaPlayer() != null && isPlaying) {
            miniPlayer.setVisibility(View.VISIBLE);
        } else {
            miniPlayer.setVisibility(View.GONE);
        }
    }

    private void hintScrollView() {
        TextView tvPlaylistTitle = findViewById(R.id.tvPlaylistTitle);
        TextView tvTrending = findViewById(R.id.txtTrending);
        TextView tvOutstanding = findViewById(R.id.txtOutstanding);
        TextView tvRecentSong = findViewById(R.id.txtRecentSong);


        RecyclerView rvPlaylist = findViewById(R.id.rvPlaylist);
        RecyclerView rvTrending = findViewById(R.id.rvTrending);
        RecyclerView rvOutstanding = findViewById(R.id.rvOutstanding);
        RecyclerView rvRecentSong = findViewById(R.id.rvRecentSong);



        rvPlaylist.setVisibility(View.GONE);
        rvTrending.setVisibility(View.GONE);
        rvOutstanding.setVisibility(View.GONE);
        rvRecentSong.setVisibility(View.GONE);


        tvPlaylistTitle.setVisibility(View.GONE);
        tvTrending.setVisibility(View.GONE);
        tvOutstanding.setVisibility(View.GONE);
        tvRecentSong.setVisibility(View.GONE);
    }

    private void showScrollView() {
        TextView tvPlaylistTitle = findViewById(R.id.tvPlaylistTitle);
        TextView tvTrending = findViewById(R.id.txtTrending);
        TextView tvOutstanding = findViewById(R.id.txtOutstanding);
        TextView tvRecentSong = findViewById(R.id.txtRecentSong);


        RecyclerView rvPlaylist = findViewById(R.id.rvPlaylist);
        RecyclerView rvTrending = findViewById(R.id.rvTrending);
        RecyclerView rvOutstanding = findViewById(R.id.rvOutstanding);
        RecyclerView rvRecentSong = findViewById(R.id.rvRecentSong);



        rvPlaylist.setVisibility(View.VISIBLE);
        rvTrending.setVisibility(View.VISIBLE);
        rvOutstanding.setVisibility(View.VISIBLE);
        rvRecentSong.setVisibility(View.VISIBLE);


        tvPlaylistTitle.setVisibility(View.VISIBLE);
        tvTrending.setVisibility(View.VISIBLE);
        tvOutstanding.setVisibility(View.VISIBLE);
        tvRecentSong.setVisibility(View.VISIBLE);
    }




    // Hàm logic lọc dữ liệu (giữ nguyên logic In-memory Filtering)
    private void filterSongs(String query) {
        if (query == null || query.isEmpty()) {
            songAdapter = new SongAdapter(allSongsList);
            rvSearchResults.setAdapter(songAdapter);
            return;
        }

        String lowerCaseQuery = query.toLowerCase(Locale.getDefault());
        List<Song> filteredList = new ArrayList<>();

        for (Song song : allSongsList) {
            boolean titleMatches = song.getNameSong().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery);
            boolean artistMatches = song.getArtist().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery);

            if (titleMatches || artistMatches) {
                filteredList.add(song);
            }
        }

        // Cập nhật Adapter của RecyclerView kết quả tìm kiếm
        songAdapter.filterList(filteredList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(miniPlayerReceiver);
            unregisterReceiver(nowPlayingReceiver);
        } catch (Exception ignored) {}
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filterUpdate = new IntentFilter(UPDATE_MINI_PLAYER);
        registerReceiver(miniPlayerReceiver, filterUpdate, Context.RECEIVER_EXPORTED);

        IntentFilter filterNowPlaying = new IntentFilter(NOW_PLAYING);
        registerReceiver(nowPlayingReceiver, filterNowPlaying, Context.RECEIVER_EXPORTED);

        setminiPlayerVisibility();

        // Hỏi lại trạng thái hiện tại khi quay lại màn hình
        Intent requestIntent = new Intent(this, MyService.class);
        requestIntent.setAction("ACTION_REQUEST_STATUS");
        startService(requestIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(miniPlayerReceiver);
        unregisterReceiver(nowPlayingReceiver);
    }
}
