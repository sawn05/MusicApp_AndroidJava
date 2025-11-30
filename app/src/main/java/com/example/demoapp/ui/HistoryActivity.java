package com.example.demoapp.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.R;
import com.example.demoapp.adapter.HistoryAdapter;
import com.example.demoapp.model.HistoryLog;
import com.example.demoapp.storage_data.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistoryLogs;
    private HistoryAdapter adapter;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Intent intent = getIntent();
        currentUsername = intent.getStringExtra("EXTRA_USERNAME");

        if (currentUsername == null || currentUsername.isEmpty()) {
            currentUsername = "default_user";
        }

        rvHistoryLogs = findViewById(R.id.rv_history_logs);

        List<HistoryLog> logs = loadAllHistoryData();

        rvHistoryLogs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(this, logs);
        rvHistoryLogs.setAdapter(adapter);

        findViewById(R.id.tv_empty_state).setVisibility(logs.isEmpty() ? View.VISIBLE : View.GONE);
    }

    /*private List<HistoryLog> loadHistoryData(String username) {
        List<HistoryLog> logs = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(this);

        Cursor cursor = dbHelper.getRecentLoginHistory(username, 1);

        if (cursor != null && cursor.moveToFirst()) {
            int typeIndex = cursor.getColumnIndex("event_type");
            int timeIndex = cursor.getColumnIndex("timestamp");
            int detailIndex = cursor.getColumnIndex("details");

            do {
                String eventType = cursor.getString(typeIndex);
                String timestamp = cursor.getString(timeIndex);
                String details = cursor.getString(detailIndex);

                logs.add(new HistoryLog(eventType, timestamp, details));

            } while (cursor.moveToNext());

            cursor.close();
        }

        dbHelper.close();
        return logs;
    }*/

    private List<HistoryLog> loadAllHistoryData() {
        List<HistoryLog> logs = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(this);

        Cursor cursor = dbHelper.getAllUsageHistory();

        if (cursor != null && cursor.moveToFirst()) {

            // Lấy chỉ mục cột
            int typeIndex = cursor.getColumnIndex("event_type");
            int timeIndex = cursor.getColumnIndex(DBHelper.KEY_TIMESTAMP);
            int detailIndex = cursor.getColumnIndex("details");

            do {
                String eventType = cursor.getString(typeIndex);
                String timestamp = cursor.getString(timeIndex);
                String details = cursor.getString(detailIndex);

                logs.add(new HistoryLog(eventType, timestamp, details));

            } while (cursor.moveToNext());

            cursor.close();
        }
        return logs;
    }
}