package com.example.demoapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demoapp.R;
import com.example.demoapp.model.SessionManager;
import com.example.demoapp.storage_data.DBHelper;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        TextView tvRegister = findViewById(R.id.Register);
        tvRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });


        EditText edtUsername = findViewById(R.id.etUsername);
        EditText edtPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(view -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            DBHelper dbHelper = new DBHelper(this);
            boolean isValid = dbHelper.checkLogin(username, password);

            if (isValid) {
                // Ghi lịch sử đăng nhập
                dbHelper.insertHistory(username);
                dbHelper.recordEvent(username, "LOGIN", "Đăng nhập thành công");

                // Lưu thông tin user cho màn hình MyAccountActivity
                getSharedPreferences("auth", MODE_PRIVATE)
                        .edit()
                        .putString("display_name", username)
                        .putString("display_email", username + "@example.com")
                        .apply();

                // Sau khi đăng nhập thành công
                SessionManager.getInstance().setUsername(username);
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                // ✅ Chuyển sang HomeActivity
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }

            dbHelper.close();
        });





    }
}
