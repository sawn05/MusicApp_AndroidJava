package com.example.demoapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.demoapp.model.Song;
import com.example.demoapp.ui.PlayMusic;

import java.io.IOException;
import java.util.List;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;


public class MyService extends Service {

    // ===== NOTIFICATION =====
    public static final String CHANNEL_ID = "music_channel_id";
    private static final int NOTIFICATION_ID = 1;

    // ===== ACTIONS =====
    public static final String ACTION_PLAY_NEW_SONG     = "ACTION_PLAY_NEW_SONG";
    public static final String ACTION_TOGGLE_PLAY_PAUSE = "ACTION_TOGGLE_PLAY_PAUSE";
    public static final String ACTION_SEEK_TO           = "ACTION_SEEK_TO";
    public static final String ACTION_REQUEST_STATUS    = "ACTION_REQUEST_STATUS";
    public static final String ACTION_REQUEST_NOW_PLAYING = "ACTION_REQUEST_NOW_PLAYING";

    public static final String ACTION_NEXT     = "com.example.demoapp.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "com.example.demoapp.ACTION_PREVIOUS";

    public static final String ACTION_UPDATE_UI    = "com.example.demoapp.ACTION_UPDATE_UI";
    public static final String EXTRA_SONG_NAME     = "extra_song_name";
    public static final String EXTRA_ARTIST        = "extra_artist";
    public static final String EXTRA_IMAGE_SONG    = "extra_image_song";
    public static final String EXTRA_SONG_RES_ID   = "extra_song_res_id";

    // ===== STATE =====
    public static String currentSongName;
    public static String currentArtist;
    public static int    currentImageRes;
    public static int    currentSongResId = -1;
    public static boolean currentIsFavorite;
    public static boolean isPlaying;

    public static MediaPlayer myMusic;
    public static List<Song> songQueue;
    public static int currentSongIndex = -1;

    public static MediaPlayer getMediaPlayer() {
        return myMusic;
    }

    public String getCurrentSongName() {
        return currentSongName;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    // ===== Notification channel =====
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Music playback";
            String description = "Music playback controls";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopForeground(true);   // tắt notification
        stopSelf();             // dừng Service
        super.onTaskRemoved(rootIntent);
    }


    private boolean hasPostNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Android < 13 không cần quyền này
    }


    // PendingIntent cho các action của notification
    private PendingIntent getActionIntent(String action) {
        Intent intent = new Intent(this, MyService.class);
        intent.setAction(action);
        return PendingIntent.getService(
                this,
                action.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    // ===== Build MediaStyle Notification (KHÔNG dùng MediaSessionCompat) =====
    private Notification buildNotification() {

        Intent openAppIntent = new Intent(this, PlayMusic.class);
        openAppIntent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        // Gửi luôn info bài đang phát sang Activity
        openAppIntent.putExtra("song_res_id", currentSongResId);
        openAppIntent.putExtra("songName",    currentSongName);
        openAppIntent.putExtra("artist",      currentArtist);
        openAppIntent.putExtra("imageSong",   currentImageRes);
        openAppIntent.putExtra("isPlaying",   isPlaying);
        openAppIntent.putExtra("from_notification", true);

        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        PendingIntent prevPendingIntent = getActionIntent(ACTION_PREVIOUS);
        PendingIntent playPausePendingIntent = getActionIntent(ACTION_TOGGLE_PLAY_PAUSE);
        PendingIntent nextPendingIntent = getActionIntent(ACTION_NEXT);

        boolean playing = myMusic != null && myMusic.isPlaying();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(currentSongName)
                        .setContentText(currentArtist)
                        .setLargeIcon(BitmapFactory.decodeResource(
                                getResources(),
                                currentImageRes
                        ))
                        .setContentIntent(contentIntent)
                        .setOngoing(playing)
                        .addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent)
                        .addAction(
                                playing ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play,
                                "Play/Pause",
                                playPausePendingIntent
                        )
                        .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent);

        androidx.media.app.NotificationCompat.MediaStyle style =
                new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1);

        builder.setStyle(style);

        return builder.build();
    }


    private void updateNotification() {
        // Chỉ check cho Android 13+ (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        Notification notification = buildNotification();
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification);
    }
    // ====== onStartCommand ======
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;

        String action = intent.getAction();

        if (ACTION_PLAY_NEW_SONG.equals(action)) {
            int songResId = intent.getIntExtra("song_res_id", -1);
            String songName = intent.getStringExtra("songName");
            String artist = intent.getStringExtra("artist");
            int imageRes = intent.getIntExtra("imageSong", 0);
            boolean isFavorite = intent.getBooleanExtra("isFavorite", false);

            Log.d("DEBUG_SERVICE",
                    "Nhận PLAY_NEW_SONG: resId=" + songResId + ", name=" + songName);

            Log.d("MyService", "onStartCommand: "
                    + "songName=" + songName
                    + ", artist=" + artist
                    + ", imageRes=" + imageRes
                    + ", songResId=" + songResId);

            if (myMusic != null) {
                if (myMusic.isPlaying()) myMusic.stop();
                myMusic.release();
            }

            myMusic = MediaPlayer.create(this, songResId);
            myMusic.setOnCompletionListener(mp -> handleSongCompletion());
            myMusic.start();

            currentSongName = songName;
            currentArtist   = artist;
            currentImageRes = imageRes;
            currentSongResId = songResId;
            currentIsFavorite = isFavorite;
            isPlaying = true;

            sendMiniPlayerUpdate();

            // foreground + notification
            startForeground(NOTIFICATION_ID, buildNotification());

        } else if (ACTION_TOGGLE_PLAY_PAUSE.equals(action)) {
            if (myMusic != null) {
                if (myMusic.isPlaying()) {
                    myMusic.pause();
                    isPlaying = false;
                } else {
                    myMusic.start();
                    isPlaying = true;
                }
                sendMiniPlayerUpdate();
                updateNotification();
            }

        } else if (ACTION_SEEK_TO.equals(action)) {
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
            updateNotification();

        } else if (ACTION_PREVIOUS.equals(action)) {
            playPreviousSong();
            sendMiniPlayerUpdate();
            updateNotification();

        } else if (ACTION_REQUEST_STATUS.equals(action)) {
            sendMiniPlayerUpdate();

        } else if (ACTION_REQUEST_NOW_PLAYING.equals(action)) {
            sendNowPlayingInfo();
        }

        return START_STICKY;
    }

    // ====== Next / Previous / Playback ======
    private void playNextSong() {
        if (songQueue == null || songQueue.isEmpty()) {
            Toast.makeText(this, "Không có bài hát nào trong danh sách.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentSongIndex++;
        if (currentSongIndex >= songQueue.size()) {
            currentSongIndex = 0;
        }

        Song nextSong = songQueue.get(currentSongIndex);

        if (myMusic != null) {
            myMusic.stop();
            myMusic.reset();
            myMusic.release();
            myMusic = null;
        }

        startPlayback(nextSong);
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
            myMusic.start();

            myMusic.setOnCompletionListener(mp -> handleSongCompletion());

            currentSongName   = song.getNameSong();
            currentArtist     = song.getArtist();
            currentImageRes   = song.getImageSong();
            currentSongResId  = song.getResId();
            currentIsFavorite = song.isFavorite();
            isPlaying         = true;

            sendMiniPlayerUpdate();
            sendUpdateBroadcast(song);

            startForeground(NOTIFICATION_ID, buildNotification());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSongCompletion() {
        if (myMusic != null && myMusic.isLooping()) {
            // nếu bạn xử lý repeat thì thêm logic ở đây
        } else {
            playNextSong();
        }
    }

    // ====== Lifecycle ======
    @Override
    public void onDestroy() {
        if (myMusic != null) {
            myMusic.stop();
            myMusic.release();
            myMusic = null;
        }
        isPlaying = false;
        stopForeground(true);
        super.onDestroy();
    }

    // ====== Broadcast UI ======
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
