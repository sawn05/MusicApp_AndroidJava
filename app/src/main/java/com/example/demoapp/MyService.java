package com.example.demoapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.net.Uri;
import android.content.Context;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.demoapp.model.Song;

import java.io.IOException;
import java.util.List;

public class MyService extends Service {
    public static String currentSongName;

    public String getCurrentSongName() {
        return currentSongName;
    }

    public static String currentArtist;
    public static int currentImageRes;
    public static int currentSongResId;
    public static boolean currentIsFavorite;
    public static boolean isPlaying;

    public static MediaPlayer myMusic;

    public static MediaPlayer getMediaPlayer() {
        return myMusic;
    }
    public static final String ACTION_NEXT = "com.example.demoapp.ACTION_NEXT";

    public static final String ACTION_PREVIOUS = "com.example.demoapp.ACTION_PREVIOUS";
    public static List<Song> songQueue;
    private int currentSongIndex = -1;

    public static final String ACTION_UPDATE_UI = "com.example.demoapp.ACTION_UPDATE_UI";
    public static final String EXTRA_SONG_NAME = "extra_song_name";
    public static final String EXTRA_ARTIST = "extra_artist";
    public static final String EXTRA_IMAGE_SONG = "extra_image_song";
    public static final String EXTRA_SONG_RES_ID = "extra_song_res_id";


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }





    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;

        String action = intent.getAction();

        if ("ACTION_PLAY_NEW_SONG".equals(action)) {
            // Nhận dữ liệu bài hát
            int songResId = intent.getIntExtra("song_res_id", -1);
            String songName = intent.getStringExtra("songName");
            String artist = intent.getStringExtra("artist");
            int imageRes = intent.getIntExtra("imageSong", 0);
            boolean isFavorite = intent.getBooleanExtra("isFavorite", false);

            Log.d("MyService", "onStartCommand: "
                    + "songName=" + songName
                    + ", artist=" + artist
                    + ", imageRes=" + imageRes
                    + ", songResId=" + songResId);

            if (myMusic != null) {
                if (myMusic.isPlaying()) myMusic.stop();
                myMusic.release();
            }

            // Tạo mới MediaPlayer và phát
            myMusic = MediaPlayer.create(this, songResId);
            myMusic.start();
            sendMiniPlayerUpdate();

            // Cập nhật trạng thái hiện tại
            currentSongName = songName;
            currentArtist = artist;
            currentImageRes = imageRes;
            currentSongResId = songResId;
            currentIsFavorite = isFavorite;
            isPlaying = true;

        } else if ("ACTION_TOGGLE_PLAY_PAUSE".equals(action)) {
            if (myMusic != null) {
                if (myMusic.isPlaying()) {
                    myMusic.pause();
                    isPlaying = false;
                } else {
                    myMusic.start();
                    isPlaying = true;
                }
                sendMiniPlayerUpdate();
            }
        } else if ("ACTION_SEEK_TO".equals(action)) {
            if (myMusic != null) {
                int position = intent.getIntExtra("seek_position", 0);
                try {
                    myMusic.seekTo(position);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        } else if (ACTION_NEXT.equals(action)) {
            playNextSong();
            sendMiniPlayerUpdate();
        } else if (ACTION_PREVIOUS.equals(action)) {
            playPreviousSong();
            sendMiniPlayerUpdate();
        }else if ("ACTION_REQUEST_STATUS".equals(action)) {
            sendMiniPlayerUpdate();
        } else if ("ACTION_REQUEST_NOW_PLAYING".equals(action)) {
            sendNowPlayingInfo();
        }

        return START_STICKY;
    }

    private void playNextSong() {
        if (songQueue == null || songQueue.isEmpty()) {
            Toast.makeText(this, "Không có bài hát nào trong danh sách.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentSongIndex++;

        if (currentSongIndex >= songQueue.size()) {
            currentSongIndex = 0; // Quay lại bài đầu tiên
        }

        Song nextSong = songQueue.get(currentSongIndex);

        // 1. Dừng và khởi tạo lại MediaPlayer
        if (myMusic != null) {
            myMusic.stop();
            myMusic.reset();
            myMusic.release();
            myMusic = null;
        }

        // 2. Tải bài hát mới và bắt đầu phát
        // ... Khởi tạo và tải nextSong.getResId() vào MediaPlayer ...
        //
        startPlayback(nextSong);

        // 3. Gửi Broadcast để PlayMusic Activity cập nhật UI
        sendUpdateBroadcast(nextSong);
    }
    private void startPlayback(Song song) {
        if (myMusic != null) {
            myMusic.stop();
            myMusic.release();
            myMusic = null;
        }

        try {
            myMusic = new MediaPlayer();

            Uri audioUri = Uri.parse("android.resource://" + getPackageName() + "/" + song.getResId());
            myMusic.setDataSource(getApplicationContext(), audioUri);
            myMusic.prepare();

            // 5. Bắt đầu phát nhạc
            myMusic.start();

            myMusic.setOnCompletionListener(mp -> {
                handleSongCompletion();
            });

            sendUpdateBroadcast(song);
        } catch (IOException e) {
            // Xử lý lỗi nếu không thể tải file nhạc
            e.printStackTrace();
            // Tùy chọn: Gửi thông báo lỗi tới người dùng
        }
    }

    private void handleSongCompletion() {
        if (myMusic.isLooping()) {

        } else {
            playNextSong();
        }
    }


    private void playPreviousSong() {
        if (songQueue == null || songQueue.isEmpty()) {
            return;
        }

        currentSongIndex--;

        if (currentSongIndex < 0) {
            currentSongIndex = songQueue.size() - 1;
        }

        Song previousSong = songQueue.get(currentSongIndex);

        startPlayback(previousSong);
    }


    @Override
    public void onDestroy() {
        if (myMusic != null) {
            myMusic.stop();
            myMusic.release();
        }
        isPlaying = false;
        super.onDestroy();
    }

    private void sendMiniPlayerUpdate() {
        if (currentSongResId == -1 || myMusic == null) return;

        Intent intent = new Intent("ACTION_UPDATE_MINI_PLAYER");
        intent.putExtra("song_name", currentSongName);
        intent.putExtra("artist_name", currentArtist);
        intent.putExtra("is_playing", myMusic.isPlaying());
        intent.putExtra("image_res", currentImageRes);
        sendBroadcast(intent);
    }

    private void sendNowPlayingInfo() {
        Intent intent = new Intent("ACTION_NOW_PLAYING");
        intent.putExtra("songName", currentSongName);
        intent.putExtra("artist", currentArtist);
        intent.putExtra("imageSong", currentImageRes);
        intent.putExtra("song_res_id", currentSongResId);
        intent.putExtra("isPlaying", isPlaying);
        sendBroadcast(intent);
        //LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public int getTotalDuration() {
        if (myMusic != null) {
            try {
                return myMusic.getDuration();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }


    private void sendUpdateBroadcast(Song newSong) {
        Intent intent = new Intent(ACTION_UPDATE_UI);

        intent.putExtra(EXTRA_SONG_NAME, newSong.getNameSong());
        intent.putExtra(EXTRA_ARTIST, newSong.getArtist());
        intent.putExtra(EXTRA_IMAGE_SONG, newSong.getImageSong());
        intent.putExtra(EXTRA_SONG_RES_ID, newSong.getResId());

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
