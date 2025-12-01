package com.example.demoapp.ui;



import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import com.example.demoapp.model.SessionManager;
import com.example.demoapp.storage_data.DBHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demoapp.MyService;
import com.example.demoapp.R;
import com.google.android.material.imageview.ShapeableImageView;

public class PlayMusic extends AppCompatActivity {
    private ImageView imgPlay, imgBack, imgSong;
    private Boolean flag;
    private ObjectAnimator rotateAnim;

    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    TextView tvStartTime, tvEndTime, tvDuration;

    private TextView tvSongName, tvArtist;
    private ImageView imgSongCover;

    private ImageView imgRepeat, showOptionSong;
    private boolean isRepeating = false;

    private MyService myService;
    private boolean isBound = false;

    private ServiceConnection connection;

    private BroadcastReceiver uiUpdateReceiver;

    private static final String ACTION_UPDATE_UI = MyService.ACTION_UPDATE_UI;
    private static final String MINI_PLAYER_ACTION = "ACTION_UPDATE_MINI_PLAYER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play_music);


        // Ánh xạ id
        seekBar = findViewById(R.id.seekBar);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);

        tvSongName = findViewById(R.id.tvNameSong);
        tvArtist = findViewById(R.id.tvNameAuthor);
        imgSongCover = findViewById(R.id.imgCover);
        setupUIUpdateReceiver();



        // Lấy dữ liệu được truyền từ Adapter
        Intent intent = getIntent();
        int songResId = intent.getIntExtra("song_res_id", -1);
        String songName = intent.getStringExtra("songName");
        String artist = intent.getStringExtra("artist");
        String duration = intent.getStringExtra("duration");
        int imageRes = intent.getIntExtra("imageSong", 0);

        TextView tvSong = findViewById(R.id.tvNameSong);
        TextView tvArtist = findViewById(R.id.tvNameAuthor);
        tvDuration = findViewById(R.id.tvDuration);
        ShapeableImageView imgCover = findViewById(R.id.imgCover);

        tvSong.setText(songName);
        tvArtist.setText("Ca sĩ - " + artist);


        imgCover.setImageResource(imageRes);


        imgRepeat = findViewById(R.id.ic_repeat);
        imgRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRepeatMode();

                String username = SessionManager.getInstance().getUsername();
                DBHelper dbHelper = new DBHelper(v.getContext());
                dbHelper.recordEvent(username, "Tạo vòng lặp", "Đã tạo vòng lặp cho bài hát " + songName);
                dbHelper.close();
            }
        });

        showOptionSong = findViewById(R.id.showOptionSong);
        showOptionSong.setOnClickListener(v -> {
            showSongOptionsDialog(v.getContext(), songName, songResId);
        });

        // Lấy MediaPlayer đang phát từ Service
        boolean isPlaying = intent.getBooleanExtra("isPlaying", false);
        int songResId1 = intent.getIntExtra("song_res_id", -1);

        if (MyService.getMediaPlayer() != null && isPlaying) {
            // Đã có bài hát đang phát
            mediaPlayer = MyService.getMediaPlayer();
            setupSeekBar(mediaPlayer);
        } else {
            // Chưa có — tạo mới, nhưng chờ Service khởi động xong
            Intent serviceIntent = new Intent(this, MyService.class);
            serviceIntent.setAction("ACTION_PLAY_NEW_SONG");
            serviceIntent.putExtra("song_res_id", songResId1);
            serviceIntent.putExtra("songName", songName);
            serviceIntent.putExtra("artist", artist);
            serviceIntent.putExtra("imageSong", imageRes);
            startService(serviceIntent);

            // Đợi Service khởi tạo MediaPlayer xong (sau 400–600ms)
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (MyService.getMediaPlayer() != null) {
                    mediaPlayer = MyService.getMediaPlayer();
                    setupSeekBar(mediaPlayer);
                } else {
                    Toast.makeText(this, "Đang khởi tạo nhạc...", Toast.LENGTH_SHORT).show();
                }
            }, 600);
        }




        // Mặc định đang phát
        imgPlay = findViewById(R.id.imgPlay);
        imgPlay.setImageResource(R.drawable.ic_pause);
        flag = true;


        // Khởi tạo animation quay
        rotateAnim = ObjectAnimator.ofFloat(imgCover, "rotation", 0f, 360f);
        rotateAnim.setDuration(10000);
        rotateAnim.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnim.setInterpolator(new LinearInterpolator());
        rotateAnim.start();

        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(view -> finish());

        // Xử lý Play/Pause
        imgPlay.setOnClickListener(v -> {
            Intent toggleIntent = new Intent(PlayMusic.this, MyService.class);
            toggleIntent.setAction("ACTION_TOGGLE_PLAY_PAUSE");
            startService(toggleIntent);

            if (flag) {
                imgPlay.setImageResource(R.drawable.ic_play);
                rotateAnim.pause();
            } else {
                imgPlay.setImageResource(R.drawable.ic_pause);
                rotateAnim.resume();
            }
            flag = !flag;
        });

        // Xử lý Next
        ImageView btnNextPlay = findViewById(R.id.btnNextPlay);

        btnNextPlay.setOnClickListener(v -> {
            Intent nextIntent = new Intent(this, MyService.class);
            nextIntent.setAction(MyService.ACTION_NEXT);
            startService(nextIntent);

            // Cập nhật........
        });


        ImageView btnPrevious = findViewById(R.id.btnPrevious);

        btnPrevious.setOnClickListener(v -> {
            // 1. Tạo Intent và thiết lập ACTION_PREVIOUS
            Intent previousIntent = new Intent(this, MyService.class);

            // ĐỊNH NGHĨA ACTION: Sử dụng chuỗi hằng số duy nhất
            previousIntent.setAction(MyService.ACTION_PREVIOUS);

            // 2. Gửi lệnh tới Service
            startService(previousIntent);
        });
    }

    private void setupUIUpdateReceiver() {
        uiUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ACTION_UPDATE_UI.equals(intent.getAction())) {

                    // Trích xuất dữ liệu mới
                    String songName = intent.getStringExtra(MyService.EXTRA_SONG_NAME);
                    String artist = intent.getStringExtra(MyService.EXTRA_ARTIST);
                    int imageResId = intent.getIntExtra(MyService.EXTRA_IMAGE_SONG, R.drawable.nguoiay);
                    // int songResId = intent.getIntExtra(MyService.EXTRA_SONG_RES_ID, 0);

                    // Cập nhật giao diện người dùng
                    tvSongName.setText(songName);
                    tvArtist.setText("Ca sĩ - " + artist);
                    imgSongCover.setImageResource(imageResId);

                    // TODO: Cập nhật trạng thái nút Play/Pause nếu cần
                }
            }
        };
    }

    private void toggleRepeatMode() {
        isRepeating = !isRepeating;

        // 1. Cập nhật Icon
        if (isRepeating) {
            imgRepeat.setImageResource(R.drawable.ic_repeat_on);
        } else {
            imgRepeat.setImageResource(R.drawable.ic_repeat);
        }

        setMediaPlayerRepeatMode(isRepeating);
    }

    private void setMediaPlayerRepeatMode(boolean repeat) {
        MediaPlayer mediaPlayer = MyService.getMediaPlayer();

        if (mediaPlayer != null) {
            mediaPlayer.setLooping(repeat);

            if (repeat) {
                Toast.makeText(this, "Chế độ lặp lại: Bật", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Chế độ lặp lại: Tắt", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không có nhạc đang phát.", Toast.LENGTH_SHORT).show();
        }
    }
    private Handler handler = new Handler(Looper.getMainLooper());

    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mp = MyService.getMediaPlayer();
                if (mp != null && mp.isPlaying()) {
                    int currentPos = mp.getCurrentPosition();
                    seekBar.setProgress(currentPos);
                    tvStartTime.setText(formatTime(currentPos));
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }







    private String formatTime(int millis) {
        int minutes = (millis / 1000) / 60;
        int seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


    private void setupSeekBar(MediaPlayer player) {
        if (player == null) return;

        int duration = player.getDuration();
        tvEndTime.setText(formatTime(duration));
        tvDuration.setText("Thời gian · " + formatTime(duration));


        seekBar.setMax(duration);

        updateSeekBar();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tvStartTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int newPosition = seekBar.getProgress();
                Intent seekIntent = new Intent(PlayMusic.this, MyService.class);
                seekIntent.setAction("ACTION_SEEK_TO");
                seekIntent.putExtra("seek_position", newPosition);
                startService(seekIntent);
            }
        });
    }

    private void showSongOptionsDialog(Context context, String nameSong, int songResId) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);

        View bottomSheetView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_song_options, null);

        TextView tvTitle = bottomSheetView.findViewById(R.id.tv_dialog_song_title);
        tvTitle.setText(nameSong);

        bottomSheetView.findViewById(R.id.option_add_to_playlist).setOnClickListener(v -> {
            final BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(context);

            View bottomSheetView1 = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_add_to_playlist, null);

            bottomSheetView1.findViewById(R.id.chillMoiNgay).setOnClickListener(v1 -> {
                DBHelper dbHelper = new DBHelper(context);

                String playlistName = "Chill mỗi ngày";
                long playlistId = dbHelper.getPlaylistIdByName(playlistName);

                if (playlistId != -1) {
                    long currentSongId = dbHelper.getSongIdByResId(songResId);

                    boolean success = dbHelper.addSongToPlaylist(playlistId, currentSongId);

                    if (success) {
                        Toast.makeText(context, "Đã thêm " + nameSong + " vào Chill mỗi ngày", Toast.LENGTH_SHORT).show();
                        String username = SessionManager.getInstance().getUsername();
                        dbHelper.recordEvent(username, "PLAYLIST", "Đã thêm bài hát " + nameSong + " vào Playlist");
                        dbHelper.close();
                    } else {
                        Toast.makeText(context,  nameSong + " đã có trong Playlist này.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, " Playlist \"Chill mỗi ngày\" không tồn tại.", Toast.LENGTH_SHORT).show();
                }

                bottomSheetDialog1.dismiss();
                bottomSheetDialog.dismiss();
            });

            bottomSheetView1.findViewById(R.id.nhacTapGym).setOnClickListener(v1 -> {
                // ... Code thêm bài hát vào DB ...

                Toast.makeText(context, "Đã thêm " + nameSong + " vào Nhạc tập gym", Toast.LENGTH_SHORT).show();
                String username = SessionManager.getInstance().getUsername();

                DBHelper dbHelper = new DBHelper(context);
                dbHelper.recordEvent(username, "PLAYLIST", "Đã thêm bài hát " + nameSong + " vào Playlist");
                dbHelper.close();
                bottomSheetDialog1.dismiss();
                bottomSheetDialog.dismiss();
            });

            bottomSheetView1.findViewById(R.id.tvCreatePlaylist).setOnClickListener(v1 -> {
                // ... Code thêm ds phát mới ...
                showCreatePlaylistDialog(context);
            });

            bottomSheetDialog1.setContentView(bottomSheetView1);
            bottomSheetDialog1.show();
        });

        // Thêm vào Yêu thích
        bottomSheetView.findViewById(R.id.option_add_to_favorite).setOnClickListener(v -> {
            // Code thêm........

            DBHelper dbHelper = new DBHelper(v.getContext());
            int favouriteValue = dbHelper.chekFavouriteSong(songResId) ? 1 : 0;
            Log.d("favouriteValue", "favouriteValue: " + favouriteValue);

            if (favouriteValue == 0) {
                dbHelper.updateFavourite(songResId, favouriteValue);

                String username = SessionManager.getInstance().getUsername();
                dbHelper.recordEvent(username, "PLAYLIST", "Đã thêm bài hát " + nameSong + " vào Playlist");
                dbHelper.close();
                Toast.makeText(context, "Đã thêm " + nameSong + " vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context, "Bài hát " + nameSong + " đã có trong danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
            bottomSheetDialog.dismiss();
        });

        // Tạo vòng lặp cho bài hát
        bottomSheetView.findViewById(R.id.option_view_artist).setOnClickListener(v -> {
            // Code thêm......

            Toast.makeText(context, "Đã chọn: Tạo vòng lặp thành công ", Toast.LENGTH_SHORT).show();

            // Update usage history
            String username = SessionManager.getInstance().getUsername();
            DBHelper dbHelper = new DBHelper(v.getContext());
            dbHelper.recordEvent(username, "Tạo vòng lặp", "Đã tạo vòng lặp cho bài hát " + nameSong);
            dbHelper.close();

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }



    private void showCreatePlaylistDialog(Context context) {
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_create_playlist, null);

        final EditText etPlaylistName = dialogView.findViewById(R.id.etPlaylistName);

        final String username = SessionManager.getInstance().getUsername();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setCancelable(true);

        builder.setPositiveButton("Lưu", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnShowListener(dialogInterface -> {

            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            saveButton.setOnClickListener(v -> {
                String newName = etPlaylistName.getText().toString().trim();
                final DBHelper dbHelper = new DBHelper(context);

                // 5. VALIDATION: Kiểm tra tên trống
                if (newName.isEmpty()) {
                    Toast.makeText(context, "Tên Playlist không được để trống.", Toast.LENGTH_SHORT).show();
                    etPlaylistName.setError("Không được để trống");
                    return;
                }

                long newId = dbHelper.addPlaylist(newName);

                if (newId != -1) {
                    Toast.makeText(context, "✅ Đã tạo Playlist \"" + newName + "\" thành công!", Toast.LENGTH_SHORT).show();

                    dbHelper.recordEvent(username, "CREATE_PLAYLIST", "Đã tạo Playlist mới: " + newName);

                    // TODO: Cập nhật danh sách Playlist hiển thị trong BottomSheetDialog 1

                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Tên Playlist đã tồn tại hoặc lỗi.", Toast.LENGTH_SHORT).show();
                    etPlaylistName.setError("Tên đã tồn tại");
                }
            });
        });
    }



    private void updateDuration(TextView tvDuration) {
        if (isBound && myService != null) {
            String currentSongName = myService.getCurrentSongName();
            int totalDurationMs = myService.getTotalDuration();

            if (currentSongName != null) {
                String formattedDuration = formatTime(totalDurationMs);
                String fullDisplay = "Thời gian · " + formattedDuration;

                tvDuration.setText(fullDisplay);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ACTION_UPDATE_UI);
        LocalBroadcastManager.getInstance(this).registerReceiver(uiUpdateReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(uiUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }




}