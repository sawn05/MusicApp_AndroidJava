package com.example.demoapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demoapp.R;
import com.example.demoapp.storage_data.DBHelper;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtName, edtEmail, edtPassword, edtConfirm;
    private CheckBox chkTerms;
    private Button btnRegister;
    private ImageView btnBack1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        edtName     = findViewById(R.id.etUsername);
        edtEmail    = findViewById(R.id.etEmail);
        edtPassword = findViewById(R.id.etPassword);
        edtConfirm  = findViewById(R.id.etConfirmPassword);
        chkTerms    = findViewById(R.id.chkTerms);
        btnRegister = findViewById(R.id.btnRegister);
        btnBack1    = findViewById(R.id.btnBack);

        // Nút Đăng ký chỉ bật khi đã đồng ý điều khoản
        btnRegister.setEnabled(chkTerms != null && chkTerms.isChecked());
        if (chkTerms != null) {
            chkTerms.setOnCheckedChangeListener((buttonView, isChecked) -> btnRegister.setEnabled(isChecked));
        }

        // Xử lý Đăng ký
        btnRegister.setOnClickListener(v -> {
            if (!validateForm()) return;

            String username = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            DBHelper dbHelper = new DBHelper(this);

            // Kiểm tra xem tài khoản đã tồn tại chưa
            if (dbHelper.isUserExists(username)) {
                Toast.makeText(this, "Tên tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thực hiện thêm mới
            boolean inserted = dbHelper.insertUser(username, password, email);
            if (inserted) {
                getSharedPreferences("auth", MODE_PRIVATE)
                        .edit()
                        .putBoolean("logged_in", true)
                        .putString("display_name", username)
                        .putString("display_email", email)
                        .apply();

                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                edtName.setText("");
                edtEmail.setText("");
                edtPassword.setText("");

                // Chuyển về màn hình đăng nhập
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi đăng ký, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });


        // Nút back
        if (btnBack1 != null) {
            btnBack1.setOnClickListener(v -> onBackPressed());
        }
    }

    private boolean validateForm() {
        String name  = edtName     != null ? edtName.getText().toString().trim()     : "";
        String email = edtEmail    != null ? edtEmail.getText().toString().trim()    : "";
        String pass  = edtPassword != null ? edtPassword.getText().toString().trim() : "";
        String conf  = edtConfirm  != null ? edtConfirm.getText().toString().trim()  : "";

        if (TextUtils.isEmpty(name)) {
            edtName.requestFocus(); edtName.setError("Nhập tên đăng nhập"); return false;
        }
        if (TextUtils.isEmpty(email)) {
            edtEmail.requestFocus(); edtEmail.setError("Nhập email"); return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.requestFocus(); edtEmail.setError("Email không hợp lệ"); return false;
        }
        if (TextUtils.isEmpty(pass)) {
            edtPassword.requestFocus(); edtPassword.setError("Nhập mật khẩu"); return false;
        }
        if (pass.length() < 6) {
            edtPassword.requestFocus(); edtPassword.setError("Mật khẩu tối thiểu 6 ký tự"); return false;
        }
        if (!pass.equals(conf)) {
            edtConfirm.requestFocus(); edtConfirm.setError("Mật khẩu xác nhận không khớp"); return false;
        }
        if (chkTerms != null && !chkTerms.isChecked()) {
            Toast.makeText(this, "Vui lòng đồng ý điều khoản", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}