package com.example.demoapp.ui;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.MyService;
import com.example.demoapp.R;
import com.example.demoapp.adapter.PlaylistAdapter;
import com.example.demoapp.model.Playlist;
import com.example.demoapp.storage_data.DBHelper;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MyAccountActivity extends AppCompatActivity {

    private RecyclerView rvPlaylists;

    private TextView tvName, tvEmail;
    public static final String EXTRA_USERNAME = "com.example.demoapp.USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        ImageButton btnSort = findViewById(R.id.btnSort);
        btnSort.setOnClickListener(v -> showCustomPopup(v));


        // --- Thông tin người dùng (hiển thị trên màn) ---
        tvName  = findViewById(R.id.tvNameUser);
        tvEmail = findViewById(R.id.tvEmailUser);

        String name  = getSharedPreferences("auth", MODE_PRIVATE)
                .getString("display_name",  "Người dùng");
        String email = getSharedPreferences("auth", MODE_PRIVATE)
                .getString("display_email", "email@example.com");

        tvName.setText(name);
        tvEmail.setText(email);

        // --- Xử lý nút đăng xuất ---
        Button btnLogout = findViewById(R.id.btnLogOut);
        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply();

            Intent stopMusic = new Intent(MyAccountActivity.this, MyService.class);
            stopService(stopMusic);

            Intent i = new Intent(MyAccountActivity.this, IntroActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });

        // --- Chỉnh sửa thông tin ---
        Button btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> showEditDialog());

        // Bind dữ liệu danh sách phát
        RecyclerView rvPlaylist = findViewById(R.id.rvPlaylists);
        DBHelper dbHelper = new DBHelper(this);
        List<Playlist> playlists = dbHelper.getAllAlbums();
        Log.d("DEBUG_PLAYLIST", "Số lượng album: " + playlists.size());

        PlaylistAdapter adapter = new PlaylistAdapter(playlists, R.layout.item_playlist_vertical);
        rvPlaylist.setLayoutManager(
                new LinearLayoutManager(this)
        );
        rvPlaylist.setAdapter(adapter);


        LinearLayout btnLikeActivity = findViewById(R.id.btnLikeActivity);
        btnLikeActivity.setOnClickListener(view -> {
            Intent intent = new Intent(MyAccountActivity.this, FavoriteSongs.class);
            startActivity(intent);
        });

        LinearLayout btnHomeActivity = findViewById(R.id.btnHomeActivity);
        btnHomeActivity.setOnClickListener(view -> {
            Intent intent = new Intent(MyAccountActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    // Popup filter
    private void showCustomPopup(View anchor) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.dialog_sort_playlist, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView optRecently = popupView.findViewById(R.id.opt_recently_added);
        TextView optName = popupView.findViewById(R.id.opt_name);
        TextView optAll = popupView.findViewById(R.id.opt_all);

        DBHelper dbHelper = new DBHelper(this);

        View.OnClickListener listener = clickedView -> {
            popupWindow.dismiss();

            List<Playlist> result;
            if (clickedView == optRecently) {
                result = dbHelper.getAlbumsRecentlyAdded();
                Toast.makeText(this, "Recently added", Toast.LENGTH_SHORT).show();
            } else if (clickedView == optName) {
                result = dbHelper.getAlbumsSortedByName();
                Toast.makeText(this, "Playlist name", Toast.LENGTH_SHORT).show();
            } else { // optAll
                result = dbHelper.getAllAlbums();
                Toast.makeText(this, "All playlists", Toast.LENGTH_SHORT).show();
            }

            // Cập nhật RecyclerView
            RecyclerView rvPlaylist = findViewById(R.id.rvPlaylists);
            PlaylistAdapter adapter = new PlaylistAdapter(result, R.layout.item_playlist_vertical);
            rvPlaylist.setAdapter(adapter);
        };

        optRecently.setOnClickListener(listener);
        optName.setOnClickListener(listener);
        optAll.setOnClickListener(listener);

        popupWindow.showAsDropDown(anchor, 0, 8);
    }



    // ========== Dialog chỉnh sửa tài khoản ==========
    private void showEditDialog() {
        try {
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_account, null, false);

            EditText edtName   = view.findViewById(R.id.edtName);
            EditText edtPhone  = view.findViewById(R.id.edtPhone);
            EditText edtEmail  = view.findViewById(R.id.edtEmail);
            CheckBox cbChange  = view.findViewById(R.id.cbChangePassword);
            LinearLayout pwGrp = view.findViewById(R.id.pwGroup);
            EditText edtCurPw  = view.findViewById(R.id.edtCurrentPassword);
            EditText edtNewPw  = view.findViewById(R.id.edtNewPassword);
            EditText edtCfPw   = view.findViewById(R.id.edtConfirmPassword);
            TextView tvHistory = view.findViewById(R.id.tvLoginHistory);

            String curName  = tvName.getText().toString();
            String curEmail = tvEmail.getText().toString();
            String curPhone = getSharedPreferences("auth", MODE_PRIVATE)
                    .getString("display_phone", "");

            edtName.setText(curName);
            edtEmail.setText(curEmail);
            edtPhone.setText(curPhone);

            // Lấy lịch sử đăng nhập
            DBHelper dbHelper = new DBHelper(this);
            String username = getSharedPreferences("auth", MODE_PRIVATE)
                    .getString("display_name", "unknown");

            List<String> history = dbHelper.getLastLoginHistory(username);

            if (history.isEmpty()) {
                tvHistory.setText("(chưa có lịch sử đăng nhập)");
            } else {
                StringBuilder sb = new StringBuilder();
                for (String item : history) {
                    try {
                        // ✅ Chấp nhận cả 2 định dạng thời gian
                        SimpleDateFormat input1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        SimpleDateFormat input2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
                        String formatted;
                        try {
                            formatted = output.format(input1.parse(item));
                        } catch (Exception ex) {
                            formatted = output.format(input2.parse(item));
                        }
                        sb.append("• ").append(formatted).append("\n");
                    } catch (Exception e) {
                        sb.append("• ").append(item).append("\n");
                    }
                }
                tvHistory.setText(sb.toString().trim());
            }

            cbChange.setOnCheckedChangeListener((b, checked) ->
                    pwGrp.setVisibility(checked ? View.VISIBLE : View.GONE));

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Chỉnh sửa tài khoản")
                    .setView(view)
                    .setNegativeButton("Hủy", (d, w) -> d.dismiss())
                    .setNeutralButton("Lịch sử sử dụng", (d, w) -> {
                        Intent intent = new Intent(this, HistoryActivity.class);
                        intent.putExtra("EXTRA_USERNAME", username);
                        startActivity(intent);
                        d.dismiss();
                    })
                    .setPositiveButton("Lưu", null)
                    .create();

            dialog.setOnShowListener(dlg -> {
                Button btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnSave.setOnClickListener(v -> {
                    String name  = edtName.getText().toString().trim();
                    String phone = edtPhone.getText().toString().trim();
                    String email = edtEmail.getText().toString().trim();

                    if (TextUtils.isEmpty(name)) {
                        edtName.setError("Vui lòng nhập tên"); return;
                    }
                    if (!phone.isEmpty() && !phone.matches("^[0-9]{9,12}$")) {
                        edtPhone.setError("SĐT không hợp lệ"); return;
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        edtEmail.setError("Email không hợp lệ"); return;
                    }

                    String newPassword = null;
                    if (cbChange.isChecked()) {
                        String cur = edtCurPw.getText().toString();
                        String nw  = edtNewPw.getText().toString();
                        String cf  = edtCfPw.getText().toString();

                        if (cur.isEmpty())      { edtCurPw.setError("Nhập mật khẩu hiện tại"); return; }
                        if (nw.length() < 6)    { edtNewPw.setError("Ít nhất 6 ký tự"); return; }
                        if (!nw.equals(cf))     { edtCfPw.setError("Không khớp"); return; }

                        newPassword = nw;
                    }

                    // ✅ Lưu local
                    getSharedPreferences("auth", MODE_PRIVATE).edit()
                            .putString("display_name", name)
                            .putString("display_email", email)
                            .putString("display_phone", phone)
                            .apply();

                    tvName.setText(name);
                    tvEmail.setText(email);

                    Toast.makeText(this, "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi mở dialog: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}