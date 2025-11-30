package com.example.demoapp.storage_data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.demoapp.R;
import com.example.demoapp.model.Playlist;
import com.example.demoapp.model.Song;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "music.db";
    private static final int DATABASE_VERSION = 6;

    // Tên bảng
    private static final String TABLE_PLAYLISTS = "playlists";
    private static final String TABLE_PLAYLIST_SONGS = "playlist_songs";
    private static final String TABLE_SONGS = "songs";

    // Cột bảng PLAYLISTS
    private static final String KEY_PLAYLIST_ID = "playlist_id";
    private static final String KEY_PLAYLIST_NAME = "name";

    // Cột bảng PLAYLIST_SONGS
    private static final String KEY_PS_PLAYLIST_ID = "playlist_id";
    private static final String KEY_PS_SONG_ID = "song_id";
    private static final String KEY_PS_ORDER = "song_order";


    // Cột bảng TABLE_HISTORY
    private static final String TABLE_HISTORY = "usage_history";
    private static final String KEY_LOG_ID = "log_id";
    private static final String KEY_EVENT_TYPE = "event_type";
    public static final String KEY_TIMESTAMP = "timestamp";




    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE songs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "imageSong INTEGER, " +
                "nameSong TEXT, " +
                "artist TEXT, " +
                "album TEXT, " +
                "duration TEXT, " +
                "res_id INTEGER, " +
                "favourite INTEGER DEFAULT 0)";
        db.execSQL(createTable);

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.noinaycoanh + "', 'Nơi này có anh', 'Sơn Tùng MTP', 'Những bài hát hay nhất', '3:01', " + R.raw.noinaycoanh + ", 1)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.lamvoaanhnhe + "', 'Làm vợ anh nhé', 'Chi Dân', 'Tuyển tập Chi Dân', '3:20', " + R.raw.lamvoanhnhe + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.amthambenem + "', 'Âm thầm bên em', 'Sơn Tùng MTP', 'Những bài hát hay nhất', '4:30', " + R.raw.amthambenem + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.binhyennoidau + "', 'Bình yên nơi đâu', 'Sơn Tùng MTP', 'Những bài hát hay nhất', '3:45', " + R.raw.binhyennoidau + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.chacaidoseve + "', 'Chắc ai đó sẽ về', 'Sơn Tùng MTP', 'Những bài hát hay nhất', '4:05', " + R.raw.chacaidoseve + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.exitsign + "', 'Exit Sign', 'HieuThuHai', 'US-UK Hits', '3:50', " + R.raw.exitsign + ", 1)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.nneungayay + "', 'Nếu một ngày', 'Nguyễn Văn A', 'Ballad Buồn', '3:40', " + R.raw.neungayay + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.nguoiay + "', 'Người ấy', 'Trịnh Thăng Bình', 'Tuyển tập Trịnh Thăng Bình', '3:55', " + R.raw.nguoiay + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.nhungngaymua + "', 'Những ngày mưa', 'Unknown', 'Ballad Buồn', '4:00', " + R.raw.nhungngaymua + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.sainguoisaithoidiem + "', 'Sai người sai thời điểm', 'Thanh Hưng', 'Ballad Buồn', '4:15', " + R.raw.sainguoisaithoidiem + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.tethatanhnhoem + "', 'Tệ thật anh nhớ em', 'Thanh Hưng', 'Ballad Buồn', '3:35', " + R.raw.tethatanhnhoem + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.coduockhongem + "', 'Có được không em', 'Chi Dân', 'Tuyển tập Chi Dân', '3:35', " + R.raw.coduockhongem + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.chuyenanhvanchuake + "', 'Chuyện anh vẫn chưa kể', 'Chi Dân', 'Tuyển tập Chi Dân', '3:35', " + R.raw.chuyenanhvanchuake + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.lonhuanhyeuem + "', 'Lỡ như anh yêu em', 'Chi Dân', 'Tuyển tập Chi Dân', '3:35', " + R.raw.lonhuanhyeuem + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.nguoiyeugiandon + "', 'Người yêu giản đơn', 'Chi Dân', 'Tuyển tập Chi Dân', '3:35', " + R.raw.nguoiyeugiandon + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.changphaitinhdausaodaudenthe + "', 'Chẳng phải tình đầu sao đau đến thế', 'Min', 'US-UK Hits', '3:35', " + R.raw.changphaitinhdausaodaudenthe + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.densaunhunglandau + "', 'Đến sau những lần đau', 'Sixkie Dawgz', 'US-UK Hits', '3:35', " + R.raw.densaunhunglandau + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.khongthesay + "', 'Không thể say', 'HieuThuHai', 'US-UK Hits', '3:35', " + R.raw.khongthesay + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.lasttime2 + "', 'Last time 2', 'CDT ft. NeyT', 'US-UK Hits', '3:35', " + R.raw.lasttime2 + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.dauaidoiminh + "', 'Đâu ai đợi mình', 'Trịnh Thăng Bình', 'Tuyển tập Trịnh Thăng Bình', '3:55', " + R.raw.dauaidoiminh + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.giuachungtacokhacbietqualon + "', 'Giữa chúng ta có khác biệt quá lớn', 'Trịnh Thăng Bình', 'Tuyển tập Trịnh Thăng Bình', '3:55', " + R.raw.giuachungtacokhacbietqualon + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.tamsutuoi30 + "', 'Tân sự tuổi 30', 'Trịnh Thăng Bình', 'Tuyển tập Trịnh Thăng Bình', '3:55', " + R.raw.tamsutuoi30 + ", 0)");

        db.execSQL("INSERT INTO songs (imageSong, nameSong, artist, album, duration, res_id, favourite) " +
                "VALUES ('" + R.drawable.khongbietcachyeuem + "', 'Không biết cách yêu em', 'Đăng Quang Trần', 'Ballad Buồn', '3:55', " + R.raw.khongbietcachyeuem + ", 0)");





        // Lệnh CREATE TABLE cho BẢNG PLAYLISTS
        String CREATE_PLAYLISTS_TABLE = "CREATE TABLE " + TABLE_PLAYLISTS + "("
                + KEY_PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_PLAYLIST_NAME + " TEXT NOT NULL UNIQUE,"
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";
        db.execSQL(CREATE_PLAYLISTS_TABLE);

        // Lệnh CREATE TABLE cho BẢNG LIÊN KẾT
        String CREATE_PLAYLIST_SONGS_TABLE = "CREATE TABLE " + TABLE_PLAYLIST_SONGS + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_PS_PLAYLIST_ID + " INTEGER NOT NULL,"
                + KEY_PS_SONG_ID + " INTEGER NOT NULL,"
                + KEY_PS_ORDER + " INTEGER,"
                + "UNIQUE(" + KEY_PS_PLAYLIST_ID + ", " + KEY_PS_SONG_ID + "),"
                + "FOREIGN KEY(" + KEY_PS_PLAYLIST_ID + ") REFERENCES " + TABLE_PLAYLISTS + "(" + KEY_PLAYLIST_ID + ") ON DELETE CASCADE,"
                    + "FOREIGN KEY(" + KEY_PS_SONG_ID + ") REFERENCES " + TABLE_SONGS + "(" + "song_id" + ") ON DELETE CASCADE"
                + ")";
        db.execSQL(CREATE_PLAYLIST_SONGS_TABLE);


        ContentValues values = new ContentValues();

        // Playlist 1
        values.put(KEY_PLAYLIST_NAME, "Chill mỗi ngày");
        db.insert(TABLE_PLAYLISTS, null, values);

        // Playlist 2
        values.clear(); // Xóa giá trị cũ
        values.put(KEY_PLAYLIST_NAME, "Nhạc tập gym");
        db.insert(TABLE_PLAYLISTS, null, values);





        String createTableUser = "CREATE TABLE user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "password TEXT, " +
                "email TEXT" + ")";
        db.execSQL(createTableUser);

        String createTableHistory = "CREATE TABLE history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createTableHistory);





        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + KEY_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT, "
                + KEY_EVENT_TYPE + " TEXT NOT NULL,"
                + KEY_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "details TEXT" + ")";
        db.execSQL(CREATE_HISTORY_TABLE);


        db.execSQL("INSERT INTO user (username, password, email) " +
                "VALUES ('1', '1', 'sang@gmail.com')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng user cũ
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);

        // Tạo lại bảng mới
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public long addPlaylist(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PLAYLIST_NAME, name);

        // Chèn hàng
        long id = db.insert(TABLE_PLAYLISTS, null, values);
        db.close();
        return id;
    }

    public boolean addSongToPlaylist(long playlistId, long songId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Để bài hát mới ở cuối
        int maxOrder = getMaxSongOrder(db, playlistId);

        values.put(KEY_PS_PLAYLIST_ID, playlistId);
        values.put(KEY_PS_SONG_ID, songId);
        values.put(KEY_PS_ORDER, maxOrder + 1);

        // Nếu UNIQUE(playlist_id, song_id) bị vi phạm, insert sẽ trả về -1
        long result = db.insert(TABLE_PLAYLIST_SONGS, null, values);
        db.close();

        return result != -1;
    }

    // Hàm hỗ trợ để lấy thứ tự lớn nhất
    private int getMaxSongOrder(SQLiteDatabase db, long playlistId) {
        int maxOrder = 0;
        String selectQuery = "SELECT MAX(" + KEY_PS_ORDER + ") FROM " + TABLE_PLAYLIST_SONGS
                + " WHERE " + KEY_PS_PLAYLIST_ID + " = " + playlistId;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            maxOrder = cursor.getInt(0);
        }
        cursor.close();
        return maxOrder;
    }


    public long getPlaylistIdByName(String playlistName) {
        SQLiteDatabase db = this.getReadableDatabase();
        long playlistId = -1;

        // Sử dụng rawQuery hoặc query
        Cursor cursor = db.query(TABLE_PLAYLISTS, new String[]{KEY_PLAYLIST_ID},
                KEY_PLAYLIST_NAME + "=?", new String[]{playlistName},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Lấy giá trị của cột KEY_PLAYLIST_ID (cột 0)
            playlistId = cursor.getLong(0);
            cursor.close();
        }

        // Đóng db (Quan trọng!)
        db.close();
        return playlistId;
    }



    public int getNumSongByAlbum(String albumName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM songs WHERE album = ?", new String[]{albumName});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    // Hàm lấy danh sách bài hát
    public List<Song> getAllSongs() {
        List<Song> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM songs", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int imageSong = cursor.getInt(cursor.getColumnIndexOrThrow("imageSong"));
                String nameSong = cursor.getString(cursor.getColumnIndexOrThrow("nameSong"));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow("artist"));
                String album = cursor.getString(cursor.getColumnIndexOrThrow("album"));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"));
                int resId = cursor.getInt(cursor.getColumnIndexOrThrow("res_id"));
                boolean favourite = cursor.getInt(cursor.getColumnIndexOrThrow("favourite")) == 1;

                list.add(new Song(id, imageSong, nameSong, artist, album, duration, resId, favourite));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    // Ghi lại lịch sử đăng nhập
    public void insertHistory(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        db.insert("history", null, values);
        db.close();
    }

    // Lấy 5 lần đăng nhập gần nhất
    public List<String> getLastLoginHistory(String username) {
        List<String> history = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT timestamp FROM history WHERE username = ? ORDER BY id DESC LIMIT 5",
                new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                String time = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
                history.add(time);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return history;
    }



    // Hàm lấy danh sách bài hát theo album
    public List<Song> getSongsByAlbum(String albumName) {
        List<Song> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM songs WHERE album = ?", new String[]{albumName});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int imageSong = cursor.getInt(cursor.getColumnIndexOrThrow("imageSong"));
                String nameSong = cursor.getString(cursor.getColumnIndexOrThrow("nameSong"));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow("artist"));
                String album = cursor.getString(cursor.getColumnIndexOrThrow("album"));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"));
                int resId = cursor.getInt(cursor.getColumnIndexOrThrow("res_id"));
                boolean favourite = cursor.getInt(cursor.getColumnIndexOrThrow("favourite")) == 1;

                list.add(new Song(id, imageSong, nameSong, artist, album, duration, resId, favourite));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    // Lấy tất cả album theo thứ tự mới thêm nhất (Recently added)
    public List<Playlist> getAlbumsRecentlyAdded() {
        List<Playlist> albums = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // SQLite không có cột 'date' nên ta giả định id tăng dần theo thứ tự thêm
        String sql = "SELECT album, imageSong FROM songs GROUP BY album ORDER BY MAX(id) DESC";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                String albumName = cursor.getString(cursor.getColumnIndexOrThrow("album"));
                int imageSong = cursor.getInt(cursor.getColumnIndexOrThrow("imageSong"));
                albums.add(new Playlist(albumName, imageSong));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return albums;
    }

    // Lấy tất cả album theo tên (Playlist name)
    public List<Playlist> getAlbumsSortedByName() {
        List<Playlist> albums = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT album, imageSong FROM songs GROUP BY album ORDER BY album ASC";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                String albumName = cursor.getString(cursor.getColumnIndexOrThrow("album"));
                int imageSong = cursor.getInt(cursor.getColumnIndexOrThrow("imageSong"));
                albums.add(new Playlist(albumName, imageSong));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return albums;
    }



    // Hàm lấy danh sách bài hát theo nghệ sĩ
    public List<Song> getSongsByArtist(String artistName) {
        List<Song> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM songs WHERE artist = ?", new String[]{artistName});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int imageSong = cursor.getInt(cursor.getColumnIndexOrThrow("imageSong"));
                String nameSong = cursor.getString(cursor.getColumnIndexOrThrow("nameSong"));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow("artist"));
                String album = cursor.getString(cursor.getColumnIndexOrThrow("album"));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"));
                int resId = cursor.getInt(cursor.getColumnIndexOrThrow("res_id"));
                boolean favourite = cursor.getInt(cursor.getColumnIndexOrThrow("favourite")) == 1;

                list.add(new Song(id, imageSong, nameSong, artist, album, duration, resId, favourite));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }


    // Hàm lấy danh sách bài hát yêu thích
    public List<Song> getFavouriteSongs() {

        List<Song> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM songs WHERE favourite = 1", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int imageSong = cursor.getInt(cursor.getColumnIndexOrThrow("imageSong"));
                String nameSong = cursor.getString(cursor.getColumnIndexOrThrow("nameSong"));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow("artist"));
                String album = cursor.getString(cursor.getColumnIndexOrThrow("album"));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"));
                int resId = cursor.getInt(cursor.getColumnIndexOrThrow("res_id"));
                boolean favourite = cursor.getInt(cursor.getColumnIndexOrThrow("favourite")) == 1;

                list.add(new Song(id, imageSong, nameSong, artist, album, duration, resId, favourite));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public void updateFavourite(int songId, int favourite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("favourite", favourite);
        db.update("songs", values, "id = ?", new String[]{String.valueOf(songId)});
        db.close();
    }


    public boolean chekFavouriteSong(int songId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT favourite FROM songs WHERE id = ?", new String[]{String.valueOf(songId)});

        if (cursor.moveToFirst()) {
            int favourite = cursor.getInt(cursor.getColumnIndexOrThrow("favourite"));
            cursor.close();
            db.close();
            return favourite == 1;
        }
        cursor.close();
        db.close();
        return false;
    }

    // Hàm lấy tất cả album bài hát
    public List<Playlist> getAllAlbums() {
        List<Playlist> albums = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Lấy mỗi album 1 ảnh (ảnh đầu tiên trong album)
        String sql = "SELECT album, imageSong FROM songs GROUP BY album";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                String albumName = cursor.getString(cursor.getColumnIndexOrThrow("album"));
                int imageSong = cursor.getInt(cursor.getColumnIndexOrThrow("imageSong"));
                albums.add(new Playlist(albumName, imageSong));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return albums;
    }






    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM user WHERE username = ? AND password = ?",
                new String[]{username, password}
        );

        boolean isValid = cursor.moveToFirst();

        cursor.close();
        db.close();
        return isValid;
    }



    public boolean insertUser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("email", email);

        long result = db.insert("user", null, values);
        db.close();
        return result != -1;
    }

    public boolean isUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE username = ?", new String[]{username});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }


    public void recordEvent(String username, String eventType, String details){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        values.put("username", username);
        values.put(KEY_EVENT_TYPE, eventType);
        values.put(KEY_TIMESTAMP, currentTime);
        values.put("details", details);

        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }


    /*public Cursor getRecentLoginHistory(String username, int limit) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = "username = ? AND " + KEY_EVENT_TYPE + " = ?";
        String[] selectionArgs = new String[]{username, "LOGIN"};

        String[] projection = new String[]{
                KEY_TIMESTAMP,
                "details",
                KEY_EVENT_TYPE
        };

        return db.query(
                TABLE_HISTORY,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                KEY_TIMESTAMP + " DESC",
                String.valueOf(limit)
        );
    }*/



    public Cursor getAllUsageHistory() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = new String[]{
                "log_id",
                "username",
                "event_type",
                KEY_TIMESTAMP,
                "details"
        };

        Cursor cursor = db.query(
                TABLE_HISTORY,
                projection,
                null,
                null,
                null,
                null,
                KEY_TIMESTAMP + " DESC"
        );

            return cursor;
    }
}

