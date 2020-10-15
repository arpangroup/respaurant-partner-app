package com.example.mainactivity.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.mainactivity.R;
import com.example.mainactivity.receivers.ConnectivityReceiver;

public class OfflineActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        swipeRefreshLayout = findViewById(R.id.swiperefresh_layout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            checkInternetConnection();
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                if(swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 1000);
        });


    }

    private void checkInternetConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if(isConnected){
            Intent intent = new Intent(OfflineActivity.this, MainActivity.class);
            //finishAffinity();
            startActivity(intent);
            finish();
        }
    }

}